package br.colapso2071.model;

import java.util.List;

/**
 * Efeito de usar um item da mochila.
 * <ul>
 *   <li>consumível: some depois do uso;</li>
 *   <li>com {@code idDepois}: é trocado por outro item (a garrafa cheia vira garrafa vazia);</li>
 *   <li>permanente: continua na mochila e só mostra a mensagem;</li>
 *   <li>com {@code paginas}: é lido (abre o texto, como o caderno);</li>
 *   <li>com {@code imagem}: é visto em tela cheia (como a foto).</li>
 * </ul>
 * {@code verbo} é o rótulo do botão na mochila (Beber, Comer, Preparar, Ler...).
 */
public record Uso(int comida, int agua, int energia, boolean consumivel, String mensagem, String idDepois,
                  String verbo, List<String> paginas, String imagem) {

    public static Uso consumivel(int comida, int agua, int energia, String verbo, String mensagem) {
        return new Uso(comida, agua, energia, true, mensagem, null, verbo, List.of(), null);
    }

    /** O item continua na mochila, mas trocado por {@code idDepois} (ex.: garrafa vazia). */
    public static Uso esvazia(int comida, int agua, int energia, String idDepois, String verbo, String mensagem) {
        return new Uso(comida, agua, energia, false, mensagem, idDepois, verbo, List.of(), null);
    }

    public static Uso permanente(String verbo, String mensagem) {
        return new Uso(0, 0, 0, false, mensagem, null, verbo, List.of(), null);
    }

    /** Item para ler: as páginas abrem numa tela de texto. */
    public static Uso leitura(String mensagem, List<String> paginas) {
        return new Uso(0, 0, 0, false, mensagem, null, "Ler", List.copyOf(paginas), null);
    }

    /** Item para ver: a imagem abre em tela cheia, com a mensagem como legenda. */
    public static Uso imagem(String arquivo, String legenda) {
        return new Uso(0, 0, 0, false, legenda, null, "Ver", List.of(), arquivo);
    }

    public boolean temEfeito() {
        return comida != 0 || agua != 0 || energia != 0;
    }

    /** Recursos que o uso consome e que o jogador precisa ter (ex.: o café precisa de água). */
    public String faltaPara(Recursos r) {
        if (agua < 0 && r.getAgua() < -agua) {
            return "Você precisa de " + (-agua) + " de água para isso.";
        }
        if (comida < 0 && r.getComida() < -comida) {
            return "Você precisa de " + (-comida) + " de comida para isso.";
        }
        return "";
    }

    void aplicar(Recursos r) {
        r.alterarComida(comida);
        r.alterarAgua(agua);
        r.alterarEnergia(energia);
    }
}
