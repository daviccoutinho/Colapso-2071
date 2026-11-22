package br.colapso2071.fx;

import br.colapso2071.persistencia.ResumoSave;
import br.colapso2071.persistencia.SaveRepository;
import br.colapso2071.persistencia.SlotInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.function.IntConsumer;

/** Tela "Carregar jogo" / "Excluir save" (protótipo "Saves"): três slots mais o automático. */
public final class TelaSaves {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ContextoFx ctx;
    private final String titulo;
    private final IntConsumer aoEscolher;
    private final Runnable aoVoltar;
    private final boolean aceitaAutomatico;
    private final boolean permiteVazio;

    public TelaSaves(ContextoFx ctx, String titulo, boolean aceitaAutomatico,
                     IntConsumer aoEscolher, Runnable aoVoltar) {
        this(ctx, titulo, aceitaAutomatico, false, aoEscolher, aoVoltar);
    }

    /** Com {@code permiteVazio} os slots vazios podem ser escolhidos (usado para salvar). */
    public TelaSaves(ContextoFx ctx, String titulo, boolean aceitaAutomatico, boolean permiteVazio,
                     IntConsumer aoEscolher, Runnable aoVoltar) {
        this.ctx = ctx;
        this.titulo = titulo;
        this.aceitaAutomatico = aceitaAutomatico;
        this.permiteVazio = permiteVazio;
        this.aoEscolher = aoEscolher;
        this.aoVoltar = aoVoltar;
    }

    public Parent construir() {
        VBox raiz = new VBox(20);
        
        raiz.getChildren().addAll(Componentes.rotulo(titulo, "cartao-titulo"), Componentes.separador());

        VBox lista = new VBox(12);
        for (SlotInfo info : ctx.saves().listarSlots()) {
            if (info.slot() == SaveRepository.SLOT_AUTO && !aceitaAutomatico) {
                continue;
            }
            lista.getChildren().add(cartao(info));
        }
        raiz.getChildren().add(lista);

        Button voltar = Componentes.botao("Voltar", AcaoMenu.Estilo.NORMAL);
        voltar.setOnAction(e -> aoVoltar.run());
        voltar.setCancelButton(true);
        HBox rodape = new HBox(voltar, Componentes.espaco(),
                Componentes.rotulo("Salvar sobre um slot cheio e excluir pedem confirmação.", "subtitulo"));
        rodape.setAlignment(Pos.CENTER_LEFT);
        raiz.getChildren().add(rodape);
        return Componentes.emCartao(raiz, 1000, 660);
    }

    private Button cartao(SlotInfo info) {
        String nome = info.slot() == SaveRepository.SLOT_AUTO ? "A" : String.valueOf(info.slot());
        Button b = new Button();
        b.setMaxWidth(Double.MAX_VALUE);
        b.getStyleClass().add("slot");
        if (info.slot() == SaveRepository.SLOT_AUTO) {
            b.getStyleClass().add("slot-auto");
        }

        VBox texto = new VBox(4);
        if (info.vazio()) {
            b.getStyleClass().add("slot-vazio");
            texto.getChildren().add(Componentes.rotulo("(vazio)", "subtitulo"));
            b.setDisable(!permiteVazio);
        } else if (info.invalido()) {
            b.getStyleClass().add("slot-ilegivel");
            texto.getChildren().addAll(
                    Componentes.rotulo("SAVE ILEGÍVEL", "titulo-slot"),
                    Componentes.rotulo("Save adulterado ou danificado: "
                            + SaveRepository.nomeDoSlot(info.slot()), "subtitulo"));
        } else {
            ResumoSave r = info.resumo();
            int total = Math.max(1, ctx.rotas().getTotalCapitulos());
            ProgressBar barra = new ProgressBar((double) r.capitulo() / total);
            barra.setPrefWidth(320);
            texto.getChildren().addAll(
                    Componentes.rotulo(r.nomeJogador(), "titulo-slot"),
                    Componentes.rotulo("Cap. " + r.capitulo() + " – " + r.tituloCapitulo(), "subtitulo"),
                    barra);
            texto.getChildren().add(Componentes.rotulo(
                    r.dataHora().format(DATA) + "   Capítulo " + r.capitulo() + " de " + total, "rotulo"));
        }
        HBox linha = new HBox(20, Componentes.rotulo(nome, "titulo"), texto);
        linha.setAlignment(Pos.CENTER_LEFT);
        b.setGraphic(linha);
        b.setOnAction(e -> aoEscolher.accept(info.slot()));
        return b;
    }
}
