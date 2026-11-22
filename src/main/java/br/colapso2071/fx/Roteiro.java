package br.colapso2071.fx;

import br.colapso2071.model.Cena;
import br.colapso2071.model.Dialogos;
import br.colapso2071.model.Escolha;
import br.colapso2071.model.Jogador;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Transforma uma cena (ou a reação a uma escolha) numa lista de páginas curtas, na mesma ordem do
 * console: título do capítulo, descrição, falas e fechamento. Texto longo é quebrado em frases para
 * caber na caixa de diálogo. Não depende de JavaFX.
 */
public final class Roteiro {
    /** Máximo de caracteres por página, para caber em até cinco linhas da caixa. */
    public static final int LIMITE = 330;

    private Roteiro() {
    }

    public static List<Pagina> daCena(Cena cena, Jogador jogador) {
        List<Pagina> paginas = new ArrayList<>();
        if (cena.isInicioDeCapitulo()) {
            paginas.add(Pagina.titulo("Capítulo " + cena.getNumeroCapitulo() + " · " + cena.getTituloCapitulo()));
            if (cena.getObjetivo() != null) {
                paginas.add(Pagina.narracao("Objetivo: " + cena.getObjetivo()));
            }
        }
        for (br.colapso2071.model.Interludio item : cena.interludioItensPara(jogador)) {
            if (item.isFala()) {
                falas(paginas, List.of(item.fala()), jogador);
            } else {
                for (String trecho : dividir(personalizar(item.texto(), jogador), LIMITE)) {
                    paginas.add(Pagina.narracao(trecho));
                }
            }
        }
        for (String trecho : dividir(personalizar(cena.getDescricao(), jogador), LIMITE)) {
            paginas.add(Pagina.narracao(trecho));
        }
        List<Dialogos> dialogos = cena.getDialogos();
        if (dialogos != null) {
            for (Dialogos d : dialogos) {
                for (String trecho : dividir(personalizar(d.getFalaNPC(), jogador), LIMITE)) {
                    paginas.add(Pagina.fala(d.getNpc().getId(), trecho));
                }
            }
        }
        if (!cena.getFechamento().isEmpty()) {
            for (String trecho : dividir(personalizar(cena.getFechamento(), jogador), LIMITE)) {
                paginas.add(Pagina.narracao(trecho));
            }
        }
        paginas.addAll(daPergunta(cena, jogador));
        return paginas;
    }

    /** A pergunta principal da cena, dita por quem espera a resposta, logo antes das opções. */
    public static List<Pagina> daPergunta(Cena cena, Jogador jogador) {
        List<Pagina> paginas = new ArrayList<>();
        Dialogos p = cena.getPergunta();
        if (p != null) {
            paginas.add(Pagina.fala(p.getNpc().getId(), personalizar(p.getFalaNPC(), jogador)));
        }
        return paginas;
    }

    /**
     * O jogador dormiu sem responder: no dia seguinte, quem fez a pergunta está na porta da casa dele,
     * e a pergunta é repetida.
     */
    public static List<Pagina> daEsperaNaPorta(Cena cena, Jogador jogador) {
        List<Pagina> paginas = new ArrayList<>();
        Dialogos p = cena.getPergunta();
        if (p == null) {
            return paginas;
        }
        paginas.add(Pagina.narracao("Na manhã do dia " + jogador.getDia() + ", alguém bate na porta da casa azul. É "
                + p.getNpc().getNome() + ", esperando a sua resposta."));
        paginas.addAll(daPergunta(cena, jogador));
        return paginas;
    }

    /** Ensina, na primeira vez, como resolver a sede ou a fome. */
    public static List<Pagina> daTutorial(br.colapso2071.model.Jogo.Necessidade n, Jogador jogador) {
        List<Pagina> p = new ArrayList<>();
        if (n == br.colapso2071.model.Jogo.Necessidade.SEDE) {
            p.add(Pagina.titulo("Como se hidratar"));
            p.add(Pagina.fala(br.colapso2071.model.Rotas.NPC_MILENA, personalizar("Você está com a boca rachada, {nome}. Escuta: no quintal da "
                    + "casa azul tem um poço de balde. Ele dá uma garrafa térmica por dia, cheia. É a sua cota.", jogador)));
            p.add(Pagina.narracao("Escolha \"Se hidratar\" (ou vá em Casa, tecla C, e encha a garrafa no poço do quintal). Depois beba pela "
                    + "mochila (tecla I). A garrafa só enche uma vez por dia; se a de hoje já foi usada, descanse até amanhã."));
        } else {
            p.add(Pagina.titulo("Como se alimentar"));
            p.add(Pagina.fala(br.colapso2071.model.Rotas.NPC_MILENA, "Você não comeu nada hoje, não foi? O depósito do Celeiro Velho "
                    + "entrega uma marmita por dia a cada morador. Vai lá antes que as pernas te deixem na mão."));
            p.add(Pagina.narracao("Escolha \"Se alimentar\" (ou vá em Casa, tecla C, e pegue a marmita no depósito). Depois coma "
                    + "pela mochila (tecla I). É uma marmita por dia; se a de hoje já foi pega, descanse até amanhã."));
        }
        return p;
    }

    /** Fim de capítulo: título e a conclusão (que depende das escolhas), ligando ao próximo capítulo. */
    public static List<Pagina> daConclusao(Cena ultimaDoCapitulo, Jogador jogador) {
        List<Pagina> paginas = new ArrayList<>();
        List<String> conclusao = ultimaDoCapitulo.conclusaoPara(jogador);
        if (conclusao.isEmpty()) {
            return paginas;
        }
        paginas.add(Pagina.titulo("Fim do capítulo " + ultimaDoCapitulo.getNumeroCapitulo() + " · "
                + ultimaDoCapitulo.getTituloCapitulo()));
        for (String paragrafo : conclusao) {
            for (String trecho : dividir(personalizar(paragrafo, jogador), LIMITE)) {
                paginas.add(Pagina.narracao(trecho));
            }
        }
        // Alguém manda o protagonista descansar: o próximo ato começa num dia novo
        if (ultimaDoCapitulo.getFalaDeDescanso() != null) {
            Dialogos d = ultimaDoCapitulo.getFalaDeDescanso();
            paginas.add(Pagina.fala(d.getNpc().getId(), personalizar(d.getFalaNPC(), jogador)));
            paginas.add(Pagina.narracao("Você volta para a casa azul e dorme. Começa o dia " + jogador.getDia() + "."));
        }
        return paginas;
    }

    /** Falas de uma lembrança de personagem. */
    public static List<Pagina> daLembranca(br.colapso2071.model.Lembranca lembranca, Jogador jogador) {
        List<Pagina> paginas = new ArrayList<>();
        paginas.add(Pagina.titulo(lembranca.titulo()));
        doJogador(paginas, lembranca.perguntaDoJogador(), jogador);
        for (Dialogos d : lembranca.falas()) {
            for (String trecho : dividir(personalizar(d.getFalaNPC(), jogador), LIMITE)) {
                paginas.add(Pagina.fala(d.getNpc().getId(), trecho));
            }
        }
        return paginas;
    }

    /** O que acontece logo depois da escolha: falas e depois a narração. Vazio se não há nada. */
    public static List<Pagina> daReacao(Escolha escolha, Jogador jogador) {
        List<Pagina> paginas = new ArrayList<>();
        // Primeiro o protagonista responde; depois os outros reagem
        doJogador(paginas, escolha.getFalaDoJogador(), jogador);
        if (escolha.isNarracaoAntes()) {
            narracao(paginas, escolha.getReacaoNarracao(), jogador);
            falas(paginas, escolha.getReacaoFalas(), jogador);
        } else {
            falas(paginas, escolha.getReacaoFalas(), jogador);
            narracao(paginas, escolha.getReacaoNarracao(), jogador);
        }
        return paginas;
    }

    private static void doJogador(List<Pagina> paginas, String fala, Jogador jogador) {
        if (fala != null && !fala.isBlank()) {
            for (String trecho : dividir(personalizar(fala, jogador), LIMITE)) {
                paginas.add(Pagina.doJogador(trecho));
            }
        }
    }

    private static void falas(List<Pagina> paginas, List<Dialogos> falas, Jogador jogador) {
        for (Dialogos d : falas) {
            for (String trecho : dividir(personalizar(d.getFalaNPC(), jogador), LIMITE)) {
                paginas.add(Pagina.fala(d.getNpc().getId(), trecho));
            }
        }
    }

    private static void narracao(List<Pagina> paginas, String texto, Jogador jogador) {
        if (texto != null && !texto.isEmpty()) {
            for (String trecho : dividir(personalizar(texto, jogador), LIMITE)) {
                paginas.add(Pagina.narracao(trecho));
            }
        }
    }

    /** Falas mostradas quando o jogador retoma uma conversa que tinha deixado para trás. */
    public static List<Pagina> daConversaRetomada(Escolha escolha, Jogador jogador) {
        List<Pagina> paginas = new ArrayList<>();
        doJogador(paginas, escolha.getFalaDoJogador(), jogador);
        for (Dialogos d : escolha.getReacaoRetomar()) {
            for (String trecho : dividir(personalizar(d.getFalaNPC(), jogador), LIMITE)) {
                paginas.add(Pagina.fala(d.getNpc().getId(), trecho));
            }
        }
        return paginas;
    }

    /** Ids dos personagens que falam nas páginas, sem repetir, na ordem em que aparecem. */
    public static List<String> npcsEmCena(List<Pagina> paginas) {
        Set<String> ids = new LinkedHashSet<>();
        for (Pagina p : paginas) {
            if (p.tipo() == Pagina.Tipo.FALA && !Pagina.JOGADOR.equals(p.npcId())) {
                ids.add(p.npcId());
            }
        }
        return new ArrayList<>(ids);
    }

    /** Troca o marcador {nome} pelo nome do jogador, como a view de console. */
    public static String personalizar(String texto, Jogador jogador) {
        return texto.replace("{nome}", jogador.getNome());
    }

    /**
     * Quebra o texto em trechos de no máximo {@code limite} caracteres, preferindo o fim das frases.
     * Uma frase maior que o limite é quebrada no último espaço que couber.
     */
    public static List<String> dividir(String texto, int limite) {
        List<String> trechos = new ArrayList<>();
        for (String paragrafo : texto.split("\n")) {
            String p = paragrafo.strip();
            if (p.isEmpty()) {
                continue;
            }
            StringBuilder atual = new StringBuilder();
            for (String frase : p.split("(?<=[.!?…”\"])\\s+")) {
                String f = frase;
                while (f.length() > limite) {
                    int corte = f.lastIndexOf(' ', limite);
                    if (corte <= 0) {
                        corte = limite;
                    }
                    guardar(trechos, atual);
                    trechos.add(f.substring(0, corte).strip());
                    f = f.substring(corte).strip();
                }
                if (atual.length() > 0 && atual.length() + 1 + f.length() > limite) {
                    guardar(trechos, atual);
                }
                if (atual.length() > 0) {
                    atual.append(' ');
                }
                atual.append(f);
            }
            guardar(trechos, atual);
        }
        return trechos;
    }

    private static void guardar(List<String> trechos, StringBuilder atual) {
        if (atual.length() > 0) {
            trechos.add(atual.toString());
            atual.setLength(0);
        }
    }
}
