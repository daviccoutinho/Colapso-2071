package br.colapso2071;

import br.colapso2071.controller.ConfiguracoesController;
import br.colapso2071.controller.JogoController;
import br.colapso2071.controller.MenuController;
import br.colapso2071.controller.ProgressoController;
import br.colapso2071.controller.SaveController;
import br.colapso2071.excecao.PersistenciaException;
import br.colapso2071.model.Configuracoes;
import br.colapso2071.model.Progresso;
import br.colapso2071.model.Rotas;
import br.colapso2071.persistencia.ConfiguracoesRepositorio;
import br.colapso2071.persistencia.ProgressoRepositorio;
import br.colapso2071.persistencia.SaveRepository;
import br.colapso2071.view.ConfiguracoesView;
import br.colapso2071.view.Console;
import br.colapso2071.view.JogoView;
import br.colapso2071.view.MenuView;
import br.colapso2071.view.ProgressoView;
import br.colapso2071.view.SaveView;

import java.nio.file.Path;
import java.time.Clock;

/** Composição: cria Model, Views, repositórios e Controllers e os conecta. */
public final class Aplicacao {
    private Aplicacao() {
    }

    public static MenuController criar(Console console, Path pastaDeDados, Clock clock) {
        Rotas rotas = new Rotas();

        SaveRepository saves = new SaveRepository(pastaDeDados, rotas, clock);
        ConfiguracoesRepositorio configuracoesRepo = new ConfiguracoesRepositorio(pastaDeDados);
        ProgressoRepositorio progressoRepo = new ProgressoRepositorio(pastaDeDados);

        ConfiguracoesView configuracoesView = new ConfiguracoesView(console);
        Configuracoes configuracoes = new Configuracoes();
        try {
            configuracoes = configuracoesRepo.carregar();
        } catch (PersistenciaException e) {
            configuracoesView.exibirAviso("Preferências ignoradas, usando valores padrão. " + e.getMessage());
        }

        Progresso progresso = new Progresso();
        try {
            progresso = progressoRepo.carregar();
        } catch (PersistenciaException e) {
            configuracoesView.exibirAviso("Registro de finais e conquistas ignorado. " + e.getMessage());
        }

        SaveController saveController = new SaveController(saves, new SaveView(console));
        JogoController jogoController = new JogoController(rotas, configuracoes, progresso, progressoRepo,
                saveController, new JogoView(console));
        ConfiguracoesController configuracoesController =
                new ConfiguracoesController(configuracoes, configuracoesRepo, configuracoesView);
        ProgressoController progressoController =
                new ProgressoController(rotas, progresso, new ProgressoView(console));

        return new MenuController(rotas, new MenuView(console), configuracoes, jogoController, saveController,
                configuracoesController, progressoController);
    }
}
