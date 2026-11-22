package br.colapso2071.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Jogador {
    private final String nome;
    private final Recursos recursos;
    private final Inventario inventario;
    private final Atributos atributos;
    private final Perfil perfil;
    private final Relacionamentos relacionamentos;
    private final Map<String, StatusMissao> missoes;
    private final List<Decisao> decisoes;
    private final Set<String> desbloqueios;
    private int reputacao;
    private int dia = 1;
    private String ultimaCenaDormida;
    private int diaGarrafaEnchida;
    private int diaDeposito;
    private int diaCarinho;
    private boolean penalizadoSede;
    private boolean penalizadoFome;
    private final Set<String> conversasRetomadas = new LinkedHashSet<>();

    /** Idade do protagonista na história. */
    public static final int IDADE = 27;

    public Jogador(String nome) {
        this(nome, Perfil.PADRAO);
    }

    public Jogador(String nome, Perfil perfil) {
        this.nome = nome;
        this.perfil = perfil;
        this.recursos = new Recursos(50, 50, 50);
        this.inventario = new Inventario();
        this.atributos = perfil.criarAtributos();
        this.relacionamentos = new Relacionamentos();
        this.missoes = new LinkedHashMap<>();
        this.decisoes = new ArrayList<>();
        this.desbloqueios = new LinkedHashSet<>();
        this.reputacao = 0;
    }

    public String getNome() { return nome; }

    /** Dia da história; avança cada vez que o jogador dorme em casa. */
    public int getDia() { return dia; }

    /** Cena em que o jogador dormiu pela última vez (não pode dormir duas vezes na mesma cena). */
    public String getUltimaCenaDormida() { return ultimaCenaDormida; }

    /** Usado pelo save para reconstruir a partida. */
    public void restaurarDia(int dia, String ultimaCenaDormida) {
        this.dia = Math.max(1, dia);
        this.ultimaCenaDormida = ultimaCenaDormida;
    }

    /** Conversas já retomadas, no formato "CENA:indice". */
    public Set<String> getConversasRetomadas() { return java.util.Collections.unmodifiableSet(conversasRetomadas); }

    public boolean registrarConversa(String chave) { return conversasRetomadas.add(chave); }

    /** Dia em que a garrafa térmica foi enchida em casa pela última vez (0 = nunca). */
    public int getDiaGarrafaEnchida() { return diaGarrafaEnchida; }

    public void restaurarDiaGarrafa(int dia) { this.diaGarrafaEnchida = Math.max(0, dia); }

    void marcarGarrafaEnchida() { this.diaGarrafaEnchida = dia; }

    public boolean isPenalizadoSede() { return penalizadoSede; }
    public boolean isPenalizadoFome() { return penalizadoFome; }
    public void marcarPenalidadeSede(boolean v) { this.penalizadoSede = v; }
    public void marcarPenalidadeFome(boolean v) { this.penalizadoFome = v; }

    /** Dia em que o jogador pegou a marmita do depósito pela última vez (0 = nunca). */
    public int getDiaDeposito() { return diaDeposito; }

    public void restaurarDiaDeposito(int dia) { this.diaDeposito = Math.max(0, dia); }

    void marcarDeposito() { this.diaDeposito = dia; }

    /** Último dia em que o jogador fez carinho no Caramelo (0 = nunca). */
    public int getDiaCarinho() { return diaCarinho; }

    public void restaurarDiaCarinho(int dia) { this.diaCarinho = Math.max(0, dia); }

    void marcarCarinho() { this.diaCarinho = dia; }

    void amanhecer() {
        this.dia++;
    }

    void dormir(String cenaId) {
        this.dia++;
        this.ultimaCenaDormida = cenaId;
    }

    public Perfil getPerfil() { return perfil; }

    public Recursos getRecursos() { return recursos; }

    public Atributos getAtributos() { return atributos; }

    public Inventario getInventario() { return inventario; }

    public Relacionamentos getRelacionamentos() { return relacionamentos; }

    public int getReputacao() { return reputacao; }

    public void alterarReputacao(int valor) {
        this.reputacao += valor;
    }

    // ----- Missões -----

    /** Inicia a missão se ela ainda não foi iniciada. */
    public void iniciarMissao(String missaoId) {
        missoes.putIfAbsent(missaoId, StatusMissao.EM_ANDAMENTO);
    }

    /** Conclui a missão, desde que ela tenha sido iniciada. @return true se concluiu */
    public boolean concluirMissao(String missaoId) {
        if (!missoes.containsKey(missaoId)) {
            return false;
        }
        missoes.put(missaoId, StatusMissao.CONCLUIDA);
        return true;
    }

    /** Usado ao carregar um save: define o status diretamente. */
    public void restaurarMissao(String missaoId, StatusMissao status) {
        missoes.put(missaoId, status);
    }

    public Map<String, StatusMissao> getMissoes() {
        return Collections.unmodifiableMap(missoes);
    }

    // ----- Decisões -----

    public void registrarDecisao(Decisao decisao) {
        decisoes.add(decisao);
    }

    public List<Decisao> getDecisoes() {
        return Collections.unmodifiableList(decisoes);
    }

    // ----- Desbloqueios (conquistas obtidas nesta partida) -----

    /** @return true se o desbloqueio era novo nesta partida */
    public boolean desbloquear(String id) {
        return desbloqueios.add(id);
    }

    public Set<String> getDesbloqueios() {
        return Collections.unmodifiableSet(desbloqueios);
    }
}
