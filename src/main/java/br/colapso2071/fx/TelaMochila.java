package br.colapso2071.fx;

import br.colapso2071.model.Item;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Uso;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Mochila em matriz 5x5 com o sprite de cada item. Clique (ou setas) seleciona uma casa;
 * o painel ao lado mostra o item, o botão Usar (U) e a opção Ver descrição (D).
 */
public final class TelaMochila {
    private static final double CASA_PX = 76;
    private static final double ICONE_PX = Arte.LADO_ICONE_ITEM * 2;

    private final Jogador jogador;
    private final Consumer<String> aoUsar;
    private final Runnable aoVoltar;
    private final String mensagem;
    private final GradeMochila grade;
    private int selecionada;
    private boolean descricaoAberta;

    private final GridPane matriz = new GridPane();
    private final VBox detalhe = new VBox(12);

    public TelaMochila(Jogador jogador, Runnable aoVoltar) {
        this(jogador, null, aoVoltar, null, null);
    }

    /**
     * @param aoUsar     recebe o id do item a usar (null desativa o botão Usar)
     * @param mensagem   resultado do último uso (pode ser nula)
     * @param itemInicial item que já aparece selecionado (pode ser nulo)
     */
    public TelaMochila(Jogador jogador, Consumer<String> aoUsar, Runnable aoVoltar, String mensagem, String itemInicial) {
        this.jogador = jogador;
        this.aoUsar = aoUsar;
        this.aoVoltar = aoVoltar;
        this.mensagem = mensagem;
        this.grade = new GradeMochila(jogador.getInventario());
        int k = itemInicial == null ? -1 : grade.indiceDe(itemInicial);
        this.selecionada = k >= 0 ? k : 0;
    }

    public Parent construir() {
        HBox recursos = new HBox(28,
                recurso("ico_agua.png", "Água", jogador.getRecursos().getAgua()),
                recurso("ico_comida.png", "Comida", jogador.getRecursos().getComida()),
                Componentes.rotulo("Dia " + jogador.getDia(), "texto"));

        matriz.setHgap(6);
        matriz.setVgap(6);
        matriz.getStyleClass().add("mochila-matriz");
        desenharMatriz();

        detalhe.setPrefWidth(380);
        detalhe.setMinWidth(380);
        detalhe.getStyleClass().add("mochila-detalhe");
        desenharDetalhe();

        HBox corpo = new HBox(28, matriz, detalhe);
        corpo.setAlignment(Pos.TOP_LEFT);

        Button voltar = Componentes.botao("Voltar", AcaoMenu.Estilo.PRIMARIO);
        voltar.setCancelButton(true);
        voltar.setOnAction(e -> aoVoltar.run());

        recursos.getStyleClass().add("mochila-recursos");
        VBox painel = new VBox(14, Componentes.rotulo("Mochila", "cartao-titulo"), recursos, Componentes.separador());
        if (mensagem != null) {
            painel.getChildren().add(Componentes.rotulo(mensagem, "cena-aviso-mochila"));
        }
        painel.getChildren().addAll(corpo,
                Componentes.rotulo("Clique numa casa ou use as setas · U: usar · D: ver descrição · Esc: voltar", "cena-dica"),
                voltar);
        painel.getStyleClass().add("cartao");
        painel.setPadding(new Insets(26, 40, 24, 40));
        painel.setMaxWidth(1060);
        painel.setMaxHeight(VBox.USE_PREF_SIZE);
        Componentes.surgir(painel, 0);

        StackPane raiz = new StackPane(Componentes.fundoDesfocado(), painel);
        raiz.setOnKeyPressed(ev -> {
            KeyCode c = ev.getCode();
            if (c == KeyCode.LEFT) {
                mover(0, -1);
            } else if (c == KeyCode.RIGHT) {
                mover(0, 1);
            } else if (c == KeyCode.UP) {
                mover(-1, 0);
            } else if (c == KeyCode.DOWN) {
                mover(1, 0);
            } else if (c == KeyCode.U) {
                usarSelecionado();
            } else if (c == KeyCode.D) {
                alternarDescricao();
            } else {
                return;
            }
            ev.consume();
        });
        raiz.sceneProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                raiz.requestFocus();
            }
        });
        return raiz;
    }

    private void desenharMatriz() {
        matriz.getChildren().clear();
        for (int lin = 0; lin < GradeMochila.LADO; lin++) {
            for (int col = 0; col < GradeMochila.LADO; col++) {
                int indice = lin * GradeMochila.LADO + col;
                GradeMochila.Casa casa = grade.em(lin, col);
                StackPane quadro = new StackPane();
                quadro.setPrefSize(CASA_PX, CASA_PX);
                quadro.setMinSize(CASA_PX, CASA_PX);
                quadro.getStyleClass().add(indice == selecionada ? "mochila-casa-selecionada" : "mochila-casa");
                if (casa != null) {
                    quadro.getChildren().add(Imagens.vista(Arte.iconeDoItem(casa.item().getId()), ICONE_PX, ICONE_PX));
                    if (casa.quantidade() > 1) {
                        Label qtd = Componentes.rotulo("×" + casa.quantidade(), "mochila-qtd");
                        StackPane.setAlignment(qtd, Pos.BOTTOM_RIGHT);
                        StackPane.setMargin(qtd, new Insets(0, 4, 2, 0));
                        quadro.getChildren().add(qtd);
                    }
                }
                quadro.setOnMouseClicked(e -> {
                    selecionar(indice);
                    if (e.getClickCount() == 2) {
                        usarSelecionado();
                    }
                });
                matriz.add(quadro, col, lin);
            }
        }
    }

    private void desenharDetalhe() {
        detalhe.getChildren().clear();
        GradeMochila.Casa casa = casaSelecionada();
        if (casa == null) {
            detalhe.getChildren().add(Componentes.rotulo(grade.getOcupadas() == 0
                    ? "A mochila está vazia. Itens aparecem aqui conforme você os ganha na história."
                    : "Casa vazia. Escolha um item na grade.", "texto"));
            return;
        }
        Item item = casa.item();
        StackPane vitrine = new StackPane(Imagens.vista(Arte.iconeDoItem(item.getId()), Arte.LADO_ICONE_ITEM * 4, Arte.LADO_ICONE_ITEM * 4));
        vitrine.getStyleClass().add("mochila-vitrine");
        vitrine.setMaxWidth(Double.MAX_VALUE);
        detalhe.getChildren().addAll(
                vitrine,
                Componentes.rotulo(item.getNome() + (casa.quantidade() > 1 ? "  ×" + casa.quantidade() : ""), "titulo-linha"),
                Componentes.rotulo(tipo(item), "cena-dica"));

        if (item.isUsavel() && aoUsar != null) {
            Button usar = Componentes.botao(item.getUso().verbo() + " (U)", AcaoMenu.Estilo.PRIMARIO);
            usar.setOnAction(e -> usarSelecionado());
            detalhe.getChildren().add(usar);
        }
        Button ver = Componentes.botao(descricaoAberta ? "Esconder descrição (D)" : "Ver descrição (D)", AcaoMenu.Estilo.NORMAL);
        ver.setOnAction(e -> alternarDescricao());
        detalhe.getChildren().add(ver);
        if (descricaoAberta) {
            detalhe.getChildren().add(Componentes.rotulo(descricaoCompleta(item), "texto"));
        }
    }

    private static String tipo(Item item) {
        if (item.getId().equals(br.colapso2071.model.Rotas.ITEM_GARRAFA_VAZIA)) {
            return "Vazia: encha em casa (tecla C), uma vez por dia.";
        }
        if (item.getId().equals(br.colapso2071.model.Rotas.ITEM_CHAVE_INGLESA)) {
            return "Ferramenta: exigida em escolhas de reparo e para forçar fechaduras.";
        }
        if (!item.isUsavel()) {
            return "Item de história: pode liberar escolhas.";
        }
        if (!item.getUso().paginas().isEmpty()) {
            return "Pode ser lido quantas vezes quiser.";
        }
        if (item.getUso().imagem() != null) {
            return "Pode ser visto em tela cheia.";
        }
        if (item.getUso().idDepois() != null) {
            return "Fica vazia depois de usar, mas continua na mochila.";
        }
        return item.getUso().consumivel() ? "Consumível: some depois de usar." : "Pode ser usado várias vezes.";
    }

    private static String descricaoCompleta(Item item) {
        StringBuilder sb = new StringBuilder(item.getDescricao());
        Uso uso = item.getUso();
        if (uso != null && uso.temEfeito()) {
            sb.append("\nEfeito:");
            if (uso.agua() != 0) sb.append(" Água +").append(uso.agua());
            if (uso.comida() != 0) sb.append(" Comida +").append(uso.comida());
        }
        return sb.toString();
    }

    private GradeMochila.Casa casaSelecionada() {
        return grade.em(selecionada / GradeMochila.LADO, selecionada % GradeMochila.LADO);
    }

    private void selecionar(int indice) {
        if (indice != selecionada) {
            descricaoAberta = false;
        }
        selecionada = indice;
        desenharMatriz();
        desenharDetalhe();
    }

    private void mover(int dLinha, int dColuna) {
        int lin = Math.floorMod(selecionada / GradeMochila.LADO + dLinha, GradeMochila.LADO);
        int col = Math.floorMod(selecionada % GradeMochila.LADO + dColuna, GradeMochila.LADO);
        selecionar(lin * GradeMochila.LADO + col);
    }

    private void alternarDescricao() {
        if (casaSelecionada() != null) {
            descricaoAberta = !descricaoAberta;
            desenharDetalhe();
        }
    }

    private void usarSelecionado() {
        GradeMochila.Casa casa = casaSelecionada();
        if (casa != null && casa.item().isUsavel() && aoUsar != null) {
            aoUsar.accept(casa.item().getId());
        }
    }

    private HBox recurso(String icone, String nome, int valor) {
        HBox h = new HBox(10, Imagens.vista(icone, 32, 32), Componentes.rotulo(nome + " " + valor, "texto"));
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }
}
