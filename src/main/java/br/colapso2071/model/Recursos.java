package br.colapso2071.model;

public class Recursos {

    private int comida;
    private int agua;
    private int energia;

    public Recursos(int comida, int agua, int energia) {
        this.comida = comida;
        this.agua = agua;
        this.energia = energia;
    }

    public int getComida() {
        return comida;
    }

    public void alterarComida(int delta) {
        this.comida = Math.max(0, this.comida + delta);
    }

    public int getAgua() {
        return agua;
    }

    public void alterarAgua(int delta) {
        this.agua = Math.max(0, this.agua + delta);
    }

    public int getEnergia() {
        return energia;
    }

    public void alterarEnergia(int delta) {
        this.energia = Math.max(0, this.energia + delta);
    }

    public boolean estaVivo() {
        return comida > 0 && agua > 0 && energia > 0;
    }
}