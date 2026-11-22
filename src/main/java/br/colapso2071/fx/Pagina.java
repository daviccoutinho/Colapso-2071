package br.colapso2071.fx;

/** Um trecho de texto mostrado de cada vez na caixa de diálogo. {@code npcId} é null fora das falas. */
public record Pagina(Tipo tipo, String npcId, String texto) {
    public enum Tipo { TITULO, NARRACAO, FALA }

    /** Id usado nas falas do protagonista (o retrato e o nome vêm do jogador, não de um NPC). */
    public static final String JOGADOR = "JOGADOR";

    public static Pagina doJogador(String texto) { return new Pagina(Tipo.FALA, JOGADOR, texto); }

    public static Pagina titulo(String texto) { return new Pagina(Tipo.TITULO, null, texto); }
    public static Pagina narracao(String texto) { return new Pagina(Tipo.NARRACAO, null, texto); }
    public static Pagina fala(String npcId, String texto) { return new Pagina(Tipo.FALA, npcId, texto); }
}
