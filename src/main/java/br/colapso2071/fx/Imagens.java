package br.colapso2071.fx;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Carrega a arte em pixel art de {@code resources/br/colapso2071/fx/img}.
 * Toda ampliação é feita sem suavização (vizinho mais próximo), para os pixels ficarem nítidos.
 */
final class Imagens {
    private static final Map<String, Image> CACHE = new HashMap<>();

    private Imagens() {
    }

    static Image carregar(String nome) {
        return CACHE.computeIfAbsent(nome, n -> {
            InputStream in = Imagens.class.getResourceAsStream("img/" + n);
            if (in == null) {
                throw new IllegalStateException("Imagem não encontrada: " + n);
            }
            return new Image(in);
        });
    }

    /** Recorte (x, y, largura, altura em pixels da imagem original) mostrado num quadrado de {@code lado} px. */
    static ImageView recorte(String nome, double x, double y, double w, double h, double lado) {
        ImageView v = new ImageView(carregar(nome));
        v.setViewport(new Rectangle2D(x, y, w, h));
        v.setSmooth(false);
        v.setPreserveRatio(false);
        v.setFitWidth(lado);
        v.setFitHeight(lado);
        return v;
    }

    /** Imagem ampliada para o tamanho dado, sem suavizar. Use múltiplos inteiros do tamanho original. */
    static ImageView vista(String nome, double largura, double altura) {
        ImageView v = new ImageView(carregar(nome));
        v.setSmooth(false);
        v.setPreserveRatio(false);
        v.setFitWidth(largura);
        v.setFitHeight(altura);
        return v;
    }
}
