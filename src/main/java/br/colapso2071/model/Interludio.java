package br.colapso2071.model;

/**
 * Trecho que abre um capítulo: um parágrafo narrado ou uma fala de personagem (a cena acontece, não só é contada).
 * Com condição, só aparece para quem tomou certas decisões.
 */
public record Interludio(Condicao condicao, String texto, Dialogos fala) {

    public Interludio(Condicao condicao, String texto) {
        this(condicao, texto, null);
    }

    public boolean aparecePara(Jogador jogador) {
        return condicao == null || condicao.atende(jogador);
    }

    public boolean isFala() {
        return fala != null;
    }
}
