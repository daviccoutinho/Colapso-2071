package br.colapso2071.fx;

import br.colapso2071.model.Conquista;
import br.colapso2071.model.Final;
import br.colapso2071.model.Progresso;
import br.colapso2071.model.Rotas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Tela "Finais e Conquistas" (protótipo "Galeria"): o que não foi descoberto aparece como ???. */
public final class TelaGaleria {
    private final Rotas rotas;
    private final Progresso progresso;
    private final Runnable aoVoltar;

    public TelaGaleria(Rotas rotas, Progresso progresso, Runnable aoVoltar) {
        this.rotas = rotas;
        this.progresso = progresso;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        VBox raiz = new VBox(18);

        HBox cabecalho = new HBox(Componentes.rotulo("Finais e Conquistas", "cartao-titulo"), Componentes.espaco(),
                Componentes.rotulo("Partidas concluídas: " + progresso.getPartidasConcluidas(), "rotulo"));
        cabecalho.setAlignment(Pos.BASELINE_LEFT);

        GridPane finais = grade(3);
        int i = 0;
        for (Final f : rotas.getFinais()) {
            boolean ok = progresso.alcancouFinal(f.getId());
            finais.add(cartao(ok ? "[X] FINAL" : "[ ] FINAL AINDA NÃO DESCOBERTO",
                    ok ? f.getTitulo() : "???", ok), i % 3, i / 3);
            i++;
        }

        GridPane conquistas = grade(2);
        int j = 0;
        int abertas = 0;
        for (Conquista c : rotas.getConquistas()) {
            boolean ok = progresso.possuiConquista(c.getId());
            abertas += ok ? 1 : 0;
            conquistas.add(cartao(ok ? "[X] " + c.getTitulo() : "[ ] ???",
                    ok ? c.getDescricao() : "conquista bloqueada", ok), j % 2, j / 2);
            j++;
        }

        Button voltar = Componentes.botao("Voltar", AcaoMenu.Estilo.NORMAL);
        voltar.setOnAction(e -> aoVoltar.run());
        voltar.setCancelButton(true);

        // Finais e conquistas não cabem todos no cartão: ficam numa área com rolagem (roda do mouse,
        // barra lateral, setas ou Page Up/Page Down); título e botão Voltar continuam sempre visíveis.
        VBox lista = new VBox(14,
                Componentes.rotulo("FINAIS  " + progresso.getFinais().size() + "/" + rotas.getFinais().size(), "rotulo"),
                finais,
                Componentes.rotulo("CONQUISTAS  " + abertas + "/" + rotas.getConquistas().size(), "rotulo"),
                conquistas);
        lista.setPadding(new Insets(0, 14, 8, 0)); // folga para a barra de rolagem não cobrir os cartões
        ScrollPane rolagem = new ScrollPane(lista);
        rolagem.setFitToWidth(true);
        rolagem.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rolagem.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rolagem.setPannable(true);
        VBox.setVgrow(rolagem, Priority.ALWAYS);

        raiz.getChildren().addAll(cabecalho, rolagem, voltar);
        return Componentes.emCartao(raiz, 1140, 680);
    }

    private static GridPane grade(int colunas) {
        GridPane g = new GridPane();
        g.setHgap(16);
        g.setVgap(10);
        for (int c = 0; c < colunas; c++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPercentWidth(100.0 / colunas);
            g.getColumnConstraints().add(cc);
        }
        return g;
    }

    private static VBox cartao(String linha1, String linha2, boolean desbloqueado) {
        VBox v = new VBox(4, Componentes.rotulo(linha1, "rotulo"), Componentes.rotulo(linha2, "titulo-linha"));
        v.getStyleClass().add(desbloqueado ? "painel" : "slot-vazio");
        v.setPadding(new Insets(12, 16, 12, 16));
        return v;
    }
}
