package br.colapso2071.controller;

import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Cena;
import br.colapso2071.model.Conquista;
import br.colapso2071.model.Configuracoes;
import br.colapso2071.model.Escolha;
import br.colapso2071.model.Final;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.Progresso;
import br.colapso2071.model.Rotas;
import br.colapso2071.persistencia.ProgressoRepositorio;
import br.colapso2071.view.JogoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller da partida: conduz o loop principal, pedindo à View que
 * exiba/leia e ao Model que aplique as regras; aciona saves, autosave e o registro de progresso.
 */
public class JogoController {
    private final Rotas rotas;
    private final Configuracoes configuracoes;
    private final Progresso progresso;
    private final ProgressoRepositorio progressoRepositorio;
    private final SaveController saveController;
    private final JogoView view;

    public JogoController(Rotas rotas, Configuracoes configuracoes, Progresso progresso,
                          ProgressoRepositorio progressoRepositorio, SaveController saveController,
                          JogoView view) {
        this.rotas = rotas;
        this.configuracoes = configuracoes;
        this.progresso = progresso;
        this.progressoRepositorio = progressoRepositorio;
        this.saveController = saveController;
        this.view = view;
    }

    /** Cria o protagonista, apresenta a introdução narrativa e conduz a partida. */
    public void novaPartida(String nomeJogador, Perfil perfil) {
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar(nomeJogador, perfil);
        view.exibirIntroducao(rotas.getIntroducao(), jogo.getJogador());
        jogar(jogo);
    }

    /** Conduz a partida (nova ou carregada) até o final ou até o jogador voltar ao menu. */
    public void jogar(Jogo jogo) {
        while (jogo.emAndamento()) {
            Cena cena = jogo.getCenaAtual();
            view.exibirCena(cena, jogo.getJogador());
            if (configuracoes.isMostrarStatus()) {
                view.exibirStatus(jogo.getJogador());
            }

            // Cena de transição para o final: encerra a partida
            if (jogo.cenaAtualLevaAoFinal()) {
                encerrarPartida(jogo);
                return;
            }

            // Evento relevante: salvamento automático
            if (cena.isAutosave() && configuracoes.isSalvamentoAutomatico()
                    && saveController.salvarAutomatico(jogo)) {
                view.exibirAutosave();
            }

            if (!processarAcao(jogo, cena)) {
                return;
            }
        }
    }

    /** @return false se o jogador voltou ao menu (ou a entrada terminou) */
    private boolean processarAcao(Jogo jogo, Cena cena) {
        while (true) {
            String digitado = view.lerAcao(cena, jogo.getJogador(), rotas);
            if (digitado == null) {
                return false; // entrada encerrada (EOF)
            }
            String comando = digitado.trim();

            if (comando.equalsIgnoreCase("M")) {
                if (view.confirmarSairDaPartida()) {
                    return false;
                }
                continue;
            }
            if (comando.equalsIgnoreCase("F")) {
                view.exibirFicha(jogo.getJogador(), rotas);
                continue;
            }
            if (comando.equalsIgnoreCase("P") && jogo.carameloNaCena()) {
                view.exibirCuidados(List.of(jogo.fazerCarinho().getMensagem()));
                continue;
            }
            if (comando.equalsIgnoreCase("D")) {
                view.exibirDiario(jogo.diario());
                continue;
            }
            if (comando.equalsIgnoreCase("S") && cena.isPontoDeSalvamento()) {
                saveController.salvarManual(jogo);
                continue;
            }
            int indice = converterParaIndice(comando);
            if (jogo.escolhaBloqueada(indice)) {
                view.exibirOpcaoBloqueada(jogo.motivoDoBloqueio(indice));
                continue;
            }
            // No console, sede e fome são resolvidas automaticamente (casa, garrafa e depósito)
            view.exibirCuidados(jogo.atenderNecessidades());
            Cena deixada = jogo.getCenaAtual();
            if (jogo.escolher(indice)) {
                Escolha feita = jogo.getUltimaEscolha();
                view.exibirReacao(feita, jogo.getJogador());
                view.exibirCuidados(jogo.consumirPenalidades());
                Cena seguinte = jogo.getCenaAtual();
                if (seguinte == null || seguinte.getNumeroCapitulo() != deixada.getNumeroCapitulo()) {
                    view.exibirConclusao(deixada, jogo.getJogador());
                }
                registrarProgresso(jogo);
                return true;
            }
            view.exibirOpcaoInvalida();
        }
    }

    private void encerrarPartida(Jogo jogo) {
        Final alcancado = jogo.calcularFinal();
        view.exibirFinal(jogo.getJogador(), alcancado);

        boolean novoFinal = progresso.registrarFinal(alcancado.getId());
        if (novoFinal) {
            view.exibirNovoFinal(alcancado);
        }
        anunciarNovasConquistas(jogo);
        progresso.registrarPartidaConcluida();
        persistirProgresso();
    }

    private void registrarProgresso(Jogo jogo) {
        if (anunciarNovasConquistas(jogo)) {
            persistirProgresso();
        }
    }

    /** Registra no progresso global os desbloqueios da partida e avisa os novos. */
    private boolean anunciarNovasConquistas(Jogo jogo) {
        List<Conquista> novas = new ArrayList<>();
        for (String id : progresso.registrarConquistas(jogo.getJogador().getDesbloqueios())) {
            Conquista c = rotas.getConquista(id);
            if (c != null) {
                novas.add(c);
            }
        }
        if (!novas.isEmpty()) {
            view.exibirConquistasDesbloqueadas(novas);
        }
        return !novas.isEmpty();
    }

    private void persistirProgresso() {
        try {
            progressoRepositorio.salvar(progresso);
        } catch (SaveException e) {
            view.exibirErro("Não foi possível registrar o progresso: " + e.getMessage());
        }
    }

    /** Converte a opção digitada (base 1) em índice (base 0); -1 se não for um número. */
    private int converterParaIndice(String texto) {
        try {
            return Integer.parseInt(texto.trim()) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
