package br.colapso2071.fx;

import br.colapso2071.model.Inventario;
import br.colapso2071.model.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Organiza a mochila numa matriz 5x5: cada casa guarda um tipo de item e a quantidade.
 * Itens iguais ocupam uma casa só; a ordem é a de chegada. Não depende de JavaFX.
 */
public final class GradeMochila {
    public static final int LADO = 5;
    public static final int CASAS = LADO * LADO;

    /** Uma casa ocupada da grade. */
    public record Casa(Item item, int quantidade) { }

    private final Casa[][] matriz = new Casa[LADO][LADO];
    private final int ocupadas;

    public GradeMochila(Inventario inventario) {
        Map<String, Casa> porId = new LinkedHashMap<>();
        for (Item i : inventario.getItens()) {
            Casa atual = porId.get(i.getId());
            porId.put(i.getId(), new Casa(atual == null ? i : atual.item(), atual == null ? 1 : atual.quantidade() + 1));
        }
        List<Casa> casas = new ArrayList<>(porId.values());
        int n = Math.min(casas.size(), CASAS);
        for (int k = 0; k < n; k++) {
            matriz[k / LADO][k % LADO] = casas.get(k);
        }
        this.ocupadas = n;
    }

    /** Casa na linha/coluna dadas, ou null se estiver vazia. */
    public Casa em(int linha, int coluna) {
        return matriz[linha][coluna];
    }

    public int getOcupadas() {
        return ocupadas;
    }

    /** Índice (linha*5+coluna) da casa com o item, ou -1. */
    public int indiceDe(String itemId) {
        for (int k = 0; k < ocupadas; k++) {
            if (matriz[k / LADO][k % LADO].item().getId().equals(itemId)) {
                return k;
            }
        }
        return -1;
    }
}
