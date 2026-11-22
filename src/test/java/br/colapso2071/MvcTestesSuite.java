package br.colapso2071;

import br.colapso2071.model.EstadoJogo;
import br.colapso2071.model.Final;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.Rotas;
import br.colapso2071.view.Console;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class MvcTestesSuite {

    private static final int[] CAMINHO_BOM = HistoriaTestesSuite.CAMINHO_BOM;
    private static final int[] CAMINHO_RUIM = HistoriaTestesSuite.CAMINHO_RUIM;
    private static final int[] CAMINHO_NEUTRO = HistoriaTestesSuite.CAMINHO_NEUTRO;

    private Final jogarAteOFinal(int[] escolhas) {
        Jogo jogo = new Jogo(new Rotas());
        jogo.iniciar("Davi");
        for (int escolha : escolhas) {
            jogo.atenderNecessidades();
            assertTrue(jogo.escolher(escolha), "escolha " + escolha + " em " + jogo.getCenaAtual().getId());
        }
        assertTrue(jogo.cenaAtualLevaAoFinal());
        return jogo.calcularFinal();
    }

    // ---------- Model ----------

    @Test
    public void jogoComecaNoMenuEIniciaNaPrimeiraCena() {
        Jogo jogo = new Jogo(new Rotas());
        assertEquals(EstadoJogo.MENU, jogo.getEstado());
        assertFalse(jogo.emAndamento());

        jogo.iniciar("Davi");
        assertEquals(EstadoJogo.EM_JOGO, jogo.getEstado());
        assertEquals("CENA_1A", jogo.getCenaAtual().getId());
        assertEquals("Davi", jogo.getJogador().getNome());
        assertEquals(Perfil.PADRAO, jogo.getJogador().getPerfil());
    }

    @Test
    public void iniciarComPerfilAplicaOsAtributosDele() {
        Rotas rotas = new Rotas();
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi", rotas.getPerfil(Rotas.PERFIL_LIDER));

        assertEquals(3, jogo.getJogador().getAtributos().getLideranca());
        assertEquals(1, jogo.getJogador().getAtributos().getTecnico());
        assertEquals(rotas.getPerfil(Rotas.PERFIL_LIDER), jogo.getJogador().getPerfil());
    }

    @Test
    public void escolherAplicaConsequenciaEAvancaDeCena() {
        Jogo jogo = new Jogo(new Rotas());
        jogo.iniciar("Davi");

        assertTrue(jogo.escolher(0)); // CENA_1A: (+5 comida, +5 água, +1 reputação)

        assertEquals("CENA_1", jogo.getCenaAtual().getId());
        assertEquals(55, jogo.getJogador().getRecursos().getComida());
        assertEquals(1, jogo.getJogador().getReputacao());
    }

    @Test
    public void escolhaInvalidaNaoAlteraEstado() {
        Jogo jogo = new Jogo(new Rotas());
        jogo.iniciar("Davi");

        assertFalse(jogo.escolher(-1));
        assertFalse(jogo.escolher(99));

        assertEquals("CENA_1A", jogo.getCenaAtual().getId());
        assertEquals(0, jogo.getJogador().getReputacao());
        assertEquals(0, jogo.getJogador().getDecisoes().size());
    }

    @Test
    public void escolhaRegistraDecisaoRelacionamentoMissaoEDesbloqueio() {
        Jogo jogo = new Jogo(new Rotas());
        jogo.iniciar("Davi");
        jogo.escolher(0);  // agradece a Milena
        jogo.escolher(0);  // CENA_1: aceita ajudar

        assertEquals(2, jogo.getJogador().getDecisoes().size());
        assertEquals(3, jogo.getJogador().getRelacionamentos().getAfinidade("NPC_MILENA"));
        assertTrue(jogo.getJogador().getMissoes().containsKey("M_APOIO"));
        assertTrue(jogo.getJogador().getDesbloqueios().contains("CONQ_MAO_ESTENDIDA"));
    }

    @Test
    public void finalDependeDaReputacao() {
        assertEquals("FINAL_BOM", jogarAteOFinal(CAMINHO_BOM).getId());
        assertEquals("FINAL_RUIM", jogarAteOFinal(CAMINHO_RUIM).getId());
        assertEquals("FINAL_NEUTRO", jogarAteOFinal(CAMINHO_NEUTRO).getId());
    }

    @Test
    public void limitesDosFinaisSaoOsDefinidosNoJogo() {
        assertEquals(15, Jogo.REPUTACAO_FINAL_BOM);
        assertEquals(-10, Jogo.REPUTACAO_FINAL_RUIM);
    }

    @Test
    public void calcularFinalEncerraAPartida() {
        Jogo jogo = new Jogo(new Rotas());
        jogo.iniciar("Davi");
        for (int escolha : CAMINHO_BOM) {
            jogo.atenderNecessidades();
            jogo.escolher(escolha);
        }
        jogo.calcularFinal();

        assertEquals(EstadoJogo.FINALIZADO, jogo.getEstado());
        assertFalse(jogo.emAndamento());
        assertFalse(jogo.escolher(0));
    }

    // ---------- Fluxo completo View + Controller + Model ----------

    private String executarMenuComEntrada(String entrada) throws Exception {
        Path pasta = Files.createTempDirectory("colapso-mvc");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        Console console = new Console(new Scanner(entrada), saida);

        Aplicacao.criar(console, pasta, Clock.systemDefaultZone()).executar();

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void menuSairEncerraSemIniciarPartida() throws Exception {
        String saida = executarMenuComEntrada("9\n");
        assertTrue(saida.contains("Saindo do jogo..."));
        assertFalse(saida.contains("FIM DE JOGO"));
    }

    @Test
    public void menuOpcaoInvalidaVoltaAoMenu() throws Exception {
        String saida = executarMenuComEntrada("x\n9\n");
        assertTrue(saida.contains("Opção inválida."));
        assertTrue(saida.contains("Saindo do jogo..."));
    }

    @Test
    public void partidaCompletaViaConsoleChegaAoFinalBom() throws Exception {
        // Novo jogo, nome, perfil 1, uma entrada inválida ("abc") e depois a opção 1 nas 24 cenas com decisão
        String saida = executarMenuComEntrada("1\nDavi\n1\nabc\n" + "1\n".repeat(25) + "9\n");

        assertTrue(saida.contains("CRIAÇÃO DO PROTAGONISTA"));
        assertTrue(saida.contains("Opção inválida! Tente novamente."));
        assertTrue(saida.contains("CAPÍTULO 10"));
        assertTrue(saida.contains("FIM DE JOGO"));
        
        assertTrue(saida.contains("Reputação final acumulada: 63"));
        assertFalse(saida.contains("Penalidade"), "quem já usou a cota do dia descansa sem penalidade");
        assertTrue(saida.contains("A Semente do Sertão"));
        assertTrue(saida.contains("*** Novo final registrado"));
        assertTrue(saida.contains("Saindo do jogo..."));
    }

    @Test
    public void introducaoEPersonalizadaComONomeDoJogador() throws Exception {
        String saida = executarMenuComEntrada("1\nDavi\n1\nM\ns\n9\n");
        assertTrue(saida.contains("Davi"));
        assertFalse(saida.contains("{nome}"));
    }

    @Test
    public void criacaoDoProtagonistaRepeteQuandoOPerfilEInvalidoEVoltaComV() throws Exception {
        String invalido = executarMenuComEntrada("1\nDavi\n7\nx\nV\n9\n");
        assertTrue(invalido.contains("Perfil inválido."));
        assertFalse(invalido.contains("COLAPSO 2071\n\n")); // não chegou a iniciar a introdução
        assertTrue(invalido.contains("Saindo do jogo..."));
    }

    @Test
    public void fichaMostraPerfilEAtributosEOpcaoBloqueadaMostraORequisito() throws Exception {
        // Perfil 3 (Chefe de Mutirão): Técnico 1, então "consertar sozinho" (CENA_2, opção 3) está bloqueada.
        String entrada = "1\nDavi\n3\n" + "1\n1\n1\n" + "3\nF\nM\ns\n9\n";
        String saida = executarMenuComEntrada(entrada);

        assertTrue(saida.contains("--- FICHA DE DAVI ---"));
        assertTrue(saida.contains("Perfil: Chefe de Mutirão"));
        assertTrue(saida.contains("[BLOQUEADA]"));
        assertTrue(saida.contains("Essa opção está bloqueada. Requer: Técnico 2."));
    }

    @Test
    public void entradaEncerradaNoMeioDaPartidaNaoLancaExcecao() {
        assertDoesNotThrow(() -> executarMenuComEntrada("1\nDavi\n1\n1\n"));
        assertDoesNotThrow(() -> executarMenuComEntrada("1\nDavi\n"));
    }

    @Test
    public void voltarAoMenuDuranteAPartidaExigeConfirmacao() throws Exception {
        String saida = executarMenuComEntrada("1\nDavi\n1\nM\nn\nM\ns\n9\n");
        assertTrue(saida.contains("O progresso não salvo será perdido"));
        assertTrue(saida.contains("Saindo do jogo..."));
    }
}
