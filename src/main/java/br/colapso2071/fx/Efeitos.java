package br.colapso2071.fx;

import br.colapso2071.model.Conquista;
import br.colapso2071.model.Item;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Missao;
import br.colapso2071.model.NPC;
import br.colapso2071.model.Rotas;
import br.colapso2071.model.StatusMissao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Feedback das escolhas: guarda uma foto do estado do jogador antes da escolha e descreve, em frases
 * curtas, o que mudou depois (atributos, recursos, reputação, afinidades, itens e missões).
 * Não depende de JavaFX; a tela só exibe a lista devolvida.
 */
public final class Efeitos {
    private Efeitos() {
    }

    /** Foto imutável do que interessa mostrar ao jogador. */
    public record Foto(int lideranca, int tecnico, int negociacao, int agua, int comida, int energia, int reputacao,
                       Map<String, Integer> afinidades, Set<String> itens, Map<String, StatusMissao> missoes,
                       Set<String> desbloqueios) { }

    public static Foto fotografar(Jogador j) {
        Set<String> itens = new HashSet<>();
        for (Item i : j.getInventario().getItens()) {
            itens.add(i.getId());
        }
        return new Foto(j.getAtributos().getLideranca(), j.getAtributos().getTecnico(), j.getAtributos().getNegociacao(),
                j.getRecursos().getAgua(), j.getRecursos().getComida(), j.getRecursos().getEnergia(), j.getReputacao(),
                new HashMap<>(j.getRelacionamentos().getAfinidades()), itens, new HashMap<>(j.getMissoes()), new HashSet<>(j.getDesbloqueios()));
    }

    /** Lista o que mudou entre a foto e o estado atual; vazia se nada mudou. */
    public static List<String> comparar(Foto antes, Jogador agora, Rotas rotas) {
        Foto d = fotografar(agora);
        List<String> r = new ArrayList<>();
        delta(r, "Liderança", d.lideranca() - antes.lideranca());
        delta(r, "Técnico", d.tecnico() - antes.tecnico());
        delta(r, "Negociação", d.negociacao() - antes.negociacao());
        delta(r, "Água", d.agua() - antes.agua());
        delta(r, "Comida", d.comida() - antes.comida());
        delta(r, "Reputação", d.reputacao() - antes.reputacao());
        Set<String> npcs = new HashSet<>(d.afinidades().keySet());
        npcs.addAll(antes.afinidades().keySet());
        for (String id : npcs) {
            int v = d.afinidades().getOrDefault(id, 0) - antes.afinidades().getOrDefault(id, 0);
            NPC npc = rotas.getNpc(id);
            delta(r, "Afinidade com " + (npc != null ? npc.getNome() : id), v);
        }
        for (String id : d.itens()) {
            if (!antes.itens().contains(id)) {
                r.add("Item adicionado ao inventário: " + nomeDoItem(rotas, id));
            }
        }
        for (String id : antes.itens()) {
            if (!d.itens().contains(id)) {
                r.add("Item usado: " + nomeDoItem(rotas, id));
            }
        }
        for (Map.Entry<String, StatusMissao> e : d.missoes().entrySet()) {
            StatusMissao antigo = antes.missoes().get(e.getKey());
            if (antigo == e.getValue()) {
                continue;
            }
            Missao m = rotas.getMissao(e.getKey());
            String titulo = m != null ? m.getTitulo() : e.getKey();
            r.add((e.getValue() == StatusMissao.CONCLUIDA ? "Missão concluída: " : "Nova missão: ") + titulo);
        }
        for (String id : d.desbloqueios()) {
            Conquista c = rotas.getConquista(id);
            if (!antes.desbloqueios().contains(id) && c != null) {
                r.add("Conquista desbloqueada: " + c.getTitulo());
            }
        }
        return r;
    }

    private static String nomeDoItem(Rotas rotas, String id) {
        Item i = rotas.getItem(id);
        return i != null ? i.getNome() : id;
    }

    private static void delta(List<String> saida, String rotulo, int valor) {
        if (valor != 0) {
            saida.add(rotulo + " " + (valor > 0 ? "+" : "−") + Math.abs(valor));
        }
    }
}
