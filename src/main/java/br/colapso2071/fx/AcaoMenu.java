package br.colapso2071.fx;

/**
 * Padrão Command: cada opção do menu é um objeto com rótulo, atalho e execução.
 * O menu só conhece esta interface, então novas opções entram sem alterar a tela.
 */
public interface AcaoMenu {
    String rotulo();

    /** Tecla numérica que dispara a ação, igual ao menu de console. */
    String atalho();

    Estilo estilo();

    void executar();

    enum Estilo { PRIMARIO, NORMAL, PERIGO }

    static AcaoMenu de(String atalho, String rotulo, Estilo estilo, Runnable acao) {
        return new AcaoMenu() {
            @Override public String rotulo() { return rotulo; }
            @Override public String atalho() { return atalho; }
            @Override public Estilo estilo() { return estilo; }
            @Override public void executar() { acao.run(); }
        };
    }
}
