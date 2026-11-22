package br.colapso2071;

import br.colapso2071.excecao.CarregamentoException;
import br.colapso2071.excecao.DadosInvalidosException;
import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Configuracoes;
import br.colapso2071.model.Decisao;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.Progresso;
import br.colapso2071.model.Rotas;
import br.colapso2071.persistencia.Json;
import br.colapso2071.persistencia.ConfiguracoesRepositorio;
import br.colapso2071.persistencia.ProgressoRepositorio;
import br.colapso2071.persistencia.ResumoSave;
import br.colapso2071.persistencia.SaveRepository;
import br.colapso2071.persistencia.SlotInfo;
import br.colapso2071.view.Console;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenciaTestesSuite {

    /** Relógio controlável, para testar data/hora dos saves e "mais recente". */
    static class RelogioFalso extends Clock {
        private Instant agora;

        RelogioFalso(String instanteIso) { this.agora = Instant.parse(instanteIso); }

        void avancarSegundos(long s) { agora = agora.plusSeconds(s); }

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zona) { return this; }
        @Override public Instant instant() { return agora; }
    }

    private final Rotas rotas = new Rotas();

    private Path novaPasta() throws Exception {
        return Files.createTempDirectory("colapso-saves");
    }

    private SaveRepository repo(Path pasta, Clock clock) {
        return new SaveRepository(pasta, rotas, clock);
    }

    /** Jogo avançado até a CENA_8 pelo caminho "bom" (18 decisões), com atributos alterados para testar a carga completa. */
    private Jogo jogoAvancado() {
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        jogo.getJogador().getAtributos().alterarLideranca(4);
        jogo.getJogador().getAtributos().alterarTecnico(7);
        jogo.getJogador().getAtributos().alterarNegociacao(-2);
        for (int i = 0; i < 19; i++) { // 15 cenas antigas + 3B, 5P, 6B e 7B
            jogo.atenderNecessidades();
            assertTrue(jogo.escolher(0));
        }
        assertEquals("CENA_8", jogo.getCenaAtual().getId());
        return jogo;
    }

    private void alterar(Path arquivo, String chave, String valor) throws Exception {
        Map<String, String> campos = new java.util.TreeMap<>(Json.ler(Files.readString(arquivo, StandardCharsets.UTF_8)));
        if (valor == null) {
            campos.remove(chave);
        } else {
            campos.put(chave, valor);
        }
        Files.writeString(arquivo, Json.escrever(campos), StandardCharsets.UTF_8);
    }

    /** Valor de um campo do arquivo JSON gravado pelo jogo. */
    private static String campo(Path arquivo, String chave) throws Exception {
        return Json.ler(Files.readString(arquivo, StandardCharsets.UTF_8)).get(chave);
    }

    // ---------- Slots, metadados e carregamento ----------

    @Test
    public void carregarRestauraEstadoCompleto() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Jogo original = jogoAvancado();
        saves.salvar(2, original);

        Jogo carregado = saves.carregar(2);
        Jogador a = original.getJogador();
        Jogador b = carregado.getJogador();

        assertEquals(original.getCenaAtual().getId(), carregado.getCenaAtual().getId());
        assertTrue(carregado.emAndamento());
        assertEquals(a.getNome(), b.getNome());
        assertEquals(a.getRecursos().getComida(), b.getRecursos().getComida());
        assertEquals(a.getRecursos().getAgua(), b.getRecursos().getAgua());
        assertEquals(a.getRecursos().getEnergia(), b.getRecursos().getEnergia());
        assertEquals(a.getAtributos().getLideranca(), b.getAtributos().getLideranca());
        assertEquals(a.getAtributos().getTecnico(), b.getAtributos().getTecnico());
        assertEquals(a.getAtributos().getNegociacao(), b.getAtributos().getNegociacao());
        assertTrue(b.getAtributos().getTecnico() >= 8, "atributos alterados no teste e ganhos na história devem sobreviver");
        assertEquals(a.getReputacao(), b.getReputacao());
        assertEquals(a.getRelacionamentos().getAfinidades(), b.getRelacionamentos().getAfinidades());
        assertEquals(a.getMissoes(), b.getMissoes());
        assertEquals(a.getDecisoes(), b.getDecisoes());
        assertEquals(a.getDesbloqueios(), b.getDesbloqueios());
        assertEquals(a.getInventario().getItens().size(), b.getInventario().getItens().size());
        assertTrue(b.getInventario().possuiItem("ITEM_PISTA_BOTAS"));
        assertEquals(19, b.getDecisoes().size());
        assertEquals(new Decisao("CENA_1A", 0), b.getDecisoes().get(0));
        assertEquals(a.getPerfil(), b.getPerfil());

        // A partida carregada continua jogável e chega ao mesmo final
        while (!carregado.cenaAtualLevaAoFinal()) { // 8, 9A, 9, 9B, 10A e 10B
            carregado.atenderNecessidades();
            assertTrue(carregado.escolher(0));
        }
        assertEquals("FINAL_BOM", carregado.calcularFinal().getId());
    }

    @Test
    public void metadadosDoSaveSaoExibiveis() throws Exception {
        RelogioFalso relogio = new RelogioFalso("2026-10-24T18:30:00Z");
        SaveRepository saves = repo(novaPasta(), relogio);

        ResumoSave r = saves.salvar(1, jogoAvancado());

        assertEquals(1, r.slot());
        assertEquals("Davi", r.nomeJogador());
        assertEquals(LocalDateTime.of(2026, 10, 24, 18, 30), r.dataHora());
        assertEquals(8, r.capitulo());
        assertEquals("A Máscara Cai", r.tituloCapitulo());
        assertEquals("CENA_8", r.cenaId());
        assertTrue(r.resumo().contains("Capítulo 8 de 10"));
        assertEquals(r, saves.resumo(1).get());
    }

    @Test
    public void ofereceTresSlotsMaisAutomaticoIndependentes() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        List<SlotInfo> inicial = saves.listarSlots();
        assertEquals(4, inicial.size());
        assertTrue(inicial.stream().allMatch(SlotInfo::vazio));

        Jogo jogo = jogoAvancado();
        saves.salvar(1, jogo);
        saves.salvar(3, jogo);

        List<SlotInfo> lista = saves.listarSlots();
        assertFalse(lista.get(0).vazio());
        assertTrue(lista.get(1).vazio());
        assertFalse(lista.get(2).vazio());
        assertTrue(lista.get(3).vazio()); // automático intocado
        assertTrue(saves.existe(1));
        assertFalse(saves.existe(2));
    }

    @Test
    public void salvamentoAutomaticoUsaSlotProprio() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        ResumoSave r = saves.salvarAutomatico(jogoAvancado());

        assertEquals(SaveRepository.SLOT_AUTO, r.slot());
        assertTrue(saves.existe(SaveRepository.SLOT_AUTO));
        assertFalse(saves.existe(1));
        assertEquals("CENA_8", saves.carregar(SaveRepository.SLOT_AUTO).getCenaAtual().getId());
    }

    @Test
    public void maisRecenteEscolheOSaveComDataHoraMaior() throws Exception {
        Path pasta = novaPasta();
        RelogioFalso relogio = new RelogioFalso("2026-10-24T10:00:00Z");
        SaveRepository saves = repo(pasta, relogio);
        assertTrue(saves.maisRecente().isEmpty());

        Jogo jogo = jogoAvancado();
        saves.salvar(1, jogo);
        relogio.avancarSegundos(60);
        saves.salvar(3, jogo);
        relogio.avancarSegundos(60);
        saves.salvar(2, jogo);
        relogio.avancarSegundos(60);
        saves.salvarAutomatico(jogo);

        assertEquals(SaveRepository.SLOT_AUTO, saves.maisRecente().get().slot());
        saves.excluir(SaveRepository.SLOT_AUTO);
        assertEquals(2, saves.maisRecente().get().slot());
    }

    @Test
    public void excluirRemoveOSave() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        saves.salvar(1, jogoAvancado());

        saves.excluir(1);

        assertFalse(saves.existe(1));
        assertThrows(SaveException.class, () -> saves.excluir(1)); // já vazio
    }

    // ---------- Exceções personalizadas ----------

    @Test
    public void slotInvalidoLancaDadosInvalidos() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        Jogo jogo = jogoAvancado();

        assertThrows(DadosInvalidosException.class, () -> saves.salvar(0, jogo)); // 0 = só automático
        assertThrows(DadosInvalidosException.class, () -> saves.salvar(4, jogo));
        assertThrows(DadosInvalidosException.class, () -> saves.carregar(-1));
        assertThrows(DadosInvalidosException.class, () -> saves.excluir(9));
    }

    @Test
    public void carregarSlotVazioLancaCarregamentoException() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        CarregamentoException e = assertThrows(CarregamentoException.class, () -> saves.carregar(2));
        assertTrue(e.getMessage().contains("vazio"));
    }

    @Test
    public void salvarSemPartidaEmAndamentoLancaSaveException() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        assertThrows(SaveException.class, () -> saves.salvar(1, new Jogo(rotas)));
    }

    @Test
    public void falhaDeGravacaoLancaSaveException() throws Exception {
        Path arquivoComum = Files.createTempFile("colapso", ".txt"); // "pasta" que é um arquivo
        SaveRepository saves = repo(arquivoComum, Clock.systemDefaultZone());
        assertThrows(SaveException.class, () -> saves.salvar(1, jogoAvancado()));
    }

    @Test
    public void dadosCorrompidosLancamDadosInvalidos() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Path arquivo = saves.arquivo(1);

        String[][] defeitos = {
                {"cena.id", "CENA_99"},                 // cena desconhecida
                {"cena.id", null},                      // campo obrigatório ausente
                {"recursos.comida", "-5"},              // fora do permitido
                {"recursos.agua", "muita"},             // não numérico
                {"inventario", "ITEM_INEXISTENTE"},     // item desconhecido
                {"afinidades", "NPC_FANTASMA:3"},       // NPC desconhecido
                {"missoes", "M_APOIO:TALVEZ"},          // status inválido
                {"decisoes", "CENA_1:7"},               // escolha inexistente
                {"desbloqueios", "CONQ_INEXISTENTE"},   // desbloqueio desconhecido
                {"versao", "99"},                       // versão não suportada
        };
        for (String[] d : defeitos) {
            saves.salvar(1, jogoAvancado());
            alterar(arquivo, d[0], d[1]);
            assertThrows(DadosInvalidosException.class, () -> saves.carregar(1),
                    "deveria rejeitar " + d[0] + "=" + d[1]);
        }
    }

    @Test
    public void arquivoComLixoEMarcadoComoIlegivelNaListagem() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Files.writeString(saves.arquivo(2), "isto nao e um save\n\\uZZZZ", StandardCharsets.UTF_8);
        saves.salvar(1, jogoAvancado());

        List<SlotInfo> lista = saves.listarSlots();
        assertFalse(lista.get(0).invalido());
        assertTrue(lista.get(1).invalido());
        assertThrows(DadosInvalidosException.class, () -> saves.carregar(2));
        // "continuar" ignora o save ilegível
        assertEquals(1, saves.maisRecente().get().slot());
    }

    // ---------- Configurações, finais e conquistas ----------

    @Test
    public void configuracoesPersistemEntreExecucoes() throws Exception {
        Path pasta = novaPasta();
        ConfiguracoesRepositorio repo = new ConfiguracoesRepositorio(pasta);

        Configuracoes padrao = repo.carregar(); // arquivo ainda não existe
        assertTrue(padrao.isSalvamentoAutomatico());
        assertFalse(padrao.isMostrarStatus());
        assertEquals("", padrao.getNomePadrao());

        padrao.setSalvamentoAutomatico(false);
        padrao.setMostrarStatus(true);
        padrao.setNomePadrao("  Dona Zefa ");
        repo.salvar(padrao);

        Configuracoes lida = new ConfiguracoesRepositorio(pasta).carregar();
        assertFalse(lida.isSalvamentoAutomatico());
        assertTrue(lida.isMostrarStatus());
        assertEquals("Dona Zefa", lida.getNomePadrao());
    }

    @Test
    public void configuracaoInvalidaLancaDadosInvalidos() throws Exception {
        Path pasta = novaPasta();
        Files.writeString(pasta.resolve("config.json"), "mostrarStatus=talvez\n");
        assertThrows(DadosInvalidosException.class, () -> new ConfiguracoesRepositorio(pasta).carregar());
    }

    @Test
    public void finaisEConquistasPersistemEntreExecucoes() throws Exception {
        Path pasta = novaPasta();
        ProgressoRepositorio repo = new ProgressoRepositorio(pasta);
        assertTrue(repo.carregar().getFinais().isEmpty());

        Progresso p = new Progresso();
        assertTrue(p.registrarFinal("FINAL_BOM"));
        assertFalse(p.registrarFinal("FINAL_BOM")); // já registrado
        assertEquals(List.of("CONQ_TRAVESSIA"), p.registrarConquistas(List.of("CONQ_TRAVESSIA")));
        assertTrue(p.registrarConquistas(List.of("CONQ_TRAVESSIA")).isEmpty());
        repo.salvar(p);

        Progresso lido = new ProgressoRepositorio(pasta).carregar();
        assertTrue(lido.alcancouFinal("FINAL_BOM"));
        assertFalse(lido.alcancouFinal("FINAL_RUIM"));
        assertTrue(lido.possuiConquista("CONQ_TRAVESSIA"));
    }

    @Test
    public void progressoCorrompidoLancaDadosInvalidos() throws Exception {
        Path pasta = novaPasta();
        Files.writeString(pasta.resolve("progresso.json"), "finais=final bom!!\n");
        assertThrows(DadosInvalidosException.class, () -> new ProgressoRepositorio(pasta).carregar());
    }

    // ---------- Versão do save e perfil do protagonista ----------

    @Test
    public void saveGuardaOPerfilEOsAtributosVoltamIguais() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi", rotas.getPerfil(Rotas.PERFIL_MECANICO));
        for (int i = 0; i < 4; i++) {
            assertTrue(jogo.escolher(0));
        }
        saves.salvar(1, jogo);

        assertEquals("2", campo(pasta.resolve("slot1.json"), "versao"));
        assertEquals(Rotas.PERFIL_MECANICO, campo(pasta.resolve("slot1.json"), "jogador.perfil"));

        Jogo carregado = saves.carregar(1);
        assertEquals(rotas.getPerfil(Rotas.PERFIL_MECANICO), carregado.getJogador().getPerfil());
        assertEquals(jogo.getJogador().getAtributos().getTecnico(), carregado.getJogador().getAtributos().getTecnico());
        assertEquals(jogo.getJogador().getAtributos().getLideranca(), carregado.getJogador().getAtributos().getLideranca());
        assertEquals(jogo.getJogador().getAtributos().getNegociacao(), carregado.getJogador().getAtributos().getNegociacao());
    }

    @Test
    public void saveAntigoVersao1ContinuaCarregandoComPerfilPadrao() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        assertTrue(jogo.escolher(0));
        saves.salvar(1, jogo);

        Path arquivo = pasta.resolve("slot1.json");
        alterar(arquivo, "versao", "1");
        alterar(arquivo, "jogador.perfil", null);
        alterar(arquivo, "integridade", null); // saves antigos não tinham selo de integridade

        Jogo carregado = saves.carregar(1);
        assertEquals("CENA_1", carregado.getCenaAtual().getId());
        assertEquals(Perfil.PADRAO, carregado.getJogador().getPerfil());
        assertEquals(jogo.getJogador().getReputacao(), carregado.getJogador().getReputacao());
    }

    @Test
    public void versaoDeSaveDesconhecidaEhRejeitada() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        saves.salvar(1, jogo);

        alterar(pasta.resolve("slot1.json"), "versao", "99");
        assertThrows(DadosInvalidosException.class, () -> saves.carregar(1));
    }

    @Test
    public void perfilDesconhecidoNoSaveEhRejeitado() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        saves.salvar(1, jogo);

        alterar(pasta.resolve("slot1.json"), "jogador.perfil", "PERFIL_INEXISTENTE");
        assertThrows(DadosInvalidosException.class, () -> saves.carregar(1));
    }

    // ---------- Fluxo completo pelo console ----------

    private String rodar(Path pasta, Clock clock, String... linhas) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        Console console = new Console(new Scanner(String.join("\n", linhas) + "\n"), saida);
        Aplicacao.criar(console, pasta, clock).executar();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private int contar(String texto, String trecho) {
        int n = 0;
        for (int i = texto.indexOf(trecho); i >= 0; i = texto.indexOf(trecho, i + 1)) {
            n++;
        }
        return n;
    }

    @Test
    public void salvarManualSoOcorreEmPontosDeSalvamento() throws Exception {
        Path pasta = novaPasta();
        // Na CENA_1A o comando S não existe: é opção inválida e nada é gravado
        String saida = rodar(pasta, Clock.systemDefaultZone(), "1", "Davi", "1", "S", "M", "s", "9");

        assertTrue(saida.contains("Opção inválida! Tente novamente."));
        assertFalse(saida.contains("S. Salvar jogo"));
        assertFalse(Files.exists(pasta.resolve("slot1.json")));
    }

    @Test
    public void sobrescritaExigeConfirmacao() throws Exception {
        Path pasta = novaPasta();
        RelogioFalso relogio = new RelogioFalso("2026-10-24T10:00:00Z");

        // Chega à CENA_3A (ponto de save) e salva no slot 1
        rodar(pasta, relogio, "1", "Davi", "1", "1", "1", "1", "1", "S", "1", "M", "s", "9");
        assertEquals("CENA_3A", campo(pasta.resolve("slot1.json"), "cena.id"));

        // Segunda partida (nome diferente) tenta sobrescrever e RECUSA
        relogio.avancarSegundos(120);
        String saida = rodar(pasta, relogio, "1", "Outro", "1", "1", "1", "1", "1", "S", "1", "n", "M", "s", "9");
        assertTrue(saida.contains("já contém um save"));
        assertEquals("Davi", campo(pasta.resolve("slot1.json"), "jogador.nome"));

        // E agora ACEITA
        rodar(pasta, relogio, "1", "Outro", "1", "1", "1", "1", "1", "S", "1", "s", "M", "s", "9");
        assertEquals("Outro", campo(pasta.resolve("slot1.json"), "jogador.nome"));
    }

    @Test
    public void exclusaoExigeConfirmacao() throws Exception {
        Path pasta = novaPasta();
        rodar(pasta, Clock.systemDefaultZone(), "1", "Davi", "1", "1", "1", "1", "1", "S", "2", "M", "s", "9");
        assertTrue(Files.exists(pasta.resolve("slot2.json")));

        String recusada = rodar(pasta, Clock.systemDefaultZone(), "4", "2", "n", "9");
        assertTrue(recusada.contains("Esta ação não pode ser desfeita"));
        assertTrue(Files.exists(pasta.resolve("slot2.json")));

        String aceita = rodar(pasta, Clock.systemDefaultZone(), "4", "2", "s", "9");
        assertTrue(aceita.contains("excluído"));
        assertFalse(Files.exists(pasta.resolve("slot2.json")));
    }

    @Test
    public void autosaveOcorreEmEventoRelevanteEPodeSerDesligado() throws Exception {
        // Chegar à CENA_4A (depois de 1A, 1, 2A, 2, 3A, 3 e 3B) dispara o salvamento automático
        Path pasta = novaPasta();
        String saida = rodar(pasta, Clock.systemDefaultZone(), "1", "Davi", "1", "1", "1", "1", "1", "1", "1", "1", "M", "s", "9");
        assertTrue(saida.contains("Progresso salvo automaticamente"));
        assertEquals("CENA_4A", campo(pasta.resolve("autosave.json"), "cena.id"));

        // Desliga nas configurações: nova partida não gera autosave
        Path outra = novaPasta();
        rodar(outra, Clock.systemDefaultZone(), "5", "1", "4", "9");
        String semAuto = rodar(outra, Clock.systemDefaultZone(), "1", "Davi", "1", "1", "1", "1", "1", "1", "1", "1", "M", "s", "9");
        assertFalse(semAuto.contains("Progresso salvo automaticamente"));
        assertFalse(Files.exists(outra.resolve("autosave.json")));
    }

    @Test
    public void continuarCarregaOSaveMaisRecente() throws Exception {
        Path pasta = novaPasta();
        RelogioFalso relogio = new RelogioFalso("2026-10-24T10:00:00Z");

        String semSaves = rodar(pasta, relogio, "2", "9");
        assertTrue(semSaves.contains("Nenhum save encontrado."));

        rodar(pasta, relogio, "1", "Antigo", "1", "1", "1", "1", "1", "S", "1", "M", "s", "9"); // slot 1, CENA_3A
        relogio.avancarSegundos(300);
        rodar(pasta, relogio, "1", "Recente", "1", "1", "1", "1", "1", "S", "3", "M", "s", "9"); // slot 3, CENA_3A

        String saida = rodar(pasta, relogio, "2", "M", "s", "9");
        assertTrue(saida.contains("Save carregado: Recente (CENA_3A)."));
    }

    @Test
    public void listaDeSavesMostraMetadados() throws Exception {
        Path pasta = novaPasta();
        RelogioFalso relogio = new RelogioFalso("2026-10-24T18:30:00Z");
        rodar(pasta, relogio, "1", "Davi", "1", "1", "1", "1", "1", "S", "2", "M", "s", "9");

        String saida = rodar(pasta, relogio, "3", "V", "9");
        assertTrue(saida.contains("[2] Davi | 24/10/2026 18:30 | Cap. 3 - Rota dos Espinhos (CENA_3A)"));
        assertTrue(saida.contains("Capítulo 3 de 10 (30%)"));
        assertTrue(saida.contains("[1] (vazio)"));
        assertTrue(saida.contains("[3] (vazio)"));
    }

    @Test
    public void saveCorrompidoNaoDerrubaOJogo() throws Exception {
        Path pasta = novaPasta();
        Files.writeString(pasta.resolve("slot1.json"), "versao=1\njogador.nome=X\n");

        String saida = rodar(pasta, Clock.systemDefaultZone(), "3", "1", "9");
        assertTrue(saida.contains("SAVE ILEGÍVEL"));
        assertTrue(saida.contains("[ERRO]"));
        assertTrue(saida.contains("Saindo do jogo..."));
    }

    @Test
    public void finalAlcancadoEConquistasSaoLembradosNaProximaExecucao() throws Exception {
        Path pasta = novaPasta();
        String antes = rodar(pasta, Clock.systemDefaultZone(), "6", "", "9");
        assertTrue(antes.contains("Finais alcançados: 0/4"));
        assertFalse(antes.contains("A Semente do Sertão"));

        String jogo = rodar(pasta, Clock.systemDefaultZone(), partidaInteira());
        assertTrue(jogo.contains("Novo final registrado: A Semente do Sertão"));
        assertTrue(jogo.contains("Conquista desbloqueada: Semente do Sertão"));

        // "Reinicia" o programa: tudo vem do disco
        String depois = rodar(pasta, Clock.systemDefaultZone(), "6", "", "9");
        assertTrue(depois.contains("Finais alcançados: 1/4"));
        assertTrue(depois.contains("[X] A Semente do Sertão"));
        assertTrue(depois.contains("[X] Mão Estendida"));
        assertTrue(depois.contains("[ ] ??? (final ainda não descoberto)"));

        // Jogar o mesmo final de novo não o anuncia como novo
        String repetido = rodar(pasta, Clock.systemDefaultZone(), partidaInteira());
        assertFalse(repetido.contains("Novo final registrado"));
    }

    @Test
    public void preferenciasSaoMantidasAposReiniciar() throws Exception {
        Path pasta = novaPasta();
        rodar(pasta, Clock.systemDefaultZone(), "5", "2", "3", "Zefa", "4", "9");

        String saida = rodar(pasta, Clock.systemDefaultZone(), "5", "4", "1", "", "1", "M", "s", "9");
        assertTrue(saida.contains("Mostrar status a cada cena: ATIVADO"));
        assertTrue(saida.contains("Nome padrão do jogador: Zefa"));
        assertTrue(saida.contains("[Enter = Zefa]"));
        // nome vazio -> usa o padrão; status visível na cena
        assertTrue(saida.contains("[Comida: 50 | Água: 50 | Reputação: 0]"));
        assertEquals(1, contar(saida, "[Enter = Zefa]"));
    }

    @Test
    public void diaEDescansoSaoSalvosECarregados() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        Jogo jogo = jogoAvancado();
        int dia = jogo.getJogador().getDia();
        assertTrue(jogo.dormir());
        saves.salvar(1, jogo);

        Jogo carregado = saves.carregar(1);
        assertEquals(dia + 1, carregado.getJogador().getDia());
        assertEquals("CENA_8", carregado.getJogador().getUltimaCenaDormida());
        // com água e comida em dia, não há motivo para dormir de novo na mesma cena
        carregado.getJogador().getRecursos().alterarAgua(100);
        carregado.getJogador().getRecursos().alterarComida(100);
        assertFalse(carregado.dormir(), "não pode dormir de novo na mesma cena depois de carregar");
    }

    @Test
    public void conversasRetomadasSaoSalvas() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        assertTrue(jogo.escolher(0));
        assertTrue(jogo.escolher(0));
        assertTrue(jogo.escolher(1));
        assertNotNull(jogo.retomarConversa(1));
        saves.salvar(1, jogo);

        Jogo carregado = saves.carregar(1);
        assertTrue(carregado.getJogador().getConversasRetomadas().contains("CENA_2A:2"));
        assertEquals(1, carregado.conversasPendentes().size());
    }

    /** Novo jogo, nome, perfil 1, opção 1 nas 24 cenas com decisão e sair. */
    private static String[] partidaInteira() {
        java.util.List<String> e = new java.util.ArrayList<>(java.util.List.of("1", "Davi", "1"));
        for (int i = 0; i < 25; i++) {
            e.add("1");
        }
        e.add("9");
        return e.toArray(new String[0]);
    }

    @Test
    public void lembrancasEDepositoSaoSalvos() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        assertTrue(jogo.escolher(0));
        assertNotNull(jogo.ouvirLembranca());
        assertTrue(jogo.buscarComida());
        saves.salvar(1, jogo);

        Jogo carregado = saves.carregar(1);
        assertTrue(carregado.getJogador().getConversasRetomadas().contains("L:milena_posto"));
        assertNull(carregado.lembrancaDaCena());
        assertFalse(carregado.buscarComida(), "a marmita de hoje já foi entregue");
    }

    @Test
    public void preferenciasDeMusicaETemaSaoSalvas() throws Exception {
        Path pasta = novaPasta();
        var repo = new br.colapso2071.persistencia.ConfiguracoesRepositorio(pasta);
        var cfg = new br.colapso2071.model.Configuracoes();
        assertTrue(cfg.isMusica() && cfg.isTemaModerno());
        cfg.setMusica(false);
        cfg.setVolumeMusica(35);
        cfg.setTemaModerno(false);
        assertFalse(cfg.isTelaCheia());
        cfg.setTelaCheia(true);
        assertEquals("AUTOMATICO", cfg.getSaidaAudio());
        cfg.setSaidaAudio("SISTEMA");
        repo.salvar(cfg);
        var lida = repo.carregar();
        assertFalse(lida.isMusica());
        assertEquals(35, lida.getVolumeMusica());
        assertFalse(lida.isTemaModerno());
        assertTrue(lida.isTelaCheia());
        assertEquals("SISTEMA", lida.getSaidaAudio());
        cfg.setVolumeMusica(500);
        assertEquals(100, cfg.getVolumeMusica());
    }

    @Test
    public void diaDoCarinhoNoCarameloESalvo() throws Exception {
        SaveRepository saves = repo(novaPasta(), Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        jogo.fazerCarinho();
        saves.salvar(1, jogo);
        Jogo carregado = saves.carregar(1);
        assertEquals(jogo.getJogador().getDia(), carregado.getJogador().getDiaCarinho());
        assertEquals(Jogo.Carinho.JA_HOJE, carregado.fazerCarinho());
    }

    @Test
    public void jogoInstaladoGuardaSavesNaPastaDoUsuario() throws Exception {
        var m = Class.forName("br.colapso2071.persistencia.PastaDeDados")
                .getDeclaredMethod("saves", String.class, String.class, String.class, String.class, java.util.Map.class);
        m.setAccessible(true);
        // pelo Maven/IDE: a pasta saves do projeto
        assertEquals(Path.of("saves"), m.invoke(null, null, null, "Linux", "/home/davi", java.util.Map.of()));
        // instalado (jpackage define jpackage.app-path)
        assertEquals(Path.of("/home/davi/.local/share/colapso2071/saves"),
                m.invoke(null, null, "/opt/colapso2071/bin/Colapso2071", "Linux", "/home/davi", java.util.Map.of()));
        assertEquals(Path.of("C:\\Users\\davi\\AppData\\Roaming", "Colapso2071", "saves"),
                m.invoke(null, null, "C:\\Program Files\\Colapso2071\\Colapso2071.exe", "Windows 11", "C:\\Users\\davi",
                        java.util.Map.of("APPDATA", "C:\\Users\\davi\\AppData\\Roaming")));
        assertEquals(Path.of("/Users/davi/Library/Application Support/Colapso2071/saves"),
                m.invoke(null, null, "/Applications/Colapso2071.app", "Mac OS X", "/Users/davi", java.util.Map.of()));
        // -Dcolapso.saves vence tudo
        assertEquals(Path.of("/tmp/x"), m.invoke(null, "/tmp/x", "/opt/a", "Linux", "/home/davi", java.util.Map.of()));
    }

    // ---------- Formato JSON ----------

    @Test
    public void saveEhGravadoEmJsonELidoDeVolta() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Zé \"Aspas\" \\ Ç");
        saves.salvar(1, jogo);

        String texto = Files.readString(pasta.resolve("slot1.json"), StandardCharsets.UTF_8);
        assertTrue(texto.startsWith("{\n"));
        assertTrue(texto.contains("\"jogador.nome\": \"Zé \\\"Aspas\\\" \\\\ Ç\""));
        assertEquals("Zé \"Aspas\" \\ Ç", saves.carregar(1).getJogador().getNome());
    }

    @Test
    public void jsonVoltaExatamenteComoFoiEscrito() {
        Map<String, String> campos = new java.util.TreeMap<>();
        campos.put("numero", "42");
        campos.put("negativo", "-7");
        campos.put("zeros", "007");
        campos.put("logico", "true");
        campos.put("texto", "linha 1\nlinha 2\t\u0001");
        campos.put("vazio", "");
        String json = Json.escrever(campos);
        assertTrue(json.contains("\"numero\": 42"));
        assertTrue(json.contains("\"zeros\": \"007\""));
        assertEquals(campos, Json.ler(json));
        assertEquals(Map.of(), Json.ler(Json.escrever(Map.of())));
        assertEquals(Map.of("a", "1.5e3"), Json.ler("{ \"a\" : 1.5e3, \"b\": null }"));
    }

    @Test
    public void jsonMalFormadoEhRejeitado() {
        for (String ruim : new String[]{"{", "{\"a\": }", "{\"a\": 1,}", "{\"a\": [1]}", "{\"a\": \"x\"} lixo",
                "{\"a\": 01}", "{\"a\": \"\\q\"}", "[]"}) {
            assertThrows(IllegalArgumentException.class, () -> Json.ler(ruim), "deveria rejeitar " + ruim);
        }
    }

    @Test
    public void jsonInvalidoNoSlotViraSaveIlegivel() throws Exception {
        Path pasta = novaPasta();
        SaveRepository saves = repo(pasta, Clock.systemDefaultZone());
        Files.writeString(pasta.resolve("slot1.json"), "{ \"versao\": 2, ", StandardCharsets.UTF_8);
        assertThrows(DadosInvalidosException.class, () -> saves.carregar(1));
        assertTrue(saves.listarSlots().get(0).invalido());
    }

    @Test
    public void arquivosDaVersaoAnteriorSaoRenomeadosELidos() throws Exception {
        Path pasta = novaPasta();
        SaveRepository antigo = repo(pasta, Clock.systemDefaultZone());
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        assertTrue(jogo.escolher(0));
        antigo.salvar(2, jogo);
        // simula o save da versão anterior: formato chave=valor com o nome slot2.save
        Map<String, String> campos = Json.ler(Files.readString(pasta.resolve("slot2.json"), StandardCharsets.UTF_8));
        java.util.Properties p = new java.util.Properties();
        p.putAll(campos);
        try (var w = Files.newBufferedWriter(pasta.resolve("slot2.save"), StandardCharsets.UTF_8)) {
            p.store(w, null);
        }
        Files.delete(pasta.resolve("slot2.json"));
        Files.writeString(pasta.resolve("config.properties"), "salvamentoAutomatico=false\n", StandardCharsets.UTF_8);

        SaveRepository novo = repo(pasta, Clock.systemDefaultZone());
        assertTrue(Files.exists(pasta.resolve("slot2.json")));
        assertFalse(Files.exists(pasta.resolve("slot2.save")));
        assertEquals("CENA_1", novo.carregar(2).getCenaAtual().getId());

        ConfiguracoesRepositorio config = new ConfiguracoesRepositorio(pasta);
        assertFalse(config.carregar().isSalvamentoAutomatico());
        config.salvar(config.carregar());
        assertTrue(Files.readString(pasta.resolve("config.json")).contains("\"salvamentoAutomatico\": false"));
    }
}
