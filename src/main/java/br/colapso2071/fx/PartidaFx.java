package br.colapso2071.fx;

import br.colapso2071.excecao.PersistenciaException;
import br.colapso2071.model.Cena;
import br.colapso2071.model.Conquista;
import br.colapso2071.model.Escolha;
import br.colapso2071.model.Final;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Perfil;
import br.colapso2071.persistencia.ResultadoCarga;
import br.colapso2071.persistencia.SaveRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Controla uma partida na interface gráfica: faz o papel do JogoController do console, mas
 * em vez de pedir texto à View, monta a tela de cena e reage aos cliques. Toda regra de jogo
 * continua no Model ({@link Jogo}); aqui só se decide qual tela mostrar e quando salvar.
 */
public final class PartidaFx {
    private final ContextoFx ctx;
    private final Navegador nav;
    private final Runnable aoSairParaMenu;
    private Jogo jogo;
    /** Última cena que já gerou salvamento automático; evita regravar ao redesenhar a mesma cena (ex.: voltar da ficha). */
    private String ultimaCenaAutosalva;
    /** Avisos (efeitos da escolha, salvamento) que serão exibidos na próxima tela de cena. */
    private final List<String> avisos = new ArrayList<>();
    /** O jogador dormiu no meio de uma cena: ao voltar, o personagem que esperava a resposta está na porta. */
    private boolean acordouComRespostaPendente;

    public PartidaFx(ContextoFx ctx, Navegador nav, Runnable aoSairParaMenu) {
        this.ctx = ctx;
        this.nav = nav;
        this.aoSairParaMenu = aoSairParaMenu;
    }

    /** Começa do zero: mostra a introdução e entra na primeira cena. */
    public void novaPartida(String nomeJogador, Perfil perfil) {
        jogo = new Jogo(ctx.rotas());
        ultimaCenaAutosalva = null;
        jogo.iniciar(nomeJogador, perfil);
        List<String> intro = new ArrayList<>();
        for (String p : ctx.rotas().getIntroducao()) {
            intro.add(Roteiro.personalizar(p, jogo.getJogador()));
        }
        Runnable comoJogar = () -> nav.mostrar(new TelaTexto("Como jogar", Instrucoes.inicio(), "Começar a jogar",
                this::mostrarCena).construir());
        nav.mostrar(new TelaTexto("Colapso 2071", intro, "Continuar", comoJogar).construir());
    }

    /** Retoma uma partida já carregada de um save. */
    public void retomar(Jogo carregado) {
        jogo = carregado;
        ultimaCenaAutosalva = carregado.getCenaAtual() != null ? carregado.getCenaAtual().getId() : null;
        mostrarCena();
    }

    // ---------- cena ----------

    private void mostrarCena() {
        mostrarCena(true);
    }

    /** Volta às opções da cena atual sem repetir o texto (depois de retomar uma conversa). */
    private void mostrarOpcoesDaCena() {
        mostrarCena(false);
    }

    private void mostrarCena(boolean comTexto) {
        if (!jogo.emAndamento()) {
            aoSairParaMenu.run();
            return;
        }
        Cena cena = jogo.getCenaAtual();
        Musica.tocar(Trilha.daCena(cena));
        List<Pagina> paginas;
        if (comTexto) {
            paginas = Roteiro.daCena(cena, jogo.getJogador());
        } else if (acordouComRespostaPendente) {
            // Dormiu no meio da cena: quem esperava a resposta está na porta, no dia seguinte
            acordouComRespostaPendente = false;
            paginas = Roteiro.daEsperaNaPorta(cena, jogo.getJogador());
        } else {
            paginas = Roteiro.daPergunta(cena, jogo.getJogador());
        }
        boolean levaAoFinal = jogo.cenaAtualLevaAoFinal();

        // Evento relevante: salvamento automático (igual ao console), exceto na cena que encerra.
        if (!levaAoFinal && cena.isAutosave() && ctx.configuracoes().isSalvamentoAutomatico()
                && !cena.getId().equals(ultimaCenaAutosalva)) {
            ultimaCenaAutosalva = cena.getId();
            if (salvarAutomatico()) {
                avisos.add("Progresso salvo automaticamente");
            }
        }

        List<TelaCena.Opcao> opcoes = new ArrayList<>();
        if (levaAoFinal) {
            opcoes.add(new TelaCena.Opcao("Ver o final", "", this::encerrar));
        } else {
            // Sede ou fome: as escolhas ficam travadas até o jogador cuidar de si. As ações que resolvem
            // aparecem primeiro na lista, para ninguém ficar preso sem saber o que fazer.
            String necessidade = jogo.motivoDasNecessidades();
            for (Jogo.Necessidade n : jogo.necessidades()) {
                opcoes.add(acaoParaNecessidade(n));
                avisos.add(n.getMensagem());
                // Na primeira vez, Milena ensina como resolver
                if (jogo.getJogador().registrarConversa("T:" + n.name())) {
                    List<Pagina> comTutorial = new ArrayList<>(paginas);
                    comTutorial.addAll(Roteiro.daTutorial(n, jogo.getJogador()));
                    paginas = comTutorial;
                }
            }
            for (int i = 0; i < cena.getEscolhas().size(); i++) {
                final int indice = i;
                String bloqueio = jogo.escolhaBloqueada(i) ? "Bloqueada: falta " + jogo.motivoDoBloqueio(i)
                        : (necessidade.isEmpty() ? "" : "Cuide de você antes de seguir.");
                opcoes.add(new TelaCena.Opcao(cena.getEscolhas().get(i).getTexto(), bloqueio, () -> escolher(indice),
                        cena.getEscolhas().get(i).resumoParaJogador(ctx.rotas())));
            }
            // O Caramelo está na cena: dá para fazer carinho nele (uma vez por dia)
            if (jogo.carameloNaCena()) {
                opcoes.add(new TelaCena.Opcao("Fazer carinho no Caramelo", "",
                        () -> fazerCarinho(Arte.fundo(cena), this::mostrarOpcoesDaCena), "Não avança a história · uma vez por dia"));
            }
            // Lembrança do capítulo: liberada pela afinidade com o personagem
            br.colapso2071.model.Lembranca lembranca = jogo.lembrancaDaCena();
            if (lembranca != null) {
                opcoes.add(new TelaCena.Opcao("Lembrança: " + lembranca.titulo(),
                        lembranca.motivoBloqueio(jogo.getJogador()), this::ouvirLembranca,
                        "Não avança a história · Afinidade com " + lembranca.npc().getNome() + " +1"));
            }
            // Conversas que ficaram para trás na cena anterior: dá para voltar ao assunto sem avançar a história
            List<Jogo.ConversaPendente> pendentes = jogo.conversasPendentes();
            for (int k = 0; k < pendentes.size(); k++) {
                final int pos = k;
                opcoes.add(new TelaCena.Opcao("Retomar conversa: " + pendentes.get(k).escolha().getTextoRetomar(), "",
                        () -> retomarConversa(pos),
                        "Não avança a história · " + pendentes.get(k).escolha().resumoParaJogador(ctx.rotas())));
            }
        }

        TelaCena tela = new TelaCena(ctx.rotas(), jogo.getJogador(), Arte.fundo(cena), rotuloDoCapitulo(cena),
                paginas, opcoes, () -> { }, atalhos(cena, this::mostrarOpcoesDaCena));
        tela.comObjetivo(jogo.objetivoAtual());
        tela.alertar(jogo.motivoDasNecessidades());
        tela.comRoupaDaViagem(Jogo.ID_CENA_INICIAL.equals(cena.getId()));
        entregarAvisos(tela);
        nav.mostrar(tela.construir());
    }

    /** Ação direta que resolve uma necessidade, mostrada no topo da lista de opções. */
    private TelaCena.Opcao acaoParaNecessidade(Jogo.Necessidade n) {
        return switch (n) {
            case SEDE -> jogo.motivoParaNaoEncher().isEmpty() || jogo.getJogador().getInventario().possuiItem(br.colapso2071.model.Rotas.ITEM_GARRAFA)
                    ? new TelaCena.Opcao("Se hidratar: ir ao poço do quintal, encher a garrafa térmica e beber", "",
                            this::hidratar, "Água +" + Jogo.AGUA_DA_GARRAFA + " · a garrafa só pode ser enchida uma vez por dia")
                    : new TelaCena.Opcao("Descansar até amanhã para encher a garrafa de novo", "",
                            () -> dormir(this::mostrarOpcoesDaCena), "A garrafa de hoje já foi usada · o dia gasta água e comida");
            case FOME -> jogo.motivoParaNaoBuscarComida().isEmpty() || jogo.getJogador().getInventario().possuiItem(br.colapso2071.model.Rotas.ITEM_MARMITA)
                    ? new TelaCena.Opcao("Se alimentar: ir ao depósito do Celeiro Velho, pegar uma marmita e comer", "",
                            this::alimentar, "Comida +" + Jogo.COMIDA_DA_MARMITA + " · uma marmita por dia")
                    : new TelaCena.Opcao("Descansar até amanhã para pegar outra marmita", "",
                            () -> dormir(this::mostrarOpcoesDaCena), "A marmita de hoje já foi entregue · o dia gasta água e comida");
        };
    }

    private void hidratar() {
        Efeitos.Foto antes = Efeitos.fotografar(jogo.getJogador());
        boolean foiAoPoco = !jogo.getJogador().getInventario().possuiItem(br.colapso2071.model.Rotas.ITEM_GARRAFA)
                && jogo.encherGarrafa();
        jogo.usarItem(br.colapso2071.model.Rotas.ITEM_GARRAFA);
        avisos.add("Você bebeu da garrafa térmica. A garrafa ficou vazia.");
        avisos.addAll(Efeitos.comparar(antes, jogo.getJogador(), ctx.rotas()));
        if (foiAoPoco) {
            mostrarLocal("cena_quintal.png", "Poço do quintal", List.of(NARRACAO_POCO, NARRACAO_POCO_2,
                    "Você bebe devagar, sentado na beira do poço. Água +" + Jogo.AGUA_DA_GARRAFA + "."),
                    this::mostrarOpcoesDaCena);
        } else {
            mostrarOpcoesDaCena();
        }
    }

    private void alimentar() {
        Efeitos.Foto antes = Efeitos.fotografar(jogo.getJogador());
        boolean foiAoDeposito = !jogo.getJogador().getInventario().possuiItem(br.colapso2071.model.Rotas.ITEM_MARMITA)
                && jogo.buscarComida();
        jogo.usarItem(br.colapso2071.model.Rotas.ITEM_MARMITA);
        avisos.add("Você comeu a marmita.");
        avisos.addAll(Efeitos.comparar(antes, jogo.getJogador(), ctx.rotas()));
        if (foiAoDeposito) {
            mostrarLocal("cena_deposito.png", "Depósito do Celeiro Velho", List.of(NARRACAO_DEPOSITO, NARRACAO_DEPOSITO_2,
                    "Você come ali mesmo, na sombra da parede do celeiro. Comida +" + Jogo.COMIDA_DA_MARMITA + "."),
                    this::mostrarOpcoesDaCena);
        } else {
            mostrarOpcoesDaCena();
        }
    }

    private static final String NARRACAO_POCO = "Você vai até o quintal da casa azul. A corda range na roldana e o balde "
            + "sobe pesado, com a água fria do fundo do poço.";
    private static final String NARRACAO_POCO_2 = "Você enche a garrafa térmica até a boca. É a cota de hoje: o poço é "
            + "raso e só dá isso por dia.";
    private static final String NARRACAO_DEPOSITO = "O depósito do Celeiro Velho abre cedo. Na mesa da porta, as marmitas "
            + "embrulhadas em pano esperam em fila, e o caderno das cotas está aberto.";
    private static final String NARRACAO_DEPOSITO_2 = "Você diz o seu nome, alguém risca uma linha no caderno e entrega a "
            + "sua marmita: feijão com farinha e um pedaço de abóbora.";

    /**
     * Mostra um lugar do povoado (o poço do quintal, o depósito) com o protagonista em cena e alguns parágrafos de
     * narração; ao terminar, segue para {@code depois}.
     */
    private void mostrarLocal(String fundo, String titulo, List<String> narracao, Runnable depois) {
        List<Pagina> paginas = new ArrayList<>();
        paginas.add(Pagina.titulo(titulo));
        for (String n : narracao) {
            paginas.add(Pagina.narracao(n));
        }
        mostrarPaginas(fundo, paginas, depois);
    }

    /** Mostra páginas soltas (sem opções) sobre um cenário e, ao terminar, segue para {@code depois}. */
    private void mostrarPaginas(String fundo, List<Pagina> paginas, Runnable depois) {
        Cena cena = jogo.getCenaAtual();
        TelaCena tela = new TelaCena(ctx.rotas(), jogo.getJogador(), fundo,
                cena == null ? "" : rotuloDoCapitulo(cena), paginas, List.of(), depois,
                atalhos(cena, () -> mostrarPaginas(fundo, paginas, depois)));
        tela.comObjetivo(jogo.objetivoAtual());
        entregarAvisos(tela);
        nav.mostrar(tela.construir());
    }

    /**
     * Carinho no Caramelo: ele faz festa para quem tem boa reputação, desconfia de quem ainda não mostrou quem é e
     * morde quem anda fazendo mal ao povoado. Uma vez por dia; não avança a história.
     */
    private void fazerCarinho(String fundo, Runnable depois) {
        Efeitos.Foto antes = Efeitos.fotografar(jogo.getJogador());
        Jogo.Carinho r = jogo.fazerCarinho();
        avisos.addAll(Efeitos.comparar(antes, jogo.getJogador(), ctx.rotas()));
        String cao = br.colapso2071.model.Rotas.NPC_CARAMELO;
        List<Pagina> p = new ArrayList<>();
        p.add(Pagina.narracao("Você se abaixa e estende a mão para o Caramelo."));
        switch (r) {
            case FESTA -> p.add(Pagina.fala(cao, "Au! Au! (pula nas suas pernas e lambe a sua mão)"));
            case DESCONFIADO -> p.add(Pagina.fala(cao, "(cheira a sua mão devagar, com as orelhas para trás)"));
            case MORDIDA -> p.add(Pagina.fala(cao, "Grrr... (rosna, mostra os dentes e morde)"));
            case JA_HOJE -> p.add(Pagina.fala(cao, "(abre um olho, abana a ponta do rabo e volta a cochilar)"));
            case AUSENTE -> { }
        }
        p.add(Pagina.narracao(r.getMensagem()));
        mostrarPaginas(fundo, p, depois);
    }

    private void ouvirLembranca() {
        br.colapso2071.model.Lembranca l = jogo.ouvirLembranca();
        if (l == null) {
            mostrarOpcoesDaCena();
            return;
        }
        avisos.add("Afinidade com " + l.npc().getNome() + " +1");
        Cena cena = jogo.getCenaAtual();
        TelaCena tela = new TelaCena(ctx.rotas(), jogo.getJogador(), Arte.fundo(cena), rotuloDoCapitulo(cena),
                Roteiro.daLembranca(l, jogo.getJogador()), List.of(), this::mostrarOpcoesDaCena,
                atalhos(cena, this::mostrarOpcoesDaCena));
        tela.comObjetivo(jogo.objetivoAtual());
        entregarAvisos(tela);
        nav.mostrar(tela.construir());
    }

    private void retomarConversa(int k) {
        Efeitos.Foto antes = Efeitos.fotografar(jogo.getJogador());
        Escolha e = jogo.retomarConversa(k);
        if (e == null) {
            mostrarOpcoesDaCena();
            return;
        }
        avisos.addAll(Efeitos.comparar(antes, jogo.getJogador(), ctx.rotas()));
        List<Pagina> falas = Roteiro.daConversaRetomada(e, jogo.getJogador());
        if (falas.isEmpty()) {
            mostrarOpcoesDaCena();
            return;
        }
        Cena cena = jogo.getCenaAtual();
        TelaCena tela = new TelaCena(ctx.rotas(), jogo.getJogador(), Arte.fundo(cena), rotuloDoCapitulo(cena),
                falas, List.of(), this::mostrarOpcoesDaCena, atalhos(cena, this::mostrarOpcoesDaCena));
        tela.comObjetivo(jogo.objetivoAtual());
        entregarAvisos(tela);
        nav.mostrar(tela.construir());
    }

    private void escolher(int indice) {
        Efeitos.Foto antes = Efeitos.fotografar(jogo.getJogador());
        Cena deixada = jogo.getCenaAtual();
        if (!jogo.escolher(indice)) {
            return;
        }
        avisos.addAll(Efeitos.comparar(antes, jogo.getJogador(), ctx.rotas()));
        Escolha feita = jogo.getUltimaEscolha();
        registrarProgresso();
        List<Pagina> reacao = new ArrayList<>(Roteiro.daReacao(feita, jogo.getJogador()));
        // Última cena do capítulo: a conclusão fecha o capítulo e liga ao próximo
        Cena seguinte = jogo.getCenaAtual();
        if (seguinte == null || seguinte.getNumeroCapitulo() != deixada.getNumeroCapitulo()) {
            reacao.addAll(Roteiro.daConclusao(deixada, jogo.getJogador()));
        }
        if (reacao.isEmpty()) {
            mostrarCena();
            return;
        }
        // A reação aparece sobre o cenário da cena anterior; o jogo já avançou, então usa o cenário da nova.
        Cena atual = jogo.getCenaAtual();
        String fundo = atual != null ? Arte.fundo(atual) : "cena_praca.png";
        String capitulo = atual != null ? rotuloDoCapitulo(atual) : "";
        mostrarReacao(reacao, fundo, capitulo, atual);
    }

    private void mostrarReacao(List<Pagina> reacao, String fundo, String capitulo, Cena atual) {
        Runnable voltar = () -> mostrarReacao(reacao, fundo, capitulo, atual);
        TelaCena tela = new TelaCena(ctx.rotas(), jogo.getJogador(), fundo, capitulo,
                reacao, List.of(), this::mostrarCena, atalhos(atual, voltar));
        tela.comObjetivo(jogo.objetivoAtual());
        entregarAvisos(tela);
        nav.mostrar(tela.construir());
    }

    private String rotuloDoCapitulo(Cena cena) {
        return "Cap. " + cena.getNumeroCapitulo() + " · " + cena.getTituloCapitulo() + " · Dia " + jogo.getJogador().getDia();
    }

    // ---------- ficha, salvar, menu ----------

    /** Ações disponíveis em qualquer momento da partida (HUD e teclado): mochila, ficha, casa, salvar, carregar, ajuda e pausa. */
    private TelaCena.Atalhos atalhos(Cena cena, Runnable voltarParaTela) {
        return new TelaCena.Atalhos(
                () -> nav.mostrar(new TelaFicha(ctx.rotas(), jogo.getJogador(), voltarParaTela).construir()),
                () -> mostrarMochila(voltarParaTela, null),
                () -> mostrarCasa(voltarParaTela),
                () -> salvarManual(voltarParaTela),
                () -> carregarDaPartida(voltarParaTela),
                () -> mostrarInstrucoes(voltarParaTela),
                () -> mostrarPausa(voltarParaTela));
    }

    // ---------- mochila, casa e instruções ----------

    private void mostrarMochila(Runnable voltar, String mensagem) {
        mostrarMochila(voltar, mensagem, null);
    }

    private void mostrarMochila(Runnable voltar, String mensagem, String itemSelecionado) {
        nav.mostrar(new TelaMochila(jogo.getJogador(), id -> usarItem(id, voltar), voltar, mensagem, itemSelecionado).construir());
    }

    private void usarItem(String itemId, Runnable voltar) {
        br.colapso2071.model.Item item = ctx.rotas().getItem(itemId);
        br.colapso2071.model.Uso uso = item != null ? item.getUso() : null;
        Runnable volta = () -> mostrarMochila(voltar, null, itemId);
        if (uso != null && uso.imagem() != null) {
            // A foto abre em tela cheia
            nav.mostrar(new TelaImagem(item.getNome(), uso.imagem(), uso.mensagem(), volta).construir());
            return;
        }
        if (uso != null && !uso.paginas().isEmpty()) {
            // O caderno abre para leitura, página por página
            List<String> texto = new ArrayList<>();
            texto.add(uso.mensagem());
            texto.addAll(uso.paginas());
            nav.mostrar(new TelaTexto(item.getNome(), texto, "Fechar o caderno", volta).construir());
            return;
        }
        Efeitos.Foto antes = Efeitos.fotografar(jogo.getJogador());
        String msg = jogo.usarItem(itemId);
        if (msg == null) {
            String falta = uso != null ? uso.faltaPara(jogo.getJogador().getRecursos()) : "";
            mostrarMochila(voltar, falta.isEmpty() ? "Esse item não pode ser usado agora." : falta, itemId);
            return;
        }
        avisos.addAll(Efeitos.comparar(antes, jogo.getJogador(), ctx.rotas()));
        mostrarMochila(voltar, msg, itemId);
    }

    private void mostrarCasa(Runnable voltar) {
        if (!jogo.temCasa()) {
            aviso("Casa", jogo.motivoParaNaoDormir());
            voltar.run();
            return;
        }
        Runnable casa = () -> mostrarCasa(voltar);
        nav.mostrar(new TelaCasa(jogo, () -> dormir(voltar), () -> encherGarrafa(voltar), () -> buscarComida(voltar),
                () -> fazerCarinho("cena_casa.png", casa), () -> mostrarMochila(casa, null), voltar).construir());
    }

    private void buscarComida(Runnable voltar) {
        if (!jogo.buscarComida()) {
            mostrarCasa(voltar);
            return;
        }
        avisos.add("Item adicionado ao inventário: Marmita de Feijão");
        mostrarLocal("cena_deposito.png", "Depósito do Celeiro Velho", List.of(NARRACAO_DEPOSITO, NARRACAO_DEPOSITO_2,
                "Você guarda a marmita na mochila e volta para casa. Coma pela mochila (tecla I) quando precisar."),
                () -> mostrarCasa(voltar));
    }

    private void encherGarrafa(Runnable voltar) {
        if (!jogo.encherGarrafa()) {
            mostrarCasa(voltar);
            return;
        }
        avisos.add("Garrafa térmica cheia");
        mostrarLocal("cena_quintal.png", "Poço do quintal", List.of(NARRACAO_POCO, NARRACAO_POCO_2,
                "Você fecha a garrafa e volta para dentro. Beba pela mochila (tecla I) quando precisar."),
                () -> mostrarCasa(voltar));
    }

    private void dormir(Runnable voltar) {
        Efeitos.Foto antes = Efeitos.fotografar(jogo.getJogador());
        if (!jogo.dormir()) {
            mostrarCasa(voltar);
            return;
        }
        avisos.add("Você dormiu. Começa o dia " + jogo.getJogador().getDia() + ".");
        acordouComRespostaPendente = jogo.getCenaAtual() != null && jogo.getCenaAtual().getPergunta() != null;
        avisos.addAll(Efeitos.comparar(antes, jogo.getJogador(), ctx.rotas()));
        mostrarLocal("cena_casa.png", "Amanhece em Bom-Jardim", List.of(
                "Você dorme na rede da casa azul. De madrugada, o vento traz cheiro de terra seca e o barulho das cabras de Sandro.",
                "Quando acorda, o sol já entra pela janela da professora Lúcia. É o dia " + jogo.getJogador().getDia() + "."),
                voltar);
    }

    private void mostrarInstrucoes(Runnable voltar) {
        nav.mostrar(new TelaTexto("Instruções", Instrucoes.paragrafos(), voltar).construir());
    }

    private void mostrarPausa(Runnable voltarParaTela) {
        Runnable pausa = () -> mostrarPausa(voltarParaTela);
        List<AcaoMenu> acoes = List.of(
                AcaoMenu.de("1", "Continuar", AcaoMenu.Estilo.PRIMARIO, voltarParaTela),
                AcaoMenu.de("2", "Salvar jogo", AcaoMenu.Estilo.NORMAL, () -> salvarManual(pausa)),
                AcaoMenu.de("3", "Diário e objetivo", AcaoMenu.Estilo.NORMAL,
                        () -> nav.mostrar(new TelaTexto("Diário", jogo.diario(), pausa).construir())),
                AcaoMenu.de("4", "Instruções", AcaoMenu.Estilo.NORMAL, () -> mostrarInstrucoes(pausa)),
                AcaoMenu.de("5", "Carregar jogo", AcaoMenu.Estilo.NORMAL, () -> carregarDaPartida(pausa)),
                AcaoMenu.de("6", "Reiniciar partida", AcaoMenu.Estilo.NORMAL, () -> confirmarReiniciar(pausa)),
                AcaoMenu.de("7", "Voltar ao menu principal", AcaoMenu.Estilo.NORMAL, () -> confirmarSairParaMenu(pausa)),
                AcaoMenu.de("8", "Sair do jogo", AcaoMenu.Estilo.PERIGO, () -> confirmarSairDoJogo(pausa)));
        Cena atual = jogo.getCenaAtual();
        nav.mostrar(new TelaPausa(acoes, atual == null ? null : rotuloDoCapitulo(atual), jogo.objetivoAtual()).construir());
    }

    private void carregarDaPartida(Runnable voltar) {
        nav.mostrar(new TelaSaves(ctx, "Carregar jogo", true, slot -> {
            if (!confirmar("Carregar este save? O progresso desde o último salvamento será perdido.")) {
                carregarDaPartida(voltar);
                return;
            }
            try {
                ResultadoCarga r = ctx.saves().carregarComRecuperacao(slot);
                if (r.recuperadoDoBackup()) {
                    aviso("Save recuperado", "O save principal estava danificado; foi usado o backup.");
                }
                avisos.clear();
                retomar(r.jogo());
            } catch (PersistenciaException e) {
                aviso("Não foi possível carregar", e.getMessage());
                carregarDaPartida(voltar);
            }
        }, voltar).construir());
    }

    private void confirmarSairDoJogo(Runnable voltar) {
        salvarContinuo();
        if (confirmar("Sair do jogo? A partida fica guardada no salvamento automático (Continuar).")) {
            nav.fechar();
        } else {
            voltar.run();
        }
    }

    private void salvarManual(Runnable voltar) {
        nav.mostrar(new TelaSaves(ctx, "Salvar jogo", false, true, slot -> {
            if (ctx.saves().existe(slot) && !confirmar("Sobrescrever o save " + SaveRepository.nomeDoSlot(slot) + "?")) {
                salvarManual(voltar);
                return;
            }
            try {
                ctx.saves().salvar(slot, jogo);
                avisos.add("Jogo salvo em " + SaveRepository.nomeDoSlot(slot));
            } catch (PersistenciaException e) {
                aviso("Não foi possível salvar", e.getMessage());
            }
            voltar.run();
        }, voltar).construir());
    }

    private boolean salvarAutomatico() {
        try {
            ctx.saves().salvarAutomatico(jogo);
            return true;
        } catch (PersistenciaException e) {
            // Falha no autosave não interrompe a partida; o jogador ainda pode salvar à mão.
            avisos.add("Não foi possível salvar automaticamente");
            return false;
        }
    }

    private void entregarAvisos(TelaCena tela) {
        if (jogo != null) {
            avisos.addAll(jogo.consumirPenalidades());
        }
        tela.avisar(new ArrayList<>(avisos));
        avisos.clear();
        // Toda vez que a tela muda depois de uma ação, o progresso já está gravado no disco
        salvarContinuo();
    }

    /** Partida em andamento, para a janela salvar se for fechada de repente. */
    private static PartidaFx ativa;

    /** Chamado quando a janela é fechada: grava a partida em andamento no salvamento automático. */
    public static void salvarAoFechar() {
        if (ativa != null) {
            ativa.salvarContinuo();
        }
    }

    /**
     * Salvamento contínuo: grava a partida no espaço automático depois de cada ação, com escrita atômica e
     * sincronizada no disco. Se o computador desligar, "Continuar" volta para a última decisão tomada.
     */
    private void salvarContinuo() {
        if (jogo == null || !jogo.emAndamento() || !ctx.configuracoes().isSalvamentoAutomatico()) {
            return;
        }
        ativa = this;
        try {
            ctx.saves().salvarAutomatico(jogo);
        } catch (PersistenciaException e) {
            System.err.println("Salvamento contínuo falhou: " + e.getMessage());
        }
    }

    private void confirmarReiniciar(Runnable voltarParaTela) {
        if (confirmar("Reiniciar a partida do começo? O progresso desde o último salvamento será perdido.")) {
            novaPartida(jogo.getJogador().getNome(), jogo.getJogador().getPerfil());
        } else {
            voltarParaTela.run();
        }
    }

    private void confirmarSairParaMenu(Runnable voltarParaTela) {
        salvarContinuo();
        if (confirmar("Voltar ao menu? A partida fica guardada no salvamento automático (Continuar).")) {
            aoSairParaMenu.run();
        } else {
            voltarParaTela.run();
        }
    }

    // ---------- progresso e final ----------

    private void registrarProgresso() {
        if (!registrarConquistas().isEmpty()) {
            persistirProgresso();
        }
    }

    private List<Conquista> registrarConquistas() {
        List<Conquista> novas = new ArrayList<>();
        for (String id : ctx.progresso().registrarConquistas(jogo.getJogador().getDesbloqueios())) {
            Conquista c = ctx.rotas().getConquista(id);
            if (c != null) {
                novas.add(c);
            }
        }
        return novas;
    }

    private void encerrar() {
        Final alcancado = jogo.calcularFinal();
        boolean novoFinal = ctx.progresso().registrarFinal(alcancado.getId());
        List<Conquista> novas = registrarConquistas();
        ctx.progresso().registrarPartidaConcluida();
        persistirProgresso();
        Musica.tocar(Trilha.doFinal(alcancado));
        nav.mostrar(new TelaFinal(alcancado, novoFinal, novas, aoSairParaMenu).construir());
    }

    private void persistirProgresso() {
        try {
            ctx.progressoRepo().salvar(ctx.progresso());
        } catch (PersistenciaException e) {
            aviso("Não foi possível registrar o progresso", e.getMessage());
        }
    }

    // ---------- diálogos ----------

    private boolean confirmar(String texto) {
        return Navegador.confirmar(texto);
    }

    private void aviso(String titulo, String texto) {
        Navegador.avisar(titulo, texto);
    }
}
