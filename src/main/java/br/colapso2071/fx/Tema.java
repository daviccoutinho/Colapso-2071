package br.colapso2071.fx;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;

/**
 * Tema base dos controles. Com o tema moderno ligado, usa o AtlantaFX (PrimerDark): botões, caixas de seleção,
 * barras de rolagem, dicas e diálogos ficam com visual atual e escuro. A identidade do jogo ({@link Estilo})
 * continua por cima, em cada cena. Desligado, volta ao Modena, o tema padrão do JavaFX.
 */
public final class Tema {
    /*
     * O AtlantaFX 2.0.1 tem regras de botão cujas cores não convertem no JavaFX 21 ("ClassCastException ...
     * while converting value for '-fx-background-color' from rule '*.button'"). Antes isso ficava escondido porque
     * a folha de estilos do jogo cobria essas regras; agora quem pinta os botões é o Estilo (em Java), então o JavaFX
     * passa a calcular a regra do tema, avisa e a descarta. O botão continua com o visual do jogo; só silenciamos o
     * aviso. A referência fica guardada num campo para o Java não descartar a configuração do logger.
     */
    private static final java.util.logging.Logger LOG_CSS = java.util.logging.Logger.getLogger("javafx.css");

    static {
        LOG_CSS.setLevel(java.util.logging.Level.SEVERE);
    }

    private Tema() {
    }

    public static void aplicar(boolean moderno) {
        try {
            Application.setUserAgentStylesheet(moderno
                    ? new PrimerDark().getUserAgentStylesheet()
                    : Application.STYLESHEET_MODENA);
        } catch (RuntimeException | LinkageError e) {
            // Biblioteca ausente ou incompatível: o jogo segue com o tema padrão
            Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
            System.err.println("Tema AtlantaFX indisponível: " + e.getMessage());
        }
    }
}
