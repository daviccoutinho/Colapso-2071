package br.colapso2071.fx;

import javafx.application.Application;

/** Ponto de entrada separado da classe Application (evita problema de módulo ao empacotar o JAR). */
public final class Lancador {
    private Lancador() {
    }

    public static void main(String[] args) {
        Application.launch(ColapsoApp.class, args);
    }
}
