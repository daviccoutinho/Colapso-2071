package br.colapso2071.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Progresso global do usuário (vale para todas as partidas):
 * finais já alcançados e conquistas já desbloqueadas.
 */
public class Progresso {
    private final Set<String> finais = new LinkedHashSet<>();
    private final Set<String> conquistas = new LinkedHashSet<>();
    private int partidasConcluidas;

    /** Conta uma partida que chegou a um final (qualquer um, inclusive repetido). */
    public void registrarPartidaConcluida() {
        partidasConcluidas++;
    }

    /** Usado ao restaurar o progresso salvo. */
    public void definirPartidasConcluidas(int total) {
        if (total < 0) {
            throw new IllegalArgumentException("Total de partidas não pode ser negativo: " + total);
        }
        partidasConcluidas = total;
    }

    public int getPartidasConcluidas() { return partidasConcluidas; }

    /** @return true se o final ainda não constava no registro */
    public boolean registrarFinal(String finalId) {
        return finais.add(finalId);
    }

    /** @return os ids que eram novos (ainda não estavam desbloqueados) */
    public List<String> registrarConquistas(Collection<String> ids) {
        List<String> novas = new ArrayList<>();
        for (String id : ids) {
            if (conquistas.add(id)) {
                novas.add(id);
            }
        }
        return novas;
    }

    public boolean alcancouFinal(String finalId) { return finais.contains(finalId); }
    public boolean possuiConquista(String id) { return conquistas.contains(id); }

    public Set<String> getFinais() { return Collections.unmodifiableSet(finais); }
    public Set<String> getConquistas() { return Collections.unmodifiableSet(conquistas); }
}
