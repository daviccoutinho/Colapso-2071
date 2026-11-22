package br.colapso2071.model;

/** Registro de uma escolha feita: em qual cena e qual opção (índice base 0). */
public record Decisao(String cenaId, int indiceEscolha) {
}
