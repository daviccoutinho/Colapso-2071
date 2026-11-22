package br.colapso2071.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro de todo o conteúdo do jogo: cenas, finais, NPCs, itens, missões, conquistas e perfis.
 * O conteúdo em si é carregado por {@link Historia}.
 */
public final class Rotas {
    // NPCs
    public static final String NPC_MILENA = "NPC_MILENA";
    public static final String NPC_JULIO = "NPC_JULIO";
    public static final String NPC_BALTAZAR = "NPC_BALTAZAR";
    public static final String NPC_SANDRO = "NPC_SANDRO";
    public static final String NPC_RAIMUNDO = "NPC_RAIMUNDO";
    public static final String NPC_VASQUES = "NPC_VASQUES";
    public static final String NPC_DANTAS = "NPC_DANTAS";
    /** O cachorro caramelo do povoado. */
    public static final String NPC_CARAMELO = "NPC_CARAMELO";

    // Itens
    public static final String ITEM_CHAVE_INGLESA = "ITEM_CHAVE_INGLESA";
    public static final String ITEM_PISTA_BOTAS = "ITEM_PISTA_BOTAS";
    public static final String ITEM_FICHA_BOQUEIRAO = "ITEM_FICHA_BOQUEIRAO";
    public static final String ITEM_RADIO = "ITEM_RADIO";
    public static final String ITEM_GARRAFA = "ITEM_GARRAFA";
    public static final String ITEM_GARRAFA_VAZIA = "ITEM_GARRAFA_VAZIA";
    public static final String ITEM_MARMITA = "ITEM_MARMITA";
    public static final String ITEM_CAFE = "ITEM_CAFE";
    public static final String ITEM_FOTO = "ITEM_FOTO";
    public static final String ITEM_CADERNO = "ITEM_CADERNO";

    // Missões
    public static final String MISSAO_APOIO = "M_APOIO";
    public static final String MISSAO_TRAIDOR = "M_TRAIDOR";
    public static final String MISSAO_DEFESA = "M_DEFESA";
    public static final String MISSAO_VIZINHOS = "M_VIZINHOS";

    // Conquistas (conteúdo desbloqueável)
    public static final String CONQ_MAO_ESTENDIDA = "CONQ_MAO_ESTENDIDA";
    public static final String CONQ_AGUA_PARA_TODOS = "CONQ_AGUA_PARA_TODOS";
    public static final String CONQ_OLHOS_NA_CAATINGA = "CONQ_OLHOS_NA_CAATINGA";
    public static final String CONQ_FARO_DE_DETETIVE = "CONQ_FARO_DE_DETETIVE";
    public static final String CONQ_TRAVESSIA = "CONQ_TRAVESSIA";
    public static final String CONQ_SEMENTE = "CONQ_SEMENTE";
    public static final String CONQ_OUVIDO_ATENTO = "CONQ_OUVIDO_ATENTO";
    public static final String CONQ_VOZ_DO_BOQUEIRAO = "CONQ_VOZ_DO_BOQUEIRAO";
    public static final String CONQ_PORTA_ABERTA = "CONQ_PORTA_ABERTA";
    public static final String CONQ_NOVA_COROA = "CONQ_NOVA_COROA";
    public static final String CONQ_AMIGO_CARAMELO = "CONQ_AMIGO_CARAMELO";
    public static final String CONQ_MORDIDO = "CONQ_MORDIDO";
    public static final String CONQ_LIGA = "CONQ_LIGA";
    public static final String CONQ_CONSELHO = "CONQ_CONSELHO";
    public static final String CONQ_LEVANTE = "CONQ_LEVANTE";
    public static final String CONQ_JUSTICA = "CONQ_JUSTICA";

    // Perfis do protagonista
    public static final String PERFIL_MECANICO = "PERFIL_MECANICO";
    public static final String PERFIL_NEGOCIADOR = "PERFIL_NEGOCIADOR";
    public static final String PERFIL_LIDER = "PERFIL_LIDER";

    private final Map<String, Cena> cenas = new LinkedHashMap<>();
    private final Map<String, Final> finais = new LinkedHashMap<>();
    private final Map<String, Item> itens = new LinkedHashMap<>();
    private final Map<String, NPC> npcs = new LinkedHashMap<>();
    private final Map<String, Missao> missoes = new LinkedHashMap<>();
    private final Map<String, Conquista> conquistas = new LinkedHashMap<>();
    private final Map<String, Perfil> perfis = new LinkedHashMap<>();
    private final List<String> introducao = new ArrayList<>();

    public Rotas() {
        Historia.carregar(this);
    }

    // ----- Cadastro (usado por Historia) -----

    void adicionar(Cena cena) { cenas.put(cena.getId(), cena); }
    void adicionar(Final f) { finais.put(f.getId(), f); }
    void adicionar(Item item) { itens.put(item.getId(), item); }
    void adicionar(NPC npc) { npcs.put(npc.getId(), npc); }
    void adicionar(Missao missao) { missoes.put(missao.getId(), missao); }
    void adicionar(Conquista conquista) { conquistas.put(conquista.getId(), conquista); }
    void adicionar(Perfil perfil) { perfis.put(perfil.getId(), perfil); }
    void adicionarParagrafoDaIntroducao(String paragrafo) { introducao.add(paragrafo); }

    // ----- Consulta -----

    public Cena getCena(String id) { return cenas.get(id); }
    public Final getFinal(String id) { return finais.get(id); }
    public Item getItem(String id) { return itens.get(id); }
    public NPC getNpc(String id) { return npcs.get(id); }
    public Missao getMissao(String id) { return missoes.get(id); }
    public Conquista getConquista(String id) { return conquistas.get(id); }

    /** Resolve um perfil pelo id; o perfil neutro também é reconhecido. */
    public Perfil getPerfil(String id) {
        return Perfil.PADRAO.getId().equals(id) ? Perfil.PADRAO : perfis.get(id);
    }

    public Collection<Cena> getCenas() { return Collections.unmodifiableCollection(cenas.values()); }
    public Collection<Final> getFinais() { return Collections.unmodifiableCollection(finais.values()); }
    public Collection<Conquista> getConquistas() { return Collections.unmodifiableCollection(conquistas.values()); }
    public Collection<Item> getItens() { return Collections.unmodifiableCollection(itens.values()); }
    public Collection<NPC> getNpcs() { return Collections.unmodifiableCollection(npcs.values()); }
    /** Perfis oferecidos na criação do protagonista. */
    public Collection<Perfil> getPerfis() { return Collections.unmodifiableCollection(perfis.values()); }
    /** Parágrafos da introdução narrativa; {nome} é substituído pelo nome do jogador. */
    public List<String> getIntroducao() { return Collections.unmodifiableList(introducao); }

    public int getTotalCapitulos() {
        return cenas.values().stream().mapToInt(Cena::getNumeroCapitulo).max().orElse(0);
    }
}
