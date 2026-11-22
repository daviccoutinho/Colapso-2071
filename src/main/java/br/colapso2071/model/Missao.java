package br.colapso2071.model;

/** Definição (catálogo) de uma missão. O andamento fica no Jogador. */
public class Missao {
    private final String id;
    private final String titulo;
    private final String descricao;

    public Missao(String id, String titulo, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
}
