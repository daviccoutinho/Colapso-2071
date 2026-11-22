package br.colapso2071.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Escolha {
    private final String texto;
    private final String proximaCenaId;
    private final Condicao condicao;
    private final Consequencia consequencia;

    private final List<Dialogos> reacaoFalas = new ArrayList<>();
    private String reacaoNarracao = "";
    private String textoRetomar;
    private String falaDoJogador;
    private boolean narracaoAntes;
    private final List<Dialogos> reacaoRetomar = new ArrayList<>();

    public Escolha(String texto, String proximaCenaId, Condicao condicao, Consequencia consequencia) {
        this.texto = texto;
        this.proximaCenaId = proximaCenaId;
        this.condicao = condicao;
        this.consequencia = consequencia;
    }

    /** Fala(s) dos personagens logo depois que a escolha é feita. */
    public Escolha comReacao(Dialogos... falas) {
        Collections.addAll(reacaoFalas, falas);
        return this;
    }

    /** Trecho narrado logo depois que a escolha é feita (aparece após as falas). */
    public Escolha comNarracao(String narracao) {
        this.reacaoNarracao = narracao;
        return this;
    }

    /** Sem condição, a escolha está sempre disponível. */
    /**
     * Marca a opção como conversa: se o jogador não a escolher, ela reaparece na cena seguinte com o texto
     * {@code textoRetomar}, para ele voltar ao assunto. {@code reacao} (opcional) substitui a reação original.
     */
    public Escolha conversa(String textoRetomar, Dialogos... reacao) {
        this.textoRetomar = textoRetomar;
        this.reacaoRetomar.addAll(List.of(reacao));
        return this;
    }

    /** O que o protagonista diz quando escolhe esta opção (mostrado antes da reação dos outros). */
    public Escolha comFalaDoJogador(String fala) {
        this.falaDoJogador = fala;
        return this;
    }

    public String getFalaDoJogador() { return falaDoJogador; }

    /** A narração da reação vem antes das falas (quando a fala é consequência do que foi narrado). */
    public Escolha comNarracaoAntes() {
        this.narracaoAntes = true;
        return this;
    }

    public boolean isNarracaoAntes() { return narracaoAntes; }

    public boolean isConversa() { return textoRetomar != null; }
    public String getTextoRetomar() { return textoRetomar; }

    /** Falas mostradas ao retomar a conversa depois; se não houver próprias, usa a reação original. */
    public List<Dialogos> getReacaoRetomar() {
        return reacaoRetomar.isEmpty() ? getReacaoFalas() : Collections.unmodifiableList(reacaoRetomar);
    }

    public boolean estaDisponivel(Jogador jogador) {
        return condicao == null || condicao.atende(jogador);
    }

    public String getTexto() { return texto; }
    public String getProximaCenaId() { return proximaCenaId; }
    public Condicao getCondicao() { return condicao; }

    /** Linha curta com o que a opção exige e o que ela muda, para o jogador decidir sabendo do custo. */
    public String resumoParaJogador(Rotas rotas) {
        List<String> partes = new ArrayList<>();
        if (condicao != null) {
            partes.add("Exige: " + condicao.descricao());
        }
        if (consequencia != null) {
            List<String> efeitos = consequencia.resumo(rotas);
            List<String> custos = efeitos.stream().filter(e -> !e.startsWith("Nova missão")).toList();
            if (!custos.isEmpty()) {
                partes.add("Custa: " + String.join(" · ", custos));
            }
            efeitos.stream().filter(e -> e.startsWith("Nova missão")).forEach(partes::add);
        }
        return String.join("  |  ", partes);
    }
    public Consequencia getConsequencia() { return consequencia; }
    public List<Dialogos> getReacaoFalas() { return Collections.unmodifiableList(reacaoFalas); }
    public String getReacaoNarracao() { return reacaoNarracao; }
}
