package br.colapso2071.fx;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

/** Tela simples de texto corrido, usada por Instruções e Créditos. */
public final class TelaTexto {
    private final String titulo;
    private final List<String> paragrafos;
    private final Runnable aoVoltar;
    private final String rotuloDoBotao;

    public TelaTexto(String titulo, List<String> paragrafos, Runnable aoVoltar) {
        this(titulo, paragrafos, "Voltar", aoVoltar);
    }

    /** Variante com rótulo próprio no botão, usada na introdução ("Começar"). */
    public TelaTexto(String titulo, List<String> paragrafos, String rotuloDoBotao, Runnable aoVoltar) {
        this.titulo = titulo;
        this.paragrafos = List.copyOf(paragrafos);
        this.rotuloDoBotao = rotuloDoBotao;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        VBox corpo = new VBox(14);
        for (String p : paragrafos) {
            corpo.getChildren().add(Componentes.rotulo(p, "texto"));
        }
        ScrollPane rolagem = new ScrollPane(corpo);
        rolagem.setFitToWidth(true);
        VBox.setVgrow(rolagem, javafx.scene.layout.Priority.ALWAYS);

        Button voltar = Componentes.botao(rotuloDoBotao, AcaoMenu.Estilo.NORMAL);
        voltar.setOnAction(e -> aoVoltar.run());
        voltar.setDefaultButton(true);

        voltar.setMaxWidth(260);
        VBox raiz = new VBox(16, Componentes.rotulo(titulo, "cartao-titulo"), Componentes.separador(), rolagem, voltar);
        return Componentes.emCartao(raiz, 900, 660);
    }
}
