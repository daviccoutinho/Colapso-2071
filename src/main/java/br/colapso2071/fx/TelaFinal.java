package br.colapso2071.fx;

import br.colapso2071.model.Conquista;
import br.colapso2071.model.Final;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

/** Tela de encerramento: final alcançado, epílogo e o que foi desbloqueado nesta partida. */
public final class TelaFinal {
    private final Final alcancado;
    private final boolean novoFinal;
    private final List<Conquista> novasConquistas;
    private final Runnable aoVoltar;

    public TelaFinal(Final alcancado, boolean novoFinal, List<Conquista> novasConquistas, Runnable aoVoltar) {
        this.alcancado = alcancado;
        this.novoFinal = novoFinal;
        this.novasConquistas = List.copyOf(novasConquistas);
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        VBox corpo = new VBox(14,
                Componentes.rotulo(alcancado.getTitulo(), "titulo"),
                Componentes.rotulo(alcancado.getMensagemEncerramento(), "texto"));
        if (alcancado.getEpilogo() != null && !alcancado.getEpilogo().isBlank()) {
            corpo.getChildren().add(Componentes.rotulo(alcancado.getEpilogo(), "texto"));
        }
        if (novoFinal) {
            corpo.getChildren().add(Componentes.rotulo("Novo final registrado na galeria.", "subtitulo"));
        }
        if (!novasConquistas.isEmpty()) {
            corpo.getChildren().add(Componentes.rotulo("Conquistas desbloqueadas", "titulo-linha"));
            for (Conquista c : novasConquistas) {
                corpo.getChildren().add(Componentes.rotulo(c.getTitulo() + " — " + c.getDescricao(), "texto"));
            }
        }
        ScrollPane rolagem = new ScrollPane(corpo);
        rolagem.setFitToWidth(true);
        VBox.setVgrow(rolagem, javafx.scene.layout.Priority.ALWAYS);

        Button menu = Componentes.botao("Voltar ao menu", AcaoMenu.Estilo.PRIMARIO);
        menu.setDefaultButton(true);
        menu.setOnAction(e -> aoVoltar.run());

        VBox painel = new VBox(20, rolagem, menu);
        painel.getStyleClass().add("painel-menu");
        painel.setPadding(new Insets(40, 200, 40, 200));
        painel.setAlignment(Pos.CENTER);
        return new StackPane(Imagens.vista(Arte.fundoDoFinal(alcancado.getId()), 1280, 720), painel);
    }
}
