package br.colapso2071.fx;

import br.colapso2071.model.Jogador;
import br.colapso2071.model.NPC;
import br.colapso2071.model.Rotas;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela de cena (protótipo "Cena"): cenário ao fundo, personagens em pé, barra de recursos no topo,
 * caixa de diálogo embaixo e, na última página, as escolhas. Também serve para mostrar a reação
 * a uma escolha (sem opções: ao fim das páginas chama {@code aoTerminar}).
 */
public final class TelaCena {
    /** Uma escolha na tela. {@code bloqueio} vazio = liberada; senão é o que falta ("requer: ..."). */
    public record Opcao(String texto, String bloqueio, Runnable acao, String detalhe) {
        public Opcao(String texto, String bloqueio, Runnable acao) {
            this(texto, bloqueio, acao, "");
        }

        public boolean liberada() { return bloqueio == null || bloqueio.isBlank(); }
    }

    /** Ações auxiliares do cabeçalho. {@code aoSalvar} é null quando a cena não é ponto de salvamento. */
    public record Atalhos(Runnable aoFicha, Runnable aoMochila, Runnable aoCasa, Runnable aoSalvar, Runnable aoCarregar,
                          Runnable aoAjuda, Runnable aoMenu) { }

    /**
     * Bonecos de corpo inteiro (16×32 px, e o Caramelo 24×17) ampliados 4×, como o cenário. Os pés (penúltima linha
     * do sprite) ficam na linha do chão do cenário ({@link Arte#PISO_Y}); a última linha é a sombra.
     */
    private static final double ESCALA = 4;
    private static final double ESPACO_ENTRE = 92;
    /** Retrato 64×64 ampliado 3×, dentro do quadro de madeira ao lado do texto (como nos RPGs de fazenda). */
    private static final double RETRATO_LADO = 192;
    /** A caixa de texto ocupa a largura que sobra ao lado do quadro do retrato. */
    private static final double LARGURA_CAIXA = 1200 - RETRATO_LADO - 44;
    /** Retrato de busto 80×104 ampliado 3×. */

    private final Rotas rotas;
    private final Jogador jogador;
    private final String fundo;
    private final String rotuloCapitulo;
    private String objetivo = "";

    /** Objetivo do capítulo, mostrado embaixo do nome do capítulo no topo da tela. */
    public TelaCena comObjetivo(String objetivo) {
        this.objetivo = objetivo == null ? "" : objetivo;
        return this;
    }
    private final List<Pagina> paginas;
    private final List<Opcao> opcoes;
    private final Runnable aoTerminar;
    private final Atalhos atalhos;

    private final Label nome = Componentes.rotulo("", "cena-nome");
    private final HBox aba = new HBox(nome);
    private final ImageView retratoNpc = new ImageView();
    /** Quadro do retrato de quem fala, ao lado da caixa de texto, com o nome embaixo. */
    private final VBox cartaoRetrato = new VBox(4);
    private final Label nomeCartao = Componentes.rotulo("", "retrato-nome");
    private final Label texto = Componentes.rotulo("", "cena-texto");
    private final Label dica = Componentes.rotulo("", "cena-dica");
    private final VBox painelOpcoes = new VBox(8);
    private final HBox caixa = new HBox(20);
    private final List<ImageView> spritesDosNpcs = new ArrayList<>();
    private final List<String> idsDosNpcs;
    private Timeline digitacao;
    /** Retrato de quem fala agora (base, boca aberta e olhos fechados). */
    private String retratoBase;
    private final java.util.Random acaso = new java.util.Random();
    private List<String> avisos = List.of();
    private String alerta = "";
    private ColorAdjust efeitoDoJogador = null;
    private int indice = -1;

    public TelaCena(Rotas rotas, Jogador jogador, String fundo, String rotuloCapitulo,
                    List<Pagina> paginas, List<Opcao> opcoes, Runnable aoTerminar, Atalhos atalhos) {
        this.rotas = rotas;
        this.jogador = jogador;
        this.fundo = fundo;
        this.rotuloCapitulo = rotuloCapitulo;
        this.paginas = List.copyOf(paginas);
        this.opcoes = List.copyOf(opcoes);
        this.aoTerminar = aoTerminar;
        this.atalhos = atalhos;
        this.idsDosNpcs = Roteiro.npcsEmCena(this.paginas);
    }

    /** Mostra avisos curtos (efeitos da escolha, salvamento) no canto da tela por alguns segundos. */
    /** Faixa fixa no alto da tela (ex.: "É necessário se hidratar"), que fica até a tela ser trocada. */
    public void alertar(String texto) {
        this.alerta = texto == null ? "" : texto;
    }

    public void avisar(List<String> textos) {
        this.avisos = textos == null ? List.of() : List.copyOf(textos);
    }

    public Parent construir() {
        Pane palco = new Pane();
        palco.getChildren().addAll(sprites());

        // Retrato de quem fala num quadro de madeira à direita da caixa de texto, com o nome embaixo
        retratoNpc.setSmooth(false);
        retratoNpc.setPreserveRatio(false);
        retratoNpc.setFitWidth(RETRATO_LADO);
        retratoNpc.setFitHeight(RETRATO_LADO);
        StackPane quadro = new StackPane(retratoNpc);
        quadro.getStyleClass().add("retrato-quadro");
        quadro.setMinSize(RETRATO_LADO + 20, RETRATO_LADO + 20);
        quadro.setMaxSize(RETRATO_LADO + 20, RETRATO_LADO + 20);
        nomeCartao.setWrapText(false);
        cartaoRetrato.getChildren().setAll(quadro, nomeCartao);
        cartaoRetrato.getStyleClass().add("retrato-cartao");
        cartaoRetrato.setAlignment(Pos.CENTER);
        cartaoRetrato.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        cartaoRetrato.setVisible(false);
        piscarDeVezEmQuando(retratoNpc, () -> retratoBase);
        StackPane.setAlignment(cartaoRetrato, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(cartaoRetrato, new Insets(0, 40, 28, 0));

        montarCaixa();
        aba.getStyleClass().add("cena-aba");
        aba.setVisible(false);
        aba.setManaged(false);
        aba.setMaxWidth(Region.USE_PREF_SIZE);
        VBox bloco = new VBox(-3, aba, caixa);
        VBox.setMargin(aba, new Insets(0, 0, 0, 36));
        bloco.setMaxWidth(LARGURA_CAIXA);
        bloco.setMaxHeight(Region.USE_PREF_SIZE);
        bloco.setPickOnBounds(false);
        StackPane.setAlignment(bloco, Pos.BOTTOM_LEFT);
        StackPane.setMargin(bloco, new Insets(0, 0, 28, 40));

        painelOpcoes.getStyleClass().add("cena-opcoes");
        painelOpcoes.setMaxWidth(720);
        painelOpcoes.setMaxHeight(VBox.USE_PREF_SIZE);
        painelOpcoes.setVisible(false);
        // No meio da tela, acima da caixa de texto.
        StackPane.setAlignment(painelOpcoes, Pos.BOTTOM_CENTER);
        StackPane.setMargin(painelOpcoes, new Insets(0, 0, 214, 0));

        Parent hud = barraSuperior();
        StackPane.setAlignment(hud, Pos.TOP_CENTER);

        StackPane raiz = new StackPane(Imagens.vista(fundo, 1280, 720), palco, bloco, cartaoRetrato, hud, painelOpcoes);
        raiz.getStyleClass().add("cena-raiz");
        if (!alerta.isBlank()) {
            Label faixa = Componentes.rotulo(alerta, "cena-alerta");
            faixa.setWrapText(true);
            faixa.setMaxWidth(900);
            faixa.setMouseTransparent(true);
            StackPane.setAlignment(faixa, Pos.TOP_CENTER);
            StackPane.setMargin(faixa, new Insets(64, 0, 0, 0));
            raiz.getChildren().add(faixa);
            // A faixa some depois de alguns segundos para não esconder as opções (as ações continuam na lista)
            javafx.animation.PauseTransition esperaFaixa = new javafx.animation.PauseTransition(Duration.seconds(6));
            esperaFaixa.setOnFinished(e -> {
                javafx.animation.FadeTransition sumir = new javafx.animation.FadeTransition(Duration.millis(700), faixa);
                sumir.setToValue(0);
                sumir.setOnFinished(x -> raiz.getChildren().remove(faixa));
                sumir.play();
            });
            esperaFaixa.play();
        }
        if (!avisos.isEmpty()) {
            // Cada aviso vira um cartão: item (com botão para abrir a mochila), conquista, necessidade ou efeito
            VBox toast = new VBox(8);
            toast.setMaxWidth(380);
            toast.setMaxHeight(VBox.USE_PREF_SIZE);
            toast.setPickOnBounds(false);
            for (String a : avisos) {
                toast.getChildren().add(cartaoDeAviso(a));
            }
            StackPane.setAlignment(toast, Pos.TOP_RIGHT);
            StackPane.setMargin(toast, new Insets(64, 24, 0, 0));
            raiz.getChildren().add(toast);
            javafx.animation.PauseTransition espera = new javafx.animation.PauseTransition(Duration.seconds(5));
            espera.setOnFinished(e -> {
                javafx.animation.FadeTransition sumir = new javafx.animation.FadeTransition(Duration.millis(600), toast);
                sumir.setToValue(0);
                sumir.setOnFinished(x -> raiz.getChildren().remove(toast));
                sumir.play();
            });
            espera.play();
        }

        // Clique no cenário ou na caixa avança; os botões não passam por aqui.
        palco.setOnMouseClicked(e -> avancar());
        cartaoRetrato.setOnMouseClicked(e -> avancar());
        caixa.setOnMouseClicked(e -> avancar());
        raiz.addEventFilter(KeyEvent.KEY_PRESSED, this::tecla);

        if (paginas.isEmpty()) {
            mostrarOpcoes();
        } else {
            mostrarPagina(0);
        }
        Platform.runLater(raiz::requestFocus);
        return raiz;
    }

    // ---------- personagens ----------

    /** Jogador à esquerda e depois os personagens da cena, lado a lado, todos na mesma linha do chão. */
    private List<ImageView> sprites() {
        List<String> ids = new ArrayList<>();
        ids.add("JOGADOR");
        for (String id : idsDosNpcs) {
            if (Arte.sprite(id) != null && ids.size() < 4) {
                ids.add(id);
            }
        }
        List<ImageView> todos = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            String arquivo = "JOGADOR".equals(id)
                    ? Arte.spriteDoPerfil(jogador.getPerfil().getId()) : Arte.sprite(id);
            javafx.scene.image.Image img = Imagens.carregar(arquivo);
            double w = img.getWidth() * ESCALA;
            double h = img.getHeight() * ESCALA;
            ImageView v = Imagens.vista(arquivo, w, h);
            double centro = 640 + (i - (ids.size() - 1) / 2.0) * ESPACO_ENTRE;
            v.setLayoutX(centro - w / 2);
            // Quem está um pouco "mais perto" desce alguns pixels, mas o tamanho não muda.
            // a penúltima linha do sprite (os pés) fica na linha do chão do cenário
            v.setLayoutY((Arte.PISO_Y - (img.getHeight() - 2)) * ESCALA + (i % 2 == 1 ? 4 : 0));
            v.setUserData(id);
            respirar(v, i * 450);
            if (!br.colapso2071.model.Rotas.NPC_CARAMELO.equals(id)) {
                String base = arquivo;
                piscarDeVezEmQuando(v, () -> base);
            }
            spritesDosNpcs.add(v);
            todos.add(v);
        }
        return todos;
    }

    /** Sobe e desce alguns pixels, devagar, em loop (respiração), com atraso para cada um ter seu ritmo. */
    private static void respirar(javafx.scene.Node n, double atrasoMs) {
        javafx.animation.TranslateTransition t = new javafx.animation.TranslateTransition(Duration.millis(2300), n);
        t.setFromY(0);
        t.setToY(-2);
        t.setAutoReverse(true);
        t.setCycleCount(javafx.animation.Animation.INDEFINITE);
        t.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
        t.setDelay(Duration.millis(atrasoMs));
        // quando a tela sai, a animação para (senão ficaria rodando para sempre em nós descartados)
        n.sceneProperty().addListener((o, antes, agora) -> {
            if (antes != null && agora == null) {
                t.stop();
            }
        });
        t.play();
    }

    /** Quando a fala é do Caramelo latindo ou rosnando, o boneco dele troca para a pose de latido (sem som). */
    private void reagirCaramelo(Pagina p) {
        boolean ehCaramelo = br.colapso2071.model.Rotas.NPC_CARAMELO.equals(p.npcId());
        String t = p.texto().toLowerCase(java.util.Locale.ROOT);
        boolean late = ehCaramelo && t.contains("au!");
        boolean rosna = ehCaramelo && (t.contains("grrr") || t.contains("rosna"));
        for (ImageView v : spritesDosNpcs) {
            if (br.colapso2071.model.Rotas.NPC_CARAMELO.equals(v.getUserData())) {
                v.setImage(Imagens.carregar(late || rosna ? Arte.CARAMELO_LATINDO
                        : Arte.sprite(br.colapso2071.model.Rotas.NPC_CARAMELO)));
            }
        }
    }

    /** Quem fala fica em cores normais; os outros escurecem um pouco. */
    private void destacar(String falanteId) {
        ColorAdjust escuro = new ColorAdjust();
        escuro.setBrightness(-0.35);
        for (ImageView v : spritesDosNpcs) {
            boolean ativo = falanteId == null || falanteId.equals(v.getUserData());
            v.setEffect(ativo ? null : escuro);
        }
    }

    /** Cartão de aviso no canto da tela, com estilo conforme o tipo. */
    private VBox cartaoDeAviso(String texto) {
        VBox cartao = new VBox(6);
        String estilo = "cena-avisos";
        String titulo = null;
        String corpo = texto;
        if (texto.startsWith("Item adicionado ao inventário: ")) {
            estilo = "aviso-item";
            titulo = "Item adicionado ao inventário";
            corpo = texto.substring("Item adicionado ao inventário: ".length());
        } else if (texto.startsWith("Conquista desbloqueada: ")) {
            estilo = "aviso-conquista";
            titulo = "Conquista desbloqueada!";
            corpo = texto.substring("Conquista desbloqueada: ".length());
        } else if (texto.startsWith("É necessário")) {
            estilo = "aviso-necessidade";
        }
        cartao.getStyleClass().add(estilo);
        if (titulo != null) {
            cartao.getChildren().add(Componentes.rotulo(titulo, "aviso-titulo"));
        }
        Label l = Componentes.rotulo(corpo, "cena-aviso");
        l.setWrapText(true);
        cartao.getChildren().add(l);
        if ("aviso-item".equals(estilo)) {
            Button abrir = Componentes.botao("Abrir inventário (I)", AcaoMenu.Estilo.NORMAL);
            abrir.getStyleClass().add("botao-hud");
            abrir.setFocusTraversable(false);
            abrir.setOnAction(e -> atalhos.aoMochila().run());
            cartao.getChildren().add(abrir);
        }
        return cartao;
    }

    /** Na primeira cena o protagonista ainda veste os trapos do Boqueirão: o retrato fica empoeirado. */
    public void comRoupaDaViagem(boolean empoeirado) {
        if (empoeirado) {
            ColorAdjust poeira = new ColorAdjust();
            poeira.setSaturation(-0.65);
            poeira.setBrightness(-0.15);
            poeira.setHue(0.05);
            efeitoDoJogador = poeira;
        }
    }

    // ---------- caixa de diálogo ----------

    private void montarCaixa() {
        caixa.getStyleClass().add("cena-caixa");
        caixa.setPrefWidth(LARGURA_CAIXA);
        caixa.setMaxWidth(LARGURA_CAIXA);
        caixa.setPrefHeight(170);
        caixa.setMinHeight(170);
        caixa.setMaxHeight(170);
        caixa.setAlignment(Pos.CENTER_LEFT);

        texto.setWrapText(true);
        texto.setMaxWidth(Double.MAX_VALUE);
        texto.setMaxHeight(Double.MAX_VALUE);
        VBox corpo = new VBox(texto);
        corpo.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(corpo, javafx.scene.layout.Priority.ALWAYS);

        dica.setText("▼");
        VBox direita = new VBox(dica);
        direita.setAlignment(Pos.BOTTOM_RIGHT);

        caixa.getChildren().addAll(corpo, direita);
    }

    private void mostrarPagina(int i) {
        indice = i;
        Pagina p = paginas.get(i);
        reagirCaramelo(p);
        boolean fala = p.tipo() == Pagina.Tipo.FALA;
        boolean titulo = p.tipo() == Pagina.Tipo.TITULO;

        boolean doJogador = fala && Pagina.JOGADOR.equals(p.npcId());
        String arquivo = null;
        String quem = "";
        if (doJogador) {
            // O protagonista fala: o retrato dele aparece no quadro
            arquivo = Arte.retratoDoPerfil(jogador.getPerfil().getId());
            quem = jogador.getNome();
            retratoNpc.setEffect(efeitoDoJogador);
            destacar(null);
        } else if (fala) {
            arquivo = Arte.retrato(p.npcId());
            NPC npc = rotas.getNpc(p.npcId());
            quem = npc != null ? npc.getNome() : "";
            retratoNpc.setEffect(null);
            destacar(p.npcId());
        } else {
            destacar(null);
        }
        cartaoRetrato.setVisible(arquivo != null);
        retratoBase = arquivo;
        if (arquivo != null) {
            retratoNpc.setImage(Imagens.carregar(arquivo));
        }
        nomeCartao.setText(quem);
        nome.setText("");
        aba.setVisible(false);
        aba.setManaged(false);
        texto.getStyleClass().remove("cena-titulo");
        if (titulo) {
            texto.getStyleClass().add("cena-titulo");
        }
        digitar(p.texto());

        boolean ultima = i == paginas.size() - 1;
        if (ultima) {
            mostrarOpcoes();
        } else {
            painelOpcoes.setVisible(false);
        }
        dica.setVisible(!ultima || opcoes.isEmpty());
    }

    /** Revela o texto aos poucos; um clique ou Enter mostra tudo de uma vez. */
    private void digitar(String completo) {
        pararDigitacao();
        texto.setText("");
        int[] n = {0};
        int[] quadro = {0};
        digitacao = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            n[0] = Math.min(completo.length(), n[0] + 2);
            texto.setText(completo.substring(0, n[0]));
            // boca abre e fecha enquanto o texto aparece (a cada ~110 ms)
            if (retratoBase != null && ++quadro[0] % 7 == 0) {
                boolean aberta = (quadro[0] / 7) % 2 == 1;
                retratoNpc.setImage(Imagens.carregar(aberta ? Arte.variacao(retratoBase, "fala") : retratoBase));
            }
        }));
        digitacao.setCycleCount(Math.max(1, (int) Math.ceil(completo.length() / 2.0)));
        digitacao.setOnFinished(e -> {
            texto.setText(completo);
            fecharBoca();
        });
        digitacao.play();
    }

    private boolean digitando() {
        return digitacao != null && digitacao.getStatus() == javafx.animation.Animation.Status.RUNNING;
    }

    private void pararDigitacao() {
        if (digitacao != null) {
            digitacao.stop();
        }
        fecharBoca();
    }

    private void fecharBoca() {
        if (retratoBase != null) {
            retratoNpc.setImage(Imagens.carregar(retratoBase));
        }
    }

    /**
     * Piscar: de tempos em tempos (2,5 a 6 s, sorteado) o retrato de quem fala e os bonecos de gente fecham os olhos
     * por 140 ms. Dá vida à cena sem distrair.
     */
    private void piscarDeVezEmQuando(ImageView v, java.util.function.Supplier<String> base) {
        javafx.animation.PauseTransition espera = new javafx.animation.PauseTransition(
                Duration.millis(2500 + acaso.nextInt(3500)));
        espera.setOnFinished(e -> {
            if (v.getScene() == null) {
                return;                                   // a tela já saiu: para de piscar
            }
            String b = base.get();
            boolean podePiscar = b != null && !(v == retratoNpc && digitando());
            if (podePiscar) {
                v.setImage(Imagens.carregar(Arte.variacao(b, "pisca")));
                javafx.animation.PauseTransition abre = new javafx.animation.PauseTransition(Duration.millis(140));
                abre.setOnFinished(x -> {
                    String agora = base.get();
                    if (agora != null) {
                        v.setImage(Imagens.carregar(agora));
                    }
                });
                abre.play();
            }
            piscarDeVezEmQuando(v, base);
        });
        espera.play();
    }

    private void avancar() {
        if (paginas.isEmpty()) {
            return;
        }
        if (digitando()) {
            pararDigitacao();
            texto.setText(paginas.get(indice).texto());
            return;
        }
        if (indice < paginas.size() - 1) {
            mostrarPagina(indice + 1);
        } else if (opcoes.isEmpty()) {
            aoTerminar.run();
        }
    }

    // ---------- escolhas ----------

    private void mostrarOpcoes() {
        if (opcoes.isEmpty()) {
            return;
        }
        painelOpcoes.getChildren().clear();
        for (int i = 0; i < opcoes.size(); i++) {
            Opcao o = opcoes.get(i);
            String rotulo = (i + 1) + "   " + o.texto();
            if (o.detalhe() != null && !o.detalhe().isBlank()) {
                rotulo += "\n      " + o.detalhe();
            }
            if (!o.liberada()) {
                rotulo += "\n" + o.bloqueio();
            }
            Button b = Componentes.botao(rotulo, AcaoMenu.Estilo.NORMAL);
            b.getStyleClass().add("cena-opcao");
            b.setWrapText(true);
            b.setAlignment(Pos.CENTER_LEFT);
            b.setFocusTraversable(false);
            b.setDisable(!o.liberada());
            b.setOnAction(e -> escolher(o));
            painelOpcoes.getChildren().add(b);
        }
        painelOpcoes.setVisible(true);
    }

    private void escolher(Opcao o) {
        if (o.liberada()) {
            pararDigitacao();
            o.acao().run();
        }
    }

    // ---------- barra superior ----------

    private Parent barraSuperior() {
        HBox barra = new HBox(24);
        barra.getStyleClass().add("cena-hud");
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setMaxHeight(64);
        barra.setMinHeight(64);

        Label capitulo = Componentes.rotulo(rotuloCapitulo, "cena-capitulo");
        capitulo.setWrapText(false);
        if (!objetivo.isEmpty()) {
            // Passar o mouse sobre o capítulo mostra o objetivo inteiro
            capitulo.setTooltip(new javafx.scene.control.Tooltip("Objetivo: " + objetivo));
        }

        HBox recursos = new HBox(18,
                recurso("ico_agua.png", jogador.getRecursos().getAgua()),
                recurso("ico_comida.png", jogador.getRecursos().getComida()),
                recurso("ico_reputacao.png", jogador.getReputacao()));
        recursos.setAlignment(Pos.CENTER_LEFT);

        VBox titulo = new VBox(0, capitulo);
        titulo.setAlignment(Pos.CENTER_LEFT);
        if (!objetivo.isEmpty()) {
            Label alvo = Componentes.rotulo("Objetivo: " + objetivo, "cena-objetivo");
            alvo.setWrapText(false);
            alvo.setMaxWidth(430);
            titulo.getChildren().add(alvo);
        }
        barra.getChildren().addAll(titulo, Componentes.espaco(), recursos, Componentes.espaco());
        barra.getChildren().add(botaoHud("I  Mochila", atalhos.aoMochila()));
        barra.getChildren().add(botaoHud("F  Ficha", atalhos.aoFicha()));
        barra.getChildren().add(botaoHud("C  Casa", atalhos.aoCasa()));
        barra.getChildren().add(botaoHud("S  Salvar", atalhos.aoSalvar()));
        barra.getChildren().add(botaoHud("L  Carregar", atalhos.aoCarregar()));
        barra.getChildren().add(botaoHud("Esc  Menu", atalhos.aoMenu()));
        return barra;
    }

    private HBox recurso(String icone, int valor) {
        Label v = Componentes.rotulo(String.valueOf(valor), "cena-valor");
        v.setWrapText(false);
        HBox h = new HBox(8, Imagens.vista(icone, 32, 32), v);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private Button botaoHud(String rotulo, Runnable acao) {
        Button b = Componentes.botao(rotulo, AcaoMenu.Estilo.NORMAL);
        b.getStyleClass().add("cena-botao-hud");
        b.setFocusTraversable(false);
        b.setMaxWidth(Region.USE_PREF_SIZE);
        b.setOnAction(e -> acao.run());
        return b;
    }

    // ---------- teclado ----------

    private void tecla(KeyEvent ev) {
        KeyCode c = ev.getCode();
        if (c == KeyCode.ENTER || c == KeyCode.SPACE) {
            avancar();
            ev.consume();
        } else if (c == KeyCode.I) {
            atalhos.aoMochila().run();
            ev.consume();
        } else if (c == KeyCode.C) {
            atalhos.aoCasa().run();
            ev.consume();
        } else if (c == KeyCode.H) {
            atalhos.aoAjuda().run();
            ev.consume();
        } else if (c == KeyCode.F) {
            atalhos.aoFicha().run();
            ev.consume();
        } else if (c == KeyCode.S) {
            atalhos.aoSalvar().run();
            ev.consume();
        } else if (c == KeyCode.L) {
            atalhos.aoCarregar().run();
            ev.consume();
        } else if (c == KeyCode.M || c == KeyCode.ESCAPE) {
            atalhos.aoMenu().run();
            ev.consume();
        } else if (painelOpcoes.isVisible() && ev.getText().length() == 1 && Character.isDigit(ev.getText().charAt(0))) {
            int n = ev.getText().charAt(0) - '1';
            if (n >= 0 && n < opcoes.size()) {
                escolher(opcoes.get(n));
                ev.consume();
            }
        }
    }
}
