package br.colapso2071.view;

import java.io.PrintStream;
import java.util.Scanner;

/** Entrada e saída de console compartilhadas por todas as views (um único Scanner). */
public class Console {
    private final Scanner entrada;
    private final PrintStream saida;

    public Console(Scanner entrada, PrintStream saida) {
        this.entrada = entrada;
        this.saida = saida;
    }

    public void print(String texto) { saida.print(texto); }

    public void println(String texto) { saida.println(texto); }

    public void println() { saida.println(); }

    /** Lê uma linha; devolve null quando a entrada terminou. */
    public String lerLinha() {
        return entrada.hasNextLine() ? entrada.nextLine() : null;
    }

    /** Pergunta sim/não. Só "s" ou "sim" confirmam; fim da entrada conta como "não". */
    public boolean confirmar(String pergunta) {
        print(pergunta + " (s/n): ");
        String resposta = lerLinha();
        if (resposta == null) {
            return false;
        }
        String r = resposta.trim();
        return r.equalsIgnoreCase("s") || r.equalsIgnoreCase("sim");
    }
}
