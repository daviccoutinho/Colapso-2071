package br.colapso2071.fx;

import br.colapso2071.model.Perfil;
import br.colapso2071.model.Rotas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

/** Criação do protagonista: nome e um dos perfis (cada um com seu sprite e atributos iniciais). */
public final class TelaNovoJogo {
    private static final int MAX_NOME = 20;

    private final Rotas rotas;
    private final String nomeSugerido;
    private final BiConsumer<String, Perfil> aoComecar;
    private final Runnable aoVoltar;

    public TelaNovoJogo(Rotas rotas, String nomeSugerido, BiConsumer<String, Perfil> aoComecar, Runnable aoVoltar) {
        this.rotas = rotas;
        this.nomeSugerido = nomeSugerido == null ? "" : nomeSugerido;
        this.aoComecar = aoComecar;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        TextField campo = new TextField(nomeSugerido);
        campo.setPromptText("Seu nome");
        campo.textProperty().addListener((o, antigo, novo) -> {
            if (novo.length() > MAX_NOME) {
                campo.setText(antigo);
            }
        });

        Label erro = Componentes.rotulo("", "rotulo");
        ToggleGroup grupo = new ToggleGroup();
        HBox cartas = new HBox(20);
        cartas.setAlignment(Pos.CENTER);
        boolean primeiro = true;
        for (Perfil p : rotas.getPerfis()) {
            ToggleButton carta = carta(p);
            carta.setToggleGroup(grupo);
            carta.setSelected(primeiro);
            primeiro = false;
            cartas.getChildren().add(carta);
        }
        // Impede deixar o grupo sem seleção ao clicar de novo na carta já escolhida.
        grupo.selectedToggleProperty().addListener((o, antigo, novo) -> {
            if (novo == null && antigo != null) {
                antigo.setSelected(true);
            }
        });

        Button comecar = Componentes.botao("Começar", AcaoMenu.Estilo.PRIMARIO);
        comecar.setDefaultButton(true);
        comecar.setOnAction(e -> {
            String nome = campo.getText().trim();
            if (nome.isEmpty()) {
                erro.setText("Digite um nome para continuar.");
                campo.requestFocus();
                return;
            }
            Perfil escolhido = (Perfil) grupo.getSelectedToggle().getUserData();
            aoComecar.accept(nome, escolhido);
        });
        Button voltar = Componentes.botao("Voltar", AcaoMenu.Estilo.NORMAL);
        voltar.setCancelButton(true);
        voltar.setOnAction(e -> aoVoltar.run());
        HBox botoes = new HBox(16, voltar, comecar);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        VBox painel = new VBox(18,
                Componentes.rotulo("Novo jogo", "cartao-titulo"),
                Componentes.rotulo("Como você se chama?", "subtitulo"),
                campo,
                Componentes.rotulo("Quem você era antes do colapso?", "subtitulo"),
                cartas, erro, botoes);
        painel.getStyleClass().add("cartao");
        Componentes.surgir(painel, 0);
        painel.setPadding(new Insets(30, 56, 30, 56));
        painel.setMaxWidth(1120);
        painel.setMaxHeight(VBox.USE_PREF_SIZE);

        StackPane raiz = new StackPane(Imagens.vista("cena_menu.png", 1280, 720), painel);
        return raiz;
    }

    private ToggleButton carta(Perfil p) {
        Label nome = Componentes.rotulo(p.getNome(), "titulo-linha");
        Label descricao = Componentes.rotulo(p.getDescricao(), "rotulo");
        Label atributos = Componentes.rotulo("Liderança " + p.getLideranca()
                + " · Técnico " + p.getTecnico() + " · Negociação " + p.getNegociacao(), "rotulo");
        VBox conteudo = new VBox(8,
                Imagens.vista(Arte.retratoDoPerfil(p.getId()), 128, 128), nome, descricao, atributos);
        conteudo.setAlignment(Pos.TOP_CENTER);
        conteudo.setPrefWidth(300);

        ToggleButton carta = new ToggleButton();
        carta.setGraphic(conteudo);
        carta.getStyleClass().add("carta-perfil");
        carta.setUserData(p);
        return carta;
    }
}
