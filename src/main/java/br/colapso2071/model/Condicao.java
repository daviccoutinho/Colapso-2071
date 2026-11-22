package br.colapso2071.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Condição de acesso a uma escolha: um conjunto de requisitos que o jogador precisa atender
 * (item, atributo, afinidade com um NPC ou decisão anterior). Todos precisam ser atendidos.
 */
public class Condicao {

    /** Um requisito individual, com texto legível para explicar o bloqueio. */
    private interface Requisito {
        boolean atende(Jogador jogador);

        String descricao();
    }

    private final List<Requisito> requisitos = new ArrayList<>();

    private Condicao() {
    }

    /** Compatível com a versão original: item opcional (id) e liderança mínima. */
    public Condicao(String itemRequeridoId, int liderancaMinima) {
        if (itemRequeridoId != null) {
            adicionarItem(itemRequeridoId, itemRequeridoId);
        }
        adicionarAtributo(TipoAtributo.LIDERANCA, liderancaMinima);
    }

    public static Condicao item(Item item) {
        Condicao c = new Condicao();
        c.adicionarItem(item.getId(), item.getNome());
        return c;
    }

    public static Condicao atributoMinimo(TipoAtributo tipo, int minimo) {
        Condicao c = new Condicao();
        c.adicionarAtributo(tipo, minimo);
        return c;
    }

    public static Condicao afinidadeMinima(NPC npc, int minimo) {
        Condicao c = new Condicao();
        c.requisitos.add(new Requisito() {
            @Override public boolean atende(Jogador j) {
                return j.getRelacionamentos().getAfinidade(npc.getId()) >= minimo;
            }
            @Override public String descricao() {
                return "afinidade " + minimo + " com " + npc.getNome();
            }
        });
        return c;
    }

    /** Exige ter feito a escolha {@code indice} (base 0) na cena {@code cenaId}. */
    public static Condicao decisao(String cenaId, int indice, String descricaoHumana) {
        Condicao c = new Condicao();
        c.requisitos.add(new Requisito() {
            @Override public boolean atende(Jogador j) {
                return j.getDecisoes().contains(new Decisao(cenaId, indice));
            }
            @Override public String descricao() {
                return descricaoHumana;
            }
        });
        return c;
    }

    /** Combina com outra condição: ambas precisam ser atendidas. */
    /** O jogador NÃO tomou essa decisão (ex.: não aceitou o acordo do Coronel). */
    public static Condicao naoDecidiu(String cenaId, int indice, String descricaoHumana) {
        Condicao c = new Condicao();
        c.requisitos.add(new Requisito() {
            @Override public boolean atende(Jogador j) {
                return !j.getDecisoes().contains(new Decisao(cenaId, indice));
            }
            @Override public String descricao() {
                return descricaoHumana;
            }
        });
        return c;
    }

    /** Atende se esta OU a outra condição for atendida (ex.: aceitou ajudar no primeiro dia ou no segundo). */
    public Condicao ou(Condicao outra) {
        Condicao a = this;
        Condicao c = new Condicao();
        c.requisitos.add(new Requisito() {
            @Override public boolean atende(Jogador j) {
                return a.atende(j) || outra.atende(j);
            }
            @Override public String descricao() {
                return a.descricao() + " ou " + outra.descricao();
            }
        });
        return c;
    }

    public Condicao e(Condicao outra) {
        Condicao c = new Condicao();
        c.requisitos.addAll(this.requisitos);
        c.requisitos.addAll(outra.requisitos);
        return c;
    }

    private void adicionarItem(String id, String nome) {
        requisitos.add(new Requisito() {
            @Override public boolean atende(Jogador j) {
                return j.getInventario().possuiItem(id);
            }
            @Override public String descricao() {
                return "ter o item " + nome;
            }
        });
    }

    private void adicionarAtributo(TipoAtributo tipo, int minimo) {
        requisitos.add(new Requisito() {
            @Override public boolean atende(Jogador j) {
                return j.getAtributos().get(tipo) >= minimo;
            }
            @Override public String descricao() {
                return tipo.getNome() + " " + minimo;
            }
        });
    }

    public boolean atende(Jogador jogador) {
        for (Requisito r : requisitos) {
            if (!r.atende(jogador)) {
                return false;
            }
        }
        return true;
    }

    /** Requisitos ainda não atendidos, em texto (ex.: "Liderança 3 e ter o item Chave Inglesa"). */
    public String motivoDoBloqueio(Jogador jogador) {
        List<String> faltando = new ArrayList<>();
        for (Requisito r : requisitos) {
            if (!r.atende(jogador)) {
                faltando.add(r.descricao());
            }
        }
        return String.join(" e ", faltando);
    }

    public String descricao() {
        List<String> todos = new ArrayList<>();
        for (Requisito r : requisitos) {
            todos.add(r.descricao());
        }
        return String.join(" e ", todos);
    }
}
