package br.colapso2071;

import br.colapso2071.view.Console;

import java.nio.file.Path;
import java.time.Clock;
import java.util.Scanner;

/** Ponto de entrada: monta a aplicação e inicia o menu. Os dados ficam na pasta "saves". */
public class Main {
    public static void main(String[] args) {
        Console console = new Console(new Scanner(System.in), System.out); // único Scanner da aplicação
        Aplicacao.criar(console, br.colapso2071.persistencia.PastaDeDados.saves(), Clock.systemDefaultZone()).executar();
    }
}
