package br.colapso2071.controller;

import br.colapso2071.model.Configuracoes;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.Rotas;

import java.util.ArrayList;
import java.util.List;
import br.colapso2071.view.MenuView;

/**
 * Controller do menu inicial: interpreta a opção escolhida e delega
 * aos demais controllers (partida, saves, configurações, galeria).
 */
public class MenuController {
    private static final String NOME_PADRAO_FALLBACK = "Sobrevivente";

    private final Rotas rotas;
    private final MenuView view;
    private final Configuracoes configuracoes;
    private final JogoController jogoController;
    private final SaveController saveController;
    private final ConfiguracoesController configuracoesController;
    private final ProgressoController progressoController;

    public MenuController(Rotas rotas, MenuView view, Configuracoes configuracoes, JogoController jogoController,
                          SaveController saveController, ConfiguracoesController configuracoesController,
                          ProgressoController progressoController) {
        this.rotas = rotas;
        this.view = view;
        this.configuracoes = configuracoes;
        this.jogoController = jogoController;
        this.saveController = saveController;
        this.configuracoesController = configuracoesController;
        this.progressoController = progressoController;
    }

    public void executar() {
        boolean rodando = true;

        while (rodando) {
            String opcao = view.lerOpcaoMenu();
            if (opcao == null) {
                break; // entrada encerrada (EOF)
            }

            switch (opcao.trim()) {
                case "1" -> rodando = novoJogo();
                case "2" -> saveController.continuar().ifPresent(jogoController::jogar);
                case "3" -> saveController.carregarPeloMenu().ifPresent(jogoController::jogar);
                case "4" -> saveController.excluirPeloMenu();
                case "5" -> configuracoesController.executar();
                case "6" -> progressoController.exibir();
                case "7" -> view.exibirInstrucoes();
                case "8" -> view.exibirCreditos();
                case "9" -> {
                    view.exibirSaindo();
                    rodando = false;
                }
                default -> view.exibirOpcaoInvalida();
            }
        }
    }

    /**
     * Criação do protagonista (nome e perfil) e início da partida.
     *
     * @return false se a entrada terminou durante a criação
     */
    private boolean novoJogo() {
        String nome = view.lerNomeJogador(configuracoes.getNomePadrao());
        if (nome == null) {
            return false;
        }
        nome = nome.trim();
        if (nome.isEmpty()) {
            nome = configuracoes.getNomePadrao().isEmpty() ? NOME_PADRAO_FALLBACK : configuracoes.getNomePadrao();
        }

        List<Perfil> perfis = new ArrayList<>(rotas.getPerfis());
        while (true) {
            String digitado = view.lerPerfil(perfis);
            if (digitado == null) {
                return false;
            }
            String texto = digitado.trim();
            if (texto.equalsIgnoreCase("V")) {
                return true; // volta ao menu
            }
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= 1 && numero <= perfis.size()) {
                    jogoController.novaPartida(nome, perfis.get(numero - 1));
                    return true;
                }
            } catch (NumberFormatException ignorada) {
                // cai na mensagem de perfil inválido
            }
            view.exibirPerfilInvalido();
        }
    }
}
