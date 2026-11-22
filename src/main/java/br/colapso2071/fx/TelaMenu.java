package br.colapso2071.fx;

import br.colapso2071.fx.AcaoMenu.Estilo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Menu principal (redesenho da versão 4.3): o panorama do povoado ao entardecer com poeira dourada flutuando,
 * um véu escuro à esquerda para o logo e, à direita, um cartão com as nove opções (ícone, rótulo, uma linha
 * explicando o que cada uma faz e a tecla de atalho).
 */
public final class TelaMenu {
    /** Ícone de 16×16 de cada opção, ampliado para 28×28. */
    private static final Map<String, String> ICONES = Map.of(
            "1", "ico_novo.png",
            "2", "ico_continuar.png",
            "3", "ico_carregar.png",
            "4", "ico_excluir.png",
            "5", "ico_config.png",
            "6", "ico_trofeu.png",
            "7", "ico_instrucoes.png",
            "8", "ico_creditos.png",
            "9", "ico_sair.png");

    /** Uma linha curta sob cada opção, dizendo o que ela faz. */
    private static final Map<String, String> DETALHES = Map.of(
            "1", "Chegar a Bom-Jardim pela primeira vez",
            "3", "Três espaços e o salvamento automático",
            "4", "Apagar um save",
            "5", "Música, volume, tema e salvamento",
            "6", "Os quatro finais e as conquistas",
            "7", "Como jogar, água, comida e atalhos",
            "8", "A história e quem fez o jogo",
            "9", "Fechar o jogo");

    private final List<AcaoMenu> acoes;
    private final String subtituloContinuar;
    private final String aviso;

    public TelaMenu(List<AcaoMenu> acoes, String subtituloContinuar, String aviso) {
        this.acoes = List.copyOf(acoes);
        this.subtituloContinuar = subtituloContinuar;
        this.aviso = aviso;
    }

    public Parent construir() {
        Pane veu = new Pane();
        veu.getStyleClass().add("menu-veu");

        BorderPane conteudo = new BorderPane();
        conteudo.setLeft(marca());
        VBox cartao = opcoes();
        BorderPane.setAlignment(cartao, Pos.CENTER_RIGHT);
        BorderPane.setMargin(cartao, new Insets(0, 56, 0, 0));
        conteudo.setRight(cartao);
        conteudo.setBottom(rodape());

        // Cenário 320×180 ampliado 4× = 1280×720, sem suavizar.
        StackPane raiz = new StackPane(Imagens.vista("cena_menu.png", 1280, 720), veu,
                Componentes.poeira(42, 2071), conteudo);
        Componentes.surgir(cartao, 120);

        // Atalhos de teclado 1..9, iguais ao menu de console.
        raiz.setOnKeyPressed(ev -> {
            for (AcaoMenu a : acoes) {
                if (a.atalho().equals(ev.getText())) {
                    a.executar();
                    ev.consume();
                    return;
                }
            }
        });
        return raiz;
    }

    private VBox marca() {
        // Logo de 170×112 ampliado 3×.
        VBox caixa = new VBox(14,
                Imagens.vista("logo.png", 510, 336),
                Componentes.rotulo("Bom-Jardim, sertão, 2071. A água é a moeda mais cara.", "menu-slogan"),
                Componentes.separador(),
                Componentes.rotulo("Uma história em dez capítulos · quatro finais · suas escolhas ficam", "menu-detalhe"));
        caixa.setAlignment(Pos.CENTER_LEFT);
        caixa.setPadding(new Insets(40, 0, 0, 64));
        caixa.setMaxWidth(580);
        caixa.setPrefWidth(620);
        return caixa;
    }

    private VBox opcoes() {
        VBox lista = new VBox(6);
        lista.getStyleClass().add("cartao");
        lista.setPadding(new Insets(24, 22, 24, 22));
        lista.setPrefWidth(440);
        lista.setMaxWidth(440);
        lista.setMaxHeight(VBox.USE_PREF_SIZE);
        lista.getChildren().addAll(Componentes.rotulo("MENU", "cartao-sobretitulo"), Componentes.separador());

        boolean primeiro = true;
        for (AcaoMenu a : acoes) {
            String detalhe = "2".equals(a.atalho())
                    ? (subtituloContinuar != null ? subtituloContinuar : "Nenhuma partida salva ainda")
                    : DETALHES.get(a.atalho());
            Button b = Componentes.botaoMenu(a.atalho(), ICONES.get(a.atalho()), a.rotulo(), detalhe, a.estilo());
            b.setOnAction(e -> a.executar());
            lista.getChildren().add(b);
            if (primeiro) {
                b.setDefaultButton(true);
                primeiro = false;
            }
        }
        if (aviso != null && !aviso.isBlank()) {
            lista.getChildren().add(Componentes.rotulo(aviso, "menu-aviso"));
        }
        return lista;
    }

    private HBox rodape() {
        HBox r = new HBox(18,
                Componentes.rotulo("F11  tela cheia", "menu-dica"),
                Componentes.rotulo("1–9  atalhos", "menu-dica"),
                Componentes.rotulo("Enter  primeira opção", "menu-dica"),
                Componentes.espaco(),
                Componentes.rotulo("Versão " + Instrucoes.VERSAO, "menu-dica"));
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(0, 64, 22, 64));
        return r;
    }

    static Estilo estiloDe(String atalho) {
        return switch (atalho) {
            case "1" -> Estilo.PRIMARIO;
            case "9" -> Estilo.PERIGO;
            default -> Estilo.NORMAL;
        };
    }
}
