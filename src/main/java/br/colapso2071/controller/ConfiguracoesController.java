package br.colapso2071.controller;

import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Configuracoes;
import br.colapso2071.persistencia.ConfiguracoesRepositorio;
import br.colapso2071.view.ConfiguracoesView;

/** Controller das preferências do usuário; grava em disco a cada alteração. */
public class ConfiguracoesController {
    private final Configuracoes configuracoes;
    private final ConfiguracoesRepositorio repositorio;
    private final ConfiguracoesView view;

    public ConfiguracoesController(Configuracoes configuracoes, ConfiguracoesRepositorio repositorio,
                                   ConfiguracoesView view) {
        this.configuracoes = configuracoes;
        this.repositorio = repositorio;
        this.view = view;
    }

    public void executar() {
        while (true) {
            String opcao = view.lerOpcao(configuracoes);
            if (opcao == null) {
                return;
            }
            switch (opcao.trim()) {
                case "1" -> {
                    configuracoes.setSalvamentoAutomatico(!configuracoes.isSalvamentoAutomatico());
                    persistir();
                }
                case "2" -> {
                    configuracoes.setMostrarStatus(!configuracoes.isMostrarStatus());
                    persistir();
                }
                case "3" -> {
                    String nome = view.lerNomePadrao();
                    if (nome == null) {
                        return;
                    }
                    configuracoes.setNomePadrao(nome);
                    persistir();
                }
                case "4" -> {
                    return;
                }
                default -> view.exibirOpcaoInvalida();
            }
        }
    }

    private void persistir() {
        try {
            repositorio.salvar(configuracoes);
        } catch (SaveException e) {
            view.exibirErro(e.getMessage() + " A preferência vale apenas nesta execução.");
        }
    }
}
