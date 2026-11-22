package br.colapso2071.model;

import java.util.ArrayList;
import java.util.List;

public class Cena {
    private final String id;
    private final String descricao;
    private final List<Escolha> escolhas;
    private final List<Dialogos> dialogos;

    private int numeroCapitulo;
    private String tituloCapitulo = "";
    private boolean pontoDeSalvamento;
    private boolean autosave;
    private boolean inicioDeCapitulo;
    private String fechamento = "";
    private final List<Interludio> interludio = new java.util.ArrayList<>();
    private final List<Interludio> conclusao = new java.util.ArrayList<>();
    private Dialogos pergunta;
    private Lembranca lembranca;

    public Cena(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
        this.escolhas = new ArrayList<>();
        this.dialogos = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getDescricao() { return descricao; }
    public List<Escolha> getEscolhas() { return escolhas; }
    public List<Dialogos> getDialogos() { return dialogos; }

    /** O personagem fala (ou aparece numa fala da abertura) nesta cena, para este jogador. */
    public boolean temNpc(String npcId, Jogador jogador) {
        for (Dialogos d : dialogos) {
            if (npcId.equals(d.getNpc().getId())) {
                return true;
            }
        }
        for (Interludio i : interludio) {
            if (i.isFala() && npcId.equals(i.fala().getNpc().getId()) && (jogador == null || i.aparecePara(jogador))) {
                return true;
            }
        }
        return false;
    }

    public void adicionarEscolha(Escolha escolha) {
        if (escolha != null) this.escolhas.add(escolha);
    }

    public void adicionarDialogo(Dialogos dialogo) {
        if (dialogo != null) this.dialogos.add(dialogo);
    }

    // ----- Capítulo e pontos de salvamento (definidos pela equipe em Rotas) -----

    public Cena capitulo(int numero, String titulo) {
        this.numeroCapitulo = numero;
        this.tituloCapitulo = titulo;
        return this;
    }

    /** Marca a cena como ponto onde o jogador pode salvar manualmente. */
    public Cena comPontoDeSalvamento() {
        this.pontoDeSalvamento = true;
        return this;
    }

    /** Marca a cena como evento relevante: o progresso é salvo automaticamente ao chegar nela. */
    public Cena comAutosave() {
        this.autosave = true;
        return this;
    }

    /** Marca a cena como a primeira do capítulo (a tela mostra o título do capítulo). */
    public Cena abreCapitulo() {
        this.inicioDeCapitulo = true;
        return this;
    }

    /** Trecho narrado que aparece depois das falas da cena. */
    public Cena comFechamento(String texto) {
        this.fechamento = texto;
        return this;
    }

    /** Parágrafos narrados antes da descrição (os dias que se passaram entre um capítulo e outro). */
    public Cena comInterludio(String... paragrafos) {
        for (String p : paragrafos) {
            interludio.add(new Interludio(null, p));
        }
        return this;
    }

    /** Parágrafo do interlúdio que só aparece se o jogador cumprir a condição (uma decisão anterior, um item...). */
    public Cena comInterludioSe(Condicao condicao, String paragrafo) {
        interludio.add(new Interludio(condicao, paragrafo));
        return this;
    }

    /** Falas que acontecem na abertura do capítulo (em vez de só narradas). */
    public Cena comInterludioFala(Dialogos... falas) {
        for (Dialogos f : falas) {
            interludio.add(new Interludio(null, null, f));
        }
        return this;
    }

    /** Fala da abertura do capítulo que só acontece se o jogador cumprir a condição. */
    public Cena comInterludioFalaSe(Condicao condicao, Dialogos fala) {
        interludio.add(new Interludio(condicao, null, fala));
        return this;
    }

    /** Trechos do interlúdio (narração ou fala) que valem para este jogador. */
    public List<Interludio> interludioItensPara(Jogador jogador) {
        List<Interludio> r = new java.util.ArrayList<>();
        for (Interludio i : interludio) {
            if (i.aparecePara(jogador)) {
                r.add(i);
            }
        }
        return r;
    }

    public List<Interludio> getInterludio() { return java.util.Collections.unmodifiableList(interludio); }

    /** Parágrafos do interlúdio que valem para este jogador, na ordem em que foram escritos. */
    public List<String> interludioPara(Jogador jogador) {
        List<String> r = new java.util.ArrayList<>();
        for (Interludio i : interludio) {
            if (i.aparecePara(jogador) && !i.isFala()) {
                r.add(i.texto());
            }
        }
        return r;
    }

    /** A pergunta principal da cena, repetida antes das opções (ex.: "Você vai nos ajudar?"). */
    public Cena comPergunta(Dialogos pergunta) {
        this.pergunta = pergunta;
        return this;
    }

    public Dialogos getPergunta() { return pergunta; }

    /** Conclusão do capítulo, narrada depois da última escolha dele, antes do próximo capítulo. */
    public Cena comConclusao(String... paragrafos) {
        for (String p : paragrafos) {
            conclusao.add(new Interludio(null, p));
        }
        return this;
    }

    /** Parágrafo da conclusão que depende de uma decisão (normalmente a escolha que acabou de ser feita). */
    public Cena comConclusaoSe(Condicao condicao, String paragrafo) {
        conclusao.add(new Interludio(condicao, paragrafo));
        return this;
    }

    public List<String> conclusaoPara(Jogador jogador) {
        List<String> r = new java.util.ArrayList<>();
        for (Interludio i : conclusao) {
            if (i.aparecePara(jogador)) {
                r.add(i.texto());
            }
        }
        return r;
    }

    public boolean temConclusao() { return !conclusao.isEmpty(); }

    private Dialogos falaDeDescanso;
    private String objetivo;
    private String resumo;

    /** Objetivo do capítulo, mostrado na abertura e no topo da tela (só na cena que abre o capítulo). */
    public Cena comObjetivo(String objetivo) {
        this.objetivo = objetivo;
        return this;
    }

    public String getObjetivo() { return objetivo; }

    /** O que aconteceu no capítulo, para o diário de quem volta ao jogo depois de um tempo. */
    public Cena comResumo(String resumo) {
        this.resumo = resumo;
        return this;
    }

    public String getResumo() { return resumo; }

    /** Fala de quem manda o protagonista descansar no fim do capítulo (o próximo ato começa num dia novo). */
    public Cena comFalaDeDescanso(Dialogos fala) {
        this.falaDeDescanso = fala;
        return this;
    }

    public Dialogos getFalaDeDescanso() { return falaDeDescanso; }

    /** Lembrança de um personagem que pode ser ouvida nesta cena, se houver afinidade. */
    public Cena comLembranca(Lembranca lembranca) {
        this.lembranca = lembranca;
        return this;
    }

    public Lembranca getLembranca() { return lembranca; }

    public boolean isInicioDeCapitulo() { return inicioDeCapitulo; }
    public String getFechamento() { return fechamento; }
    public int getNumeroCapitulo() { return numeroCapitulo; }
    public String getTituloCapitulo() { return tituloCapitulo; }
    public boolean isPontoDeSalvamento() { return pontoDeSalvamento; }
    public boolean isAutosave() { return autosave; }
}
