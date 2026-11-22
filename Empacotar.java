import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Gera o Colapso 2071 pronto para baixar, com o Java embutido (quem baixa não precisa ter Java).
 *
 * <p>Como usar (JDK 21, na pasta do projeto):
 * <pre>
 *   mvn -DskipTests package
 *   java Empacotar.java
 * </pre>
 * No IntelliJ: rode o Maven "package" (painel Maven › Lifecycle) e depois clique em ▶ ao lado do {@code main}
 * deste arquivo. Os arquivos saem na pasta {@code dist/}:
 * <ul>
 *   <li>Linux: pasta portátil (.tar.gz), instalador .deb (se houver dpkg-deb e fakeroot) e .rpm (se houver rpmbuild)</li>
 *   <li>Windows: pasta portátil (.zip) e instalador .exe (se o WiX Toolset estiver instalado)</li>
 *   <li>macOS: instalador .dmg</li>
 * </ul>
 * Cada sistema gera o próprio instalador; o GitHub Actions ({@code .github/workflows/instaladores.yml}) roda este
 * arquivo nos três ao mesmo tempo. O empacotamento é feito pelo jpackage, que vem com o JDK.
 */
public class Empacotar {
    static final String NOME = "Colapso2071";
    static final Path JAR = Path.of("target", "colapso2071.jar");
    static final Path PACOTE = Path.of("target", "pacote");
    static final Path DIST = Path.of("dist");

    public static void main(String[] args) throws Exception {
        String versao = versaoDoPom();
        System.out.println("Colapso 2071 versão " + versao);
        if (!Files.exists(JAR)) {
            System.err.println("Não achei " + JAR + ". Gere antes com: mvn -DskipTests package");
            System.exit(1);
        }
        apagar(PACOTE);
        apagar(DIST);
        Files.createDirectories(PACOTE);
        Files.createDirectories(DIST);
        Files.copy(JAR, PACOTE.resolve(JAR.getFileName()));

        String sistema = System.getProperty("os.name").toLowerCase();
        if (sistema.contains("win")) {
            windows(versao);
        } else if (sistema.contains("mac")) {
            jpackage(versao, "--type", "dmg", "--icon", "arte/icone/icone.icns", "--mac-package-name", "Colapso 2071");
        } else {
            linux(versao);
        }

        System.out.println("Pronto. Arquivos em dist/:");
        try (Stream<Path> arquivos = Files.list(DIST)) {
            arquivos.sorted().forEach(a -> System.out.printf("  %s (%d MB)%n", a.getFileName(), tamanho(a) / 1_000_000));
        }
    }

    private static void linux(String versao) throws IOException {
        // pasta portátil: descompacte e rode Colapso2071/bin/Colapso2071
        jpackage(versao, "--type", "app-image", "--icon", "arte/icone/icone.png");
        Path tar = DIST.resolve(NOME + "-" + versao + "-linux-portatil.tar.gz");
        Tar.gerar(DIST.resolve(NOME), tar);
        apagar(DIST.resolve(NOME));

        String[] menu = {"--icon", "arte/icone/icone.png", "--linux-shortcut", "--linux-menu-group", "Game",
                "--linux-package-name", "colapso2071"};
        if (existeNoPath("dpkg-deb") && existeNoPath("fakeroot")) {
            jpackage(versao, juntar(new String[]{"--type", "deb", "--linux-app-category", "games"}, menu));
        } else {
            System.out.println("Sem dpkg-deb ou fakeroot: pulei o .deb (no Ubuntu/Zorin: instale o pacote fakeroot)");
        }
        if (existeNoPath("rpmbuild")) {
            jpackage(versao, juntar(new String[]{"--type", "rpm"}, menu));
        }
    }

    private static void windows(String versao) throws IOException {
        // versão portátil (não precisa instalar)
        jpackage(versao, "--type", "app-image", "--icon", "arte/icone/icone.ico");
        Path zip = DIST.resolve(NOME + "-" + versao + "-windows-portatil.zip");
        zipar(DIST.resolve(NOME), zip);
        apagar(DIST.resolve(NOME));

        // instalador .exe: o jpackage usa o WiX Toolset (https://wixtoolset.org)
        if (existeNoPath("candle.exe") || existeNoPath("wix.exe")) {
            jpackage(versao, "--type", "exe", "--icon", "arte/icone/icone.ico", "--win-menu", "--win-menu-group",
                    "Colapso 2071", "--win-shortcut", "--win-dir-chooser", "--win-per-user-install");
        } else {
            System.out.println("WiX Toolset não encontrado: gerei só a versão portátil (.zip).");
        }
    }

    /** Chama o jpackage do próprio JDK com as opções comuns mais as do sistema. */
    private static void jpackage(String versao, String... extras) {
        ToolProvider jpackage = ToolProvider.findFirst("jpackage").orElseThrow(
                () -> new IllegalStateException("jpackage não encontrado: use um JDK 21 completo (não só o JRE)"));
        List<String> opcoes = new ArrayList<>(List.of(
                "--name", NOME,
                "--app-version", versao,
                "--vendor", "Davi",
                "--description", "Colapso 2071: RPG de escolhas no sertão",
                "--input", PACOTE.toString(),
                "--main-jar", JAR.getFileName().toString(),
                "--main-class", "br.colapso2071.fx.Lancador",
                "--java-options", "--enable-native-access=ALL-UNNAMED",
                "--java-options", "-Djdk.gtk.version=3",
                "--jlink-options", "--strip-debug --no-man-pages --no-header-files",
                "--dest", DIST.toString()));
        opcoes.addAll(List.of(extras));
        System.out.println("jpackage " + String.join(" ", extras));
        int codigo = jpackage.run(System.out, System.err, opcoes.toArray(String[]::new));
        if (codigo != 0) {
            throw new IllegalStateException("o jpackage terminou com erro (código " + codigo + ")");
        }
    }

    static String versaoDoPom() throws IOException {
        String pom = Files.readString(Path.of("pom.xml"), StandardCharsets.UTF_8);
        // a primeira <version> depois do <artifactId> do projeto (antes das dependências)
        Matcher m = Pattern.compile("<artifactId>colapso2071</artifactId>\\s*<version>([0-9][0-9.]*)</version>")
                .matcher(pom);
        if (!m.find()) {
            throw new IllegalStateException("versão não encontrada no pom.xml");
        }
        return m.group(1);
    }

    static boolean existeNoPath(String programa) {
        String path = System.getenv("PATH");
        if (path == null) {
            return false;
        }
        for (String pasta : path.split(java.io.File.pathSeparator)) {
            if (!pasta.isBlank() && Files.isExecutable(Path.of(pasta, programa))) {
                return true;
            }
        }
        return false;
    }

    static void zipar(Path pasta, Path destino) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(destino));
             Stream<Path> arquivos = Files.walk(pasta)) {
            for (Path arquivo : (Iterable<Path>) arquivos.sorted()::iterator) {
                String nome = pasta.getParent().relativize(arquivo).toString().replace('\\', '/');
                if (Files.isDirectory(arquivo)) {
                    zip.putNextEntry(new ZipEntry(nome + "/"));
                } else {
                    zip.putNextEntry(new ZipEntry(nome));
                    Files.copy(arquivo, zip);
                }
                zip.closeEntry();
            }
        }
    }

    static void apagar(Path caminho) throws IOException {
        if (!Files.exists(caminho)) {
            return;
        }
        try (Stream<Path> tudo = Files.walk(caminho)) {
            for (Path p : (Iterable<Path>) tudo.sorted(Comparator.reverseOrder())::iterator) {
                Files.delete(p);
            }
        }
    }

    private static long tamanho(Path p) {
        try {
            return Files.size(p);
        } catch (IOException e) {
            return 0;
        }
    }

    private static String[] juntar(String[] a, String[] b) {
        String[] r = new String[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    /**
     * Gera .tar.gz (formato ustar) só com Java, guardando a permissão de execução dos arquivos, para o programa
     * abrir direto depois de descompactado no Linux.
     */
    static final class Tar {
        private Tar() {
        }

        static void gerar(Path pasta, Path destino) throws IOException {
            try (OutputStream saida = new GZIPOutputStream(Files.newOutputStream(destino));
                 Stream<Path> arquivos = Files.walk(pasta)) {
                for (Path arquivo : (Iterable<Path>) arquivos.sorted()::iterator) {
                    String nome = pasta.getParent().relativize(arquivo).toString().replace('\\', '/');
                    boolean diretorio = Files.isDirectory(arquivo);
                    long tamanho = diretorio ? 0 : Files.size(arquivo);
                    saida.write(cabecalho(diretorio ? nome + "/" : nome, tamanho, modo(arquivo, diretorio), diretorio));
                    if (!diretorio) {
                        Files.copy(arquivo, saida);
                        int sobra = (int) (tamanho % 512);
                        if (sobra != 0) {
                            saida.write(new byte[512 - sobra]);
                        }
                    }
                }
                saida.write(new byte[1024]); // dois blocos vazios marcam o fim
            }
        }

        private static int modo(Path arquivo, boolean diretorio) {
            if (diretorio) {
                return 0755;
            }
            try {
                Set<PosixFilePermission> permissoes = Files.getPosixFilePermissions(arquivo);
                return permissoes.contains(PosixFilePermission.OWNER_EXECUTE) ? 0755 : 0644;
            } catch (UnsupportedOperationException | IOException e) {
                return Files.isExecutable(arquivo) ? 0755 : 0644;
            }
        }

        static byte[] cabecalho(String nome, long tamanho, int modo, boolean diretorio) {
            byte[] h = new byte[512];
            byte[] bytes = nome.getBytes(StandardCharsets.UTF_8);
            String prefixo = "";
            if (bytes.length > 100) {
                // ustar: nomes longos são divididos em prefixo (até 155 bytes) + nome (até 100), numa das barras
                int corte = -1;
                for (int k = nome.indexOf('/'); k > 0; k = nome.indexOf('/', k + 1)) {
                    if (nome.substring(k + 1).getBytes(StandardCharsets.UTF_8).length <= 100) {
                        corte = k;
                        break;
                    }
                }
                if (corte <= 0 || nome.substring(0, corte).getBytes(StandardCharsets.UTF_8).length > 155) {
                    throw new IllegalArgumentException("caminho longo demais para o tar: " + nome);
                }
                prefixo = nome.substring(0, corte);
                bytes = nome.substring(corte + 1).getBytes(StandardCharsets.UTF_8);
            }
            System.arraycopy(bytes, 0, h, 0, bytes.length);
            octal(h, 100, 8, modo);
            octal(h, 108, 8, 0);
            octal(h, 116, 8, 0);
            octal(h, 124, 12, tamanho);
            octal(h, 136, 12, System.currentTimeMillis() / 1000);
            for (int i = 148; i < 156; i++) {
                h[i] = ' ';
            }
            h[156] = (byte) (diretorio ? '5' : '0');
            System.arraycopy("ustar\u000000".getBytes(StandardCharsets.US_ASCII), 0, h, 257, 8);
            byte[] p = prefixo.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(p, 0, h, 345, p.length);
            long soma = 0;
            for (byte b : h) {
                soma += b & 0xff;
            }
            octal(h, 148, 7, soma);
            return h;
        }

        private static void octal(byte[] h, int inicio, int largura, long valor) {
            String s = Long.toOctalString(valor);
            while (s.length() < largura - 1) {
                s = "0" + s;
            }
            byte[] b = s.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(b, 0, h, inicio, largura - 1);
            h[inicio + largura - 1] = 0;
        }
    }
}
