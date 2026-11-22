package br.colapso2071.model;

public class Atributos {
    private int lideranca;
    private int tecnico;
    private int negociacao;

    public Atributos(int lideranca, int tecnico, int negociacao) {
        this.lideranca = lideranca;
        this.tecnico = tecnico;
        this.negociacao = negociacao;
    }

    public int getLideranca() {
        return lideranca;
    }

    public void alterarLideranca(int delta) {
        this.lideranca += delta;
    }

    public int getTecnico() {
        return tecnico;
    }

    public void alterarTecnico(int delta) {
        this.tecnico += delta;
    }

    public int getNegociacao() {
        return negociacao;
    }

    public void alterarNegociacao(int delta) {
        this.negociacao += delta;
    }

    public int get(TipoAtributo tipo) {
        return switch (tipo) {
            case LIDERANCA -> lideranca;
            case TECNICO -> tecnico;
            case NEGOCIACAO -> negociacao;
        };
    }

    public void alterar(TipoAtributo tipo, int delta) {
        switch (tipo) {
            case LIDERANCA -> alterarLideranca(delta);
            case TECNICO -> alterarTecnico(delta);
            case NEGOCIACAO -> alterarNegociacao(delta);
        }
    }
}
