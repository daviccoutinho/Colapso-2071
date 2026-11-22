package br.colapso2071.model;

public class Dialogos {
    private final NPC npc;
    private final String falaNPC;

    public Dialogos(NPC npc, String falaNPC) {
        this.npc = npc;
        this.falaNPC = falaNPC;
    }

    public NPC getNpc() { return npc; }
    public String getFalaNPC() { return falaNPC; }
}