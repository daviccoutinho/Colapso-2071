package br.colapso2071.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Consequencia {
    private final int deltaComida;
    private final int deltaAgua;
    private final int deltaEnergia;
    private final int deltaReputacao; // Adicionado para calcular o final
    private final Item itemGanho;

    // Efeitos opcionais (encadeáveis): relacionamentos, missões e desbloqueios
    private final Map<String, Integer> deltasAfinidade = new LinkedHashMap<>();
    private final Map<TipoAtributo, Integer> deltasAtributo = new LinkedHashMap<>();
    private final List<String> missoesIniciadas = new ArrayList<>();
    private final List<String> missoesConcluidas = new ArrayList<>();
    private final List<String> desbloqueios = new ArrayList<>();
    private Item garrafaCheia;

    public Consequencia(int deltaComida, int deltaAgua, int deltaEnergia, int deltaReputacao, Item itemGanho) {
        this.deltaComida = deltaComida;
        this.deltaAgua = deltaAgua;
        this.deltaEnergia = deltaEnergia;
        this.deltaReputacao = deltaReputacao;
        this.itemGanho = itemGanho;
    }

    public Consequencia comAfinidade(String npcId, int delta) {
        deltasAfinidade.merge(npcId, delta, Integer::sum);
        return this;
    }

    public Consequencia comAtributo(TipoAtributo tipo, int delta) {
        deltasAtributo.merge(tipo, delta, Integer::sum);
        return this;
    }

    public Consequencia iniciaMissao(String missaoId) {
        missoesIniciadas.add(missaoId);
        return this;
    }

    public Consequencia concluiMissao(String missaoId) {
        missoesConcluidas.add(missaoId);
        return this;
    }

    /**
     * Alguém enche a garrafa térmica do jogador: a vazia vira cheia; quem ainda não tem ganha uma
     * cheia; quem já está com a garrafa cheia bebe um gole a mais (+5 de água).
     */
    public Consequencia encheGarrafa(Item garrafaCheia) {
        this.garrafaCheia = garrafaCheia;
        return this;
    }

    public Consequencia desbloqueia(String id) {
        desbloqueios.add(id);
        return this;
    }

    public void aplicar(Jogador jogador) {
        jogador.getRecursos().alterarComida(deltaComida);
        jogador.getRecursos().alterarAgua(deltaAgua);
        jogador.getRecursos().alterarEnergia(deltaEnergia);
        jogador.alterarReputacao(deltaReputacao); // Atualiza a rota do jogador
        if (itemGanho != null) {
            jogador.getInventario().adicionarItem(itemGanho);
        }
        deltasAfinidade.forEach((npcId, delta) -> jogador.getRelacionamentos().alterarAfinidade(npcId, delta));
        deltasAtributo.forEach((tipo, delta) -> jogador.getAtributos().alterar(tipo, delta));
        missoesIniciadas.forEach(jogador::iniciarMissao);
        missoesConcluidas.forEach(jogador::concluirMissao);
        desbloqueios.forEach(jogador::desbloquear);
        if (garrafaCheia != null) {
            encherGarrafaDe(jogador, garrafaCheia);
        }
    }

    private static void encherGarrafaDe(Jogador jogador, Item cheia) {
        Inventario inv = jogador.getInventario();
        if (inv.removerUm(Rotas.ITEM_GARRAFA_VAZIA)) {
            inv.adicionarItem(cheia);
        } else if (!inv.possuiItem(Rotas.ITEM_GARRAFA)) {
            inv.adicionarItem(cheia);
        } else {
            jogador.getRecursos().alterarAgua(5);
        }
    }

    /**
     * O que a escolha acrescenta ou cobra, em frases curtas para mostrar ao lado da opção
     * (ex.: "Água −10", "Afinidade com Milena +2", "Liderança +1", "Item: Rádio de Manivela").
     */
    public List<String> resumo(Rotas rotas) {
        // Só o que a escolha cobra (água, comida, energia) e as missões que ela abre: o que se ganha em
        // reputação, afinidade ou atributos fica para o jogador descobrir.
        List<String> r = new ArrayList<>();
        if (deltaAgua < 0) {
            sinal(r, "Água", deltaAgua);
        }
        if (deltaComida < 0) {
            sinal(r, "Comida", deltaComida);
        }
        for (String id : missoesIniciadas) {
            Missao m = rotas.getMissao(id);
            r.add("Nova missão: " + (m != null ? m.getTitulo() : id));
        }
        return r;
    }

    private static void sinal(List<String> r, String rotulo, int valor) {
        if (valor != 0) {
            r.add(rotulo + " " + (valor > 0 ? "+" : "−") + Math.abs(valor));
        }
    }
}
