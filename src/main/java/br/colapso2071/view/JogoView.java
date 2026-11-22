package br.colapso2071.view;

import br.colapso2071.model.Cena;
import br.colapso2071.model.Conquista;
import br.colapso2071.model.Decisao;
import br.colapso2071.model.Dialogos;
import br.colapso2071.model.Escolha;
import br.colapso2071.model.Final;
import br.colapso2071.model.Item;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Rotas;
import br.colapso2071.model.StatusMissao;

import java.util.List;
import java.util.Map;

/**
 * View da partida: desenha introdução, cenas, diálogos, escolhas, ficha do personagem e o final no
 * console e lê a opção digitada. Não altera o estado do jogo.
 */
public class JogoView {
    private static final String LINHA = "-------------------------------------------------";

    private final Console console;

    public JogoView(Console console) {
        this.console = console;
    }

    /** Troca o marcador {nome} pelo nome do jogador. */
    private static String personalizar(String texto, Jogador jogador) {
        return texto.replace("{nome}", jogador.getNome());
    }

    public void exibirIntroducao(List<String> paragrafos, Jogador jogador) {
        console.println("\n=================================================");
        console.println("                  COLAPSO 2071");
        console.println("=================================================");
        for (String paragrafo : paragrafos) {
            console.println();
            console.println(personalizar(paragrafo, jogador));
        }
    }

    /** Título do capítulo (na primeira cena dele), descrição, falas dos personagens e fechamento. */
    public void exibirCena(Cena cena, Jogador jogador) {
        console.println("\n" + LINHA);
        if (cena.isInicioDeCapitulo()) {
            console.println("CAPÍTULO " + cena.getNumeroCapitulo() + " - " + cena.getTituloCapitulo().toUpperCase());
            if (cena.getObjetivo() != null) {
                console.println("Objetivo: " + cena.getObjetivo());
            }
            console.println(LINHA);
        }
        for (br.colapso2071.model.Interludio item : cena.interludioItensPara(jogador)) {
            if (item.isFala()) {
                exibirFala(item.fala(), jogador);
            } else {
                console.println(personalizar(item.texto(), jogador));
                console.println();
            }
        }
        console.println(personalizar(cena.getDescricao(), jogador));

        List<Dialogos> dialogos = cena.getDialogos();
        if (dialogos != null && !dialogos.isEmpty()) {
            console.println();
            for (Dialogos dialogo : dialogos) {
                exibirFala(dialogo, jogador);
            }
        }
        if (!cena.getFechamento().isEmpty()) {
            console.println();
            console.println(personalizar(cena.getFechamento(), jogador));
        }
        if (cena.getPergunta() != null) {
            console.println();
            exibirFala(cena.getPergunta(), jogador);
        }
    }

    /** Conclusão do capítulo que acabou, ligando ao próximo. */
    public void exibirConclusao(Cena ultimaDoCapitulo, Jogador jogador) {
        List<String> conclusao = ultimaDoCapitulo.conclusaoPara(jogador);
        if (conclusao.isEmpty()) {
            return;
        }
        console.println("\n" + LINHA);
        console.println("FIM DO CAPÍTULO " + ultimaDoCapitulo.getNumeroCapitulo());
        for (String paragrafo : conclusao) {
            console.println(personalizar(paragrafo, jogador));
        }
        if (ultimaDoCapitulo.getFalaDeDescanso() != null) {
            exibirFala(ultimaDoCapitulo.getFalaDeDescanso(), jogador);
            console.println("Você volta para a casa azul e dorme. Começa o dia " + jogador.getDia() + ".");
        }
    }

    /** O que o jogador fez para cuidar de si (dormir, beber, comer) antes de seguir. */
    /** Diário da partida: o que aconteceu em cada capítulo e o objetivo atual. */
    public void exibirDiario(List<String> linhas) {
        console.println("\n" + LINHA + "\nDIÁRIO\n" + LINHA);
        for (String l : linhas) {
            console.println(l);
            console.println("");
        }
    }

    public void exibirCuidados(List<String> feitos) {
        for (String f : feitos) {
            console.println("» " + f);
        }
    }

    private void exibirFala(Dialogos dialogo, Jogador jogador) {
        console.println(dialogo.getNpc().getNome() + ": \"" + personalizar(dialogo.getFalaNPC(), jogador) + "\"");
    }

    /** O que acontece logo depois da escolha: falas e narração. */
    public void exibirReacao(Escolha escolha, Jogador jogador) {
        boolean temFalas = !escolha.getReacaoFalas().isEmpty();
        boolean temNarracao = !escolha.getReacaoNarracao().isEmpty();
        boolean temFalaJogador = escolha.getFalaDoJogador() != null;
        if (!temFalas && !temNarracao && !temFalaJogador) {
            return;
        }
        console.println();
        if (temFalaJogador) {
            console.println(jogador.getNome() + ": " + personalizar(escolha.getFalaDoJogador(), jogador));
        }
        if (temNarracao && escolha.isNarracaoAntes()) {
            console.println(personalizar(escolha.getReacaoNarracao(), jogador));
        }
        for (Dialogos fala : escolha.getReacaoFalas()) {
            exibirFala(fala, jogador);
        }
        if (temNarracao && !escolha.isNarracaoAntes()) {
            console.println(personalizar(escolha.getReacaoNarracao(), jogador));
        }
    }

    public void exibirStatus(Jogador jogador) {
        console.println("\n[Comida: " + jogador.getRecursos().getComida()
                + " | Água: " + jogador.getRecursos().getAgua()
                + " | Reputação: " + jogador.getReputacao() + "]");
    }

    /**
     * Lista as escolhas numeradas (as bloqueadas aparecem com o que falta para liberá-las), mais os
     * comandos F, S e M, e devolve o texto digitado, ou null se a entrada terminou.
     */
    public String lerAcao(Cena cena, Jogador jogador) {
        return lerAcao(cena, jogador, null);
    }

    /** Igual a {@link #lerAcao(Cena, Jogador)}, mostrando abaixo de cada opção o que ela exige e o que muda. */
    public String lerAcao(Cena cena, Jogador jogador, Rotas rotas) {
        console.println();
        List<Escolha> escolhas = cena.getEscolhas();
        for (int i = 0; i < escolhas.size(); i++) {
            Escolha escolha = escolhas.get(i);
            if (escolha.estaDisponivel(jogador)) {
                console.println((i + 1) + ". " + escolha.getTexto());
            } else {
                console.println((i + 1) + ". [BLOQUEADA] " + escolha.getTexto()
                        + " (requer: " + escolha.getCondicao().motivoDoBloqueio(jogador) + ")");
            }
            if (rotas != null) {
                String resumo = escolha.resumoParaJogador(rotas);
                if (!resumo.isEmpty()) {
                    console.println("     " + resumo);
                }
            }
        }
        console.println("F. Ver ficha do personagem");
        console.println("D. Diário (o que aconteceu até aqui)");
        if (cena.temNpc(br.colapso2071.model.Rotas.NPC_CARAMELO, jogador)) {
            console.println("P. Fazer carinho no Caramelo");
        }
        if (cena.isPontoDeSalvamento()) {
            console.println("S. Salvar jogo");
        }
        console.println("M. Voltar ao menu principal");

        console.print("\nEscolha sua ação: ");
        return console.lerLinha();
    }

    public void exibirOpcaoInvalida() {
        console.println("Opção inválida! Tente novamente.");
    }

    public void exibirOpcaoBloqueada(String motivo) {
        console.println("Essa opção está bloqueada. Requer: " + motivo + ".");
    }

    /** Ficha: perfil, atributos, recursos, inventário, relacionamentos, missões e decisões. */
    public void exibirFicha(Jogador jogador, Rotas rotas) {
        console.println("\n--- FICHA DE " + jogador.getNome().toUpperCase() + " ---");
        console.println("Perfil: " + jogador.getPerfil().getNome());
        console.println("Atributos: Liderança " + jogador.getAtributos().getLideranca()
                + " | Técnico " + jogador.getAtributos().getTecnico()
                + " | Negociação " + jogador.getAtributos().getNegociacao());
        console.println("Recursos: Comida " + jogador.getRecursos().getComida()
                + " | Água " + jogador.getRecursos().getAgua());
        console.println("Reputação: " + jogador.getReputacao());

        console.println("Inventário:");
        if (jogador.getInventario().getItens().isEmpty()) {
            console.println("  (vazio)");
        }
        for (Item item : jogador.getInventario().getItens()) {
            console.println("  - " + item.getNome() + ": " + item.getDescricao());
        }

        console.println("Relacionamentos:");
        if (jogador.getRelacionamentos().getAfinidades().isEmpty()) {
            console.println("  (ninguém ainda)");
        }
        for (Map.Entry<String, Integer> e : jogador.getRelacionamentos().getAfinidades().entrySet()) {
            String nome = rotas.getNpc(e.getKey()) != null ? rotas.getNpc(e.getKey()).getNome() : e.getKey();
            console.println("  - " + nome + ": " + (e.getValue() > 0 ? "+" : "") + e.getValue());
        }

        console.println("Missões:");
        if (jogador.getMissoes().isEmpty()) {
            console.println("  (nenhuma)");
        }
        for (Map.Entry<String, StatusMissao> e : jogador.getMissoes().entrySet()) {
            String titulo = rotas.getMissao(e.getKey()) != null ? rotas.getMissao(e.getKey()).getTitulo() : e.getKey();
            console.println("  - " + titulo + " [" + (e.getValue() == StatusMissao.CONCLUIDA ? "concluída" : "em andamento") + "]");
        }

        console.println("Decisões tomadas (" + jogador.getDecisoes().size() + "):");
        List<Decisao> decisoes = jogador.getDecisoes();
        if (decisoes.isEmpty()) {
            console.println("  (nenhuma)");
        }
        for (Decisao d : decisoes) {
            Cena cena = rotas.getCena(d.cenaId());
            if (cena != null && d.indiceEscolha() < cena.getEscolhas().size()) {
                console.println("  - Cap. " + cena.getNumeroCapitulo() + ": "
                        + cena.getEscolhas().get(d.indiceEscolha()).getTexto());
            }
        }
    }

    public boolean confirmarSairDaPartida() {
        return console.confirmar("Voltar ao menu? O progresso não salvo será perdido");
    }

    public void exibirAutosave() {
        console.println("(Progresso salvo automaticamente.)");
    }

    public void exibirConquistasDesbloqueadas(List<Conquista> conquistas) {
        for (Conquista c : conquistas) {
            console.println("\n*** Conquista desbloqueada: " + c.getTitulo() + " - " + c.getDescricao());
        }
    }

    public void exibirNovoFinal(Final alcancado) {
        console.println("*** Novo final registrado: " + alcancado.getTitulo());
    }

    public void exibirFinal(Jogador jogador, Final finalAlcancado) {
        console.println("\n=================================================");
        console.println("FIM DE JOGO");
        console.println("Reputação final acumulada: " + jogador.getReputacao());
        console.println("\nTítulo: " + finalAlcancado.getTitulo());
        console.println("Desfecho: " + finalAlcancado.getMensagemEncerramento());
        if (!finalAlcancado.getEpilogo().isEmpty()) {
            console.println("\n" + personalizar(finalAlcancado.getEpilogo(), jogador));
        }
        console.println("=================================================");
    }

    public void exibirErro(String mensagem) {
        console.println("[ERRO] " + mensagem);
    }
}
