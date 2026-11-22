package br.colapso2071.fx;

import br.colapso2071.model.Jogo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * A casa azul do jogador (redesenho da versão 4.3). O cenário é a casa por dentro, à noite: a rede, a mesa com a
 * lamparina, a estante da professora Lúcia. À direita, um cartão com água e comida e as ações: encher a garrafa no
 * poço do quintal, buscar a marmita no depósito, dormir na rede, fazer carinho no Caramelo, abrir a mochila ou sair.
 * Teclas 1 a 6.
 */
public final class TelaCasa {
    private final Jogo jogo;
    private final Runnable aoDormir;
    private final Runnable aoEncher;
    private final Runnable aoDeposito;
    private final Runnable aoCarinho;
    private final Runnable aoMochila;
    private final Runnable aoVoltar;

    public TelaCasa(Jogo jogo, Runnable aoDormir, Runnable aoEncher, Runnable aoDeposito, Runnable aoCarinho,
                    Runnable aoMochila, Runnable aoVoltar) {
        this.aoCarinho = aoCarinho;
        this.jogo = jogo;
        this.aoDormir = aoDormir;
        this.aoEncher = aoEncher;
        this.aoDeposito = aoDeposito;
        this.aoMochila = aoMochila;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        var r = jogo.getJogador().getRecursos();

        String motivoEncher = jogo.motivoParaNaoEncher();
        Button encher = Componentes.botaoMenu("1", "item_garrafa.png", "Encher a garrafa no poço do quintal",
                motivoEncher.isEmpty() ? "Uma garrafa por dia · beber dá Água +" + Jogo.AGUA_DA_GARRAFA : motivoEncher,
                AcaoMenu.Estilo.NORMAL);
        encher.setDisable(!motivoEncher.isEmpty());
        encher.setOnAction(e -> aoEncher.run());

        String motivoDeposito = jogo.motivoParaNaoBuscarComida();
        Button deposito = Componentes.botaoMenu("2", "item_marmita.png", "Buscar a marmita no depósito",
                motivoDeposito.isEmpty() ? "Uma por dia, no Celeiro Velho · comer dá Comida +" + Jogo.COMIDA_DA_MARMITA
                        : motivoDeposito, AcaoMenu.Estilo.NORMAL);
        deposito.setDisable(!motivoDeposito.isEmpty());
        deposito.setOnAction(e -> aoDeposito.run());

        String motivo = jogo.motivoParaNaoDormir();
        Button dormir = Componentes.botaoMenu("3", "ico_rede.png", "Dormir na rede até amanhã",
                motivo.isEmpty() ? "Começa outro dia · gasta " + Math.abs(Jogo.AGUA_AO_DORMIR) + " de água e "
                        + Math.abs(Jogo.COMIDA_AO_DORMIR) + " de comida" : motivo, AcaoMenu.Estilo.NORMAL);
        dormir.setDisable(!motivo.isEmpty());
        dormir.setOnAction(e -> aoDormir.run());

        boolean jaHoje = jogo.getJogador().getDiaCarinho() == jogo.getJogador().getDia();
        Button carinho = Componentes.botaoMenu("4", "ico_caramelo.png", "Fazer carinho no Caramelo",
                jaHoje ? "Ele já ganhou carinho hoje" : "Ele dorme na varanda · uma vez por dia", AcaoMenu.Estilo.NORMAL);
        carinho.setOnAction(e -> aoCarinho.run());

        Button mochila = Componentes.botaoMenu("5", "ico_mochila.png", "Abrir a mochila", "Beber, comer, ler e ver itens",
                AcaoMenu.Estilo.NORMAL);
        mochila.setOnAction(e -> aoMochila.run());

        Button voltar = Componentes.botaoMenu("6", "ico_sair.png", "Sair de casa", "Voltar para a história",
                AcaoMenu.Estilo.PRIMARIO);
        voltar.setCancelButton(true);
        voltar.setOnAction(e -> aoVoltar.run());

        HBox recursos = new HBox(14,
                chip("ico_agua.png", "Água", r.getAgua()),
                chip("ico_comida.png", "Comida", r.getComida()));
        recursos.setAlignment(Pos.CENTER_LEFT);

        String urgente = jogo.motivoDasNecessidades();
        Label estado = Componentes.rotulo(urgente.isEmpty() ? "Você está bem por enquanto." : urgente,
                urgente.isEmpty() ? "casa-estado" : "casa-estado-urgente");

        VBox painel = new VBox(10,
                Componentes.rotulo("Casa azul", "cartao-titulo"),
                Componentes.rotulo("Dia " + jogo.getJogador().getDia() + " · a casa da professora Lúcia", "cartao-sub"),
                recursos, estado, Componentes.separador(),
                encher, deposito, dormir, carinho, mochila, voltar);
        painel.getStyleClass().add("cartao");
        painel.setPadding(new Insets(26, 26, 24, 26));
        painel.setPrefWidth(500);
        painel.setMaxWidth(500);
        painel.setMaxHeight(VBox.USE_PREF_SIZE);
        Componentes.surgir(painel, 60);

        Label legenda = Componentes.rotulo("A professora Lúcia ensinou trinta anos na escola de Bom-Jardim. Ainda há livros "
                + "na estante, a rede armada e, na parede, a foto da feira de quando a lagoa enchia todo ano.", "casa-legenda");
        legenda.setMaxWidth(560);

        StackPane raiz = new StackPane(Imagens.vista("cena_casa.png", 1280, 720), painel, legenda);
        StackPane.setAlignment(painel, Pos.CENTER_RIGHT);
        StackPane.setMargin(painel, new Insets(0, 44, 0, 0));
        StackPane.setAlignment(legenda, Pos.BOTTOM_LEFT);
        StackPane.setMargin(legenda, new Insets(0, 0, 30, 44));
        raiz.setOnKeyPressed(ev -> {
            Button alvo = switch (ev.getText()) {
                case "1" -> encher;
                case "2" -> deposito;
                case "3" -> dormir;
                case "4" -> carinho;
                case "5" -> mochila;
                case "6" -> voltar;
                default -> null;
            };
            if (alvo != null && !alvo.isDisabled()) {
                alvo.fire();
                ev.consume();
            }
        });
        return raiz;
    }

    private static HBox chip(String icone, String nome, int valor) {
        HBox h = new HBox(8, Imagens.vista(icone, 24, 24), Componentes.rotulo(nome + "  " + valor, "chip-texto"));
        h.setAlignment(Pos.CENTER_LEFT);
        h.getStyleClass().add(valor <= 0 ? "chip-vazio" : "chip");
        return h;
    }
}
