package br.colapso2071.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Menu de pausa da partida (redesenho da versão 4.3): a cena de onde o jogador saiu fica ao fundo, desfocada, e um
 * cartão no centro mostra onde ele está e as ações. Cada botão é uma {@link AcaoMenu} (padrão Command, o mesmo do
 * menu principal); a primeira ação é "Continuar" e responde a Enter e Esc. As teclas 1–9 também funcionam.
 */
public final class TelaPausa {
    private static final Map<String, String> ICONES = Map.of(
            "Continuar", "ico_continuar.png",
            "Salvar jogo", "ico_disquete.png",
            "Diário e objetivo", "ico_diario.png",
            "Instruções", "ico_instrucoes.png",
            "Carregar jogo", "ico_carregar.png",
            "Reiniciar partida", "ico_novo.png",
            "Voltar ao menu principal", "ico_casa.png",
            "Sair do jogo", "ico_sair.png");

    private final List<AcaoMenu> acoes;
    private final String ondeEsta;
    private final String objetivo;

    public TelaPausa(List<AcaoMenu> acoes) {
        this(acoes, null, null);
    }

    /**
     * @param ondeEsta capítulo e dia atuais (pode ser nulo)
     * @param objetivo objetivo do capítulo (pode ser nulo)
     */
    public TelaPausa(List<AcaoMenu> acoes, String ondeEsta, String objetivo) {
        this.acoes = acoes;
        this.ondeEsta = ondeEsta;
        this.objetivo = objetivo;
    }

    public Parent construir() {
        VBox lista = new VBox(4);
        boolean primeira = true;
        for (AcaoMenu a : acoes) {
            Button b = Componentes.botaoMenu(a.atalho(), ICONES.get(a.rotulo()), a.rotulo(), null, a.estilo());
            b.getStyleClass().add("botao-menu-compacto");
            b.setOnAction(e -> a.executar());
            if (primeira) {
                b.setDefaultButton(true);
                b.setCancelButton(true);
                primeira = false;
            }
            lista.getChildren().add(b);
        }
        VBox painel = new VBox(10, Componentes.rotulo("Pausa", "cartao-titulo"));
        if (ondeEsta != null && !ondeEsta.isBlank()) {
            painel.getChildren().add(Componentes.rotulo(ondeEsta, "cartao-sub"));
        }
        if (objetivo != null && !objetivo.isBlank()) {
            painel.getChildren().add(Componentes.rotulo("Objetivo: " + objetivo, "cartao-objetivo"));
        }
        painel.getChildren().addAll(Componentes.separador(), lista,
                Componentes.rotulo("Esc volta para o jogo · 1–8 atalhos · F11 tela cheia", "menu-dica"));
        painel.getStyleClass().add("cartao");
        painel.setPadding(new Insets(30, 34, 26, 34));
        painel.setMaxWidth(540);
        painel.setMaxHeight(VBox.USE_PREF_SIZE);
        Componentes.surgir(painel, 40);

        StackPane raiz = new StackPane(Componentes.fundoDesfocado(), painel);
        StackPane.setAlignment(painel, Pos.CENTER);
        raiz.setOnKeyPressed(ev -> {
            for (AcaoMenu a : acoes) {
                if (a.atalho().equals(ev.getText())) {
                    a.executar();
                    ev.consume();
                    return;
                }
            }
        });
        return raiz;
    }
}
