package br.colapso2071.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Mostra uma imagem em tela cheia (ex.: a Foto da Feira de 2030), com legenda e botão de voltar. */
public final class TelaImagem {
    private final String titulo;
    private final String arquivo;
    private final String legenda;
    private final Runnable aoVoltar;

    public TelaImagem(String titulo, String arquivo, String legenda, Runnable aoVoltar) {
        this.titulo = titulo;
        this.arquivo = arquivo;
        this.legenda = legenda;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        StackPane moldura = new StackPane(Imagens.vista(arquivo, 880, 495));
        moldura.getStyleClass().add("foto-moldura");
        moldura.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        Label texto = Componentes.rotulo(legenda, "texto");
        texto.setWrapText(true);
        texto.setMaxWidth(900);

        Button voltar = Componentes.botao("Voltar à mochila", AcaoMenu.Estilo.PRIMARIO);
        voltar.setDefaultButton(true);
        voltar.setCancelButton(true);
        voltar.setOnAction(e -> aoVoltar.run());

        VBox painel = new VBox(14, Componentes.rotulo(titulo, "titulo"), moldura, texto, voltar);
        painel.setAlignment(Pos.CENTER);
        painel.setPadding(new Insets(20));
        StackPane fundo = new StackPane(painel);
        fundo.getStyleClass().add("fundo-imagem");
        return fundo;
    }
}
