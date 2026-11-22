package br.colapso2071;

import br.colapso2071.excecao.DadosInvalidosException;
import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Atributos;
import br.colapso2071.model.Inventario;
import br.colapso2071.model.Item;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Progresso;
import br.colapso2071.model.Recursos;
import br.colapso2071.model.Relacionamentos;
import br.colapso2071.model.Rotas;
import br.colapso2071.model.TipoAtributo;
import br.colapso2071.persistencia.ProgressoRepositorio;
import br.colapso2071.persistencia.ResultadoCarga;
import br.colapso2071.persistencia.SaveRepository;
import br.colapso2071.view.Console;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de unidade: cada teste exercita uma classe isolada (model, repositórios, console),
 * incluindo as melhorias da Fase 2 (selo de integridade, backup de saves e contador de partidas).
 */
public class UnidadesTestesSuite {

    private final Rotas rotas = new Rotas();

    private Path pasta() throws Exception {
        return Files.createTempDirectory("colapso-unidades");
    }

    private Jogo jogoNaCena(int escolhasFeitas) {
        Jogo jogo = new Jogo(rotas);
        jogo.iniciar("Davi");
        for (int i = 0; i < escolhasFeitas; i++) {
            assertTrue(jogo.escolher(0));
        }
        return jogo;
    }

    private void trocarNoArquivo(Path arquivo, String de, String para) throws Exception {
        String texto = Files.readString(arquivo, StandardCharsets.UTF_8);
        assertTrue(texto.contains(de), "trecho não encontrado: " + de);
        Files.writeString(arquivo, texto.replace(de, para), StandardCharsets.UTF_8);
    }

    // ================= Model: Recursos, Atributos, Inventário, Relacionamentos, Jogador =================

    @Test
    public void recursosNuncaFicamAbaixoDeZeroEIndicamSeOJogadorSobrevive() {
        Recursos r = new Recursos(50, 20, 10);
        r.alterarAgua(-500);
        r.alterarComida(30);
        assertEquals(0, r.getAgua());
        assertEquals(80, r.getComida());
        assertFalse(r.estaVivo());

        r.alterarAgua(1);
        assertTrue(r.estaVivo());
    }

    @Test
    public void atributosSomamESubtraemPorTipo() {
        Atributos a = new Atributos(1, 1, 1);
        a.alterarLideranca(2);
        a.alterarTecnico(-1);
        a.alterar(TipoAtributo.NEGOCIACAO, 4);
        assertEquals(3, a.getLideranca());
        assertEquals(0, a.getTecnico());
        assertEquals(5, a.get(TipoAtributo.NEGOCIACAO));
    }

    @Test
    public void inventarioIgnoraNuloEBuscaPorIdSemDiferenciarMaiusculas() {
        Inventario inv = new Inventario();
        inv.adicionarItem(null);
        assertEquals(0, inv.getItens().size());

        inv.adicionarItem(new Item("ITEM_X", "X", "d"));
        assertTrue(inv.possuiItem("item_x"));
        assertTrue(inv.removerItemPorId("ITEM_X"));
        assertFalse(inv.possuiItem("ITEM_X"));
        assertFalse(inv.removerItemPorId("ITEM_X"));
    }

    @Test
    public void afinidadeAcumulaEComecaEmZero() {
        Relacionamentos rel = new Relacionamentos();
        assertEquals(0, rel.getAfinidade("NPC_NOVO"));
        rel.alterarAfinidade("NPC_NOVO", 2);
        rel.alterarAfinidade("NPC_NOVO", -5);
        assertEquals(-3, rel.getAfinidade("NPC_NOVO"));
    }

    @Test
    public void reputacaoDoJogadorAcumula() {
        Jogador j = new Jogador("Davi");
        j.alterarReputacao(7);
        j.alterarReputacao(-2);
        assertEquals(5, j.getReputacao());
    }

    // ================= Model: Progresso =================

    @Test
    public void progressoRegistraFinalSoUmaVezEContaPartidas() {
        Progresso p = new Progresso();
        assertTrue(p.registrarFinal("FINAL_BOM"));
        assertFalse(p.registrarFinal("FINAL_BOM"));
        assertEquals(0, p.getPartidasConcluidas());

        p.registrarPartidaConcluida();
        p.registrarPartidaConcluida();
        assertEquals(2, p.getPartidasConcluidas());
    }

    @Test
    public void progressoRetornaApenasConquistasNovas() {
        Progresso p = new Progresso();
        assertEquals(List.of("A", "B"), p.registrarConquistas(List.of("A", "B")));
        assertEquals(List.of("C"), p.registrarConquistas(List.of("A", "C")));
        assertEquals(3, p.getConquistas().size());
    }

    @Test
    public void progressoRecusaTotalDePartidasNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Progresso().definirPartidasConcluidas(-1));
    }

    // ================= Persistência: progresso =================

    @Test
    public void progressoGuardaEReaproveitaOTotalDePartidas() throws Exception {
        Path dir = pasta();
        ProgressoRepositorio repo = new ProgressoRepositorio(dir);
        Progresso p = new Progresso();
        p.registrarPartidaConcluida();
        p.registrarPartidaConcluida();
        p.registrarPartidaConcluida();
        repo.salvar(p);

        assertEquals(3, new ProgressoRepositorio(dir).carregar().getPartidasConcluidas());
    }

    @Test
    public void progressoAntigoSemContadorCarregaComZeroPartidas() throws Exception {
        Path dir = pasta();
        Files.writeString(dir.resolve("progresso.json"), "finais=FINAL_BOM\nconquistas=\n");
        Progresso p = new ProgressoRepositorio(dir).carregar();
        assertTrue(p.alcancouFinal("FINAL_BOM"));
        assertEquals(0, p.getPartidasConcluidas());
    }

    @Test
    public void contadorDePartidasInvalidoNoArquivoEhRejeitado() throws Exception {
        Path dir = pasta();
        Files.writeString(dir.resolve("progresso.json"), "finais=\nconquistas=\npartidas=-4\n");
        assertThrows(DadosInvalidosException.class, () -> new ProgressoRepositorio(dir).carregar());

        Files.writeString(dir.resolve("progresso.json"), "finais=\nconquistas=\npartidas=muitas\n");
        assertThrows(DadosInvalidosException.class, () -> new ProgressoRepositorio(dir).carregar());
    }

    // ================= Persistência: selo de integridade =================

    @Test
    public void saveGravadoTemSeloEContinuaCarregandoSeNadaMudou() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvar(1, jogoNaCena(3));

        assertTrue(Files.readString(dir.resolve("slot1.json")).contains("\"integridade\": "));
        assertEquals("CENA_2", saves.carregar(1).getCenaAtual().getId());
    }

    @Test
    public void editarOSaveNaMaoInvalidaOSelo() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvar(1, jogoNaCena(1));

        trocarNoArquivo(dir.resolve("slot1.json"), "reputacao\": 1,", "reputacao\": 999,");

        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> saves.carregar(1));
        assertTrue(e.getMessage().contains("adulterado"));
        // a listagem também sinaliza o slot como ilegível, sem lançar exceção
        assertTrue(saves.listarSlots().get(0).invalido());
    }

    @Test
    public void saveSemSeloContinuaSendoAceito() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvar(1, jogoNaCena(1));

        Path arquivo = dir.resolve("slot1.json");
        String semSelo = Files.readAllLines(arquivo).stream()
                .filter(l -> !l.strip().startsWith("\"integridade\""))
                .collect(java.util.stream.Collectors.joining("\n"));
        Files.writeString(arquivo, semSelo);

        assertEquals("CENA_1", saves.carregar(1).getCenaAtual().getId());
    }

    // ================= Persistência: backup e recuperação =================

    @Test
    public void primeiroSaveNaoGeraBackupMasASobrescritaGera() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());

        saves.salvar(2, jogoNaCena(1));
        assertFalse(saves.existeBackup(2));

        saves.salvar(2, jogoNaCena(3));
        assertTrue(saves.existeBackup(2));
        assertTrue(Files.exists(dir.resolve("slot2.bak")));
        assertEquals("CENA_2", saves.carregar(2).getCenaAtual().getId());
    }

    @Test
    public void saveDanificadoEhRecuperadoDoBackup() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvar(1, jogoNaCena(1));   // versão antiga: CENA_1
        saves.salvar(1, jogoNaCena(3));   // versão nova: CENA_2 (a antiga vai para o backup)

        Files.writeString(dir.resolve("slot1.json"), "lixo sem formato\n");

        // carregamento estrito falha...
        assertThrows(DadosInvalidosException.class, () -> saves.carregar(1));
        // ...a recuperação devolve a versão anterior e avisa
        ResultadoCarga r = saves.carregarComRecuperacao(1);
        assertTrue(r.recuperadoDoBackup());
        assertEquals("CENA_1", r.jogo().getCenaAtual().getId());
    }

    @Test
    public void recuperacaoNaoEhUsadaQuandoOSaveEstaIntacto() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvar(1, jogoNaCena(1));
        saves.salvar(1, jogoNaCena(3));

        ResultadoCarga r = saves.carregarComRecuperacao(1);
        assertFalse(r.recuperadoDoBackup());
        assertEquals("CENA_2", r.jogo().getCenaAtual().getId());
    }

    @Test
    public void semBackupUtilOErroOriginalEhRepassado() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvar(1, jogoNaCena(1));
        saves.salvar(1, jogoNaCena(3));
        Files.writeString(dir.resolve("slot1.json"), "lixo\n");
        Files.writeString(dir.resolve("slot1.bak"), "lixo tambem\n");

        assertThrows(DadosInvalidosException.class, () -> saves.carregarComRecuperacao(1));
    }

    @Test
    public void excluirSlotRemoveTambemOBackup() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvar(3, jogoNaCena(1));
        saves.salvar(3, jogoNaCena(2));
        assertTrue(Files.exists(dir.resolve("slot3.bak")));

        saves.excluir(3);
        assertFalse(Files.exists(dir.resolve("slot3.json")));
        assertFalse(Files.exists(dir.resolve("slot3.bak")));
        assertThrows(SaveException.class, () -> saves.excluir(3));
    }

    @Test
    public void autosaveTambemTemBackupProprio() throws Exception {
        Path dir = pasta();
        SaveRepository saves = new SaveRepository(dir, rotas, Clock.systemDefaultZone());
        saves.salvarAutomatico(jogoNaCena(1));
        saves.salvarAutomatico(jogoNaCena(2));
        assertTrue(Files.exists(dir.resolve("autosave.bak")));
    }

    // ================= View/Controller: Console e fluxo =================

    private String rodar(Path dir, String... linhas) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        Console console = new Console(new Scanner(String.join("\n", linhas) + "\n"), saida);
        Aplicacao.criar(console, dir, Clock.systemDefaultZone()).executar();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void consoleConfirmarAceitaSimNaoEFimDaEntrada() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        assertTrue(new Console(new Scanner("s\n"), saida).confirmar("ok?"));
        assertTrue(new Console(new Scanner(" SIM \n"), saida).confirmar("ok?"));
        assertFalse(new Console(new Scanner("n\n"), saida).confirmar("ok?"));
        assertFalse(new Console(new Scanner("talvez\n"), saida).confirmar("ok?"));
        assertFalse(new Console(new Scanner(""), saida).confirmar("ok?"));
        assertNull(new Console(new Scanner(""), saida).lerLinha());
    }

    /** Menu: novo jogo, nome, perfil 1, 24 decisões "1" (final bom) e sair. */
    private static String[] partidaCompleta() {
        List<String> linhas = new java.util.ArrayList<>(List.of("1", "Davi", "1"));
        for (int i = 0; i < 25; i++) {
            linhas.add("1");
        }
        linhas.add("9");
        return linhas.toArray(new String[0]);
    }

    @Test
    public void galeriaMostraQuantasPartidasForamConcluidas() throws Exception {
        Path dir = pasta();
        assertTrue(rodar(dir, "6", "", "9").contains("Partidas concluídas: 0"));

        rodar(dir, partidaCompleta());
        assertEquals(1, new ProgressoRepositorio(dir).carregar().getPartidasConcluidas());

        // jogar o mesmo final de novo ainda conta como partida concluída
        rodar(dir, partidaCompleta());
        assertEquals(2, new ProgressoRepositorio(dir).carregar().getPartidasConcluidas());
        assertTrue(rodar(dir, "6", "", "9").contains("Partidas concluídas: 2"));
    }

    @Test
    public void carregarSaveDanificadoPeloMenuAvisaQueUsouOBackup() throws Exception {
        Path dir = pasta();
        // salva duas vezes no slot 1 (cena 3A) para existir backup
        rodar(dir, "1", "Davi", "1", "1", "1", "1", "1", "S", "1", "S", "1", "s", "M", "s", "9");
        assertTrue(Files.exists(dir.resolve("slot1.bak")));

        Files.writeString(dir.resolve("slot1.json"), "danificado\n");
        String saida = rodar(dir, "3", "1", "M", "s", "9");

        assertTrue(saida.contains("estava danificado"));
        assertTrue(saida.contains("Save carregado: Davi (CENA_3A)."));
    }

    @Test
    public void saveAdulteradoPeloMenuMostraErroSemDerrubarOJogo() throws Exception {
        Path dir = pasta();
        rodar(dir, "1", "Davi", "1", "1", "1", "1", "1", "S", "1", "M", "s", "9");
        trocarNoArquivo(dir.resolve("slot1.json"), "\"jogador.nome\": \"Davi\"", "\"jogador.nome\": \"Outro\"");

        String saida = rodar(dir, "3", "1", "9");
        assertTrue(saida.contains("[ERRO]"));
        assertTrue(saida.contains("adulterado"));
        assertTrue(saida.contains("Saindo do jogo..."));
    }
}
