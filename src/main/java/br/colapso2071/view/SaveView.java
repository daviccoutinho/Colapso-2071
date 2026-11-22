package br.colapso2071.view;

import br.colapso2071.persistencia.ResumoSave;
import br.colapso2071.persistencia.SaveRepository;
import br.colapso2071.persistencia.SlotInfo;

import java.time.format.DateTimeFormatter;
import java.util.List;

/** View dos saves: lista slots com metadados, pede slot e confirmações. */
public class SaveView {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Console console;

    public SaveView(Console console) {
        this.console = console;
    }

    public void exibirSlots(List<SlotInfo> slots, boolean incluirAutomatico) {
        console.println("\n--- SAVES ---");
        for (SlotInfo info : slots) {
            if (info.slot() == SaveRepository.SLOT_AUTO && !incluirAutomatico) {
                continue;
            }
            String rotulo = info.slot() == SaveRepository.SLOT_AUTO ? "A (automático)" : String.valueOf(info.slot());
            if (info.vazio()) {
                console.println("[" + rotulo + "] (vazio)");
            } else if (info.invalido()) {
                console.println("[" + rotulo + "] SAVE ILEGÍVEL - " + info.erro());
            } else {
                ResumoSave r = info.resumo();
                console.println("[" + rotulo + "] " + r.nomeJogador() + " | " + r.dataHora().format(FORMATO)
                        + " | Cap. " + r.capitulo() + " - " + r.tituloCapitulo() + " (" + r.cenaId() + ")");
                console.println("      " + r.resumo());
            }
        }
    }

    /** Pergunta qual slot usar; devolve o texto digitado, ou null se a entrada terminou. */
    public String lerSlot(String acao, boolean permiteAutomatico) {
        String opcoes = permiteAutomatico ? "1-3, A = automático" : "1-3";
        console.print("Qual slot deseja " + acao + "? [" + opcoes + ", V = voltar]: ");
        return console.lerLinha();
    }

    public boolean confirmarSobrescrita(int slot, ResumoSave existente) {
        String detalhe = (existente == null) ? "" : " (" + existente.nomeJogador() + ", "
                + existente.dataHora().format(FORMATO) + ")";
        return console.confirmar("O slot " + SaveRepository.nomeDoSlot(slot) + " já contém um save" + detalhe
                + ". Deseja sobrescrevê-lo?");
    }

    public boolean confirmarExclusao(int slot, ResumoSave existente) {
        String detalhe = (existente == null) ? "" : " (" + existente.nomeJogador() + ", "
                + existente.dataHora().format(FORMATO) + ")";
        return console.confirmar("Excluir o save do slot " + SaveRepository.nomeDoSlot(slot) + detalhe
                + "? Esta ação não pode ser desfeita");
    }

    public void exibirSalvo(ResumoSave r) {
        console.println("Jogo salvo no slot " + SaveRepository.nomeDoSlot(r.slot()) + " em "
                + r.dataHora().format(FORMATO) + ".");
    }

    public void exibirExcluido(int slot) {
        console.println("Save do slot " + SaveRepository.nomeDoSlot(slot) + " excluído.");
    }

    public void exibirCarregado(String nome, String cenaId) {
        console.println("Save carregado: " + nome + " (" + cenaId + ").");
    }

    public void exibirRecuperadoDoBackup(int slot) {
        console.println("Aviso: o save do slot " + SaveRepository.nomeDoSlot(slot)
                + " estava danificado. Foi recuperada a versão anterior (backup).");
    }

    public void exibirNenhumSave() {
        console.println("Nenhum save encontrado.");
    }

    public void exibirSlotInvalido() {
        console.println("Slot inválido.");
    }

    public void exibirErro(String mensagem) {
        console.println("[ERRO] " + mensagem);
    }
}
