package br.colapso2071.fx;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Toca a trilha sonora em loop, com transição suave quando o clima muda.
 *
 * <p>Há três saídas de áudio ({@link Saida}). No automático, tenta nesta ordem: o Java Sound
 * ({@code javax.sound.sampled}, vem com o JDK), o tocador do sistema ({@code pw-play}, {@code paplay}, {@code aplay}
 * ou {@code afplay}, ver {@link TocadorSistema}) e o {@code MediaPlayer} do javafx-media. A primeira que tocar fica
 * valendo. O jogador pode escolher uma saída fixa em Configurações › Som e testar.</p>
 *
 * <p>Tudo que mexe com áudio roda numa thread própria ("musica"), para abrir arquivos e fazer fades sem travar a tela.</p>
 */
public final class Musica {
    private static final long FADE_MS = 1400;
    private static final long PASSO_MS = 40;

    /** Por onde o som sai. */
    public enum Saida {
        AUTOMATICO("Automático"),
        JAVA_SOUND("Java Sound (áudio do Java)"),
        SISTEMA("Tocador do sistema (pw-play, paplay, aplay)"),
        JAVAFX("JavaFX Media");

        private final String rotulo;

        Saida(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }

        @Override
        public String toString() {
            return rotulo;
        }

        public static Saida de(String nome) {
            try {
                return Saida.valueOf(nome);
            } catch (RuntimeException e) {
                return AUTOMATICO;
            }
        }
    }

    private static final ScheduledExecutorService RELOGIO = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "musica");
        t.setDaemon(true);
        return t;
    });

    /** Uma faixa tocando, seja qual for a saída. */
    private interface Faixa {
        void volume(double v);

        void parar();

        /** Se aguenta mudar de volume várias vezes por segundo (fade). */
        boolean fazFade();

        String saida();
    }

    // estado: só é lido e alterado na thread "musica"
    private static Faixa atual;
    private static Trilha.Clima climaAtual;
    private static boolean ligada = true;
    private static double volume = 0.6;
    private static Saida preferida = Saida.AUTOMATICO;
    private static ScheduledFuture<?> volumeAdiado;
    private static String ultimoErro = "";
    private static volatile String dicaAtual = null;
    private static volatile String emUso = "nenhuma (ainda não tocou)";

    private Musica() {
    }

    /** Toca o clima pedido. Se já está tocando, não reinicia a faixa. */
    public static void tocar(Trilha.Clima clima) {
        if (clima != null) {
            RELOGIO.execute(() -> trocar(clima, false));
        }
    }

    /** Liga ou desliga a música (preferência do jogador). */
    public static void setLigada(boolean valor) {
        RELOGIO.execute(() -> {
            ligada = valor;
            if (!valor) {
                pararAtual();
                emUso = "desligada";
            } else if (atual == null && climaAtual != null) {
                trocar(climaAtual, true);
            }
        });
    }

    /** Volume de 0 a 100. Para o tocador do sistema, o volume novo entra quando o jogador para de mexer. */
    public static void setVolume(int percentual) {
        RELOGIO.execute(() -> {
            volume = Math.max(0, Math.min(100, percentual)) / 100.0;
            if (atual == null) {
                return;
            }
            if (atual.fazFade()) {
                atual.volume(volume);
            } else {
                if (volumeAdiado != null) {
                    volumeAdiado.cancel(false);
                }
                Faixa f = atual;
                volumeAdiado = RELOGIO.schedule(() -> f.volume(volume), 350, TimeUnit.MILLISECONDS);
            }
        });
    }

    /** Escolhe a saída de áudio e reabre a faixa atual por ela. */
    public static void setSaida(Saida saida) {
        RELOGIO.execute(() -> {
            preferida = saida == null ? Saida.AUTOMATICO : saida;
            if (ligada && climaAtual != null) {
                trocar(climaAtual, true);
            }
        });
    }

    /**
     * Reabre a faixa atual (ou a neutra) e devolve, na thread "musica", o resultado: a saída que está tocando ou o
     * motivo de não tocar.
     */
    public static void testar(Consumer<String> resultado) {
        RELOGIO.execute(() -> {
            if (!ligada) {
                resultado.accept("A música está desligada. Ligue a trilha sonora para testar.");
                return;
            }
            Trilha.Clima c = climaAtual != null ? climaAtual : Trilha.Clima.NEUTRA;
            trocar(c, true);
            resultado.accept(atual != null ? "Tocando por: " + atual.saida()
                    : (dicaAtual != null ? "Sem som. " + dicaAtual : "Sem som. " + ultimoErro));
        });
    }

    /** Descrição da saída de áudio em uso, para mostrar em Configurações. */
    public static String saidaEmUso() {
        return emUso;
    }

    /** Para tudo ao fechar o jogo. */
    public static void parar() {
        RELOGIO.execute(Musica::pararAtual);
    }

    // ------------------------------------------------------------------ na thread "musica"

    private static void trocar(Trilha.Clima clima, boolean forcar) {
        if (!forcar && clima == climaAtual && atual != null) {
            return;
        }
        climaAtual = clima;
        if (!ligada) {
            return;
        }
        URL url = Musica.class.getResource(clima.getArquivo());
        if (url == null) {
            System.err.println("Música não encontrada: " + clima.getArquivo());
            return;
        }
        Faixa antiga = atual;
        if (antiga != null && !antiga.fazFade()) {
            antiga.parar();                       // o tocador do sistema não faz fade: troca direto
            antiga = null;
        }
        Faixa nova = abrir(url, clima.name().toLowerCase(java.util.Locale.ROOT));
        atual = nova;
        if (nova == null) {
            if (antiga != null) {
                antiga.parar();
            }
            emUso = dicaAtual != null ? "nenhuma. " + dicaAtual : "nenhuma (" + ultimoErro + ")";
            return;
        }
        emUso = nova.saida();
        if (!nova.fazFade() && antiga != null) {
            antiga.parar();
            return;
        }
        long passos = FADE_MS / PASSO_MS;
        Faixa velha = antiga;
        for (long i = 1; i <= passos; i++) {
            double k = (double) i / passos;
            RELOGIO.schedule(() -> {
                if (atual == nova && nova.fazFade()) {
                    nova.volume(volume * k);
                }
                if (velha != null) {
                    velha.volume(volume * (1 - k));
                }
            }, i * PASSO_MS, TimeUnit.MILLISECONDS);
        }
        if (velha != null) {
            RELOGIO.schedule(velha::parar, FADE_MS + PASSO_MS, TimeUnit.MILLISECONDS);
        }
    }

    private static void pararAtual() {
        if (atual != null) {
            atual.parar();
            atual = null;
        }
    }

    private static Faixa abrir(URL url, String nome) {
        List<String> erros = new ArrayList<>();
        List<Saida> ordem = preferida == Saida.AUTOMATICO
                ? List.of(Saida.JAVA_SOUND, Saida.SISTEMA, Saida.JAVAFX) : List.of(preferida);
        for (Saida s : ordem) {
            try {
                return switch (s) {
                    case JAVA_SOUND -> javaSound(url);
                    case SISTEMA -> sistema(url, nome);
                    case JAVAFX -> javaFx(url);
                    default -> throw new IllegalStateException();
                };
            } catch (Exception | LinkageError e) {
                erros.add(s.getRotulo() + ": " + e.getMessage());
            }
        }
        String msg = String.join(" | ", erros);
        String dica = TocadorSistema.dicaDoFlatpak();
        if (!msg.equals(ultimoErro)) {
            System.err.println("Trilha sonora sem saída de áudio. " + msg);
            if (dica != null) {
                System.err.println(dica);
            }
        }
        ultimoErro = msg;
        if (dica != null) {
            dicaAtual = dica;
        }
        return null;
    }

    // ------------------------------------------------------------------ saída 1: Java Sound

    private static Faixa javaSound(URL url) throws Exception {
        byte[] pcm;
        AudioFormat formato;
        try (InputStream bruto = new BufferedInputStream(url.openStream());
             AudioInputStream in = AudioSystem.getAudioInputStream(bruto)) {
            formato = in.getFormat();
            pcm = in.readAllBytes();
        }
        Clip clip;
        try {
            clip = AudioSystem.getClip();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("o Java não encontrou nenhuma placa de som");
        }
        try {
            clip.open(formato, pcm, 0, pcm.length);
        } catch (IllegalArgumentException | javax.sound.sampled.LineUnavailableException e) {
            // Placa que não aceita 22 kHz mono: converte para 44,1 kHz estéreo e tenta de novo
            clip.close();
            clip = AudioSystem.getClip();
            byte[] convertido = para44kEstereo(pcm, formato);
            clip.open(new AudioFormat(44100f, 16, 2, true, false), convertido, 0, convertido.length);
        }
        Clip c = clip;
        FloatControl ganho = c.isControlSupported(FloatControl.Type.MASTER_GAIN)
                ? (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN) : null;
        Faixa f = new Faixa() {
            @Override
            public void volume(double v) {
                if (ganho == null) {
                    return;
                }
                float db = v <= 0.001 ? ganho.getMinimum() : (float) (20 * Math.log10(v));
                ganho.setValue(Math.max(ganho.getMinimum(), Math.min(ganho.getMaximum(), db)));
            }

            @Override
            public void parar() {
                c.stop();
                c.close();
            }

            @Override
            public boolean fazFade() {
                return true;
            }

            @Override
            public String saida() {
                return Saida.JAVA_SOUND.getRotulo();
            }
        };
        f.volume(0);
        c.loop(Clip.LOOP_CONTINUOUSLY);
        return f;
    }

    /** PCM 16 bits, mono ou estéreo, de qualquer taxa: reamostra para 44 100 Hz estéreo little-endian. */
    static byte[] para44kEstereo(byte[] pcm, AudioFormat f) {
        int canais = f.getChannels();
        int quadros = pcm.length / (2 * canais);
        double razao = f.getSampleRate() / 44100.0;
        int saida = (int) (quadros / razao);
        byte[] out = new byte[saida * 4];
        java.nio.ByteBuffer src = java.nio.ByteBuffer.wrap(pcm).order(f.isBigEndian()
                ? java.nio.ByteOrder.BIG_ENDIAN : java.nio.ByteOrder.LITTLE_ENDIAN);
        java.nio.ByteBuffer dst = java.nio.ByteBuffer.wrap(out).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < saida; i++) {
            double pos = i * razao;
            int a = Math.min((int) pos, quadros - 1);
            int b = Math.min(a + 1, quadros - 1);
            double t = pos - a;
            for (int ch = 0; ch < 2; ch++) {
                int origem = Math.min(ch, canais - 1);
                short sa = src.getShort((a * canais + origem) * 2);
                short sb = src.getShort((b * canais + origem) * 2);
                dst.putShort((short) Math.round(sa + (sb - sa) * t));
            }
        }
        return out;
    }

    // ------------------------------------------------------------------ saída 2: tocador do sistema

    private static Faixa sistema(URL url, String nome) throws Exception {
        List<List<String>> candidatos = TocadorSistema.candidatos();
        if (candidatos.isEmpty()) {
            throw new IllegalStateException("nenhum tocador encontrado (pw-play, paplay, aplay, afplay)");
        }
        // tenta cada tocador encontrado: pw-play pode existir sem o PipeWire rodando, aí o paplay ou o aplay assume
        List<String> erros = new ArrayList<>();
        TocadorSistema.Faixa achada = null;
        for (List<String> c : candidatos) {
            try {
                achada = new TocadorSistema.Faixa(c, url, nome, volume);
                break;
            } catch (java.io.IOException e) {
                erros.add(e.getMessage());
            }
        }
        if (achada == null) {
            throw new IllegalStateException(String.join("; ", erros));
        }
        TocadorSistema.Faixa f = achada;
        return new Faixa() {
            @Override
            public void volume(double v) {
                f.volume(v);
            }

            @Override
            public void parar() {
                f.parar();
            }

            @Override
            public boolean fazFade() {
                return false;
            }

            @Override
            public String saida() {
                return "Tocador do sistema (" + f.descricao() + ")";
            }
        };
    }

    // ------------------------------------------------------------------ saída 3: JavaFX Media

    private static Faixa javaFx(URL url) {
        MediaPlayer p = new MediaPlayer(new Media(url.toExternalForm()));
        p.setCycleCount(MediaPlayer.INDEFINITE);
        p.setVolume(0);
        p.play();
        return new Faixa() {
            @Override
            public void volume(double v) {
                Platform.runLater(() -> p.setVolume(v));
            }

            @Override
            public void parar() {
                Platform.runLater(() -> {
                    p.stop();
                    p.dispose();
                });
            }

            @Override
            public boolean fazFade() {
                return true;
            }

            @Override
            public String saida() {
                return Saida.JAVAFX.getRotulo();
            }
        };
    }
}
