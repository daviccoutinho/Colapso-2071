package br.colapso2071.persistencia;

import br.colapso2071.model.Jogo;

/** Partida carregada e a informação de que ela veio do backup (o save principal estava danificado). */
public record ResultadoCarga(Jogo jogo, boolean recuperadoDoBackup) {
}
