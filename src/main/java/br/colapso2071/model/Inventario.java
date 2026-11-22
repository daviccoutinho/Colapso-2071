package br.colapso2071.model;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private final List<Item> itens;

    public Inventario() {
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(Item item) {
        if (item != null) {
            itens.add(item);
        }
    }

    public boolean removerItemPorId(String id) {
        return itens.removeIf(item -> item.getId().equalsIgnoreCase(id));
    }

    /** Remove só uma unidade do item (a mochila pode ter vários iguais). */
    public boolean removerUm(String id) {
        for (int i = 0; i < itens.size(); i++) {
            if (itens.get(i).getId().equalsIgnoreCase(id)) {
                itens.remove(i);
                return true;
            }
        }
        return false;
    }

    public int quantidade(String id) {
        return (int) itens.stream().filter(item -> item.getId().equalsIgnoreCase(id)).count();
    }

    public boolean possuiItem(String id) {
        return itens.stream().anyMatch(item -> item.getId().equalsIgnoreCase(id));
    }

    public List<Item> getItens() {
        return itens;
    }
}
