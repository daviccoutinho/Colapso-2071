package br.colapso2071.fx;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Toca a música com o tocador de áudio do próprio sistema operacional, num processo separado:
 * {@code pw-play} (PipeWire), {@code paplay} (PulseAudio), {@code aplay} (ALSA) ou {@code afplay} (macOS).
 *
 * <p>É o caminho que funciona quando o Java não enxerga a placa de som (o Java Sound responde "No line matching
 * interface Clip") e o javafx-media não tem GStreamer ("Could not create player"), situação comum em Linux com
 * PipeWire ou com o JDK instalado sem as bibliotecas de som. O volume é aplicado gravando uma cópia do WAV já na
 * altura certa, porque cada tocador tem um jeito diferente de receber volume. Não depende de JavaFX.</p>
 */
public final class TocadorSistema {

    /** Tocadores conhecidos, na ordem de preferência. */
    public enum Programa {
        PW_PLAY("pw-play"), PAPLAY("paplay"), APLAY("aplay"), AFPLAY("afplay");

        private final String executavel;

        Programa(String executavel) {
            this.executavel = executavel;
        }

        String executavel() {
            return executavel;
        }

        public List<String> comando(Path arquivo) {
            List<String> c = new ArrayList<>();
            c.add(executavel);
            if (this == APLAY) {
                c.add("-q");
            }
            c.add(arquivo.toString());
            return c;
        }
    }

    private TocadorSistema() {
    }

    /**
     * Todos os jeitos de tocar encontrados, na ordem de preferência. Se o jogo roda dentro de um Flatpak (por exemplo,
     * aberto pelo terminal do VS Code instalado como Flatpak), o som de dentro da caixa costuma não funcionar; aí
     * entram também os tocadores do sistema "de fora", chamados por {@code flatpak-spawn --host}.
     */
    public static List<List<String>> candidatos() {
        List<List<String>> lista = new ArrayList<>();
        String so = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (so.contains("win")) {
            return lista;
        }
        for (Programa p : Programa.values()) {
            if (noPath(p.executavel())) {
                lista.add(List.of(p.executavel()));
            }
        }
        if (dentroDeFlatpak() && noPath("flatpak-spawn")) {
            for (Programa p : Programa.values()) {
                if (p != Programa.AFPLAY) {
                    lista.add(List.of("flatpak-spawn", "--host", p.executavel()));
                }
            }
        }
        return lista;
    }

    /** Id do aplicativo Flatpak em que o jogo está rodando (ex.: com.visualstudio.code), ou null. */
    public static String idDoFlatpak() {
        String id = System.getenv("FLATPAK_ID");
        if (id != null && !id.isBlank()) {
            return id;
        }
        try {
            for (String linha : Files.readAllLines(Path.of("/.flatpak-info"))) {
                if (linha.startsWith("name=")) {
                    return linha.substring(5).strip();
                }
            }
        } catch (IOException | RuntimeException e) {
            // fora de um Flatpak
        }
        return null;
    }

    /**
     * Explica como liberar o som quando o jogo roda dentro de um Flatpak sem acesso ao áudio (null fora dele).
     * O Flatpak do editor (VS Code, IntelliJ...) não deixa o Java chegar no servidor de som do sistema.
     */
    public static String dicaDoFlatpak() {
        String id = idDoFlatpak();
        if (id == null) {
            return null;
        }
        return "O jogo está rodando dentro do Flatpak " + id + ", que não tem acesso ao som. Para liberar, rode num "
                + "terminal do sistema: flatpak override --user --socket=pulseaudio " + id + " e reabra o programa. "
                + "Ou abra o jogo por um terminal fora dele (mvn javafx:run).";
    }

    static boolean dentroDeFlatpak() {
        return System.getenv("FLATPAK_ID") != null || Files.exists(Path.of("/.flatpak-info"));
    }

    private static boolean noPath(String executavel) {
        String path = System.getenv("PATH");
        if (path == null) {
            return false;
        }
        for (String dir : path.split(File.pathSeparator)) {
            if (!dir.isEmpty() && Files.isExecutable(Path.of(dir, executavel))) {
                return true;
            }
        }
        return false;
    }

    /** Monta a linha de comando: prefixo (ex.: pw-play, ou flatpak-spawn --host aplay) + opções + arquivo. */
    static List<String> comando(List<String> prefixo, Path arquivo) {
        List<String> c = new ArrayList<>(prefixo);
        if (prefixo.get(prefixo.size() - 1).equals("aplay")) {
            c.add("-q");
        }
        c.add(arquivo.toString());
        return c;
    }

    /** O primeiro tocador encontrado no PATH (Linux e macOS), se houver. */
    public static Optional<Programa> detectar() {
        String so = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (so.contains("win")) {
            return Optional.empty();
        }
        String path = System.getenv("PATH");
        if (path == null) {
            return Optional.empty();
        }
        for (Programa p : Programa.values()) {
            for (String dir : path.split(File.pathSeparator)) {
                if (!dir.isEmpty() && Files.isExecutable(Path.of(dir, p.executavel()))) {
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    /** Pasta temporária onde ficam as cópias dos WAV (uma por faixa e por volume). */
    private static Path pasta() throws IOException {
        String cache = System.getenv("XDG_CACHE_HOME");
        Path base = cache != null && !cache.isBlank() ? Path.of(cache) : Path.of(System.getProperty("user.home"), ".cache");
        Path d = base.resolve("colapso2071").resolve("som");
        Files.createDirectories(d);
        return d;
    }

    /**
     * Grava uma cópia do WAV com o volume aplicado (de 10 em 10%) e devolve o caminho. Reaproveita a cópia se ela já
     * existir.
     */
    public static Path copiaComVolume(URL url, String nome, double volume) throws IOException {
        int nivel = (int) Math.round(Math.max(0, Math.min(1, volume)) * 10);
        Path destino = pasta().resolve(nome + "_v" + nivel + ".wav");
        if (Files.exists(destino) && Files.size(destino) > 44) {
            return destino;
        }
        try (InputStream bruto = new BufferedInputStream(url.openStream());
             AudioInputStream in = AudioSystem.getAudioInputStream(bruto)) {
            AudioFormat f = in.getFormat();
            byte[] pcm = in.readAllBytes();
            escalar(pcm, f, nivel / 10.0);
            Path tmp = Files.createTempFile(pasta(), nome, ".tmp");
            try (AudioInputStream saida = new AudioInputStream(new ByteArrayInputStream(pcm), f, pcm.length / f.getFrameSize())) {
                AudioSystem.write(saida, AudioFileFormat.Type.WAVE, tmp.toFile());
            }
            Files.move(tmp, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return destino;
        } catch (javax.sound.sampled.UnsupportedAudioFileException e) {
            throw new IOException("WAV inválido: " + nome, e);
        }
    }

    /** Multiplica as amostras PCM de 16 bits pelo fator (0 a 1). */
    static void escalar(byte[] pcm, AudioFormat f, double fator) {
        ByteBuffer b = ByteBuffer.wrap(pcm).order(f.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i + 1 < pcm.length; i += 2) {
            b.putShort(i, (short) Math.round(b.getShort(i) * fator));
        }
    }

    /** Uma faixa tocando em loop pelo tocador do sistema. */
    static final class Faixa {
        private final List<String> prefixo;
        private final URL url;
        private final String nome;
        private volatile boolean parado;
        private volatile Process processo;
        private volatile Path arquivo;
        private double volume;

        Faixa(Programa programa, URL url, String nome, double volume) throws IOException {
            this(List.of(programa.executavel()), url, nome, volume);
        }

        Faixa(List<String> prefixo, URL url, String nome, double volume) throws IOException {
            this.prefixo = List.copyOf(prefixo);
            this.url = url;
            this.nome = nome;
            this.volume = volume;
            this.arquivo = copiaComVolume(url, nome, volume);
            // primeira execução: se o tocador falhar logo de cara, avisa quem chamou (com a mensagem de erro dele)
            Path log = Files.createTempFile(pasta(), "tocador", ".log");
            ProcessBuilder pb = new ProcessBuilder(comando(prefixo, arquivo));
            pb.redirectErrorStream(true);
            pb.redirectOutput(log.toFile());
            Process p = pb.start();
            processo = p;
            try {
                if (p.waitFor(700, java.util.concurrent.TimeUnit.MILLISECONDS) && p.exitValue() != 0) {
                    String saida = Files.readString(log).strip().replace('\n', ' ');
                    if (saida.length() > 160) {
                        saida = saida.substring(0, 160) + "...";
                    }
                    throw new IOException(descricao() + " terminou com código " + p.exitValue()
                            + (saida.isEmpty() ? "" : " (" + saida + ")"));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                try {
                    Files.deleteIfExists(log);
                } catch (IOException ignorado) {
                    // arquivo de log temporário: se não der para apagar, fica no cache
                }
            }
            Thread laco = new Thread(this::repetir, "musica-" + prefixo.get(prefixo.size() - 1));
            laco.setDaemon(true);
            laco.start();
        }

        private Process iniciar() throws IOException {
            ProcessBuilder pb = new ProcessBuilder(comando(prefixo, arquivo));
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            processo = pb.start();
            return processo;
        }

        /** Quando a faixa acaba, começa de novo (loop). */
        private void repetir() {
            while (!parado) {
                try {
                    Process p = processo;
                    long inicio = System.nanoTime();
                    p.waitFor();
                    if (parado) {
                        break;
                    }
                    if (p.exitValue() != 0 && System.nanoTime() - inicio < 1_000_000_000L && processo == p) {
                        break;                      // falhou de novo na hora: desiste para não ficar em loop
                    }
                    if (processo == p) {
                        iniciar();
                    } else {
                        Thread.sleep(50);           // trocou de volume: o novo processo já está tocando
                    }
                } catch (IOException e) {
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        /** Troca o volume: grava (ou reaproveita) a cópia no volume novo e reinicia a faixa. */
        synchronized void volume(double v) {
            if (parado || Math.abs(v - volume) < 0.05) {
                return;
            }
            volume = v;
            try {
                arquivo = copiaComVolume(url, nome, v);
                Process antigo = processo;
                iniciar();
                if (antigo != null) {
                    antigo.destroy();
                }
            } catch (IOException e) {
                System.err.println("Não deu para mudar o volume: " + e.getMessage());
            }
        }

        void parar() {
            parado = true;
            Process p = processo;
            if (p != null) {
                p.destroy();
            }
        }

        String descricao() {
            return String.join(" ", prefixo);
        }
    }
}
