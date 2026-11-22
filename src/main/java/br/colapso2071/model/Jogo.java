package br.colapso2071.model;

import java.util.List;

/**
 * Model principal: guarda o estado da partida (jogador, cena atual, estado)
 * e aplica as regras do jogo. Não faz leitura nem impressão no console.
 */
public class Jogo {
    public static final String ID_CENA_INICIAL = "CENA_1A";
    /** Opção da CENA_10A que leva o Coronel e a milícia a julgamento. */
    public static final int ESCOLHA_TRIBUNAL = 5;
    /** Dormir recupera energia, mas a noite consome um pouco de comida e água. */
    /** Consumo de cada dia novo (ao dormir ou quando um capítulo termina): obriga a beber e comer uma vez por dia. */
    public static final int COMIDA_AO_DORMIR = -20;
    public static final int AGUA_AO_DORMIR = -20;
    /** Quanto a garrafa térmica cheia devolve de água ao beber. */
    public static final int AGUA_DA_GARRAFA = 45;
    /** Quanto a marmita do depósito devolve de comida. */
    public static final int COMIDA_DA_MARMITA = 45;
    public static final String ID_VERIFICAR_FINAL = "VERIFICAR_FINAL";

    /** Reputação mínima para o final bom e máxima para o final ruim; o meio é o final neutro. */
    public static final int REPUTACAO_FINAL_BOM = 15;
    public static final int REPUTACAO_FINAL_RUIM = -10;

    private final Rotas rotas;
    private Jogador jogador;
    private Cena cenaAtual;
    private EstadoJogo estado;
    private Escolha ultimaEscolha;

    public Jogo() {
        this(new Rotas());
    }

    public Jogo(Rotas rotas) {
        this.rotas = rotas;
        this.estado = EstadoJogo.MENU;
    }

    public void iniciar(String nomeJogador) {
        iniciar(nomeJogador, Perfil.PADRAO);
    }

    public void iniciar(String nomeJogador, Perfil perfil) {
        this.jogador = new Jogador(nomeJogador, perfil);
        this.ultimaEscolha = null;
        this.cenaAtual = rotas.getCena(ID_CENA_INICIAL); // Inicia o MVP no Capítulo 1
        this.estado = (cenaAtual != null) ? EstadoJogo.EM_JOGO : EstadoJogo.FINALIZADO;
    }

    /**
     * Retoma uma partida a partir de um estado já reconstruído (ex.: lido de um save).
     *
     * @throws IllegalArgumentException se a cena não existir
     */
    public void restaurar(Jogador jogador, String cenaId) {
        Cena cena = rotas.getCena(cenaId);
        if (jogador == null || cena == null) {
            throw new IllegalArgumentException("Estado inválido para restaurar a partida: " + cenaId);
        }
        this.jogador = jogador;
        this.cenaAtual = cena;
        this.ultimaEscolha = null;
        this.estado = EstadoJogo.EM_JOGO;
    }

    public boolean emAndamento() {
        return estado == EstadoJogo.EM_JOGO && cenaAtual != null;
    }

    /** A cena atual é a de transição para o final (sua primeira escolha leva a VERIFICAR_FINAL)? */
    public boolean cenaAtualLevaAoFinal() {
        if (cenaAtual == null) {
            return false;
        }
        List<Escolha> escolhas = cenaAtual.getEscolhas();
        return !escolhas.isEmpty()
                && ID_VERIFICAR_FINAL.equals(escolhas.get(0).getProximaCenaId());
    }

    /**
     * Executa a escolha de índice {@code indice} (base 0) na cena atual:
     * registra a decisão, aplica a consequência e avança para a próxima cena.
     *
     * @return false se não há partida em andamento, o índice é inválido ou a escolha está bloqueada
     */
    public boolean escolher(int indice) {
        if (!emAndamento() || indice < 0 || indice >= cenaAtual.getEscolhas().size()) {
            return false;
        }

        Escolha escolha = cenaAtual.getEscolhas().get(indice);
        if (!escolha.estaDisponivel(jogador) || !necessidades().isEmpty()) {
            return false;
        }
        this.ultimaEscolha = escolha;
        int capituloAntes = cenaAtual.getNumeroCapitulo();
        jogador.registrarDecisao(new Decisao(cenaAtual.getId(), indice));

        if (escolha.getConsequencia() != null) {
            escolha.getConsequencia().aplicar(jogador);
        }

        this.cenaAtual = rotas.getCena(escolha.getProximaCenaId());
        if (cenaAtual == null) {
            this.estado = EstadoJogo.FINALIZADO;
        } else if (cenaAtual.getNumeroCapitulo() != capituloAntes) {
            // Entre um capítulo e outro passa um dia: o corpo gasta água e comida
            amanhecer();
        }
        return true;
    }

    // ---------- penalidades de sede e fome ----------

    /** Reputação perdida por cada noite passada com a água ou a comida em zero. */
    public static final int PENALIDADE_REPUTACAO = -2;
    private final List<String> penalidades = new java.util.ArrayList<>();

    /**
     * Passar a noite com a água ou a comida em zero, tendo como resolver naquele dia, cobra um preço: o protagonista
     * acorda fraco, irritado e trabalha mal, e o povoado percebe. Quem já usou a cota do dia e precisa descansar até
     * amanhã não é penalizado.
     *
     * @param semAgua  a água já estava em zero antes da noite
     * @param semComida a comida já estava em zero antes da noite
     */
    private void verificarPenalidades(boolean semAgua, boolean semComida) {
        jogador.marcarPenalidadeSede(semAgua);
        jogador.marcarPenalidadeFome(semComida);
        if (semAgua) {
            jogador.alterarReputacao(PENALIDADE_REPUTACAO);
            penalidades.add("Penalidade da sede: você passou a noite sem beber. A cabeça lateja e você responde mal a "
                    + "quem pede ajuda. Reputação −2.");
        }
        if (semComida) {
            jogador.alterarReputacao(PENALIDADE_REPUTACAO);
            penalidades.add("Penalidade da fome: você dormiu sem comer. As mãos tremem e o trabalho rende pela metade. "
                    + "Reputação −2.");
        }
    }

    /** Havia como beber hoje: a garrafa cheia na mochila ou o poço ainda não usado. */
    private boolean podiaBeber() {
        return jogador.getInventario().possuiItem(Rotas.ITEM_GARRAFA) || jogador.getDiaGarrafaEnchida() != jogador.getDia();
    }

    /** Havia como comer hoje: uma marmita na mochila ou o depósito ainda não visitado. */
    private boolean podiaComer() {
        return jogador.getInventario().possuiItem(Rotas.ITEM_MARMITA) || jogador.getDiaDeposito() != jogador.getDia();
    }

    // ---------- o Caramelo ----------

    /** Reputação abaixo disso: o Caramelo morde quem tenta fazer carinho. */
    public static final int REPUTACAO_PARA_MORDIDA = 0;
    /** Reputação a partir disso: o Caramelo faz festa. Entre os dois, só deixa, desconfiado. */
    public static final int REPUTACAO_PARA_FESTA = 5;

    /** Como o Caramelo reage ao carinho. */
    public enum Carinho {
        FESTA("Caramelo deita de barriga para cima e abana o rabo tão rápido que levanta poeira. Au! Au! "
                + "Quem passa na rua sorri. Reputação +1, afinidade com Caramelo +1."),
        DESCONFIADO("Caramelo cheira a sua mão por um bom tempo. Deixa o carinho, mas sem abanar o rabo: ainda está "
                + "decidindo quem você é."),
        MORDIDA("Caramelo rosna, mostra os dentes e morde a sua mão. Quem viu comenta baixinho: cachorro não se "
                + "engana com gente. Reputação −1."),
        JA_HOJE("Caramelo já ganhou carinho hoje. Ele cochila na sombra e só levanta uma orelha para você."),
        AUSENTE("O Caramelo não está por perto agora.");

        private final String mensagem;

        Carinho(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }
    }

    /** O Caramelo está na cena atual (late, fareja ou deita em algum momento dela). */
    public boolean carameloNaCena() {
        return cenaAtual != null && cenaAtual.temNpc(Rotas.NPC_CARAMELO, jogador);
    }

    /** Dá para fazer carinho no Caramelo: ele está na cena ou o jogador já tem a casa azul (ele dorme na varanda). */
    public boolean carameloPorPerto() {
        return emAndamento() && (carameloNaCena() || temCasa());
    }

    /**
     * Faz carinho no Caramelo, uma vez por dia. Ele faz festa para quem tem boa reputação, desconfia de quem ainda
     * não mostrou quem é e morde quem anda fazendo mal ao povoado (reputação negativa).
     */
    public Carinho fazerCarinho() {
        if (!carameloPorPerto()) {
            return Carinho.AUSENTE;
        }
        if (jogador.getDiaCarinho() == jogador.getDia()) {
            return Carinho.JA_HOJE;
        }
        jogador.marcarCarinho();
        int rep = jogador.getReputacao();
        if (rep < REPUTACAO_PARA_MORDIDA) {
            jogador.alterarReputacao(-1);
            jogador.desbloquear(Rotas.CONQ_MORDIDO);
            return Carinho.MORDIDA;
        }
        if (rep < REPUTACAO_PARA_FESTA) {
            return Carinho.DESCONFIADO;
        }
        jogador.alterarReputacao(1);
        jogador.getRelacionamentos().alterarAfinidade(Rotas.NPC_CARAMELO, 1);
        jogador.desbloquear(Rotas.CONQ_AMIGO_CARAMELO);
        return Carinho.FESTA;
    }

    // ---------- objetivo e diário: para quem joga pela primeira vez não se perder na história ----------

    /** A cena que abre o capítulo informado (onde ficam o objetivo e o resumo), ou null. */
    private Cena aberturaDoCapitulo(int capitulo) {
        for (Cena c : rotas.getCenas()) {
            if (c.getNumeroCapitulo() == capitulo && c.getObjetivo() != null) {
                return c;
            }
        }
        return null;
    }

    /** O objetivo do capítulo atual, em uma frase ("" se não houver partida). */
    public String objetivoAtual() {
        if (cenaAtual == null) {
            return "";
        }
        Cena abertura = aberturaDoCapitulo(cenaAtual.getNumeroCapitulo());
        return abertura == null ? "" : abertura.getObjetivo();
    }

    /**
     * Diário da partida: para cada capítulo já vivido, o que aconteceu e o que o jogador decidiu, na ordem.
     * Ajuda quem parou de jogar por uns dias a lembrar onde estava.
     */
    public List<String> diario() {
        List<String> linhas = new java.util.ArrayList<>();
        int atual = cenaAtual == null ? Integer.MAX_VALUE : cenaAtual.getNumeroCapitulo();
        for (int cap = 1; cap < atual; cap++) {
            Cena abertura = aberturaDoCapitulo(cap);
            if (abertura == null) {
                break;
            }
            StringBuilder sb = new StringBuilder("CAPÍTULO " + cap + " · " + abertura.getTituloCapitulo().toUpperCase()
                    + " — " + abertura.getResumo());
            for (Decisao d : jogador.getDecisoes()) {
                Cena c = rotas.getCena(d.cenaId());
                if (c != null && c.getNumeroCapitulo() == cap && d.indiceEscolha() < c.getEscolhas().size()) {
                    sb.append("\n• Você: ").append(c.getEscolhas().get(d.indiceEscolha()).getTexto());
                }
            }
            linhas.add(sb.toString());
        }
        if (cenaAtual != null) {
            linhas.add("AGORA · Capítulo " + cenaAtual.getNumeroCapitulo() + " · " + cenaAtual.getTituloCapitulo()
                    + " — Objetivo: " + objetivoAtual());
        }
        return linhas;
    }

    /** Penalidades aplicadas desde a última consulta (a tela mostra e a lista é esvaziada). */
    public List<String> consumirPenalidades() {
        List<String> r = List.copyOf(penalidades);
        penalidades.clear();
        return r;
    }

    /** Começa um dia novo: o dia avança e o consumo diário de água e comida é cobrado. */
    private void amanhecer() {
        // Entre capítulos não há penalidade: o jogador não teve como beber ou comer depois da última escolha.
        // A penalidade vale para quem escolhe dormir (dormir()) com sede ou fome tendo como resolver.
        Recursos rec = jogador.getRecursos();
        rec.alterarComida(COMIDA_AO_DORMIR);
        rec.alterarAgua(AGUA_AO_DORMIR);
        jogador.amanhecer();
    }

    // ---------- necessidades: sono, sede e fome ----------

    /** O que o corpo do jogador exige antes de ele seguir a história. */
    public enum Necessidade {
        SEDE("É necessário se hidratar: a sua água acabou. Encha a garrafa térmica no poço do quintal e beba."),
        FOME("É necessário se alimentar: a sua comida acabou. Pegue uma marmita no depósito do Celeiro Velho e coma.");

        private final String mensagem;

        Necessidade(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }
    }

    /** Necessidades urgentes: energia, água ou comida chegaram a zero. Enquanto houver alguma, não dá para escolher. */
    public List<Necessidade> necessidades() {
        List<Necessidade> r = new java.util.ArrayList<>();
        if (!emAndamento()) {
            return r;
        }
        Recursos rec = jogador.getRecursos();
        if (rec.getAgua() <= 0) {
            r.add(Necessidade.SEDE);
        }
        if (rec.getComida() <= 0) {
            r.add(Necessidade.FOME);
        }
        return r;
    }

    /** Texto do que falta fazer, ou vazio. */
    public String motivoDasNecessidades() {
        return String.join(" ", necessidades().stream().map(Necessidade::getMensagem).toList());
    }

    /**
     * Resolve as necessidades do jeito que o jogador faria: dormir em casa, encher e beber a garrafa,
     * buscar e comer a marmita do depósito. Usado pelo menu de console e pelos testes.
     *
     * @return o que foi feito, em frases curtas
     */
    public List<String> atenderNecessidades() {
        List<String> feito = new java.util.ArrayList<>();
        for (int volta = 0; volta < 4 && !necessidades().isEmpty(); volta++) {
            if (necessidades().contains(Necessidade.SEDE)) {
                if (!jogador.getInventario().possuiItem(Rotas.ITEM_GARRAFA) && !encherGarrafa() && dormir()) {
                    feito.add("A garrafa de hoje já foi usada. Você dorme e começa o dia " + jogador.getDia() + ".");
                    encherGarrafa();
                }
                if (usarItem(Rotas.ITEM_GARRAFA) != null) {
                    feito.add("Você puxa o balde do poço do quintal, enche a garrafa térmica e bebe.");
                }
            }
            if (necessidades().contains(Necessidade.FOME)) {
                if (!jogador.getInventario().possuiItem(Rotas.ITEM_MARMITA) && !buscarComida() && dormir()) {
                    feito.add("A marmita de hoje já foi entregue. Você dorme e começa o dia " + jogador.getDia() + ".");
                    buscarComida();
                }
                if (usarItem(Rotas.ITEM_MARMITA) != null) {
                    feito.add("Você pega uma marmita no depósito e come.");
                }
            }
        }
        return feito;
    }

    /** Vazio se pode pegar comida no depósito agora; senão, o motivo. */
    public String motivoParaNaoBuscarComida() {
        if (!temCasa()) {
            return "Você ainda não tem acesso ao depósito de Bom-Jardim.";
        }
        if (jogador.getDiaDeposito() == jogador.getDia()) {
            return "O depósito já entregou a sua marmita de hoje. Volte amanhã.";
        }
        return "";
    }

    /** Pega uma marmita no depósito comunitário: uma por dia, ou sempre que o jogador estiver com fome. */
    public boolean buscarComida() {
        if (!motivoParaNaoBuscarComida().isEmpty()) {
            return false;
        }
        jogador.getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_MARMITA));
        jogador.marcarDeposito();
        return true;
    }

    // ---------- lembranças dos personagens ----------

    /** A lembrança desta cena, se existir e ainda não tiver sido ouvida (pode estar bloqueada por afinidade). */
    public Lembranca lembrancaDaCena() {
        if (!emAndamento() || cenaAtual.getLembranca() == null) {
            return null;
        }
        Lembranca l = cenaAtual.getLembranca();
        return jogador.getConversasRetomadas().contains(l.chave()) ? null : l;
    }

    /** Ouve a lembrança (se houver afinidade): não avança a história e aproxima o jogador do personagem. */
    public Lembranca ouvirLembranca() {
        Lembranca l = lembrancaDaCena();
        if (l == null || !l.disponivelPara(jogador)) {
            return null;
        }
        jogador.registrarConversa(l.chave());
        jogador.getRelacionamentos().alterarAfinidade(l.npc().getId(), 1);
        return l;
    }

    // ---------- conversas retomáveis ----------

    /** Opção de conversa da cena anterior que o jogador não escolheu e ainda pode retomar. */
    public record ConversaPendente(String cenaId, int indice, Escolha escolha) {
        public String chave() { return cenaId + ":" + indice; }
    }

    /**
     * Conversas que o jogador deixou para trás na cena anterior (opções marcadas com {@link Escolha#conversa}
     * que não foram escolhidas) e ainda não retomou. Só valem na cena seguinte.
     */
    public List<ConversaPendente> conversasPendentes() {
        List<ConversaPendente> r = new java.util.ArrayList<>();
        List<Decisao> decisoes = emAndamento() ? jogador.getDecisoes() : List.of();
        if (decisoes.isEmpty()) {
            return r;
        }
        Decisao ultima = decisoes.get(decisoes.size() - 1);
        Cena anterior = rotas.getCena(ultima.cenaId());
        if (anterior == null) {
            return r;
        }
        for (int i = 0; i < anterior.getEscolhas().size(); i++) {
            Escolha e = anterior.getEscolhas().get(i);
            ConversaPendente p = new ConversaPendente(anterior.getId(), i, e);
            if (i != ultima.indiceEscolha() && e.isConversa() && e.estaDisponivel(jogador)
                    && !jogador.getConversasRetomadas().contains(p.chave())) {
                r.add(p);
            }
        }
        return r;
    }

    /**
     * Retoma a conversa pendente de posição {@code k}: aplica o efeito da opção, sem avançar de cena
     * e sem contar como decisão. Devolve a opção (para mostrar a reação) ou null se não existe.
     */
    public Escolha retomarConversa(int k) {
        List<ConversaPendente> pendentes = conversasPendentes();
        if (k < 0 || k >= pendentes.size()) {
            return null;
        }
        ConversaPendente p = pendentes.get(k);
        jogador.registrarConversa(p.chave());
        if (p.escolha().getConsequencia() != null) {
            p.escolha().getConsequencia().aplicar(jogador);
        }
        return p.escolha();
    }

    // ---------- casa e mochila ----------

    /** A casa é cedida por Milena depois da primeira cena; daí em diante o jogador pode voltar a ela. */
    public boolean temCasa() {
        return emAndamento() && !ID_CENA_INICIAL.equals(cenaAtual.getId());
    }

    /** Vazio se pode dormir agora; senão, o motivo. */
    public String motivoParaNaoDormir() {
        if (!temCasa()) {
            return "Você ainda não tem uma casa em Bom-Jardim.";
        }
        if (cenaAtual.getId().equals(jogador.getUltimaCenaDormida()) && necessidades().isEmpty()) {
            return "Você já descansou. Siga com o dia antes de dormir de novo.";
        }
        return "";
    }

    /** Dorme em casa: avança o dia, recupera energia e gasta um pouco de comida e água. */
    public boolean dormir() {
        if (!motivoParaNaoDormir().isEmpty()) {
            return false;
        }
        Recursos rec = jogador.getRecursos();
        boolean semAgua = rec.getAgua() <= 0 && podiaBeber();
        boolean semComida = rec.getComida() <= 0 && podiaComer();
        rec.alterarComida(COMIDA_AO_DORMIR);
        rec.alterarAgua(AGUA_AO_DORMIR);
        jogador.dormir(cenaAtual.getId());
        verificarPenalidades(semAgua, semComida);
        return true;
    }

    /**
     * Usa um item da mochila: aplica o efeito nos recursos e, se for consumível, gasta uma unidade.
     *
     * @return a mensagem do uso, ou null se o jogador não tem o item ou ele não pode ser usado
     */
    public String usarItem(String itemId) {
        if (!emAndamento() || !jogador.getInventario().possuiItem(itemId)) {
            return null;
        }
        Item item = rotas.getItem(itemId);
        if (item == null || !item.isUsavel()) {
            return null;
        }
        Uso uso = item.getUso();
        if (!uso.faltaPara(jogador.getRecursos()).isEmpty()) {
            return null;
        }
        uso.aplicar(jogador.getRecursos());
        if (uso.consumivel()) {
            jogador.getInventario().removerUm(itemId);
        } else if (uso.idDepois() != null) {
            jogador.getInventario().removerUm(itemId);
            jogador.getInventario().adicionarItem(rotas.getItem(uso.idDepois()));
        }
        return uso.mensagem();
    }

    /** Vazio se pode encher a garrafa agora; senão, o motivo. */
    public String motivoParaNaoEncher() {
        if (!temCasa()) {
            return "Você ainda não tem uma casa em Bom-Jardim.";
        }
        if (jogador.getInventario().possuiItem(Rotas.ITEM_GARRAFA)) {
            return "Sua garrafa já está cheia.";
        }
        if (jogador.getDiaGarrafaEnchida() == jogador.getDia()) {
            return "Você já pegou a cota de hoje. Durma para encher amanhã.";
        }
        return "";
    }

    /** Enche a garrafa térmica no poço do quintal: uma vez por dia, como a cota de água. */
    public boolean encherGarrafa() {
        if (!motivoParaNaoEncher().isEmpty()) {
            return false;
        }
        // Quem ainda não tem garrafa usa a que ficou na casa da professora Lúcia
        jogador.getInventario().removerUm(Rotas.ITEM_GARRAFA_VAZIA);
        jogador.getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_GARRAFA));
        jogador.marcarGarrafaEnchida();
        return true;
    }

    /** A escolha existe, mas o jogador ainda não cumpre a condição de acesso? */
    public boolean escolhaBloqueada(int indice) {
        return emAndamento() && indice >= 0 && indice < cenaAtual.getEscolhas().size()
                && !cenaAtual.getEscolhas().get(indice).estaDisponivel(jogador);
    }

    /** O que falta para liberar a escolha (vazio se ela está liberada ou não existe). */
    public String motivoDoBloqueio(int indice) {
        if (!escolhaBloqueada(indice)) {
            return "";
        }
        return cenaAtual.getEscolhas().get(indice).getCondicao().motivoDoBloqueio(jogador);
    }

    /** Define o final a partir da reputação acumulada, desbloqueia conquistas e encerra a partida. */
    public Final calcularFinal() {
        this.estado = EstadoJogo.FINALIZADO;

        int reputacao = jogador.getReputacao();
        Final alcancado;
        if (jogador.getDecisoes().contains(new Decisao("CENA_10A", ESCOLHA_TRIBUNAL)) && reputacao > REPUTACAO_FINAL_RUIM) {
            // O Coronel e a milícia respondem diante de um tribunal de verdade
            alcancado = rotas.getFinal("FINAL_JUSTICA");
            jogador.desbloquear(Rotas.CONQ_JUSTICA);
        } else if (reputacao >= REPUTACAO_FINAL_BOM) {
            alcancado = rotas.getFinal("FINAL_BOM");
            jogador.desbloquear(Rotas.CONQ_SEMENTE);
        } else if (reputacao <= REPUTACAO_FINAL_RUIM) {
            alcancado = rotas.getFinal("FINAL_RUIM");
        } else {
            alcancado = rotas.getFinal("FINAL_NEUTRO");
        }
        jogador.desbloquear(Rotas.CONQ_TRAVESSIA);
        return alcancado;
    }

    /** Texto curto com o andamento da partida (usado nos metadados do save). */
    public String resumoProgressao() {
        int capitulo = (cenaAtual != null) ? cenaAtual.getNumeroCapitulo() : 0;
        int total = rotas.getTotalCapitulos();
        int percentual = (total > 0) ? capitulo * 100 / total : 0;
        long concluidas = jogador.getMissoes().values().stream()
                .filter(s -> s == StatusMissao.CONCLUIDA).count();

        return String.format("Capítulo %d de %d (%d%%) | Reputação %d | %d decisão(ões) | %d/%d missão(ões) concluída(s) | %d desbloqueio(s)",
                capitulo, total, percentual, jogador.getReputacao(), jogador.getDecisoes().size(),
                concluidas, jogador.getMissoes().size(), jogador.getDesbloqueios().size());
    }

    public Rotas getRotas() { return rotas; }
    public Jogador getJogador() { return jogador; }
    public Cena getCenaAtual() { return cenaAtual; }
    public EstadoJogo getEstado() { return estado; }

    /** Última escolha executada nesta sessão (para exibir a reação); não é salva. */
    public Escolha getUltimaEscolha() { return ultimaEscolha; }
}
