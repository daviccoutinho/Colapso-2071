package br.colapso2071.fx;

import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Identidade visual do jogo (cores, fontes, bordas, sombras e estados de passar o mouse, foco e clique), escrita
 * só em Java com a API do JavaFX.
 *
 * <p>As telas continuam marcando os nós com nomes de estilo ({@code getStyleClass().add("cartao")}); esta classe
 * acompanha a árvore da cena e, para cada nó, junta as {@link Regra regras} que valem para ele e aplica o resultado
 * com {@code setBackground}, {@code setBorder}, {@code setFont}, {@code setTextFill} etc. Quando o nó muda de
 * estado (mouse por cima, foco, clicado, desabilitado, selecionado), muda de nome de estilo ou entra na tela, o
 * visual é recalculado. Regras mais específicas vencem as mais gerais; entre iguais, vence a declarada depois.
 */
final class Estilo {
    private Estilo() {
    }

    // ---------------- Paleta ----------------
    static final Color CARVAO = Color.rgb(0x17, 0x11, 0x0D);
    static final Color BARRO = Color.rgb(0x2A, 0x20, 0x19);
    static final Color LINHA = Color.rgb(0x4A, 0x3A, 0x2D);
    static final Color POEIRA = Color.rgb(0xBF, 0xAE, 0x92);
    static final Color AREIA = Color.rgb(0xF1, 0xE6, 0xD2);
    static final Color LATAO = Color.rgb(0xD9, 0xA4, 0x41);
    static final Color AGUA = Color.rgb(0x4F, 0xA3, 0xB5);
    static final Color ARGILA = Color.rgb(0xD9, 0x76, 0x4C);
    static final Color TRANSPARENTE = Color.TRANSPARENT;

    // ---------------- Fontes (usa a primeira instalada) ----------------
    static final String[] SANS = {"IBM Plex Sans", "Segoe UI", "DejaVu Sans", "System"};
    static final String[] SERIFA = {"Alfa Slab One", "Georgia", "DejaVu Serif", "Serif"};
    static final String[] MONO = {"IBM Plex Mono", "Consolas", "DejaVu Sans Mono", "Monospaced"};
    static final String[] PIXEL = {"Silkscreen", "IBM Plex Mono", "DejaVu Sans Mono", "Monospaced"};
    static final double TAMANHO_PADRAO = 14;

    // ---------------- Estados ----------------
    static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    static final PseudoClass FOCO = PseudoClass.getPseudoClass("focused");
    static final PseudoClass CLICADO = PseudoClass.getPseudoClass("pressed");
    static final PseudoClass DESABILITADO = PseudoClass.getPseudoClass("disabled");
    static final PseudoClass SELECIONADO = PseudoClass.getPseudoClass("selected");
    static final PseudoClass PADRAO = PseudoClass.getPseudoClass("default");

    private static final String CHAVE_OBSERVADO = "colapso.estilo.observado";
    private static final String CHAVE_VISUAL = "colapso.estilo.visual";
    private static final String CHAVE_REGRAS = "colapso.estilo.regras";

    // =====================================================================================================
    // Seletores e regras
    // =====================================================================================================

    /** Quais nós uma regra atinge: nomes de estilo e estados do próprio nó e, opcionalmente, de um ancestral. */
    static final class Seletor {
        private final Set<String> classes;
        private final Set<PseudoClass> estados;
        private final Seletor ancestral;
        private final boolean paiDireto;

        private Seletor(Set<String> classes, Set<PseudoClass> estados, Seletor ancestral, boolean paiDireto) {
            this.classes = classes;
            this.estados = estados;
            this.ancestral = ancestral;
            this.paiDireto = paiDireto;
        }

        /** O mesmo seletor, mas só quando o nó está nesses estados. */
        Seletor em(PseudoClass... novos) {
            Set<PseudoClass> todos = new HashSet<>(estados);
            todos.addAll(Arrays.asList(novos));
            return new Seletor(classes, todos, ancestral, paiDireto);
        }

        /** Nós que casam com {@code alvo} e estão em qualquer nível dentro de um nó que casa com este. */
        Seletor dentro(Seletor alvo) {
            return new Seletor(alvo.classes, alvo.estados, this, false);
        }

        /** Nós que casam com {@code alvo} e são filhos diretos de um nó que casa com este. */
        Seletor filho(Seletor alvo) {
            return new Seletor(alvo.classes, alvo.estados, this, true);
        }

        int especificidade() {
            return classes.size() + estados.size() + (ancestral == null ? 0 : ancestral.especificidade());
        }

        boolean casa(Node no) {
            if (!no.getStyleClass().containsAll(classes) || !no.getPseudoClassStates().containsAll(estados)) {
                return false;
            }
            if (ancestral == null) {
                return true;
            }
            Parent pai = no.getParent();
            if (paiDireto) {
                return pai != null && ancestral.casa(pai);
            }
            for (; pai != null; pai = pai.getParent()) {
                if (ancestral.casa(pai)) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Seletor dos nós que têm todos esses nomes de estilo. */
    static Seletor sel(String... classes) {
        return new Seletor(new HashSet<>(Arrays.asList(classes)), Set.of(), null, false);
    }

    record Regra(Seletor seletor, Consumer<Visual> efeito, int ordem) {
    }

    private static final List<Regra> REGRAS = new ArrayList<>();

    private static void regra(Seletor seletor, Consumer<Visual> efeito) {
        REGRAS.add(new Regra(seletor, efeito, REGRAS.size()));
    }

    /** Mesma aparência para vários seletores. */
    private static void regra(Seletor[] seletores, Consumer<Visual> efeito) {
        for (Seletor s : seletores) {
            regra(s, efeito);
        }
    }

    private static Seletor[] todos(Seletor... s) {
        return s;
    }

    // =====================================================================================================
    // Aparência calculada de um nó
    // =====================================================================================================

    /** O que as regras definem para um nó; campo nulo = a regra não mexe nisso. */
    static final class Visual {
        Paint fundo;
        double[] raioFundo;
        Paint[] corBorda;
        double[] larguraBorda;
        double[] raioBorda;
        Boolean tracejada;
        double[] espaco;
        Double alturaMinima;
        Cursor cursor;
        String[] familia;
        Double tamanho;
        FontWeight peso;
        FontPosture postura;
        Paint corTexto;
        Pos alinhamento;
        TextAlignment alinhamentoTexto;
        Double entreLinhas;
        Effect efeito;
        Double opacidade;

        Visual fundo(Paint p) {
            fundo = p;
            return this;
        }

        Visual raioFundo(double... r) {
            raioFundo = r;
            return this;
        }

        /** Uma cor para os quatro lados, ou quatro cores (cima, direita, baixo, esquerda). */
        Visual borda(Paint... cores) {
            corBorda = cores.length == 1 ? new Paint[]{cores[0], cores[0], cores[0], cores[0]} : cores;
            return this;
        }

        Visual larguraBorda(double... l) {
            larguraBorda = quatro(l);
            return this;
        }

        Visual raioBorda(double... r) {
            raioBorda = r;
            return this;
        }

        /** Mesmo raio no fundo e na borda. */
        Visual raio(double... r) {
            return raioFundo(r).raioBorda(r);
        }

        Visual tracejada() {
            tracejada = true;
            return this;
        }

        /** Espaço interno: 1 valor (todos), 2 (vertical, horizontal) ou 4 (cima, direita, baixo, esquerda). */
        Visual espaco(double... e) {
            espaco = quatro(e);
            return this;
        }

        Visual alturaMinima(double a) {
            alturaMinima = a;
            return this;
        }

        Visual mao() {
            cursor = Cursor.HAND;
            return this;
        }

        Visual familia(String[] f) {
            familia = f;
            return this;
        }

        Visual tamanho(double t) {
            tamanho = t;
            return this;
        }

        Visual seminegrito() {
            peso = FontWeight.SEMI_BOLD;
            return this;
        }

        Visual negrito() {
            peso = FontWeight.BOLD;
            return this;
        }

        Visual italico() {
            postura = FontPosture.ITALIC;
            return this;
        }

        Visual cor(Paint p) {
            corTexto = p;
            return this;
        }

        Visual alinhar(Pos p) {
            alinhamento = p;
            return this;
        }

        Visual alinharTexto(TextAlignment a) {
            alinhamentoTexto = a;
            return this;
        }

        Visual entreLinhas(double e) {
            entreLinhas = e;
            return this;
        }

        Visual efeito(Effect e) {
            efeito = e;
            return this;
        }

        Visual opacidade(double o) {
            opacidade = o;
            return this;
        }

        private static double[] quatro(double[] v) {
            return switch (v.length) {
                case 1 -> new double[]{v[0], v[0], v[0], v[0]};
                case 2 -> new double[]{v[0], v[1], v[0], v[1]};
                case 3 -> new double[]{v[0], v[1], v[2], v[1]};
                default -> v;
            };
        }
    }

    // =====================================================================================================
    // As regras do jogo (na mesma ordem e com o mesmo efeito da antiga folha de estilos)
    // =====================================================================================================

    static {
        // ---- base ----
        regra(sel("root"), v -> v.fundo(CARVAO).familia(SANS).cor(AREIA));
        regra(sel("label"), v -> v.cor(AREIA));
        regra(sel("titulo"), v -> v.familia(SERIFA).tamanho(40));
        regra(sel("titulo-grande"), v -> v.familia(SERIFA).tamanho(64).cor(LATAO));
        regra(sel("subtitulo"), v -> v.cor(POEIRA).tamanho(16));
        regra(sel("rotulo"), v -> v.familia(MONO).tamanho(13).cor(POEIRA));
        regra(sel("mono"), v -> v.familia(MONO));
        regra(sel("painel"), v -> v.fundo(BARRO).raioFundo(8).espaco(20));
        regra(sel("painel-menu"), v -> v.fundo(rgba(23, 17, 13, 0.62)).espaco(32, 64));

        // ---- botões ----
        regra(todos(sel("botao"), sel("botao-primario"), sel("botao-perigo")), v -> v.raio(6).alturaMinima(48).mao()
                .tamanho(16).seminegrito().espaco(0, 28).alinhar(Pos.CENTER_LEFT));
        regra(sel("botao"), v -> v.fundo(TRANSPARENTE).borda(LATAO).larguraBorda(1.5).cor(LATAO));
        regra(sel("botao").em(HOVER), v -> v.fundo(LINHA));
        regra(sel("botao-primario"), v -> v.fundo(LATAO).cor(CARVAO));
        regra(sel("botao-primario").em(HOVER), v -> v.fundo(clarear(LATAO, 0.15)));
        regra(sel("botao-perigo"), v -> v.fundo(TRANSPARENTE).borda(ARGILA).larguraBorda(1.5).cor(ARGILA));
        regra(sel("botao-perigo").em(HOVER), v -> v.fundo(LINHA));
        regra(todos(sel("botao").em(FOCO), sel("botao-primario").em(FOCO), sel("botao-perigo").em(FOCO)),
                v -> v.borda(AGUA).larguraBorda(2.5));
        regra(todos(sel("botao").em(DESABILITADO), sel("botao-primario").em(DESABILITADO)), v -> v.opacidade(0.4));
        regra(sel("atalho"), v -> v.familia(MONO).cor(POEIRA));

        // ---- slots ----
        regra(sel("slot"), v -> v.fundo(BARRO).borda(LATAO).larguraBorda(1.5).raio(8).espaco(16, 20).mao());
        regra(sel("slot-vazio"), v -> v.fundo(TRANSPARENTE).borda(LINHA).tracejada());
        regra(sel("slot-ilegivel"), v -> v.borda(ARGILA));
        regra(sel("slot-auto"), v -> v.borda(AGUA));

        // ---- campos ----
        regra(sel("text-field"), v -> v.fundo(CARVAO).borda(LATAO).larguraBorda(1.5).raio(6).alturaMinima(44)
                .tamanho(16));
        regra(sel("check-box").dentro(sel("box")), v -> v.fundo(LINHA).raioFundo(4));
        regra(sel("check-box").em(SELECIONADO).dentro(sel("box")), v -> v.fundo(LATAO));
        regra(sel("check-box").em(SELECIONADO).dentro(sel("mark")), v -> v.fundo(CARVAO));
        regra(todos(sel("scroll-pane"), sel("scroll-pane").filho(sel("viewport"))), v -> v.fundo(TRANSPARENTE));
        regra(sel("titulo-slot"), v -> v.tamanho(18).seminegrito());
        regra(sel("titulo-linha"), v -> v.tamanho(18).seminegrito());
        regra(sel("texto"), v -> v.tamanho(17).entreLinhas(4));
        regra(sel("progress-bar").filho(sel("track")), v -> v.fundo(LINHA).raioFundo(3));
        regra(sel("progress-bar").filho(sel("bar")), v -> v.fundo(LATAO).raioFundo(3).espaco(3));

        // ---- tela de cena ----
        regra(sel("cena-hud"), v -> v.fundo(rgba(23, 17, 13, 0.78)).espaco(0, 24)
                .borda(TRANSPARENTE, TRANSPARENTE, LATAO, TRANSPARENTE).larguraBorda(0, 0, 2, 0));
        regra(sel("cena-capitulo"), v -> v.familia(PIXEL).tamanho(14).cor(LATAO));
        regra(sel("cena-valor"), v -> v.familia(MONO).tamanho(18).cor(AREIA));
        regra(sel("cena-botao-hud"), v -> v.tamanho(12).espaco(6, 12).alturaMinima(34));
        regra(sel("cena-caixa"), v -> v.fundo(hex(0xECD9A8)).borda(hex(0x8A5A2B)).larguraBorda(5).raio(6)
                .espaco(16, 32).efeito(new InnerShadow(BlurType.GAUSSIAN, LATAO, 0, 1, 0, 0)));
        regra(sel("cena-aba"), v -> v.fundo(hex(0xECD9A8)).borda(hex(0x8A5A2B)).larguraBorda(5, 5, 0, 5)
                .raio(8, 8, 0, 0).espaco(6, 34, 8, 34));
        regra(sel("cena-nome"), v -> v.familia(PIXEL).tamanho(20).negrito().cor(hex(0x4A2E16)));
        regra(sel("cena-texto"), v -> v.tamanho(22).cor(hex(0x3A2616)).entreLinhas(5));
        regra(sel("cena-titulo"), v -> v.familia(SERIFA).tamanho(32).cor(hex(0x7A4A1E)));
        regra(sel("cena-dica"), v -> v.cor(hex(0x7A4A1E)).tamanho(20));
        regra(sel("cena-opcoes"), v -> v.fundo(rgba(23, 17, 13, 0.86)).borda(LATAO).larguraBorda(2).espaco(12));
        regra(sel("cena-opcao"), v -> v.alinhar(Pos.CENTER_LEFT).alinharTexto(TextAlignment.LEFT).tamanho(15)
                .espaco(8, 12));

        // ---- cartas de perfil na criação do protagonista ----
        regra(sel("carta-perfil"), v -> v.fundo(BARRO).borda(LINHA).larguraBorda(2).espaco(16).mao());
        regra(sel("carta-perfil").em(HOVER), v -> v.borda(POEIRA));
        regra(sel("carta-perfil").em(SELECIONADO), v -> v.borda(LATAO).fundo(LINHA));

        // ---- avisos e mochila ----
        regra(sel("cena-avisos"), v -> v.fundo(rgba(30, 20, 12, 0.88)).raio(6).borda(LATAO).larguraBorda(2)
                .espaco(10, 14));
        regra(sel("cena-aviso"), v -> v.cor(hex(0xFBE9B7)).tamanho(16).negrito());
        regra(sel("cena-aviso-mochila"), v -> v.cor(hex(0xFBE9B7)).tamanho(16).fundo(rgba(217, 164, 65, 0.22))
                .espaco(8, 12).raioFundo(6));
        regra(sel("mochila-matriz"), v -> v.espaco(8).fundo(rgba(30, 20, 12, 0.55)).raioFundo(6));
        regra(sel("mochila-casa"), v -> v.fundo(hex(0x5A4330)).borda(hex(0x2B1A12)).larguraBorda(2).raio(4).mao());
        regra(sel("mochila-casa").em(HOVER), v -> v.fundo(hex(0x6E533B)));
        regra(sel("mochila-casa-selecionada"), v -> v.fundo(hex(0x7A5C3E)).borda(hex(0xF2C86A)).larguraBorda(3)
                .raio(4).mao());
        regra(sel("mochila-qtd"), v -> v.cor(hex(0xFBE9B7)).tamanho(14).negrito()
                .efeito(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 2, 0.9, 0, 0)));
        regra(sel("mochila-detalhe"), v -> v.fundo(rgba(236, 217, 168, 0.10)).espaco(16).raioFundo(6));
        regra(sel("cena-alerta"), v -> v.fundo(rgba(140, 32, 24, 0.92)).cor(hex(0xFFF2D8)).tamanho(18).negrito()
                .espaco(10, 18).raio(6).borda(hex(0xFFB36B)).larguraBorda(2));
        regra(sel("aviso-item"), v -> v.fundo(rgba(28, 52, 40, 0.94)).raio(8).borda(hex(0x7FD08A)).larguraBorda(2)
                .espaco(10, 14));
        regra(sel("aviso-conquista"), v -> v.fundo(rgba(60, 42, 8, 0.95)).raio(8).borda(hex(0xF2C86A))
                .larguraBorda(3).espaco(12, 16)
                .efeito(new DropShadow(BlurType.GAUSSIAN, hex(0xF2C86A), 14, 0.3, 0, 0)));
        regra(sel("aviso-necessidade"), v -> v.fundo(rgba(120, 28, 20, 0.95)).raio(8).borda(hex(0xFFB36B))
                .larguraBorda(2).espaco(10, 14));
        regra(sel("aviso-titulo"), v -> v.cor(Color.WHITE).tamanho(15).negrito());
        regra(sel("foto-moldura"), v -> v.fundo(hex(0xF1E6CE)).espaco(18, 18, 46, 18)
                .efeito(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 24, 0.4, 0, 6)));
        regra(sel("cena-objetivo"), v -> v.tamanho(12).cor(hex(0xE8D9B0)).opacidade(0.85));

        // ---- v4.3: menu, pausa, mochila, ficha, casa e diálogos ----
        regra(sel("moldura-janela"), v -> v.fundo(Color.BLACK));
        regra(sel("cartao"), v -> v.fundo(vertical(rgba(46, 33, 24, 0.95), rgba(23, 17, 13, 0.95))).raio(16)
                .borda(rgba(217, 164, 65, 0.55)).larguraBorda(1.5)
                .efeito(new DropShadow(BlurType.GAUSSIAN, rgba(0, 0, 0, 0.65), 34, 0.18, 0, 12)));
        regra(sel("cartao-titulo"), v -> v.familia(SERIFA).tamanho(34).cor(LATAO));
        regra(sel("cartao-sobretitulo"), v -> v.familia(PIXEL).tamanho(13).cor(LATAO).opacidade(0.85));
        regra(sel("cartao-sub"), v -> v.cor(POEIRA).tamanho(15));
        regra(sel("cartao-objetivo"), v -> v.cor(hex(0xF2DDA4)).tamanho(14).fundo(rgba(217, 164, 65, 0.12))
                .raioFundo(8).espaco(6, 10));
        regra(sel("separador"), v -> v.fundo(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, rgba(217, 164, 65, 0)), new Stop(0.3, rgba(217, 164, 65, 0.75)),
                new Stop(0.7, rgba(217, 164, 65, 0.75)), new Stop(1, rgba(217, 164, 65, 0)))));
        regra(sel("veu-escuro"), v -> v.fundo(radial(0.5, 0.5, 0.75, rgba(10, 7, 5, 0.15), rgba(10, 7, 5, 0.7))));

        // botões de menu: ícone, rótulo, detalhe e tecla de atalho
        regra(sel("botao-menu"), v -> v.fundo(rgba(241, 230, 210, 0.045)).raioFundo(10).borda(TRANSPARENTE)
                .larguraBorda(0, 0, 0, 3).raioBorda(10, 0, 0, 10).espaco(9, 14).mao().alinhar(Pos.CENTER_LEFT)
                .alturaMinima(50));
        regra(todos(sel("botao-menu").em(HOVER), sel("botao-menu").em(FOCO)),
                v -> v.fundo(rgba(217, 164, 65, 0.17)).borda(LATAO));
        regra(sel("botao-menu").em(CLICADO), v -> v.fundo(rgba(217, 164, 65, 0.28)));
        regra(sel("botao-menu").em(DESABILITADO), v -> v.opacidade(0.45));
        regra(sel("botao-menu-primario"), v -> v.fundo(horizontal(hex(0xD9A441), hex(0xD98A4C))));
        regra(todos(sel("botao-menu-primario").em(HOVER), sel("botao-menu-primario").em(FOCO)),
                v -> v.fundo(horizontal(hex(0xE8B652), hex(0xE59A5A))).borda(hex(0xFBE9B8)));
        regra(sel("botao-menu-primario").dentro(sel("menu-rotulo")), v -> v.cor(hex(0x1C130D)));
        regra(sel("botao-menu-primario").dentro(sel("menu-detalhe")), v -> v.cor(hex(0x3A2616)));
        regra(sel("botao-menu-primario").dentro(sel("menu-tecla")),
                v -> v.fundo(rgba(28, 19, 13, 0.18)).cor(hex(0x1C130D)));
        regra(sel("botao-menu-perigo").dentro(sel("menu-rotulo")), v -> v.cor(hex(0xF0A07E)));
        regra(todos(sel("botao-menu-perigo").em(HOVER), sel("botao-menu-perigo").em(FOCO)),
                v -> v.fundo(rgba(217, 118, 76, 0.18)).borda(ARGILA));
        regra(sel("menu-rotulo"), v -> v.tamanho(17).seminegrito().cor(AREIA));
        regra(sel("menu-detalhe"), v -> v.tamanho(12.5).cor(POEIRA));
        regra(sel("menu-tecla"), v -> v.familia(MONO).tamanho(13).negrito().cor(LATAO)
                .fundo(rgba(217, 164, 65, 0.14)).raioFundo(6).espaco(2, 8));
        regra(sel("menu-veu"), v -> v.fundo(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, rgba(12, 8, 6, 0.82)), new Stop(0.45, rgba(12, 8, 6, 0.45)),
                new Stop(0.7, rgba(12, 8, 6, 0.10)), new Stop(1, rgba(12, 8, 6, 0.35)))));
        regra(sel("menu-slogan"), v -> v.tamanho(19).cor(hex(0xF2DDA4))
                .efeito(new DropShadow(BlurType.GAUSSIAN, rgba(0, 0, 0, 0.8), 6, 0.4, 0, 1)));
        regra(sel("menu-dica"), v -> v.familia(MONO).tamanho(12).cor(rgba(241, 230, 210, 0.6)));
        regra(sel("menu-aviso"), v -> v.cor(hex(0xF0A07E)).tamanho(13));

        // casa
        regra(sel("chip"), v -> v.fundo(rgba(79, 163, 181, 0.14)).raioFundo(20).espaco(5, 14, 5, 8));
        regra(sel("chip-vazio"), v -> v.fundo(rgba(200, 60, 40, 0.35)).raio(20).espaco(5, 14, 5, 8)
                .borda(hex(0xFFB36B)).larguraBorda(1));
        regra(sel("chip-texto"), v -> v.familia(MONO).tamanho(16).cor(AREIA));
        regra(sel("casa-estado"), v -> v.cor(hex(0xBFD8A0)).tamanho(14));
        regra(sel("casa-estado-urgente"), v -> v.cor(hex(0xFFD9B0)).tamanho(14).fundo(rgba(140, 32, 24, 0.55))
                .raioFundo(8).espaco(6, 10));
        regra(sel("casa-legenda"), v -> v.cor(AREIA).tamanho(14).italico().fundo(rgba(12, 8, 6, 0.62))
                .raioFundo(10).espaco(10, 14));

        // mochila (redesenho)
        regra(sel("mochila-recursos"), v -> v.espaco(0, 0, 2, 0));
        regra(sel("mochila-matriz"), v -> v.espaco(10).fundo(rgba(12, 8, 6, 0.55)).raio(12)
                .borda(rgba(217, 164, 65, 0.25)).larguraBorda(1));
        regra(sel("mochila-casa"), v -> v.fundo(vertical(hex(0x4A3727), hex(0x3A2B1F))).raio(8)
                .borda(rgba(0, 0, 0, 0.55), rgba(255, 220, 160, 0.08), rgba(255, 220, 160, 0.12), rgba(0, 0, 0, 0.55))
                .larguraBorda(2).mao());
        regra(sel("mochila-casa").em(HOVER), v -> v.fundo(vertical(hex(0x5C4632), hex(0x47352A))));
        regra(sel("mochila-casa-selecionada"), v -> v.fundo(vertical(hex(0x6E5338), hex(0x57412D))).raio(8)
                .borda(hex(0xF2C86A)).larguraBorda(2.5).mao()
                .efeito(new DropShadow(BlurType.GAUSSIAN, rgba(242, 200, 106, 0.55), 14, 0.2, 0, 0)));
        regra(sel("mochila-detalhe"), v -> v.fundo(rgba(236, 217, 168, 0.07)).espaco(18).raio(12)
                .borda(rgba(217, 164, 65, 0.22)).larguraBorda(1));
        regra(sel("mochila-vitrine"), v -> v.fundo(radial(0.5, 0.5, 0.6, rgba(242, 200, 106, 0.22),
                rgba(0, 0, 0, 0))).espaco(10).raioFundo(10));

        // diálogos de confirmação (Alert) no estilo do jogo
        regra(sel("dialogo"), v -> v.fundo(hex(0x231912)));
        regra(sel("dialogo").dentro(sel("content", "label")), v -> v.cor(AREIA).tamanho(15).espaco(18, 20, 8, 20));
        regra(sel("dialogo").dentro(sel("header-panel")), v -> v.fundo(hex(0x2E2118)));
        regra(sel("dialogo").dentro(sel("header-panel")).dentro(sel("label")),
                v -> v.cor(LATAO).tamanho(17).negrito());
        regra(sel("dialogo").dentro(sel("button-bar")).dentro(sel("button")), v -> v.fundo(rgba(217, 164, 65, 0.16))
                .cor(AREIA).raio(8).borda(LATAO).larguraBorda(1).espaco(6, 18));
        regra(sel("dialogo").dentro(sel("button-bar")).dentro(sel("button").em(PADRAO)),
                v -> v.fundo(LATAO).cor(hex(0x1C130D)));
        regra(sel("config-linha"), v -> v.fundo(rgba(241, 230, 210, 0.05)).raioFundo(10).espaco(12, 16));
        regra(sel("config-linha").dentro(sel("titulo-linha")), v -> v.tamanho(16));
        regra(sel("config-linha").dentro(sel("subtitulo")), v -> v.tamanho(13));
        regra(sel("botao-menu-compacto"), v -> v.alturaMinima(42).espaco(6, 14));

        // v4.5: retrato de quem fala num quadro de madeira ao lado da caixa de texto
        regra(sel("retrato-cartao"), v -> v.fundo(vertical(hex(0xA4683A), hex(0x7A4A24))).raio(8)
                .borda(hex(0x4A2A14)).larguraBorda(4).espaco(8, 8, 6, 8)
                .efeito(new DropShadow(BlurType.GAUSSIAN, rgba(0, 0, 0, 0.5), 18, 0.2, 0, 6)));
        regra(sel("retrato-quadro"), v -> v.fundo(radial(0.5, 0.4, 0.7, hex(0xF6E6C2), hex(0xE2C690))).raio(4)
                .borda(hex(0x5A3418), hex(0xC8925A), hex(0xC8925A), hex(0x5A3418)).larguraBorda(4));
        regra(sel("retrato-nome"), v -> v.familia(PIXEL).tamanho(18).negrito().cor(hex(0x3A2010))
                .fundo(hex(0xECD9A8)).raio(4).espaco(3, 14).borda(hex(0x4A2A14)).larguraBorda(2));
        regra(sel("config-secao"), v -> v.familia(PIXEL).tamanho(13).cor(LATAO));
        regra(sel("fundo-imagem"), v -> v.fundo(hex(0x1B130D)));

        // mais específicas primeiro na hora de calcular (entre iguais, a declarada depois vence)
        REGRAS.sort(Comparator.comparingInt((Regra r) -> r.seletor().especificidade()).thenComparingInt(Regra::ordem));
    }

    // =====================================================================================================
    // Acompanhar a árvore e aplicar
    // =====================================================================================================

    /** Aplica o visual a tudo o que estiver (ou entrar) na cena. */
    static void observar(Scene cena) {
        cena.rootProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                observar(nova);
            }
        });
        if (cena.getRoot() != null) {
            observar(cena.getRoot());
        }
    }

    /** Aplica o visual ao nó e a tudo dentro dele, e continua acompanhando as mudanças. */
    static void observar(Node no) {
        if (!no.getProperties().containsKey(CHAVE_OBSERVADO)) {
            no.getProperties().put(CHAVE_OBSERVADO, Boolean.TRUE);
            no.getStyleClass().addListener((ListChangeListener<String>) m -> reaplicar(no));
            no.getPseudoClassStates().addListener((SetChangeListener<PseudoClass>) m -> {
                // só alguns estados mudam o visual dos filhos (ex.: caixa de seleção marcada); nos outros,
                // basta o próprio nó, o que evita recalcular a tela inteira a cada movimento do mouse
                if (estadoAfetaDescendentes(no)) {
                    reaplicar(no);
                } else {
                    aplicar(no);
                }
            });
            if (no instanceof Parent pai) {
                pai.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) m -> {
                    while (m.next()) {
                        for (Node novo : m.getAddedSubList()) {
                            observar(novo);
                        }
                    }
                });
            }
        }
        reaplicar(no);
    }

    /** Recalcula o nó e os descendentes (o visual deles pode depender do estado do nó). */
    private static void reaplicar(Node no) {
        aplicar(no);
        if (no instanceof Parent pai) {
            for (Node filho : List.copyOf(pai.getChildrenUnmodifiable())) {
                if (filho.getProperties().containsKey(CHAVE_OBSERVADO)) {
                    reaplicar(filho);
                } else {
                    observar(filho);
                }
            }
        }
    }

    /** Regras que valem para o nó, na ordem em que são aplicadas. */
    static List<Regra> regrasDe(Node no) {
        List<Regra> valem = new ArrayList<>();
        for (Regra r : REGRAS) {
            if (r.seletor().casa(no)) {
                valem.add(r);
            }
        }
        return valem;
    }

    static Visual calcular(List<Regra> regras) {
        Visual v = new Visual();
        for (Regra r : regras) {
            r.efeito().accept(v);
        }
        return v;
    }

    private static boolean estadoAfetaDescendentes(Node no) {
        for (Regra r : REGRAS) {
            for (Seletor a = r.seletor().ancestral; a != null; a = a.ancestral) {
                if (!a.estados.isEmpty() && no.getStyleClass().containsAll(a.classes)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void aplicar(Node no) {
        List<Regra> regras = regrasDe(no);
        Object anteriores = no.getProperties().get(CHAVE_REGRAS);
        Visual antes = (Visual) no.getProperties().get(CHAVE_VISUAL);
        if (regras.equals(anteriores) && antes != null) {
            // nada mudou para este nó; só a fonte pode ter mudado, se um ancestral mudou
            atualizarFonte(no, antes);
            return;
        }
        Visual v = calcular(regras);
        no.getProperties().put(CHAVE_REGRAS, regras);
        no.getProperties().put(CHAVE_VISUAL, v);

        if (no instanceof Region r) {
            if (v.fundo != null) {
                r.setBackground(new Background(new BackgroundFill(v.fundo, raios(v.raioFundo), Insets.EMPTY)));
            } else if (antes != null && antes.fundo != null) {
                r.setBackground(null);
            }
            if (v.corBorda != null) {
                double[] l = v.larguraBorda != null ? v.larguraBorda : new double[]{1, 1, 1, 1};
                BorderStrokeStyle tipo = Boolean.TRUE.equals(v.tracejada) ? BorderStrokeStyle.DASHED : BorderStrokeStyle.SOLID;
                r.setBorder(new Border(new BorderStroke(v.corBorda[0], v.corBorda[1], v.corBorda[2], v.corBorda[3],
                        tipo, tipo, tipo, tipo, raios(v.raioBorda), new BorderWidths(l[0], l[1], l[2], l[3]),
                        Insets.EMPTY)));
            } else if (antes != null && antes.corBorda != null) {
                r.setBorder(null);
            }
            if (v.espaco != null) {
                r.setPadding(new Insets(v.espaco[0], v.espaco[1], v.espaco[2], v.espaco[3]));
            }
            if (v.alturaMinima != null) {
                r.setMinHeight(v.alturaMinima);
            }
        }

        atualizarFonte(no, v);
        if (no instanceof Labeled rotulo) {
            if (v.corTexto != null) {
                rotulo.setTextFill(v.corTexto);
            }
            if (v.alinhamento != null) {
                rotulo.setAlignment(v.alinhamento);
            }
            if (v.alinhamentoTexto != null) {
                rotulo.setTextAlignment(v.alinhamentoTexto);
            }
            if (v.entreLinhas != null) {
                rotulo.setLineSpacing(v.entreLinhas);
            }
        }

        if (v.cursor != null) {
            no.setCursor(v.cursor);
        } else if (antes != null && antes.cursor != null) {
            no.setCursor(null);
        }
        if (v.efeito != null) {
            no.setEffect(v.efeito);
        } else if (antes != null && antes.efeito != null) {
            no.setEffect(null);
        }
        if (v.opacidade != null) {
            no.setOpacity(v.opacidade);
        } else if (antes != null && antes.opacidade != null) {
            no.setOpacity(1);
        }
    }

    private static void atualizarFonte(Node no, Visual v) {
        if (no instanceof Labeled rotulo) {
            Font f = fonte(no, v);
            if (!f.equals(rotulo.getFont())) {
                rotulo.setFont(f);
            }
        } else if (no instanceof TextInputControl campo) {
            Font f = fonte(no, v);
            if (!f.equals(campo.getFont())) {
                campo.setFont(f);
            }
        }
    }

    /** Fonte do nó: o que as regras dele dizem e, no que faltar, o que vale para o ancestral mais próximo. */
    private static Font fonte(Node no, Visual v) {
        String[] familia = v.familia;
        Double tamanho = v.tamanho;
        FontWeight peso = v.peso;
        FontPosture postura = v.postura;
        for (Parent p = no.getParent(); p != null; p = p.getParent()) {
            if (familia != null && tamanho != null && peso != null && postura != null) {
                break;
            }
            Object guardado = p.getProperties().get(CHAVE_VISUAL);
            if (guardado instanceof Visual a) {
                familia = familia != null ? familia : a.familia;
                tamanho = tamanho != null ? tamanho : a.tamanho;
                peso = peso != null ? peso : a.peso;
                postura = postura != null ? postura : a.postura;
            }
        }
        return Font.font(instalada(familia != null ? familia : SANS),
                peso != null ? peso : FontWeight.NORMAL,
                postura != null ? postura : FontPosture.REGULAR,
                tamanho != null ? tamanho : TAMANHO_PADRAO);
    }

    private static Set<String> familiasInstaladas;

    private static String instalada(String[] opcoes) {
        if (familiasInstaladas == null) {
            familiasInstaladas = new HashSet<>(Font.getFamilies());
        }
        for (String f : opcoes) {
            if (familiasInstaladas.contains(f)) {
                return f;
            }
        }
        return opcoes[opcoes.length - 1];
    }

    // =====================================================================================================
    // Cores, degradês e cantos
    // =====================================================================================================

    static Color hex(int rgb) {
        return Color.rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    static Color rgba(int r, int g, int b, double alfa) {
        return Color.rgb(r, g, b, alfa);
    }

    /** Mais clara, como o "derive" positivo do JavaFX: sobe o brilho e tira um pouco da saturação. */
    static Color clarear(Color c, double fracao) {
        double brilho = c.getBrightness() + (1 - c.getBrightness()) * fracao;
        double saturacao = c.getSaturation() - c.getSaturation() * fracao;
        return Color.hsb(c.getHue(), saturacao, brilho, c.getOpacity());
    }

    static LinearGradient vertical(Color cima, Color baixo) {
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, cima), new Stop(1, baixo));
    }

    static LinearGradient horizontal(Color esquerda, Color direita) {
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, esquerda), new Stop(1, direita));
    }

    static RadialGradient radial(double cx, double cy, double raio, Color centro, Color borda) {
        return new RadialGradient(0, 0, cx, cy, raio, true, CycleMethod.NO_CYCLE, new Stop(0, centro), new Stop(1, borda));
    }

    /** Cantos: 1 valor (todos) ou 4 (cima-esquerda, cima-direita, baixo-direita, baixo-esquerda). */
    private static CornerRadii raios(double[] r) {
        if (r == null) {
            return CornerRadii.EMPTY;
        }
        if (r.length == 1) {
            return new CornerRadii(r[0]);
        }
        return new CornerRadii(r[0], r[1], r[2], r[3], false);
    }
}
