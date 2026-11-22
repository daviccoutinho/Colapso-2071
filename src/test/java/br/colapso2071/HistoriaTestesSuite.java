package br.colapso2071;

import br.colapso2071.model.Cena;
import br.colapso2071.model.Dialogos;
import br.colapso2071.model.Escolha;
import br.colapso2071.model.Final;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.Rotas;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Integridade do conteúdo narrativo e requisitos da fase 1. */
public class HistoriaTestesSuite {

    /** Total de cenas com decisão antes do final (25 para quem pede um dia para pensar no capítulo 1). */
    static final int DECISOES = 25;

    // Escolhas (base 0) nas 19 cenas "originais": 1A,1,2A,2,3A,3,4A,4,5A,5,6A,6,7A,7,8A,8,9A,9,10A
    static final int[] ANTIGO_BOM = new int[19];
    static final int[] ANTIGO_NEUTRO = {1, 1, 1, 0, 0, 0, 1, 2, 1, 1, 0, 1, 0, 1, 1, 2, 0, 0, 0};
    static final int[] ANTIGO_RUIM = {1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 2, 0, 2, 2, 1, 1};

    /**
     * Cenas novas (3B, 5P, 6B, 7B, 9B e 10B) entram depois das cenas antigas de posição 5 (CENA_3),
     * 7 (CENA_4), 11 (CENA_6), 13 (CENA_7), 17 (CENA_9) e 18 (CENA_10A). Em cada uma, a opção 0 é solidária, a 1 é neutra (sem mudar
     * reputação, atributos ou afinidades) e a 2 é egoísta.
     */
    static final int[] DEPOIS_DE = {5, 7, 11, 13, 17, 18};

    static int[] comNovas(int[] antigo, int opcaoNasNovas) {
        java.util.List<Integer> r = new java.util.ArrayList<>();
        for (int i = 0; i < antigo.length; i++) {
            r.add(antigo[i]);
            for (int d : DEPOIS_DE) {
                if (d == i) {
                    r.add(opcaoNasNovas);
                }
            }
        }
        return r.stream().mapToInt(Integer::intValue).toArray();
    }

    static final int[] CAMINHO_BOM = comNovas(ANTIGO_BOM, 0);
    static final int[] CAMINHO_NEUTRO = comNovas(ANTIGO_NEUTRO, 1);
    static final int[] CAMINHO_RUIM = comNovas(ANTIGO_RUIM, 2);

    private final Rotas rotas = new Rotas();

    private Jogo iniciar(Perfil perfil) {
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi", perfil);
        return jogo;
    }

    /** Joga uma sequência escrita para as 19 cenas antigas, com a opção neutra (1) nas cenas novas. */
    static void jogarAntigo(Jogo jogo, int... escolhasAntigas) {
        jogar(jogo, comNovas(escolhasAntigas, 1));
    }

    /** Joga as escolhas como um jogador: antes de cada uma, cuida do sono, da sede e da fome. */
    static void jogar(Jogo jogo, int... escolhas) {
        for (int escolha : escolhas) {
            jogo.atenderNecessidades();
            assertTrue(jogo.escolher(escolha), "escolha " + escolha + " deveria estar liberada em " + jogo.getCenaAtual().getId());
        }
    }

    // ---------- Estrutura da história ----------

    @Test
    public void historiaTemAoMenosDezCapitulosEVinteCenas() {
        assertEquals(10, rotas.getTotalCapitulos());
        assertEquals(27, rotas.getCenas().size());
        for (int capitulo = 1; capitulo <= 10; capitulo++) {
            final int n = capitulo;
            long cenas = rotas.getCenas().stream().filter(c -> c.getNumeroCapitulo() == n).count();
            assertTrue(cenas >= 2 && cenas <= 3, "capítulo " + n + " tem " + cenas + " cenas");
            assertEquals(1L, rotas.getCenas().stream().filter(c -> c.getNumeroCapitulo() == n && c.isInicioDeCapitulo()).count());
        }
    }

    @Test
    public void todaEscolhaLevaACenaExistenteOuAoFinal() {
        for (Cena cena : rotas.getCenas()) {
            assertFalse(cena.getEscolhas().isEmpty(), cena.getId() + " sem escolhas");
            for (Escolha e : cena.getEscolhas()) {
                assertTrue(rotas.getCena(e.getProximaCenaId()) != null || Jogo.ID_VERIFICAR_FINAL.equals(e.getProximaCenaId()),
                        cena.getId() + " aponta para cena inexistente: " + e.getProximaCenaId());
            }
        }
    }

    @Test
    public void todaCenaTemUmaEscolhaSemCondicaoParaNuncaTravarOJogo() {
        for (Cena cena : rotas.getCenas()) {
            assertTrue(cena.getEscolhas().stream().anyMatch(e -> e.getCondicao() == null),
                    cena.getId() + " não tem escolha livre");
        }
    }

    @Test
    public void todasAsCenasSaoAlcancaveisAPartirDaInicial() {
        Set<String> vistas = new HashSet<>();
        Deque<String> fila = new ArrayDeque<>();
        fila.add(Jogo.ID_CENA_INICIAL);
        while (!fila.isEmpty()) {
            String id = fila.poll();
            if (!vistas.add(id) || rotas.getCena(id) == null) {
                continue;
            }
            rotas.getCena(id).getEscolhas().forEach(e -> fila.add(e.getProximaCenaId()));
        }
        for (Cena cena : rotas.getCenas()) {
            assertTrue(vistas.contains(cena.getId()), cena.getId() + " é inalcançável");
        }
    }

    @Test
    public void cenasDeAmbientacaoTemDialogosEIntroducaoExiste() {
        for (Cena cena : rotas.getCenas()) {
            if (cena.getId().endsWith("A") || !cena.getId().equals("CENA_10")) {
                assertFalse(cena.getDialogos().isEmpty(), cena.getId() + " sem diálogos");
            }
        }
        assertTrue(rotas.getIntroducao().size() >= 3);
        assertTrue(rotas.getIntroducao().stream().anyMatch(p -> p.contains("{nome}")));
    }

    @Test
    public void ha_pelo_menos_cinco_personagens_com_fala() {
        Set<String> falantes = new HashSet<>();
        for (Cena cena : rotas.getCenas()) {
            for (Dialogos d : cena.getDialogos()) {
                falantes.add(d.getNpc().getId());
            }
            for (Escolha e : cena.getEscolhas()) {
                for (Dialogos d : e.getReacaoFalas()) {
                    falantes.add(d.getNpc().getId());
                }
            }
        }
        assertTrue(falantes.size() >= 5, "falantes: " + falantes);
    }

    @Test
    public void ha_tres_perfis_distintos_e_tres_finais() {
        assertEquals(3, rotas.getPerfis().size());
        assertEquals(4, rotas.getFinais().size()); // bom, neutro, ruim e o da justiça
        for (Perfil p : rotas.getPerfis()) {
            assertEquals(5, p.getLideranca() + p.getTecnico() + p.getNegociacao(), p.getNome());
        }
        rotas.getFinais().forEach(f -> assertFalse(f.getEpilogo().isEmpty(), f.getId()));
    }

    // ---------- Finais ----------

    @Test
    public void caminhosPreparadosLevamAosTresFinais() {
        Jogo bom = iniciar(Perfil.PADRAO);
        jogar(bom, CAMINHO_BOM);
        assertTrue(bom.cenaAtualLevaAoFinal());
        assertEquals("FINAL_BOM", bom.calcularFinal().getId());
        assertTrue(bom.getJogador().getReputacao() >= Jogo.REPUTACAO_FINAL_BOM);

        Jogo neutro = iniciar(Perfil.PADRAO);
        jogar(neutro, CAMINHO_NEUTRO);
        assertEquals("FINAL_NEUTRO", neutro.calcularFinal().getId());

        Jogo ruim = iniciar(Perfil.PADRAO);
        jogar(ruim, CAMINHO_RUIM);
        assertEquals("FINAL_RUIM", ruim.calcularFinal().getId());
        assertTrue(ruim.getJogador().getReputacao() <= Jogo.REPUTACAO_FINAL_RUIM);
    }

    @Test
    public void passeiosAleatoriosSempreTerminamEmUmFinalComEstadoConsistente() {
        Random sorteio = new Random(2071);
        Set<String> finaisVistos = new HashSet<>();
        for (int partida = 0; partida < 3000; partida++) {
            Jogo jogo = iniciar(Perfil.PADRAO);
            int passos = 0;
            while (!jogo.cenaAtualLevaAoFinal()) {
                Cena cena = jogo.getCenaAtual();
                jogo.atenderNecessidades();
                assertTrue(jogo.necessidades().isEmpty(), "dormir, beber e comer sempre resolvem as necessidades");
                int n = cena.getEscolhas().size();
                int escolhida = sorteio.nextInt(n);
                if (jogo.escolhaBloqueada(escolhida)) {
                    assertFalse(jogo.escolher(escolhida)); // bloqueada nunca executa
                    assertFalse(jogo.motivoDoBloqueio(escolhida).isEmpty());
                    continue;
                }
                assertTrue(jogo.escolher(escolhida));
                assertTrue(++passos <= DECISOES + 1, "mais passos do que cenas");
            }
            Jogador j = jogo.getJogador();
            finaisVistos.add(jogo.calcularFinal().getId());

            // tudo o que o jogador acumulou existe no catálogo
            j.getDesbloqueios().forEach(id -> assertTrue(rotas.getConquista(id) != null, id));
            j.getInventario().getItens().forEach(i -> assertTrue(rotas.getItem(i.getId()) != null, i.getId()));
            j.getRelacionamentos().getAfinidades().keySet().forEach(id -> assertTrue(rotas.getNpc(id) != null, id));
            j.getMissoes().keySet().forEach(id -> assertTrue(rotas.getMissao(id) != null, id));
            int esperadas = j.getDecisoes().contains(new br.colapso2071.model.Decisao("CENA_1", 2)) ? DECISOES + 1 : DECISOES;
            assertEquals(esperadas, j.getDecisoes().size());
            assertTrue(j.getRecursos().getComida() >= 0 && j.getRecursos().getAgua() >= 0 && j.getRecursos().getEnergia() >= 0);
        }
        assertEquals(4, finaisVistos.size());
    }

    // ---------- Condições de acesso (bloqueio e liberação) ----------

    @Test
    public void conserto_sozinho_so_para_quem_tem_tecnico_2() {
        // Perfil padrão (Técnico 1): bloqueada. Faz-tudo (Técnico 3): liberada.
        Jogo padrao = iniciar(Perfil.PADRAO);
        jogar(padrao, 0, 0, 0);
        assertEquals("CENA_2", padrao.getCenaAtual().getId());
        assertTrue(padrao.escolhaBloqueada(2));
        assertEquals("Técnico 2", padrao.motivoDoBloqueio(2));
        assertFalse(padrao.escolher(2));
        assertEquals("CENA_2", padrao.getCenaAtual().getId());

        Jogo mecanico = iniciar(rotas.getPerfil(Rotas.PERFIL_MECANICO));
        jogar(mecanico, 0, 0, 0);
        assertFalse(mecanico.escolhaBloqueada(2));
        assertTrue(mecanico.escolher(2));
        assertTrue(mecanico.getJogador().getInventario().possuiItem(Rotas.ITEM_CHAVE_INGLESA));
        assertEquals(4, mecanico.getJogador().getAtributos().getTecnico());
    }

    @Test
    public void negociar_com_o_coronel_exige_negociacao_2() {
        // Quem perguntou a Júlio como ele sabia (2A, opção 0) ganha +1 Negociação e libera a negociação.
        Jogo atento = iniciar(Perfil.PADRAO);
        jogarAntigo(atento, 0, 0, 0, 0, 0, 0, 0);
        assertEquals("CENA_4", atento.getCenaAtual().getId());
        assertFalse(atento.escolhaBloqueada(1));

        Jogo distraido = iniciar(Perfil.PADRAO);
        jogarAntigo(distraido, 0, 0, 1, 0, 0, 0, 0);
        assertEquals("CENA_4", distraido.getCenaAtual().getId());
        assertTrue(distraido.escolhaBloqueada(1));
        assertEquals("Negociação 2", distraido.motivoDoBloqueio(1));

        // O perfil Mascate de Água já começa liberado
        Jogo mascate = iniciar(rotas.getPerfil(Rotas.PERFIL_NEGOCIADOR));
        jogarAntigo(mascate, 0, 0, 1, 0, 0, 0, 0);
        assertFalse(mascate.escolhaBloqueada(1));
    }

    @Test
    public void enfrentar_julio_com_provas_exige_o_item_da_investigacao() {
        // Investigou o celeiro (5B, opção 0): ganha o Rastro das Botas e libera a opção em 8B.
        Jogo investigou = iniciar(Perfil.PADRAO);
        jogarAntigo(investigou, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEquals("CENA_8", investigou.getCenaAtual().getId());
        assertFalse(investigou.escolhaBloqueada(0));

        // Deixou para lá (5B, opção 1): sem item, a opção fica bloqueada.
        Jogo ignorou = iniciar(Perfil.PADRAO);
        jogarAntigo(ignorou, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
        assertEquals("CENA_8", ignorou.getCenaAtual().getId());
        assertTrue(ignorou.escolhaBloqueada(0));
        assertTrue(ignorou.motivoDoBloqueio(0).contains("Rastro das Botas"));
        assertFalse(ignorou.escolhaBloqueada(1)); // as outras seguem livres
    }

    @Test
    public void defender_a_cisterna_com_milena_exige_afinidade_e_aceiro_exige_ter_reforcado_cercas() {
        // Foi grosseiro com Milena desde o início: afinidade <= 0 bloqueia 7B opção 1
        Jogo grosseiro = iniciar(Perfil.PADRAO);
        jogarAntigo(grosseiro, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEquals("CENA_7", grosseiro.getCenaAtual().getId());
        assertTrue(grosseiro.escolhaBloqueada(1));
        assertTrue(grosseiro.motivoDoBloqueio(1).contains("Milena"));

        // Reforçou as cercas com Baltazar (7A opção 0): libera o aceiro em 9A
        Jogo reforcou = iniciar(Perfil.PADRAO);
        jogarAntigo(reforcou, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEquals("CENA_9A", reforcou.getCenaAtual().getId());
        assertFalse(reforcou.escolhaBloqueada(1));

        // Ajudou Milena na cisterna (7A opção 1): o aceiro fica bloqueado
        Jogo cisterna = iniciar(Perfil.PADRAO);
        jogarAntigo(cisterna, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
        assertEquals("CENA_9A", cisterna.getCenaAtual().getId());
        assertTrue(cisterna.escolhaBloqueada(1));
        assertTrue(cisterna.motivoDoBloqueio(1).contains("Baltazar"));
    }

    @Test
    public void expor_o_coronel_e_assumir_a_cota_exigem_lideranca_3() {
        Jogo lider = iniciar(rotas.getPerfil(Rotas.PERFIL_LIDER));
        jogarAntigo(lider, java.util.Arrays.copyOf(ANTIGO_BOM, 18));
        assertEquals("CENA_10A", lider.getCenaAtual().getId());
        assertFalse(lider.escolhaBloqueada(3));

        // Perfil neutro com escolhas egoístas nunca chega a Liderança 3
        Jogo fraco = iniciar(Perfil.PADRAO);
        jogarAntigo(fraco, java.util.Arrays.copyOf(ANTIGO_RUIM, 18));
        assertEquals("CENA_10A", fraco.getCenaAtual().getId());
        assertTrue(fraco.escolhaBloqueada(3));
        assertEquals("Liderança 3", fraco.motivoDoBloqueio(3));
    }

    @Test
    public void escolhas_tem_reacao_e_efeito_posterior_em_atributos() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        Escolha primeira = jogo.getCenaAtual().getEscolhas().get(0);
        assertFalse(primeira.getReacaoFalas().isEmpty());

        jogarAntigo(jogo, 0, 0, 0, 0, 0, 0, 0);
        // Dividir o cantil (3A) e encarar o Coronel (4A) somam Liderança
        assertEquals(3, jogo.getJogador().getAtributos().getLideranca());
        assertNotNull(jogo.getUltimaEscolha());
    }

    // ---------- Relacionamentos ----------

    @Test
    public void caminhoBomMantemAfinidadeComPeloMenosTresPersonagens() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, CAMINHO_BOM);
        List<String> comAfinidade = jogo.getJogador().getRelacionamentos().getAfinidades().keySet().stream().toList();
        assertTrue(comAfinidade.size() >= 3, comAfinidade.toString());
    }

    // ---------- Conteúdo adicional: rádio, vizinhos e nomes ----------

    @Test
    public void personagensPrincipaisSeChamamMilenaEJulio() {
        assertEquals("Milena", rotas.getNpc(Rotas.NPC_MILENA).getNome());
        assertEquals("Júlio", rotas.getNpc(Rotas.NPC_JULIO).getNome());
    }

    @Test
    public void radioAchadoNaEstacaoLiberaOCaminhoDosVizinhosAteALiga() {
        // 3 (opção 2) acha o rádio; 5 (opção 2), 7 (opção 3) e 10A (opção 4) exigem o item
        Jogo jogo = iniciar(Perfil.PADRAO);
        int[] caminho = {0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 4};
        jogarAntigo(jogo, caminho);
        assertTrue(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_RADIO));
        assertTrue(jogo.getJogador().getDesbloqueios().contains(Rotas.CONQ_LIGA));
        assertEquals(br.colapso2071.model.StatusMissao.CONCLUIDA,
                jogo.getJogador().getMissoes().get(Rotas.MISSAO_VIZINHOS));
    }

    @Test
    public void semORadioAsOpcoesDosVizinhosFicamBloqueadasComMotivo() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogarAntigo(jogo, 0, 0, 0, 0, 0, 0, 0, 0, 0); // chega à cena 5 sem passar pela estação
        assertEquals("CENA_5", jogo.getCenaAtual().getId());
        assertTrue(jogo.escolhaBloqueada(2));
        assertTrue(jogo.motivoDoBloqueio(2).contains("Rádio de Manivela"));
    }

    @Test
    public void cenasTemDialogosMaisLongosComVariasVozes() {
        for (Cena cena : rotas.getCenas()) {
            if (cena.getId().equals("CENA_10")) {
                continue;
            }
            assertTrue(cena.getDialogos().size() >= 4, cena.getId() + " tem poucas falas: " + cena.getDialogos().size());
        }
    }

    // ---------- Casa, descanso e itens usáveis ----------

    @Test
    public void povoadoSeChamaBomJardimEContaComoEraAntes() {
        String c1 = String.join(" ", rotas.getCena("CENA_1").getDialogos().stream().map(Dialogos::getFalaNPC).toList());
        assertTrue(c1.contains("Nove mil"), "a cidade tinha menos de 10 mil habitantes");
        assertTrue(c1.contains("zona rural"));
        assertTrue(c1.contains("foi embora no primeiro ano"));
        assertTrue(c1.contains("morreram"));
        assertTrue(c1.contains("Vinte e sete anos"));
        for (Cena cena : rotas.getCenas()) {
            assertFalse(cena.getDescricao().contains("Cacimba"), cena.getId());
            cena.getDialogos().forEach(d -> assertFalse(d.getFalaNPC().contains("Cacimba"), cena.getId()));
        }
    }

    @Test
    public void soPodeDormirDepoisDeGanharACasaEUmaVezPorCena() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        assertFalse(jogo.temCasa());
        assertFalse(jogo.dormir());

        jogar(jogo, 0); // chega à CENA_1: Milena cede a casa
        assertTrue(jogo.temCasa());
        int agua = jogo.getJogador().getRecursos().getAgua();
        int dia = jogo.getJogador().getDia();
        assertTrue(jogo.dormir());
        assertEquals(dia + 1, jogo.getJogador().getDia());
        assertEquals(Math.max(0, agua + Jogo.AGUA_AO_DORMIR), jogo.getJogador().getRecursos().getAgua());

        assertFalse(jogo.dormir(), "já dormiu nesta cena");
        assertFalse(jogo.motivoParaNaoDormir().isEmpty());

        jogar(jogo, 0); // avança: pode dormir de novo
        int antes = jogo.getJogador().getDia();
        assertTrue(jogo.dormir());
        assertEquals(antes + 1, jogo.getJogador().getDia());
    }

    @Test
    public void usarItemConsumivelRecuperaRecursoEGastaUmaUnidade() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0); // "Dizer o nome" dá a Marmita
        assertTrue(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_MARMITA));
        int comida = jogo.getJogador().getRecursos().getComida();

        assertNotNull(jogo.usarItem(Rotas.ITEM_MARMITA));
        assertEquals(comida + Jogo.COMIDA_DA_MARMITA, jogo.getJogador().getRecursos().getComida());
        assertFalse(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_MARMITA));
        assertNull(jogo.usarItem(Rotas.ITEM_MARMITA)); // não tem mais o item
    }

    @Test
    public void itemPermanenteNaoSomeEItemDePistaNaoEUsavel() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogo.getJogador().getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_RADIO));
        jogo.getJogador().getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_PISTA_BOTAS));

        assertNotNull(jogo.usarItem(Rotas.ITEM_RADIO));
        assertTrue(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_RADIO));
        assertNull(jogo.usarItem(Rotas.ITEM_PISTA_BOTAS));
    }

    @Test
    public void milenaContaOsAcontecimentosEstranhosNaCenaUm() {
        String falas = String.join(" ", rotas.getCena("CENA_1").getDialogos().stream().map(Dialogos::getFalaNPC).toList());
        assertTrue(falas.contains("números nas portas"));
        assertTrue(falas.contains("drone"));
        assertTrue(falas.contains("marco o nível"));
    }

    @Test
    public void textoUsaLagoaESandroESemPiadaDoMar() {
        assertEquals("Sandro", rotas.getNpc(Rotas.NPC_SANDRO).getNome());
        StringBuilder tudo = new StringBuilder(String.join(" ", rotas.getIntroducao()));
        for (Cena cena : rotas.getCenas()) {
            tudo.append(cena.getDescricao());
            cena.getDialogos().forEach(d -> tudo.append(d.getFalaNPC()));
            cena.getEscolhas().forEach(e -> e.getReacaoFalas().forEach(d -> tudo.append(d.getFalaNPC())));
        }
        rotas.getFinais().forEach(f -> tudo.append(f.getEpilogo()));
        String t = tudo.toString();
        assertFalse(t.contains("açude"));
        assertFalse(t.contains("Tiquinho"));
        assertFalse(java.util.regex.Pattern.compile("\\bmar\\b").matcher(t).find());
        assertFalse(t.contains("aprendeu a ler"));
    }

    // ---------- Garrafa térmica e interlúdios ----------

    @Test
    public void garrafaFicaVaziaAoBeberEEnchidaEmCasaUmaVezPorDia() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0, 0); // aceita ajudar: Milena enche a garrafa
        var inv = jogo.getJogador().getInventario();
        assertTrue(inv.possuiItem(Rotas.ITEM_GARRAFA));

        int agua = jogo.getJogador().getRecursos().getAgua();
        assertNotNull(jogo.usarItem(Rotas.ITEM_GARRAFA));
        assertEquals(agua + Jogo.AGUA_DA_GARRAFA, jogo.getJogador().getRecursos().getAgua());
        assertFalse(inv.possuiItem(Rotas.ITEM_GARRAFA));
        assertTrue(inv.possuiItem(Rotas.ITEM_GARRAFA_VAZIA), "a garrafa continua na mochila, vazia");
        assertNull(jogo.usarItem(Rotas.ITEM_GARRAFA_VAZIA));

        assertTrue(jogo.encherGarrafa());
        assertTrue(inv.possuiItem(Rotas.ITEM_GARRAFA));
        jogo.usarItem(Rotas.ITEM_GARRAFA);
        assertFalse(jogo.encherGarrafa(), "só uma vez por dia");
        assertTrue(jogo.motivoParaNaoEncher().contains("hoje"));

        assertTrue(jogo.dormir());
        assertTrue(jogo.encherGarrafa(), "no dia seguinte pode de novo");
        assertEquals(1, inv.quantidade(Rotas.ITEM_GARRAFA) + inv.quantidade(Rotas.ITEM_GARRAFA_VAZIA));
    }

    @Test
    public void alguemDividirAguaEncheAGarrafaVazia() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0, 0);
        jogo.usarItem(Rotas.ITEM_GARRAFA);
        // 5A opção 2 (Raimundo abre o porão) também enche a garrafa
        jogar(jogo, 0, 0, 0, 0, 1, 0, 0, 1); // 2A, 2, 3A, 3, 3B, 4A, 4, 5P
        assertEquals("CENA_5A", jogo.getCenaAtual().getId());
        jogar(jogo, 2);
        assertTrue(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_GARRAFA));
        assertFalse(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_GARRAFA_VAZIA));
    }

    @Test
    public void capitulosDoDoisAoDezAbremComInterludio() {
        for (int cap = 2; cap <= 10; cap++) {
            final int n = cap;
            Cena abertura = rotas.getCenas().stream().filter(c -> c.getNumeroCapitulo() == n && c.isInicioDeCapitulo())
                    .findFirst().orElseThrow();
            assertTrue(abertura.getInterludio().size() >= 2, "capítulo " + cap + " sem interlúdio");
        }
    }

    @Test
    public void interludioMudaConformeAsDecisoesAnteriores() {
        Jogo ajudou = iniciar(Perfil.PADRAO);
        jogar(ajudou, 0, 0);
        Jogo recusou = iniciar(Perfil.PADRAO);
        jogar(recusou, 0, 1);
        Cena c2a = rotas.getCena("CENA_2A");
        String a = String.join(" ", c2a.interludioPara(ajudou.getJogador()));
        String r = String.join(" ", c2a.interludioPara(recusou.getJogador()));
        assertTrue(a.contains("começa a trabalhar") && !a.contains("disse não"));
        assertTrue(r.contains("disse não") && !r.contains("começa a trabalhar"));

        Jogo subornou = iniciar(Perfil.PADRAO);
        jogar(subornou, 0, 0, 0, 1);
        assertTrue(String.join(" ", rotas.getCena("CENA_3A").interludioPara(subornou.getJogador())).contains("garrafa do porão"));
    }

    @Test
    public void raimundoMorreNoIncendioENaoFalaMaisDepois() {
        assertTrue(rotas.getCena("CENA_9").getDescricao().contains("Seu Raimundo"));
        Cena c10a = rotas.getCena("CENA_10A");
        c10a.getDialogos().forEach(d -> assertFalse(Rotas.NPC_RAIMUNDO.equals(d.getNpc().getId())));
        c10a.getEscolhas().forEach(e -> e.getReacaoFalas().forEach(d -> assertFalse(Rotas.NPC_RAIMUNDO.equals(d.getNpc().getId()))));
    }

    @Test
    public void dantasAmarraCaetanoPorOrdemDeVasquesEOProtestoEReprimido() {
        Cena castigo = rotas.getCena("CENA_5P");
        assertTrue(castigo.isInicioDeCapitulo());
        String t = textoDa(castigo);
        assertTrue(t.contains("Sargento Dantas. Amarre esse rapaz"));
        assertTrue(t.contains("Desculpa, rapaz"));
        assertTrue(t.contains("atiram"));
        assertTrue(t.contains("O protesto não mudou nada."));
        assertEquals("CENA_5P", rotas.getCena("CENA_4").getEscolhas().get(0).getProximaCenaId());
    }

    @Test
    public void conversaNaoEscolhidaReapareceNaCenaSeguinteEUmaVezSo() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0, 0, 1); // 2A: aperta a mão de Júlio (não pergunta, não fala com Baltazar)
        assertEquals("CENA_2", jogo.getCenaAtual().getId());
        List<Jogo.ConversaPendente> pendentes = jogo.conversasPendentes();
        assertEquals(2, pendentes.size());
        assertEquals(0, pendentes.get(0).indice());
        assertEquals(2, pendentes.get(1).indice());

        int baltazar = jogo.getJogador().getRelacionamentos().getAfinidade(Rotas.NPC_BALTAZAR);
        assertNotNull(jogo.retomarConversa(1)); // conversa com Baltazar
        assertEquals(baltazar + 1, jogo.getJogador().getRelacionamentos().getAfinidade(Rotas.NPC_BALTAZAR));
        assertEquals("CENA_2", jogo.getCenaAtual().getId(), "retomar não avança a história");
        assertEquals(3, jogo.getJogador().getDecisoes().size(), "retomar não conta como decisão");
        assertEquals(1, jogo.conversasPendentes().size(), "não dá para retomar a mesma conversa duas vezes");

        jogar(jogo, 0); // segue para 3A: as conversas da 2A expiram
        assertTrue(jogo.conversasPendentes().isEmpty());
    }

    @Test
    public void retomarPerguntaAJulioLiberaANegociacaoComOCoronel() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0, 0, 1);
        assertNotNull(jogo.retomarConversa(0)); // "como você sabia?" dá +1 Negociação
        jogar(jogo, 0, 0, 0, 1, 0); // 2, 3A, 3, 3B, 4A
        assertEquals("CENA_4", jogo.getCenaAtual().getId());
        assertFalse(jogo.escolhaBloqueada(1));
    }

    @Test
    public void opcoesDeEnredoNaoViramConversa() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0); // 1A: "Dizer o nome" — a pergunta (opção 2) fica pendente; tomar a caneca (1) não
        List<Jogo.ConversaPendente> p = jogo.conversasPendentes();
        assertEquals(1, p.size());
        assertEquals(2, p.get(0).indice());
        assertNull(jogo.retomarConversa(5));
    }

    // ---------- Conselho, apoio do povo, confronto e levante ----------

    private static String textoDa(Cena cena) {
        StringBuilder t = new StringBuilder(cena.getDescricao()).append(' ').append(cena.getFechamento());
        cena.getInterludio().forEach(i -> t.append(' ').append(i.texto()));
        cena.getDialogos().forEach(d -> t.append(' ').append(d.getFalaNPC()));
        for (Escolha e : cena.getEscolhas()) {
            e.getReacaoFalas().forEach(d -> t.append(' ').append(d.getFalaNPC()));
            t.append(' ').append(e.getReacaoNarracao());
        }
        return t.toString();
    }

    @Test
    public void povoadoTemCercaDeQuatrocentasPessoas() {
        assertTrue(textoDa(rotas.getCena("CENA_1")).contains("Nove mil virou quatrocentos"));
        assertTrue(textoDa(rotas.getCena("CENA_4A")).contains("Quatrocentas e doze pessoas"));
        assertFalse(textoDa(rotas.getCena("CENA_1")).contains("noventa e quatro"));
    }

    @Test
    public void milenaDaVozEVotoAoJogadorAntesDoCapitulo5() {
        Cena assembleia = rotas.getCena("CENA_3B");
        assertTrue(assembleia.getNumeroCapitulo() < 5);
        assertTrue(textoDa(assembleia).contains("voz e voto no conselho"));
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogarAntigo(jogo, 0, 0, 0, 0, 0, 0);
        assertEquals("CENA_4A", jogo.getCenaAtual().getId());
        assertTrue(jogo.getJogador().getDesbloqueios().contains(Rotas.CONQ_CONSELHO));
    }

    @Test
    public void jogadorEBaltazarPedemApoioDoPovoAntesDoCerco() {
        Cena chamado = rotas.getCena("CENA_6B");
        String t = textoDa(chamado);
        assertTrue(t.contains("Você e Baltazar"));
        assertTrue(t.contains("Cento e oitenta pessoas entram no mutirão de defesa"));
        assertEquals("CENA_7A", chamado.getEscolhas().get(0).getProximaCenaId());

        // O tamanho do mutirão aparece na batalha da cerca leste
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, CAMINHO_BOM.length > 16 ? java.util.Arrays.copyOf(CAMINHO_BOM, 17) : CAMINHO_BOM);
        assertEquals("CENA_7B", jogo.getCenaAtual().getId());
        assertTrue(String.join(" ", jogo.getCenaAtual().interludioPara(jogo.getJogador())).contains("cento e oitenta"));
    }

    @Test
    public void baltazarMorreNoLevanteEmQualquerCaminhoENaoFalaMais() {
        Cena levante = rotas.getCena("CENA_9B");
        assertTrue(textoDa(levante).contains("revolta"));
        for (Escolha e : levante.getEscolhas()) {
            assertTrue(e.getReacaoNarracao().contains("Baltazar"), e.getTexto());
        }
        for (String id : new String[] {"CENA_10A", "CENA_10B", "CENA_10"}) {
            Cena c = rotas.getCena(id);
            c.getDialogos().forEach(d -> assertFalse(Rotas.NPC_BALTAZAR.equals(d.getNpc().getId()), id));
            c.getEscolhas().forEach(e -> e.getReacaoFalas()
                    .forEach(d -> assertFalse(Rotas.NPC_BALTAZAR.equals(d.getNpc().getId()), id)));
        }
        assertTrue(textoDa(rotas.getCena("CENA_10B")).contains("Baltazar Rocha"));
    }

    @Test
    public void caminhoPassaPelasCincoCenasNovas() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        java.util.Set<String> vistas = new HashSet<>();
        for (int e : CAMINHO_BOM) {
            vistas.add(jogo.getCenaAtual().getId());
            jogo.atenderNecessidades();
            assertTrue(jogo.escolher(e));
        }
        assertTrue(vistas.containsAll(List.of("CENA_3B", "CENA_6B", "CENA_7B", "CENA_9B", "CENA_10B")));
    }

    // ---------- Pergunta da cena, conclusão do capítulo, necessidades, lembranças e justiça ----------

    @Test
    public void todaCenaTemUmaPerguntaPrincipalAntesDasOpcoes() {
        for (Cena cena : rotas.getCenas()) {
            assertNotNull(cena.getPergunta(), cena.getId());
            assertTrue(cena.getPergunta().getFalaNPC().endsWith("?"), cena.getId());
        }
        assertTrue(rotas.getCena("CENA_1").getPergunta().getFalaNPC().contains("Você vai nos ajudar?"));
    }

    @Test
    public void capitulosUmANoveTerminamComConclusaoQueDependeDaEscolha() {
        for (String id : new String[] {"CENA_1", "CENA_2", "CENA_3B", "CENA_4", "CENA_5", "CENA_6B", "CENA_7B", "CENA_8", "CENA_9B"}) {
            Cena ultima = rotas.getCena(id);
            assertTrue(ultima.temConclusao(), id);
            String proxima = ultima.getEscolhas().get(0).getProximaCenaId();
            assertTrue(rotas.getCena(proxima).getNumeroCapitulo() == ultima.getNumeroCapitulo() + 1, id + " fecha o capítulo");
        }
        Jogo aceitou = iniciar(Perfil.PADRAO);
        jogar(aceitou, 0, 0);
        Jogo recusou = iniciar(Perfil.PADRAO);
        jogar(recusou, 0, 1);
        Cena c1 = rotas.getCena("CENA_1");
        String a = String.join(" ", c1.conclusaoPara(aceitou.getJogador()));
        String r = String.join(" ", c1.conclusaoPara(recusou.getJogador()));
        assertTrue(a.contains("Amanhã cedo, na bomba") && !r.contains("Amanhã cedo, na bomba"));
        assertTrue(r.contains("se fez a coisa certa"));
        assertTrue(a.contains("a bomba do Poço da Santa para de vez"), "a conclusão liga ao capítulo 2");
    }

    @Test
    public void sedeEFomeTravamAsEscolhasAteOJogadorCuidarDeSi() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0);
        var rec = jogo.getJogador().getRecursos();
        rec.alterarEnergia(-500);
        rec.alterarAgua(-500);
        rec.alterarComida(-500);
        assertEquals(2, jogo.necessidades().size(), "a energia não é mais uma necessidade");
        assertFalse(jogo.escolher(0), "com sede e com fome não dá para seguir");
        assertTrue(jogo.motivoDasNecessidades().contains("garrafa térmica"));
        assertTrue(jogo.motivoDasNecessidades().contains("depósito"));

        assertTrue(jogo.encherGarrafa(), "a casa tem uma garrafa térmica");
        assertNotNull(jogo.usarItem(Rotas.ITEM_GARRAFA));
        assertFalse(jogo.necessidades().contains(Jogo.Necessidade.SEDE));

        assertTrue(jogo.buscarComida());
        assertNotNull(jogo.usarItem(Rotas.ITEM_MARMITA));
        assertTrue(jogo.necessidades().isEmpty());
        assertTrue(jogo.escolher(0));
    }

    @Test
    public void comSedeESemCotaDoDiaPodeDormirDeNovoNaMesmaCena() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0);
        assertTrue(jogo.dormir());
        assertFalse(jogo.dormir());
        jogo.getJogador().getRecursos().alterarAgua(-500);
        assertTrue(jogo.dormir(), "com sede e sem água no dia, descansar até amanhã é a saída");
    }

    @Test
    public void noiteSemAguaOuComidaCobraReputacao() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0);
        int rep = jogo.getJogador().getReputacao();
        jogo.getJogador().getRecursos().alterarAgua(-500);
        jogo.getJogador().getRecursos().alterarComida(-500);
        assertTrue(jogo.dormir());
        assertEquals(rep + 2 * Jogo.PENALIDADE_REPUTACAO, jogo.getJogador().getReputacao());
        List<String> msgs = jogo.consumirPenalidades();
        assertEquals(2, msgs.size());
        assertTrue(msgs.get(0).startsWith("Penalidade da sede"));
        assertTrue(jogo.consumirPenalidades().isEmpty(), "cada penalidade aparece uma vez");
    }

    @Test
    public void quemCuidaDeSiNaoEPenalizado() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        for (int e : CAMINHO_BOM) {
            jogo.atenderNecessidades();
            assertTrue(jogo.escolher(e));
            assertTrue(jogo.consumirPenalidades().isEmpty(), "penalizado na " + jogo.getCenaAtual());
            if (!jogo.emAndamento()) {
                break;
            }
        }
    }

    @Test
    public void depositoEntregaUmaMarmitaPorDia() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 1);
        assertTrue(jogo.buscarComida());
        assertFalse(jogo.buscarComida());
        assertTrue(jogo.motivoParaNaoBuscarComida().contains("amanhã"));
        assertTrue(jogo.dormir());
        assertTrue(jogo.buscarComida());
    }

    @Test
    public void cadaCapituloTemUmaLembrancaLiberadaPelaAfinidade() {
        java.util.Set<Integer> capitulos = new HashSet<>();
        java.util.Set<String> pessoas = new HashSet<>();
        for (Cena c : rotas.getCenas()) {
            if (c.getLembranca() != null) {
                assertTrue(capitulos.add(c.getNumeroCapitulo()), "uma por capítulo");
                pessoas.add(c.getLembranca().npc().getId());
                assertTrue(c.getLembranca().falas().size() >= 4, c.getId());
            }
        }
        assertEquals(10, capitulos.size());
        assertTrue(pessoas.size() >= 6);

        Jogo grosseiro = iniciar(Perfil.PADRAO);
        jogar(grosseiro, 1); // pega a caneca: afinidade com Milena cai
        assertNotNull(grosseiro.lembrancaDaCena());
        assertFalse(grosseiro.lembrancaDaCena().motivoBloqueio(grosseiro.getJogador()).isEmpty());
        assertNull(grosseiro.ouvirLembranca());

        Jogo gentil = iniciar(Perfil.PADRAO);
        jogar(gentil, 0);
        int antes = gentil.getJogador().getRelacionamentos().getAfinidade(Rotas.NPC_MILENA);
        assertNotNull(gentil.ouvirLembranca());
        assertEquals(antes + 1, gentil.getJogador().getRelacionamentos().getAfinidade(Rotas.NPC_MILENA));
        assertNull(gentil.lembrancaDaCena()); // cada lembrança é ouvida uma vez
        assertEquals("CENA_1", gentil.getCenaAtual().getId(), "ouvir não avança a história");
    }

    @Test
    public void tribunalLevaAoFinalDaJusticaComProcessoJusto() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, java.util.Arrays.copyOf(CAMINHO_BOM, 23));
        assertEquals("CENA_10A", jogo.getCenaAtual().getId());
        assertTrue(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_CADERNO));
        assertFalse(jogo.escolhaBloqueada(Jogo.ESCOLHA_TRIBUNAL));
        jogar(jogo, Jogo.ESCOLHA_TRIBUNAL, 0);
        Final f = jogo.calcularFinal();
        assertEquals("FINAL_JUSTICA", f.getId());
        assertTrue(f.getEpilogo().contains("defensor público"));
        assertTrue(f.getEpilogo().contains("condenado"));
        assertTrue(f.getEpilogo().contains("pelo que fez, e não pela farda"));
        assertTrue(jogo.getJogador().getDesbloqueios().contains(Rotas.CONQ_JUSTICA));
    }

    @Test
    public void quemEntregouOOlhoNaoConsegueLevarOCoronelATribunal() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, java.util.Arrays.copyOf(CAMINHO_RUIM, 23));
        assertEquals("CENA_10A", jogo.getCenaAtual().getId());
        assertTrue(jogo.escolhaBloqueada(Jogo.ESCOLHA_TRIBUNAL));
        assertTrue(jogo.motivoDoBloqueio(Jogo.ESCOLHA_TRIBUNAL).contains("Olho"));
    }

    // ---------- Capítulo 1 com um dia para pensar, efeitos visíveis, personagens só quando relevantes ----------

    @Test
    public void pedirUmDiaLevaAUmaCenaDoCapituloUmAntesDeResponder() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0, 2);
        assertEquals("CENA_1B", jogo.getCenaAtual().getId());
        assertEquals(1, jogo.getCenaAtual().getNumeroCapitulo());
        assertTrue(jogo.getCenaAtual().getPergunta().getFalaNPC().contains("vai nos ajudar?"));

        jogar(jogo, 0); // aceita depois de conhecer o povoado
        assertEquals("CENA_2A", jogo.getCenaAtual().getId());
        assertTrue(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_FOTO));
        assertTrue(jogo.getJogador().getMissoes().containsKey(Rotas.MISSAO_APOIO));
        String inter = String.join(" ", jogo.getCenaAtual().interludioPara(jogo.getJogador()));
        assertTrue(inter.contains("começa a trabalhar"), "o capítulo 2 reconhece a ajuda dada depois de um dia");
        assertTrue(rotas.getCena("CENA_1B").temConclusao());
    }

    @Test
    public void cadaOpcaoMostraOQueExigeEOQueMuda() {
        Escolha ajudar = rotas.getCena("CENA_1").getEscolhas().get(0);
        String r = ajudar.resumoParaJogador(rotas);
        assertTrue(r.contains("Custa: Água −10"), r);
        assertFalse(r.contains("Energia"), "não há mais energia: " + r);
        assertTrue(r.contains("Nova missão: "), r);
        assertFalse(r.contains("Reputação") || r.contains("Afinidade") || r.contains("Garrafa"), "ganhos ficam escondidos: " + r);

        Escolha conserto = rotas.getCena("CENA_2").getEscolhas().get(2);
        assertTrue(conserto.resumoParaJogador(rotas).startsWith("Exige: "), conserto.resumoParaJogador(rotas));
        assertFalse(rotas.getCena("CENA_3B").getEscolhas().get(0).resumoParaJogador(rotas).contains("Liderança"));
    }

    @Test
    public void mensagensDeNecessidadeDizemOQueFazer() {
        assertTrue(Jogo.Necessidade.SEDE.getMensagem().startsWith("É necessário se hidratar"));
        assertTrue(Jogo.Necessidade.FOME.getMensagem().startsWith("É necessário se alimentar"));
        assertEquals(2, Jogo.Necessidade.values().length, "sem sistema de energia");
    }

    @Test
    public void sandroNaoApareceEmTodasAsCenas() {
        int com = 0;
        for (Cena c : rotas.getCenas()) {
            boolean fala = c.getDialogos().stream().anyMatch(d -> Rotas.NPC_SANDRO.equals(d.getNpc().getId()));
            if (fala) {
                com++;
            }
        }
        assertTrue(com <= rotas.getCenas().size() / 2, "Sandro fala em " + com + " cenas");
        for (String id : new String[] {"CENA_1A", "CENA_2A", "CENA_2", "CENA_3B", "CENA_7A", "CENA_7B"}) {
            assertFalse(rotas.getCena(id).getDialogos().stream().anyMatch(d -> Rotas.NPC_SANDRO.equals(d.getNpc().getId())), id);
        }
    }

    @Test
    public void coronelNaoExigeVoluntarios() {
        for (Cena c : rotas.getCenas()) {
            assertFalse(textoDa(c).toLowerCase().contains("voluntári"), c.getId());
        }
    }

    @Test
    public void naCenaDoRastroComEnergiaZeradaHaComoSeguir() {
        // Regressão: no caminho solidário a energia zera na CENA_5 e todas as escolhas travavam sem saída clara
        Jogo jogo = iniciar(Perfil.PADRAO);
        int[] ate5 = java.util.Arrays.copyOf(CAMINHO_BOM, 11);
        for (int e : ate5) {
            jogo.atenderNecessidades();
            assertTrue(jogo.escolher(e));
        }
        assertEquals("CENA_5", jogo.getCenaAtual().getId());
        if (!jogo.necessidades().isEmpty()) {
            assertFalse(jogo.escolher(0));
            jogo.atenderNecessidades();
        }
        assertTrue(jogo.necessidades().isEmpty());
        assertTrue(jogo.escolher(0));
    }

    // ---------- Versão 4.1: fala do protagonista, castigo em cena, itens com uso próprio, consumo diário ----------

    @Test
    public void todaEscolhaTemUmaFalaDoProtagonista() {
        for (Cena c : rotas.getCenas()) {
            if (c.getId().equals("CENA_10")) {
                continue;
            }
            for (Escolha e : c.getEscolhas()) {
                assertNotNull(e.getFalaDoJogador(), c.getId() + ": " + e.getTexto());
            }
        }
    }

    @Test
    public void caetanoEPunidoEmCenaJogavelComOpcoes() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, java.util.Arrays.copyOf(CAMINHO_BOM, 9));
        assertEquals("CENA_5P", jogo.getCenaAtual().getId());
        assertTrue(jogo.getCenaAtual().getEscolhas().size() >= 3);
        assertTrue(jogo.getCenaAtual().getPergunta().getFalaNPC().contains("Caetano"));
    }

    @Test
    public void chaveInglesaNaoSeUsaMasEExigidaEmReparosEFechaduras() {
        assertFalse(rotas.getItem(Rotas.ITEM_CHAVE_INGLESA).isUsavel());
        long exigem = rotas.getCenas().stream().flatMap(c -> c.getEscolhas().stream())
                .filter(e -> e.getCondicao() != null && e.getCondicao().descricao().contains("Chave Inglesa")).count();
        assertTrue(exigem >= 2, "escolhas que exigem a chave: " + exigem);
    }

    @Test
    public void itensTemUsoProprio() {
        assertTrue(rotas.getItem(Rotas.ITEM_RADIO).getUso().mensagem().contains("nenhuma estação"));
        assertEquals("Sintonizar", rotas.getItem(Rotas.ITEM_RADIO).getUso().verbo());
        assertEquals("foto_feira_2030.png", rotas.getItem(Rotas.ITEM_FOTO).getUso().imagem());
        assertTrue(rotas.getItem(Rotas.ITEM_CADERNO).getUso().paginas().size() >= 6);
        assertTrue(String.join(" ", rotas.getItem(Rotas.ITEM_CADERNO).getUso().paginas()).contains("A. Vasques"));
        assertEquals("Preparar", rotas.getItem(Rotas.ITEM_CAFE).getUso().verbo());
    }

    @Test
    public void cafePrecisaDeAguaParaSerPreparado() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogo.getJogador().getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_CAFE));
        jogo.getJogador().getRecursos().alterarAgua(-500);
        assertNull(jogo.usarItem(Rotas.ITEM_CAFE));
        assertTrue(jogo.getJogador().getInventario().possuiItem(Rotas.ITEM_CAFE));
        jogo.getJogador().getRecursos().alterarAgua(10);
        int comida = jogo.getJogador().getRecursos().getComida();
        assertNotNull(jogo.usarItem(Rotas.ITEM_CAFE));
        assertEquals(5, jogo.getJogador().getRecursos().getAgua());
        assertEquals(comida + 15, jogo.getJogador().getRecursos().getComida());
    }

    @Test
    public void cadaDiaNovoConsomeAguaEComida() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0);
        var r = jogo.getJogador().getRecursos();
        int agua = r.getAgua(), comida = r.getComida(), dia = jogo.getJogador().getDia();
        jogar(jogo, 1); // recusa: termina o capítulo 1 e amanhece (a recusa devolve 10 de cada)
        assertEquals(dia + 1, jogo.getJogador().getDia());
        assertEquals(Math.max(0, agua + 10 + Jogo.AGUA_AO_DORMIR), r.getAgua());
        assertEquals(Math.max(0, comida + 10 + Jogo.COMIDA_AO_DORMIR), r.getComida());
    }

    @Test
    public void eventosDuroAcontecemComFalasENaoSoNarrados() {
        assertTrue(rotas.getCena("CENA_4A").getInterludio().stream().anyMatch(i -> i.isFala()
                && i.fala().getFalaNPC().contains("Rosa Lima")));
        assertTrue(rotas.getCena("CENA_6A").getInterludio().stream().anyMatch(i -> i.isFala()
                && Rotas.NPC_DANTAS.equals(i.fala().getNpc().getId())));
        assertTrue(rotas.getCena("CENA_7A").getInterludio().stream().anyMatch(i -> i.isFala()
                && i.fala().getFalaNPC().contains("Diesel")));
        Escolha subir = rotas.getCena("CENA_9B").getEscolhas().get(0);
        assertTrue(subir.isNarracaoAntes());
        assertTrue(subir.getReacaoFalas().get(0).getFalaNPC().contains("Juntos"));
    }

    @Test
    public void lembrancasComecamComUmaPerguntaDoProtagonista() {
        for (Cena c : rotas.getCenas()) {
            if (c.getLembranca() != null) {
                assertTrue(c.getLembranca().perguntaDoJogador().endsWith("?"), c.getId());
            }
        }
        assertTrue(rotas.getCena("CENA_7A").getLembranca().perguntaDoJogador().contains("quase foi embora"));
    }

    // ---------- Versão 4.2: objetivo de cada capítulo, diário e cenas de contexto ----------

    @Test
    public void todoCapituloTemObjetivoEResumo() {
        Set<Integer> comObjetivo = new HashSet<>();
        for (Cena c : rotas.getCenas()) {
            if (c.getObjetivo() != null) {
                assertTrue(c.isInicioDeCapitulo(), "objetivo fica na cena que abre o capítulo: " + c.getId());
                assertNotNull(c.getResumo(), c.getId());
                assertTrue(comObjetivo.add(c.getNumeroCapitulo()), "um objetivo por capítulo");
            }
        }
        assertEquals(10, comObjetivo.size());
    }

    @Test
    public void objetivoAcompanhaOCapituloAtual() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        assertTrue(jogo.objetivoAtual().startsWith("Conhecer Bom-Jardim"));
        Jogo outro = iniciar(Perfil.PADRAO);
        int[] ate3 = java.util.Arrays.copyOf(CAMINHO_BOM, 5);
        for (int e : ate3) {
            outro.atenderNecessidades();
            assertTrue(outro.escolher(e));
        }
        assertEquals(3, outro.getCenaAtual().getNumeroCapitulo());
        assertTrue(outro.objetivoAtual().contains("Estação 7"), outro.objetivoAtual());
    }

    @Test
    public void diarioContaOQueAconteceuEOQueOJogadorDecidiu() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        int[] ate3 = java.util.Arrays.copyOf(CAMINHO_BOM, 5);
        for (int e : ate3) {
            jogo.atenderNecessidades();
            assertTrue(jogo.escolher(e));
        }
        List<String> d = jogo.diario();
        assertEquals(3, d.size(), "dois capítulos vividos e o atual");
        assertTrue(d.get(0).startsWith("CAPÍTULO 1"));
        assertTrue(d.get(0).contains("• Você: "), d.get(0));
        assertTrue(d.get(2).startsWith("AGORA · Capítulo 3"));
        assertTrue(d.get(2).contains("Objetivo: "));
    }

    @Test
    public void cenasDeContextoExplicamAAguaEAJunta() {
        Jogador jogador = new Jogador("Davi");
        String agua = String.join(" ", rotas.getCena("CENA_2A").interludioItensPara(jogador).stream()
                .filter(i -> i.isFala()).map(i -> i.fala().getFalaNPC()).toList());
        assertTrue(agua.contains("Olho") && agua.contains("poço") && agua.contains("Celeiro Velho"), agua);
        String junta = String.join(" ", rotas.getCena("CENA_3A").interludioItensPara(jogador).stream()
                .filter(i -> i.isFala()).map(i -> i.fala().getFalaNPC()).toList());
        assertTrue(junta.contains("junta") && junta.contains("Estação 7"), junta);
    }

    @Test
    public void conselhoSoEMencionadoParaQuemAceitouAVoz() {
        Jogador aceitou = new Jogador("Davi");
        aceitou.registrarDecisao(new br.colapso2071.model.Decisao("CENA_3B", 0));
        Jogador recusou = new Jogador("Davi");
        recusou.registrarDecisao(new br.colapso2071.model.Decisao("CENA_3B", 2));
        java.util.function.Function<Jogador, String> falas = j -> String.join(" ", rotas.getCena("CENA_6A")
                .interludioItensPara(j).stream().filter(i -> i.isFala()).map(i -> i.fala().getFalaNPC()).toList());
        assertTrue(falas.apply(aceitou).contains("voz e voto"));
        assertFalse(falas.apply(recusou).contains("voz e voto"));
    }

    // ---------- Versão 4.4: o Caramelo ----------

    @Test
    public void carameloAparecesoEmAlgumasCenas() {
        int com = 0;
        for (Cena c : rotas.getCenas()) {
            if (c.temNpc(Rotas.NPC_CARAMELO, null)) {
                com++;
            }
        }
        assertTrue(com >= 5 && com <= 9, "Caramelo em " + com + " cenas");
        assertTrue(rotas.getCena("CENA_1A").temNpc(Rotas.NPC_CARAMELO, null), "late para o forasteiro na chegada");
        assertTrue(rotas.getCena("CENA_4A").temNpc(Rotas.NPC_CARAMELO, null), "late para a milícia");
    }

    @Test
    public void carinhoNoCarameloDependeDaReputacao() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        assertTrue(jogo.carameloNaCena());
        int rep = jogo.getJogador().getReputacao();
        // começo do jogo: reputação 0, ele ainda desconfia
        assertEquals(Jogo.Carinho.DESCONFIADO, jogo.fazerCarinho());
        assertEquals(rep, jogo.getJogador().getReputacao());
        assertEquals(Jogo.Carinho.JA_HOJE, jogo.fazerCarinho(), "uma vez por dia");

        Jogo bom = iniciar(Perfil.PADRAO);
        bom.getJogador().alterarReputacao(Jogo.REPUTACAO_PARA_FESTA);
        int antes = bom.getJogador().getReputacao();
        assertEquals(Jogo.Carinho.FESTA, bom.fazerCarinho());
        assertEquals(antes + 1, bom.getJogador().getReputacao());
        assertEquals(1, bom.getJogador().getRelacionamentos().getAfinidade(Rotas.NPC_CARAMELO));
        assertTrue(bom.getJogador().getDesbloqueios().contains(Rotas.CONQ_AMIGO_CARAMELO));

        Jogo ruim = iniciar(Perfil.PADRAO);
        ruim.getJogador().alterarReputacao(-3);
        assertEquals(Jogo.Carinho.MORDIDA, ruim.fazerCarinho());
        assertEquals(-4, ruim.getJogador().getReputacao());
        assertTrue(ruim.getJogador().getDesbloqueios().contains(Rotas.CONQ_MORDIDO));
        assertTrue(Jogo.Carinho.MORDIDA.getMensagem().contains("morde"));
    }

    @Test
    public void carameloSoEstaPorPertoNaCenaOuEmCasa() {
        Jogo jogo = iniciar(Perfil.PADRAO);
        jogar(jogo, 0);                     // CENA_1: ele não está na cena, mas o jogador já tem a casa
        assertFalse(jogo.carameloNaCena());
        assertTrue(jogo.carameloPorPerto());
    }
}
