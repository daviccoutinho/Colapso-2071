package br.colapso2071.model;

/** Os três atributos do protagonista. */
public enum TipoAtributo {
    LIDERANCA("Liderança"),
    TECNICO("Técnico"),
    NEGOCIACAO("Negociação");

    private final String nome;

    TipoAtributo(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
}
