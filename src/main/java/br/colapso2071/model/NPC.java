package br.colapso2071.model;

public class NPC {
    private final String id;
    private final String nome;
    private final String papel;

    public NPC(String id, String nome, String papel) {
        this.id = id;
        this.nome = nome;
        this.papel = papel;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPapel() {
        return papel;
    }
}