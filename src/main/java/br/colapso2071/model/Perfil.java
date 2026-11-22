package br.colapso2071.model;

/** Origem/ofício escolhido na criação do protagonista; define os atributos iniciais. */
public class Perfil {
    /** Perfil neutro, usado quando nenhum é escolhido (testes e saves antigos). Não aparece na criação. */
    public static final Perfil PADRAO = new Perfil("PERFIL_SOBREVIVENTE", "Sobrevivente",
            "Alguém que aprendeu um pouco de tudo para chegar vivo até aqui.", 1, 1, 1);

    private final String id;
    private final String nome;
    private final String descricao;
    private final int lideranca;
    private final int tecnico;
    private final int negociacao;

    public Perfil(String id, String nome, String descricao, int lideranca, int tecnico, int negociacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.lideranca = lideranca;
        this.tecnico = tecnico;
        this.negociacao = negociacao;
    }

    public Atributos criarAtributos() {
        return new Atributos(lideranca, tecnico, negociacao);
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getLideranca() { return lideranca; }
    public int getTecnico() { return tecnico; }
    public int getNegociacao() { return negociacao; }
}
