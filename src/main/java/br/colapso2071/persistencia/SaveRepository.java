package br.colapso2071.persistencia;

import br.colapso2071.excecao.CarregamentoException;
import br.colapso2071.excecao.DadosInvalidosException;
import br.colapso2071.excecao.PersistenciaException;
import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Rotas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Gerencia os arquivos de save: {@value #TOTAL_SLOTS} slots manuais (1 a 3) e um slot
 * de salvamento automático ({@link #SLOT_AUTO}).
 */
public class SaveRepository {
    public static final int SLOT_AUTO = 0;
    public static final int TOTAL_SLOTS = 3;

    private final Path diretorio;
    private final Rotas rotas;
    private final Clock clock;

    public SaveRepository(Path diretorio, Rotas rotas, Clock clock) {
        this.diretorio = diretorio;
        this.rotas = rotas;
        this.clock = clock;
        for (int slot = SLOT_AUTO; slot <= TOTAL_SLOTS; slot++) {
            Path novo = arquivo(slot);
            ArquivoDados.migrar(novo.resolveSibling(novo.getFileName().toString().replace(".json", ".save")), novo);
        }
    }

    public Path arquivo(int slot) {
        return diretorio.resolve(slot == SLOT_AUTO ? "autosave.json" : "slot" + slot + ".json");
    }

    /** Cópia do save anterior, feita automaticamente toda vez que o slot é sobrescrito. */
    public Path arquivoDeBackup(int slot) {
        Path principal = arquivo(slot);
        return principal.resolveSibling(principal.getFileName().toString().replace(".json", ".bak"));
    }

    public boolean existeBackup(int slot) {
        return Files.exists(arquivoDeBackup(slot));
    }

    public static boolean slotManualValido(int slot) {
        return slot >= 1 && slot <= TOTAL_SLOTS;
    }

    private static void validarSlot(int slot, boolean permiteAuto) throws DadosInvalidosException {
        boolean ok = slotManualValido(slot) || (permiteAuto && slot == SLOT_AUTO);
        if (!ok) {
            throw new DadosInvalidosException("Slot inválido: " + slot);
        }
    }

    public boolean existe(int slot) {
        return Files.exists(arquivo(slot));
    }

    /** Situação dos slots 1..3 e, por último, do salvamento automático. */
    public List<SlotInfo> listarSlots() {
        List<SlotInfo> lista = new ArrayList<>();
        for (int slot = 1; slot <= TOTAL_SLOTS; slot++) {
            lista.add(informacao(slot));
        }
        lista.add(informacao(SLOT_AUTO));
        return lista;
    }

    private SlotInfo informacao(int slot) {
        if (!existe(slot)) {
            return new SlotInfo(slot, null, null);
        }
        try {
            return new SlotInfo(slot, lerResumoDoArquivo(slot), null);
        } catch (PersistenciaException e) {
            return new SlotInfo(slot, null, e.getMessage());
        }
    }

    private ResumoSave lerResumoDoArquivo(int slot) throws CarregamentoException, DadosInvalidosException {
        return SaveSerializador.lerResumo(slot, lerVerificado(arquivo(slot)));
    }

    /** Metadados do slot, ou vazio se não houver save. */
    public Optional<ResumoSave> resumo(int slot) throws CarregamentoException, DadosInvalidosException {
        validarSlot(slot, true);
        if (!existe(slot)) {
            return Optional.empty();
        }
        return Optional.of(lerResumoDoArquivo(slot));
    }

    /** Save legível mais recente entre todos os slots (inclusive o automático). */
    public Optional<ResumoSave> maisRecente() {
        return listarSlots().stream()
                .map(SlotInfo::resumo)
                .filter(r -> r != null)
                .max(Comparator.comparing(ResumoSave::dataHora));
    }

    /** Salva manualmente no slot 1..3. A confirmação de sobrescrita é responsabilidade de quem chama. */
    public ResumoSave salvar(int slot, Jogo jogo) throws SaveException, DadosInvalidosException {
        if (!slotManualValido(slot)) {
            throw new DadosInvalidosException("Slot inválido para salvamento manual: " + slot);
        }
        return gravar(slot, jogo);
    }

    public ResumoSave salvarAutomatico(Jogo jogo) throws SaveException {
        return gravar(SLOT_AUTO, jogo);
    }

    private ResumoSave gravar(int slot, Jogo jogo) throws SaveException {
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MILLIS);
        Properties dados = SaveSerializador.serializar(jogo, agora);
        Integridade.selar(dados);
        guardarBackup(slot);
        ArquivoDados.gravar(arquivo(slot), dados);
        try {
            return SaveSerializador.lerResumo(slot, dados);
        } catch (DadosInvalidosException e) {
            throw new SaveException("O save foi gravado, mas seus metadados são inconsistentes.", e);
        }
    }

    /** Antes de sobrescrever, preserva o save atual como backup. Falha no backup não impede o novo save. */
    private void guardarBackup(int slot) {
        if (!existe(slot)) {
            return;
        }
        try {
            Files.copy(arquivo(slot), arquivoDeBackup(slot), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignorada) {
            // backup é uma cortesia: o salvamento segue normalmente
        }
    }

    private static Properties lerVerificado(Path arquivo) throws CarregamentoException, DadosInvalidosException {
        Properties dados = ArquivoDados.ler(arquivo);
        Integridade.verificar(dados, arquivo.getFileName().toString());
        return dados;
    }

    /** Carregamento estrito: se o arquivo do slot estiver danificado, lança exceção. */
    public Jogo carregar(int slot) throws CarregamentoException, DadosInvalidosException {
        validarSlot(slot, true);
        if (!existe(slot)) {
            throw new CarregamentoException("O slot " + nomeDoSlot(slot) + " está vazio.");
        }
        return SaveSerializador.desserializar(lerVerificado(arquivo(slot)), rotas);
    }

    /**
     * Como {@link #carregar}, mas se o save principal estiver ilegível, adulterado ou inválido tenta o
     * backup do slot. Se o backup também falhar, lança a exceção do save principal.
     */
    public ResultadoCarga carregarComRecuperacao(int slot) throws CarregamentoException, DadosInvalidosException {
        try {
            return new ResultadoCarga(carregar(slot), false);
        } catch (CarregamentoException | DadosInvalidosException principal) {
            if (existe(slot) && existeBackup(slot)) {
                try {
                    Jogo doBackup = SaveSerializador.desserializar(lerVerificado(arquivoDeBackup(slot)), rotas);
                    return new ResultadoCarga(doBackup, true);
                } catch (CarregamentoException | DadosInvalidosException ignorada) {
                    // backup também inutilizável: vale o erro original
                }
            }
            throw principal;
        }
    }

    public void excluir(int slot) throws SaveException, DadosInvalidosException {
        validarSlot(slot, true);
        try {
            if (!Files.deleteIfExists(arquivo(slot))) {
                throw new SaveException("O slot " + nomeDoSlot(slot) + " já está vazio.");
            }
            Files.deleteIfExists(arquivoDeBackup(slot));
        } catch (IOException e) {
            throw new SaveException("Não foi possível excluir o slot " + nomeDoSlot(slot) + ": " + e.getMessage(), e);
        }
    }

    public static String nomeDoSlot(int slot) {
        return slot == SLOT_AUTO ? "automático" : String.valueOf(slot);
    }
}
