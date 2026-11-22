package br.colapso2071.fx;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/** Fábrica de componentes visuais da identidade (botões, rótulos), para as telas não repetirem estilo. */
final class Componentes {
    private Componentes() {
    }

    static Button botao(String texto, AcaoMenu.Estilo estilo) {
        Button b = new Button(texto);
        b.getStyleClass().add(switch (estilo) {
            case PRIMARIO -> "botao-primario";
            case PERIGO -> "botao-perigo";
            case NORMAL -> "botao";
        });
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    static Label rotulo(String texto, String... classes) {
        Label l = new Label(texto);
        l.getStyleClass().addAll(classes);
        l.setWrapText(true);
        return l;
    }

    static Region espaco() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    // ---------- versão 4.3: peças do redesenho da interface ----------

    /**
     * Fundo das telas que abrem por cima do jogo (pausa, mochila, ficha): a última cena, desfocada e escurecida.
     * Sem cena ainda, usa o panorama do menu.
     */
    static StackPane fundoDesfocado() {
        javafx.scene.image.Image foto = Navegador.ultimaCena();
        ImageView v = foto != null ? new ImageView(foto) : Imagens.vista("cena_menu.png", 1280, 720);
        v.setFitWidth(1340);
        v.setFitHeight(754);
        GaussianBlur desfoque = new GaussianBlur(16);
        ColorAdjust escuro = new ColorAdjust();
        escuro.setBrightness(-0.32);
        escuro.setSaturation(-0.2);
        escuro.setInput(desfoque);
        v.setEffect(escuro);
        Pane veu = new Pane();
        veu.getStyleClass().add("veu-escuro");
        StackPane fundo = new StackPane(v, veu);
        fundo.setMinSize(1280, 720);
        fundo.setMaxSize(1280, 720);
        return fundo;
    }

    /** Linha fina dourada que some nas pontas, para separar blocos dentro dos cartões. */
    static Region separador() {
        Region r = new Region();
        r.getStyleClass().add("separador");
        r.setMinHeight(2);
        r.setPrefHeight(2);
        r.setMaxHeight(2);
        return r;
    }

    /**
     * Botão de menu: ícone, rótulo, uma linha de detalhe (opcional) e a tecla de atalho num selo à direita.
     * Ao passar o mouse (ou com o foco do teclado), desliza um pouco para a direita.
     */
    static Button botaoMenu(String tecla, String icone, String rotulo, String detalhe, AcaoMenu.Estilo estilo) {
        Button b = new Button();
        b.getStyleClass().add("botao-menu");
        if (estilo == AcaoMenu.Estilo.PRIMARIO) {
            b.getStyleClass().add("botao-menu-primario");
        } else if (estilo == AcaoMenu.Estilo.PERIGO) {
            b.getStyleClass().add("botao-menu-perigo");
        }
        VBox textos = new VBox(1, rotulo(rotulo, "menu-rotulo"));
        if (detalhe != null && !detalhe.isBlank()) {
            Label d = rotulo(detalhe, "menu-detalhe");
            d.setWrapText(false);
            textos.getChildren().add(d);
        }
        textos.setAlignment(Pos.CENTER_LEFT);
        HBox conteudo = new HBox(14);
        conteudo.setAlignment(Pos.CENTER_LEFT);
        if (icone != null) {
            conteudo.getChildren().add(Imagens.vista(icone, 28, 28));
        }
        conteudo.getChildren().add(textos);
        conteudo.getChildren().add(espaco());
        if (tecla != null) {
            conteudo.getChildren().add(rotulo(tecla, "menu-tecla"));
        }
        b.setGraphic(conteudo);
        b.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        b.setMaxWidth(Double.MAX_VALUE);
        conteudo.prefWidthProperty().bind(b.widthProperty().subtract(34));
        TranslateTransition ir = new TranslateTransition(Duration.millis(120), b);
        Runnable entrar = () -> { ir.stop(); ir.setToX(6); ir.play(); };
        Runnable sair = () -> { ir.stop(); ir.setToX(0); ir.play(); };
        b.setOnMouseEntered(e -> entrar.run());
        b.setOnMouseExited(e -> { if (!b.isFocused()) sair.run(); });
        b.focusedProperty().addListener((o, antes, agora) -> { if (agora) entrar.run(); else if (!b.isHover()) sair.run(); });
        return b;
    }

    /** Poeira dourada flutuando devagar pela tela (fundo do menu). */
    static Pane poeira(int quantidade, long semente) {
        Pane camada = new Pane();
        camada.setMouseTransparent(true);
        camada.setMinSize(1280, 720);
        camada.setMaxSize(1280, 720);
        Random r = new Random(semente);
        for (int i = 0; i < quantidade; i++) {
            double lado = 2 + r.nextInt(4);
            Rectangle grao = new Rectangle(lado, lado, Color.web(r.nextBoolean() ? "#F6CF86" : "#F1E6D2"));
            grao.setLayoutX(r.nextDouble() * 1280);
            grao.setLayoutY(120 + r.nextDouble() * 560);
            grao.setOpacity(0);
            Duration d = Duration.seconds(10 + r.nextDouble() * 16);
            TranslateTransition voo = new TranslateTransition(d, grao);
            voo.setByX(-(120 + r.nextDouble() * 260));
            voo.setByY(-(20 + r.nextDouble() * 90));
            voo.setInterpolator(Interpolator.LINEAR);
            double alvo = 0.25 + r.nextDouble() * 0.5;
            FadeTransition surge = new FadeTransition(d.divide(3), grao);
            surge.setFromValue(0);
            surge.setToValue(alvo);
            FadeTransition paira = new FadeTransition(d.divide(3), grao);
            paira.setFromValue(alvo);
            paira.setToValue(alvo);
            FadeTransition some = new FadeTransition(d.divide(3), grao);
            some.setFromValue(alvo);
            some.setToValue(0);
            ParallelTransition ciclo = new ParallelTransition(voo, new SequentialTransition(surge, paira, some));
            ciclo.setCycleCount(Animation.INDEFINITE);
            ciclo.setDelay(Duration.seconds(r.nextDouble() * 8));
            ciclo.play();
            camada.getChildren().add(grao);
        }
        return camada;
    }

    /** Faz um nó surgir subindo um pouco (entrada de cartões). */
    static void surgir(Node n, double atrasoMs) {
        n.setOpacity(0);
        n.setTranslateY(18);
        FadeTransition f = new FadeTransition(Duration.millis(380), n);
        f.setToValue(1);
        TranslateTransition t = new TranslateTransition(Duration.millis(380), n);
        t.setToY(0);
        t.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition p = new ParallelTransition(f, t);
        p.setDelay(Duration.millis(atrasoMs));
        p.play();
    }

    /** Põe o conteúdo de uma tela num cartão centralizado sobre o fundo desfocado. */
    static StackPane emCartao(javafx.scene.layout.Region conteudo, double maxLargura, double maxAltura) {
        conteudo.getStyleClass().add("cartao");
        conteudo.setPadding(new javafx.geometry.Insets(26, 34, 24, 34));
        conteudo.setMaxWidth(maxLargura);
        conteudo.setMaxHeight(maxAltura);
        surgir(conteudo, 0);
        return new StackPane(fundoDesfocado(), conteudo);
    }
}
