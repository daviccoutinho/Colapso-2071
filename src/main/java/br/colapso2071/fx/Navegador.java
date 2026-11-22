package br.colapso2071.fx;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Troca a tela exibida na janela e aplica a folha de estilo da identidade visual em todas elas.
 *
 * <p>Todas as telas são desenhadas numa área fixa de 1280×720. Em janela maximizada ou em tela cheia, essa área é
 * ampliada por inteiro (mesma proporção, com faixas pretas se a tela não for 16:9). Assim nada sai do lugar, nada
 * fica cortado e a pixel art continua nítida. F11 ou Alt+Enter alternam a tela cheia, e a tela cheia continua ligada
 * quando o jogo troca de tela.</p>
 */
public final class Navegador {
    public static final double LARGURA = 1280;
    public static final double ALTURA = 720;

    private static Stage janela;
    /** Foto da última cena da partida, usada como fundo desfocado na pausa, na mochila e na ficha. */
    private static javafx.scene.image.Image ultimaCena;
    private StackPane atual;

    private final Stage palco;

    public Navegador(Stage palco) {
        this.palco = Objects.requireNonNull(palco);
        janela = palco;
        palco.setMinWidth(640);
        palco.setMinHeight(400);
        palco.setFullScreenExitHint("F11 ou Alt+Enter sai da tela cheia");
        // Esc é a pausa do jogo: não pode também derrubar a tela cheia (era o que acontecia antes)
        palco.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
    }

    /** Foto da última cena exibida (ou null se ainda não houve cena). */
    static javafx.scene.image.Image ultimaCena() {
        return ultimaCena;
    }

    /** Esquece a foto da cena (ao voltar para o menu principal). */
    static void esquecerCena() {
        ultimaCena = null;
    }

    /** A janela do jogo, para os diálogos abrirem por cima dela (inclusive em tela cheia). */
    static Stage janela() {
        return janela;
    }

    public void mostrar(Parent raiz) {
        // Se a tela que sai é uma cena da história, guarda uma foto dela para o fundo da pausa e da mochila
        if (atual != null && !atual.getChildren().isEmpty()
                && atual.getChildren().get(0).getStyleClass().contains("cena-raiz")) {
            try {
                ultimaCena = atual.snapshot(null, null);
            } catch (RuntimeException e) {
                ultimaCena = null;
            }
        }
        // Área fixa de 1280×720 com as telas do jogo
        StackPane fixo = new StackPane(raiz);
        fixo.setMinSize(LARGURA, ALTURA);
        fixo.setPrefSize(LARGURA, ALTURA);
        fixo.setMaxSize(LARGURA, ALTURA);
        fixo.setClip(new Rectangle(LARGURA, ALTURA));
        Group escalavel = new Group(fixo);
        atual = fixo;

        // Moldura preta que ocupa a janela inteira e centraliza a área do jogo
        StackPane moldura = new StackPane(escalavel);
        moldura.setMinSize(0, 0);
        moldura.getStyleClass().add("moldura-janela");

        boolean cheia = palco.isFullScreen();
        Scene anterior = palco.getScene();
        double w = anterior == null ? LARGURA : anterior.getWidth();
        double h = anterior == null ? ALTURA : anterior.getHeight();
        Scene cena = new Scene(moldura, w, h, Color.BLACK);
        Estilo.observar(cena);

        Runnable ajustar = () -> {
            double escala = Math.min(cena.getWidth() / LARGURA, cena.getHeight() / ALTURA);
            if (escala > 0) {
                escalavel.setScaleX(escala);
                escalavel.setScaleY(escala);
            }
        };
        cena.widthProperty().addListener((o, a, b) -> ajustar.run());
        cena.heightProperty().addListener((o, a, b) -> ajustar.run());
        ajustar.run();

        // Tela cheia: F11 ou Alt+Enter, em qualquer tela
        cena.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.F11 || ev.getCode() == KeyCode.ENTER && ev.isAltDown()) {
                palco.setFullScreen(!palco.isFullScreen());
                ev.consume();
            }
        });

        // Transição suave entre telas (a tela nova aparece em 0,25 s)
        raiz.setOpacity(0);
        palco.setScene(cena);
        if (cheia && !palco.isFullScreen()) {
            palco.setFullScreen(true);
        }
        FadeTransition entrar = new FadeTransition(Duration.millis(250), raiz);
        entrar.setToValue(1);
        entrar.play();
    }

    public void fechar() {
        palco.close();
    }

    // ---------- diálogos: sempre presos à janela, para não sumirem atrás dela em tela cheia ----------

    static boolean confirmar(String texto) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.setTitle("Colapso 2071");
        preparar(a);
        return a.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    static void avisar(String titulo, String texto) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, texto, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(titulo);
        preparar(a);
        a.showAndWait();
    }

    private static void preparar(Alert a) {
        if (janela != null && janela.getScene() != null) {
            a.initOwner(janela);
            a.initModality(Modality.WINDOW_MODAL);
        }
        a.getDialogPane().getStyleClass().add("dialogo");
        Estilo.observar(a.getDialogPane());
    }
}
