package br.colapso2071.model;

import java.util.ArrayList;
import java.util.List;

public class Capitulo {
    private final int numero;
    private final String titulo;
    private final String introducao;
    private final List<Cena> cenas;

    public Capitulo(int numero, String titulo, String introducao) {
        this.numero = numero;
        this.titulo = titulo;
        this.introducao = introducao;
        this.cenas = new ArrayList<>();
    }

    public void adicionarCena(Cena cena) {
        if (cena != null) {
            this.cenas.add(cena);
        }
    }

    public int getNumero() { return numero; }
    public String getTitulo() { return titulo; }
    public String getIntroducao() { return introducao; }
    public List<Cena> getCenas() { return cenas; }
}