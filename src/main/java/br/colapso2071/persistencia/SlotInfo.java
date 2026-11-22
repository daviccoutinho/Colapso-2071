package br.colapso2071.persistencia;

/** Situação de um slot: vazio, com save válido ({@code resumo}) ou ilegível ({@code erro}). */
public record SlotInfo(int slot, ResumoSave resumo, String erro) {
    public boolean vazio() { return resumo == null && erro == null; }
    public boolean invalido() { return erro != null; }
}
