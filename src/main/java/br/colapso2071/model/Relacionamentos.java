package br.colapso2071.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Relacionamentos {
    private final Map<String, Integer> afinidades;

    public Relacionamentos() {
        this.afinidades = new LinkedHashMap<>();
    }

    public void alterarAfinidade(String npcId, int delta) {
        afinidades.put(npcId, afinidades.getOrDefault(npcId, 0) + delta);
    }

    public int getAfinidade(String npcId) {
        return afinidades.getOrDefault(npcId, 0);
    }

    /** Visão somente leitura de todas as afinidades (npcId -> valor). */
    public Map<String, Integer> getAfinidades() {
        return Collections.unmodifiableMap(afinidades);
    }
}
