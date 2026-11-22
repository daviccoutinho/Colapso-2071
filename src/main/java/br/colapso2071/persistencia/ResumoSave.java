package br.colapso2071.persistencia;

import java.time.LocalDateTime;

/** Metadados exibidos na lista de saves. */
public record ResumoSave(int slot,
                         String nomeJogador,
                         LocalDateTime dataHora,
                         int capitulo,
                         String tituloCapitulo,
                         String cenaId,
                         String resumo) {
}
