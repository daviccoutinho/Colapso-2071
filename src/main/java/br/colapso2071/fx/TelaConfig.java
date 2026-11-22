package br.colapso2071.fx;

import br.colapso2071.excecao.PersistenciaException;
import br.colapso2071.model.Configuracoes;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Tela de configurações (protótipo "Config"). Usa o padrão Observer do JavaFX:
 * cada controle está ligado a uma Property, e qualquer mudança é persistida na hora.
 */
public final class TelaConfig {
    private final ContextoFx ctx;
    private final Runnable aoVoltar;
    private final Label estado = Componentes.rotulo("", "rotulo");

    public TelaConfig(ContextoFx ctx, Runnable aoVoltar) {
        this.ctx = ctx;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        Configuracoes cfg = ctx.configuracoes();
        BooleanProperty auto = new SimpleBooleanProperty(cfg.isSalvamentoAutomatico());
        BooleanProperty status = new SimpleBooleanProperty(cfg.isMostrarStatus());
        StringProperty nome = new SimpleStringProperty(cfg.getNomePadrao());
        BooleanProperty musica = new SimpleBooleanProperty(cfg.isMusica());
        BooleanProperty tema = new SimpleBooleanProperty(cfg.isTemaModerno());
        BooleanProperty cheia = new SimpleBooleanProperty(cfg.isTelaCheia());
        javafx.scene.control.Slider volume = new javafx.scene.control.Slider(0, 100, cfg.getVolumeMusica());
        volume.setPrefWidth(240);
        volume.setShowTickMarks(true);
        volume.setMajorTickUnit(25);

        auto.addListener((o, antes, agora) -> { cfg.setSalvamentoAutomatico(agora); persistir(); });
        status.addListener((o, antes, agora) -> { cfg.setMostrarStatus(agora); persistir(); });
        nome.addListener((o, antes, agora) -> { cfg.setNomePadrao(agora); persistir(); });
        musica.addListener((o, antes, agora) -> { cfg.setMusica(agora); Musica.setLigada(agora); persistir(); });
        tema.addListener((o, antes, agora) -> { cfg.setTemaModerno(agora); Tema.aplicar(agora); persistir(); });
        volume.valueProperty().addListener((o, antes, agora) -> {
            cfg.setVolumeMusica(agora.intValue());
            Musica.setVolume(agora.intValue());
        });
        // grava o volume só quando o jogador solta o controle
        volume.valueChangingProperty().addListener((o, antes, mexendo) -> {
            if (!mexendo) {
                persistir();
            }
        });
        CheckBox cMusica = new CheckBox();
        cMusica.selectedProperty().bindBidirectional(musica);
        cheia.addListener((o, antes, agora) -> {
            cfg.setTelaCheia(agora);
            if (Navegador.janela() != null) {
                Navegador.janela().setFullScreen(agora);
            }
            persistir();
        });
        // Som: saída de áudio, teste e situação atual
        javafx.scene.control.ComboBox<Musica.Saida> saida = new javafx.scene.control.ComboBox<>(
                javafx.collections.FXCollections.observableArrayList(Musica.Saida.values()));
        saida.setValue(Musica.Saida.de(cfg.getSaidaAudio()));
        saida.setPrefWidth(300);
        Label situacao = Componentes.rotulo("Saída em uso: " + Musica.saidaEmUso(), "subtitulo");
        situacao.setWrapText(true);
        situacao.setMaxWidth(520);
        Runnable atualizar = () -> javafx.application.Platform.runLater(
                () -> situacao.setText("Saída em uso: " + Musica.saidaEmUso()));
        saida.valueProperty().addListener((o, antes, agora) -> {
            cfg.setSaidaAudio(agora.name());
            Musica.setSaida(agora);
            persistir();
            Musica.testar(r -> atualizar.run());
        });
        Button testar = Componentes.botao("Testar som", AcaoMenu.Estilo.NORMAL);
        testar.setOnAction(e -> {
            situacao.setText("Testando...");
            Musica.testar(r -> javafx.application.Platform.runLater(() -> situacao.setText(r)));
        });
        CheckBox cCheia = new CheckBox();
        cCheia.selectedProperty().bindBidirectional(cheia);
        CheckBox cTema = new CheckBox();
        cTema.selectedProperty().bindBidirectional(tema);

        CheckBox cAuto = new CheckBox();
        cAuto.selectedProperty().bindBidirectional(auto);
        CheckBox cStatus = new CheckBox();
        cStatus.selectedProperty().bindBidirectional(status);
        TextField campo = new TextField();
        campo.setPrefWidth(240);
        campo.textProperty().bindBidirectional(nome);

        VBox linhas = new VBox(8,
                secao("Jogo"),
                linha("Salvamento automático",
                        "Salva depois de cada ação e ao fechar o jogo, sem você pedir.", cAuto),
                linha("Mostrar status a cada cena",
                        "Exibe comida, água e reputação no topo de cada cena.", cStatus),
                linha("Nome padrão do jogador",
                        "Aparece como sugestão ao começar um novo jogo.", campo),
                secao("Som"),
                linha("Trilha sonora",
                        "Uma música calma no dia a dia; tensão, luto e festa só quando a história pede.", cMusica),
                linha("Volume da música", "De 0 a 100.", volume),
                linha("Saída de áudio", "No automático, o jogo tenta o áudio do Java, depois o tocador do sistema "
                        + "(pw-play, paplay, aplay) e por último o JavaFX.", saida),
                linhaNo("Testar o som", situacao, testar),
                secao("Tela"),
                linha("Tela cheia", "Abre o jogo em tela cheia. F11 ou Alt+Enter alternam a qualquer momento.", cCheia),
                linha("Tema moderno (AtlantaFX)",
                        "Controles com visual atual. Desligue se preferir o visual clássico do JavaFX.", cTema));
        javafx.scene.control.ScrollPane rolagem = new javafx.scene.control.ScrollPane(linhas);
        rolagem.setFitToWidth(true);
        rolagem.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(rolagem, javafx.scene.layout.Priority.ALWAYS);

        Button voltar = Componentes.botao("Voltar", AcaoMenu.Estilo.PRIMARIO);
        voltar.setOnAction(e -> aoVoltar.run());
        voltar.setCancelButton(true);
        voltar.setMaxWidth(180);
        HBox rodape = new HBox(16, voltar, estado);
        rodape.setAlignment(Pos.CENTER_LEFT);

        VBox painel = new VBox(12, Componentes.rotulo("Configurações", "cartao-titulo"), Componentes.separador(), rolagem, rodape);
        painel.getStyleClass().add("cartao");
        painel.setPadding(new Insets(26, 32, 22, 32));
        painel.setMaxWidth(860);
        painel.setMaxHeight(640);
        Componentes.surgir(painel, 0);
        javafx.scene.layout.StackPane raiz = new javafx.scene.layout.StackPane(Componentes.fundoDesfocado(), painel);
        return raiz;
    }

    private Label secao(String nome) {
        Label l = Componentes.rotulo(nome.toUpperCase(), "config-secao");
        VBox.setMargin(l, new Insets(10, 0, 0, 4));
        return l;
    }

    /** Linha com um texto que muda (a situação do som) no lugar da descrição fixa. */
    private HBox linhaNo(String titulo, Label descricao, javafx.scene.Node controle) {
        VBox textos = new VBox(4, Componentes.rotulo(titulo, "titulo-linha"), descricao);
        textos.setMaxWidth(560);
        HBox linha = new HBox(24, textos, Componentes.espaco(), controle);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.getStyleClass().add("config-linha");
        return linha;
    }

    private HBox linha(String titulo, String descricao, javafx.scene.Node controle) {
        VBox textos = new VBox(4,
                Componentes.rotulo(titulo, "titulo-linha"),
                Componentes.rotulo(descricao, "subtitulo"));
        textos.setMaxWidth(560);
        HBox linha = new HBox(24, textos, Componentes.espaco(), controle);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.getStyleClass().add("config-linha");
        return linha;
    }

    private void persistir() {
        try {
            ctx.configuracoesRepo().salvar(ctx.configuracoes());
            estado.setText("As mudanças ficam salvas para as próximas vezes que você abrir o jogo.");
        } catch (PersistenciaException e) {
            estado.setText("Não foi possível salvar as preferências: " + e.getMessage());
        }
    }
}
