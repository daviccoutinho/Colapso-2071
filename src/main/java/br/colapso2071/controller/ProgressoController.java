package br.colapso2071.controller;

import br.colapso2071.model.Progresso;
import br.colapso2071.model.Rotas;
import br.colapso2071.view.ProgressoView;

/** Controller da galeria de finais e conquistas. */
public class ProgressoController {
    private final Rotas rotas;
    private final Progresso progresso;
    private final ProgressoView view;

    public ProgressoController(Rotas rotas, Progresso progresso, ProgressoView view) {
        this.rotas = rotas;
        this.progresso = progresso;
        this.view = view;
    }

    public void exibir() {
        view.exibir(rotas, progresso);
    }
}
