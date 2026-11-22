package br.colapso2071.fx;

import br.colapso2071.model.Cena;
import br.colapso2071.model.Final;

/**
 * Escolhe a música de fundo de cada momento do jogo. Não depende de JavaFX (testável): quem toca é {@link Musica}.
 * As faixas (som/*.wav) foram compostas para o jogo, com instrumentos do Nordeste.
 */
public final class Trilha {

    /** O clima da música. */
    public enum Clima {
        /** Dia a dia em Ré mixolídio: viola dedilhada, sanfona de fundo, pífano raro. Toca quase o jogo todo. */
        NEUTRA("som/neutra.wav"),
        /** Baião em Sol mixolídio: sanfona, zabumba e triângulo. Esperança, festa, o Bom-Jardim de antes. */
        ALEGRE("som/alegre.wav"),
        /** Toada lenta em Lá menor: viola dedilhada e pífano. Perda, saudade, a sede de todo dia. */
        TRISTE("som/triste.wav"),
        /** Ostinato em Ré frígio: cordas graves, zabumba pesada e rabeca. Ameaça, cerco, confronto. */
        TENSA("som/tensa.wav");

        private final String arquivo;

        Clima(String arquivo) {
            this.arquivo = arquivo;
        }

        /** Caminho do arquivo, relativo ao pacote br.colapso2071.fx nos recursos. */
        public String getArquivo() {
            return arquivo;
        }
    }

    /** Cenas de confronto e ameaça: a milícia chega, castiga, cerca e ataca. */
    private static final java.util.Set<String> TENSAS = java.util.Set.of(
            "CENA_4A", "CENA_5P", "CENA_7", "CENA_7B", "CENA_9A", "CENA_9", "CENA_9B");
    /** Cenas de luto: o enterro de Damião e o julgamento depois das perdas. */
    private static final java.util.Set<String> TRISTES = java.util.Set.of("CENA_8A", "CENA_10A");
    /** Cenas de respiro: a volta da Estação 7 com a assembleia e o broto do Umbuzeiro. */
    private static final java.util.Set<String> ALEGRES = java.util.Set.of("CENA_3B", "CENA_10");

    private Trilha() {
    }

    /** Música do menu principal e das telas fora da partida. */
    public static Clima doMenu() {
        return Clima.TRISTE;
    }

    /** Música de uma cena: a neutra na maior parte do jogo; as outras só quando o momento pede. */
    public static Clima daCena(Cena cena) {
        if (cena == null) {
            return doMenu();
        }
        String id = cena.getId();
        if (TENSAS.contains(id)) {
            return Clima.TENSA;
        }
        if (TRISTES.contains(id)) {
            return Clima.TRISTE;
        }
        if (ALEGRES.contains(id)) {
            return Clima.ALEGRE;
        }
        return Clima.NEUTRA;
    }

    /** Música do final alcançado. */
    public static Clima doFinal(Final alcancado) {
        if (alcancado == null) {
            return doMenu();
        }
        return switch (alcancado.getId()) {
            case "FINAL_BOM", "FINAL_JUSTICA" -> Clima.ALEGRE;
            case "FINAL_RUIM" -> Clima.TENSA;
            default -> Clima.TRISTE;
        };
    }
}
