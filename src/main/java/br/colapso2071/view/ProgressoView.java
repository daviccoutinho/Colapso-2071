package br.colapso2071.view;

import br.colapso2071.model.Conquista;
import br.colapso2071.model.Final;
import br.colapso2071.model.Progresso;
import br.colapso2071.model.Rotas;

/** View da galeria: finais alcançados e conquistas desbloqueadas. */
public class ProgressoView {
    private final Console console;

    public ProgressoView(Console console) {
        this.console = console;
    }

    public void exibir(Rotas rotas, Progresso progresso) {
        console.println("\n--- FINAIS ---");
        int alcancados = 0;
        for (Final f : rotas.getFinais()) {
            if (progresso.alcancouFinal(f.getId())) {
                alcancados++;
                console.println("[X] " + f.getTitulo() + " - " + f.getMensagemEncerramento());
            } else {
                console.println("[ ] ??? (final ainda não descoberto)");
            }
        }
        console.println("Finais alcançados: " + alcancados + "/" + rotas.getFinais().size());

        console.println("\n--- CONQUISTAS ---");
        int obtidas = 0;
        for (Conquista c : rotas.getConquistas()) {
            if (progresso.possuiConquista(c.getId())) {
                obtidas++;
                console.println("[X] " + c.getTitulo() + " - " + c.getDescricao());
            } else {
                console.println("[ ] ??? (conquista bloqueada)");
            }
        }
        console.println("Conquistas: " + obtidas + "/" + rotas.getConquistas().size());
        console.println("Partidas concluídas: " + progresso.getPartidasConcluidas());
        console.println("Pressione ENTER para voltar...");
        console.lerLinha();
    }
}
