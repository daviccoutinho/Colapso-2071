package br.colapso2071.model;

/** Conteúdo desbloqueável: uma conquista que persiste entre execuções do jogo. */
public class Conquista {
    private final String id;
    private final String titulo;
    private final String descricao;

    public Conquista(String id, String titulo, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
}
