package br.colapso2071.model;

import java.util.List;

/**
 * Lembrança de um personagem: um pedaço do passado dele que só é contado a quem tem afinidade suficiente.
 * Começa com a pergunta do protagonista ({@code perguntaDoJogador}). Ouvir não avança a história.
 */
public record Lembranca(String id, NPC npc, int afinidadeMinima, String titulo, String perguntaDoJogador,
                        List<Dialogos> falas) {

    public boolean disponivelPara(Jogador jogador) {
        return jogador.getRelacionamentos().getAfinidade(npc.getId()) >= afinidadeMinima;
    }

    /** Vazio se o jogador pode ouvir; senão, o que falta. */
    public String motivoBloqueio(Jogador jogador) {
        return disponivelPara(jogador) ? ""
                : "Requer afinidade " + afinidadeMinima + " com " + npc.getNome()
                        + " (atual: " + jogador.getRelacionamentos().getAfinidade(npc.getId()) + ")";
    }

    public String chave() {
        return "L:" + id;
    }
}
