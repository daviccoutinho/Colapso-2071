package br.colapso2071.model;

public class Item {
    private final String id;
    private final String nome;
    private final String descricao;
    private final Uso uso;

    public Item(String id, String nome, String descricao) {
        this(id, nome, descricao, null);
    }

    /** Item que pode ser usado na mochila; {@code uso} nulo significa item só de história. */
    public Item(String id, String nome, String descricao, Uso uso) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.uso = uso;
    }

    public Uso getUso() {
        return uso;
    }

    public boolean isUsavel() {
        return uso != null;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
}
