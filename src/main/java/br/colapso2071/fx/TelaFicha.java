package br.colapso2071.fx;

import br.colapso2071.model.Item;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Missao;
import br.colapso2071.model.NPC;
import br.colapso2071.model.Rotas;
import br.colapso2071.model.StatusMissao;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Map;

/** Ficha do protagonista (protótipo "Ficha"): recursos, atributos, mochila, missões e afinidades. */
public final class TelaFicha {
    private final Rotas rotas;
    private final Jogador jogador;
    private final Runnable aoVoltar;

    public TelaFicha(Rotas rotas, Jogador jogador, Runnable aoVoltar) {
        this.rotas = rotas;
        this.jogador = jogador;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        HBox cabeca = new HBox(24,
                Imagens.vista(Arte.retratoDoPerfil(jogador.getPerfil().getId()), 128, 128),
                new VBox(6,
                        Componentes.rotulo(jogador.getNome(), "titulo"),
                        Componentes.rotulo(jogador.getPerfil().getNome() + " · " + br.colapso2071.model.Jogador.IDADE + " anos", "subtitulo"),
                        Componentes.rotulo("Reputação " + jogador.getReputacao(), "subtitulo")));
        cabeca.setAlignment(Pos.CENTER_LEFT);

        HBox recursos = new HBox(32,
                recurso("ico_agua.png", "Água", jogador.getRecursos().getAgua()),
                recurso("ico_comida.png", "Comida", jogador.getRecursos().getComida()));

        VBox corpo = new VBox(18, cabeca, recursos,
                secao("Atributos",
                        "Liderança " + jogador.getAtributos().getLideranca(),
                        "Técnico " + jogador.getAtributos().getTecnico(),
                        "Negociação " + jogador.getAtributos().getNegociacao()),
                secao("Mochila", itens()),
                secao("Missões", missoes()),
                secao("Afinidades", afinidades()),
                secao("Decisões importantes", decisoes()));
        corpo.setPadding(new Insets(0, 24, 0, 0));

        ScrollPane rolagem = new ScrollPane(corpo);
        rolagem.setFitToWidth(true);
        VBox.setVgrow(rolagem, javafx.scene.layout.Priority.ALWAYS);

        Button voltar = Componentes.botao("Voltar à cena", AcaoMenu.Estilo.PRIMARIO);
        voltar.setDefaultButton(true);
        voltar.setCancelButton(true);
        voltar.setOnAction(e -> aoVoltar.run());

        VBox painel = new VBox(14, Componentes.rotulo("Ficha", "cartao-titulo"), Componentes.separador(), rolagem, voltar);
        painel.getStyleClass().add("cartao");
        painel.setPadding(new Insets(28, 40, 24, 40));
        painel.setMaxWidth(900);
        painel.setMaxHeight(660);
        Componentes.surgir(painel, 0);
        return new StackPane(Componentes.fundoDesfocado(), painel);
    }

    private HBox recurso(String icone, String nome, int valor) {
        HBox h = new HBox(10, Imagens.vista(icone, 32, 32), Componentes.rotulo(nome + " " + valor, "texto"));
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private VBox secao(String titulo, String... linhas) {
        VBox v = new VBox(4, Componentes.rotulo(titulo, "titulo-linha"));
        for (String l : linhas) {
            v.getChildren().add(Componentes.rotulo(l, "texto"));
        }
        return v;
    }

    private String[] itens() {
        if (jogador.getInventario().getItens().isEmpty()) {
            return new String[] {"Vazia."};
        }
        return jogador.getInventario().getItens().stream()
                .map((Item i) -> i.getNome() + " — " + i.getDescricao()).toArray(String[]::new);
    }

    private String[] missoes() {
        if (jogador.getMissoes().isEmpty()) {
            return new String[] {"Nenhuma missão ainda."};
        }
        return jogador.getMissoes().entrySet().stream().map((Map.Entry<String, StatusMissao> e) -> {
            Missao m = rotas.getMissao(e.getKey());
            String titulo = m != null ? m.getTitulo() : e.getKey();
            return (e.getValue() == StatusMissao.CONCLUIDA ? "[concluída] " : "[em andamento] ") + titulo;
        }).toArray(String[]::new);
    }

    private String[] afinidades() {
        if (jogador.getRelacionamentos().getAfinidades().isEmpty()) {
            return new String[] {"Você ainda não conhece ninguém bem."};
        }
        return jogador.getRelacionamentos().getAfinidades().entrySet().stream().map(e -> {
            NPC n = rotas.getNpc(e.getKey());
            return (n != null ? n.getNome() : e.getKey()) + ": " + e.getValue();
        }).toArray(String[]::new);
    }

    private String[] decisoes() {
        var lista = jogador.getDecisoes();
        if (lista.isEmpty()) {
            return new String[] {"Nenhuma decisão ainda."};
        }
        int de = Math.max(0, lista.size() - 8);
        java.util.List<String> linhas = new java.util.ArrayList<>();
        for (int i = lista.size() - 1; i >= de; i--) {
            var d = lista.get(i);
            var cena = rotas.getCena(d.cenaId());
            String texto = cena != null && d.indiceEscolha() < cena.getEscolhas().size()
                    ? cena.getEscolhas().get(d.indiceEscolha()).getTexto() : d.cenaId();
            linhas.add("• " + texto);
        }
        return linhas.toArray(String[]::new);
    }
}
