package br.colapso2071.view;

import br.colapso2071.model.Configuracoes;

/** View das configurações: mostra os valores atuais e lê a opção/valor digitado. */
public class ConfiguracoesView {
    private final Console console;

    public ConfiguracoesView(Console console) {
        this.console = console;
    }

    /** Exibe as preferências e devolve a opção digitada (null se a entrada terminou). */
    public String lerOpcao(Configuracoes c) {
        console.println("\n--- CONFIGURAÇÕES ---");
        console.println("1. Salvamento automático: " + (c.isSalvamentoAutomatico() ? "ATIVADO" : "DESATIVADO"));
        console.println("2. Mostrar status a cada cena: " + (c.isMostrarStatus() ? "ATIVADO" : "DESATIVADO"));
        console.println("3. Nome padrão do jogador: " + (c.getNomePadrao().isEmpty() ? "(nenhum)" : c.getNomePadrao()));
        console.println("4. Voltar");
        console.print("Escolha uma opção: ");
        return console.lerLinha();
    }

    public String lerNomePadrao() {
        console.print("Novo nome padrão (vazio para remover): ");
        return console.lerLinha();
    }

    public void exibirOpcaoInvalida() {
        console.println("Opção inválida.");
    }

    public void exibirErro(String mensagem) {
        console.println("[ERRO] " + mensagem);
    }

    public void exibirAviso(String mensagem) {
        console.println("[AVISO] " + mensagem);
    }
}
