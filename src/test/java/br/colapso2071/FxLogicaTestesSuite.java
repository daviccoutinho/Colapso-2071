package br.colapso2071;

import br.colapso2071.fx.Arte;
import br.colapso2071.fx.Efeitos;
import br.colapso2071.fx.GradeMochila;
import br.colapso2071.fx.Instrucoes;
import br.colapso2071.fx.Pagina;
import br.colapso2071.fx.Roteiro;
import br.colapso2071.model.Cena;
import br.colapso2071.model.Dialogos;
import br.colapso2071.model.Escolha;
import br.colapso2071.model.Final;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.NPC;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.Rotas;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testa a lógica da camada JavaFX que não depende de JavaFX: mapa de arte e paginação de texto. */
class FxLogicaTestesSuite {
    private final Rotas rotas = new Rotas();
    private final Jogador jogador = new Jogador("Davi", Perfil.PADRAO);

    private static boolean existe(String arquivo) {
        return Arte.class.getResource("img/" + arquivo) != null;
    }

    @Test
    void todosOsArquivosDeArteExistemNosRecursos() {
        for (String arquivo : Arte.todos()) {
            assertTrue(existe(arquivo), "Falta a imagem " + arquivo);
        }
    }

    @Test
    void todoPersonagemDaHistoriaTemSprite() {
        for (NPC npc : rotas.getNpcs()) {
            assertNotNull(Arte.sprite(npc.getId()), "Sem sprite para " + npc.getNome());
            assertNotNull(Arte.retrato(npc.getId()), "Sem retrato para " + npc.getNome());
            assertTrue(existe(Arte.retrato(npc.getId())));
            assertTrue(existe(Arte.sprite(npc.getId())));
        }
    }

    @Test
    void todoPerfilTemSpriteDiferente() {
        List<String> usados = new ArrayList<>();
        for (Perfil p : rotas.getPerfis()) {
            String s = Arte.spriteDoPerfil(p.getId());
            assertTrue(existe(s));
            assertTrue(existe(Arte.retratoDoPerfil(p.getId())), "Sem retrato para o perfil " + p.getId());
            assertFalse(usados.contains(s), "Dois perfis com o mesmo sprite: " + s);
            usados.add(s);
        }
        assertEquals(3, usados.size());
        assertTrue(existe(Arte.spriteDoPerfil(Perfil.PADRAO.getId())));
        assertTrue(existe(Arte.retratoDoPerfil(Perfil.PADRAO.getId())));
    }

    @Test
    void todaCenaEFinalTemCenarioExistente() {
        for (Cena c : rotas.getCenas()) {
            assertTrue(existe(Arte.fundo(c)), "Cenário ausente na cena " + c.getId());
        }
        for (Final f : rotas.getFinais()) {
            assertTrue(existe(Arte.fundoDoFinal(f.getId())), "Cenário ausente no final " + f.getId());
        }
    }

    @Test
    void divisaoMantemTodoOTextoENuncaPassaDoLimite() {
        String texto = "Primeira frase curta. Segunda frase um pouco maior que a primeira! "
                + "Terceira, com vírgulas, e mais palavras para encher? Quarta e última.";
        List<String> trechos = Roteiro.dividir(texto, 60);
        assertTrue(trechos.size() > 1);
        for (String t : trechos) {
            assertTrue(t.length() <= 60, "Trecho grande demais: " + t.length());
        }
        assertEquals(texto, String.join(" ", trechos));
    }

    @Test
    void fraseMaiorQueOLimiteEQuebradaNoEspaco() {
        String longa = "palavra ".repeat(40).strip();
        List<String> trechos = Roteiro.dividir(longa, 50);
        for (String t : trechos) {
            assertTrue(t.length() <= 50);
        }
        assertEquals(longa, String.join(" ", trechos));
    }

    @Test
    void textoVazioNaoGeraPaginas() {
        assertTrue(Roteiro.dividir("   \n  ", 100).isEmpty());
    }

    @Test
    void cenaInicialMostraSoMilenaETerminaComAPergunta() {
        Cena inicial = rotas.getCena("CENA_1A");
        List<Pagina> paginas = Roteiro.daCena(inicial, jogador);
        assertEquals(Pagina.Tipo.TITULO, paginas.get(0).tipo());
        assertTrue(paginas.get(0).texto().startsWith("Capítulo 1"));
        // Sandro só aparece quando é relevante; o Caramelo late para o forasteiro
        assertEquals(List.of("NPC_MILENA", "NPC_CARAMELO"), Roteiro.npcsEmCena(paginas));
        assertTrue(paginas.get(paginas.size() - 1).texto().endsWith("?"), "a última página é a pergunta da cena");
        int ultimaNarracao = -1, primeiraFala = -1;
        for (int i = 0; i < paginas.size(); i++) {
            if (paginas.get(i).tipo() == Pagina.Tipo.NARRACAO) ultimaNarracao = i;
            if (paginas.get(i).tipo() == Pagina.Tipo.FALA && primeiraFala < 0) primeiraFala = i;
        }
        assertTrue(ultimaNarracao < primeiraFala, "A descrição vem antes das falas");
    }

    @Test
    void nenhumaPaginaDaHistoriaPassaDoLimiteNemFicaComMarcadorCru() {
        for (Cena c : rotas.getCenas()) {
            List<Pagina> todas = new ArrayList<>(Roteiro.daCena(c, jogador));
            for (Escolha e : c.getEscolhas()) {
                todas.addAll(Roteiro.daReacao(e, jogador));
            }
            for (Pagina p : todas) {
                assertTrue(p.texto().length() <= Roteiro.LIMITE || p.tipo() == Pagina.Tipo.TITULO,
                        "Página grande demais em " + c.getId() + ": " + p.texto().length());
                assertFalse(p.texto().contains("{nome}"), "Marcador {nome} sem trocar em " + c.getId());
                assertFalse(p.texto().isBlank());
            }
        }
    }

    @Test
    void marcadorDeNomeEhTrocadoPeloNomeDoJogador() {
        assertEquals("Oi, Davi!", Roteiro.personalizar("Oi, {nome}!", jogador));
    }

    @Test
    void reacaoSemFalasENemNarracaoNaoGeraPaginas() {
        Escolha vazia = new Escolha("x", "CENA_1", null, null);
        assertTrue(Roteiro.daReacao(vazia, jogador).isEmpty());
    }

    @Test
    void reacaoComFalaEcomNarracaGeraFalaAntesDaNarracao() {
        NPC milena = rotas.getNpcs().iterator().next();
        Escolha e = new Escolha("x", "CENA_1", null, null)
                .comReacao(new Dialogos(milena, "Certo."))
                .comNarracao("Ela sorri.");
        List<Pagina> p = Roteiro.daReacao(e, jogador);
        assertEquals(2, p.size());
        assertEquals(Pagina.Tipo.FALA, p.get(0).tipo());
        assertEquals(Pagina.Tipo.NARRACAO, p.get(1).tipo());
    }

    private static java.awt.image.BufferedImage imagem(String arquivo) throws java.io.IOException {
        try (java.io.InputStream in = Arte.class.getResourceAsStream("img/" + arquivo)) {
            assertNotNull(in, "Arquivo ausente: " + arquivo);
            return ImageIO.read(in);
        }
    }

    @Test
    void artesTemOsTamanhosEsperados() throws Exception {
        for (String arquivo : Arte.todos()) {
            java.awt.image.BufferedImage img = imagem(arquivo);
            if (arquivo.startsWith("cena_")) {
                assertEquals(320, img.getWidth(), arquivo);
                assertEquals(180, img.getHeight(), arquivo);
            } else if (arquivo.startsWith("fig_caramelo")) {
                assertEquals(24, img.getWidth(), arquivo);
                assertEquals(18, img.getHeight(), arquivo);
            } else if (arquivo.startsWith("fig_")) {
                // bonecos de 16x32, como nos RPGs de fazenda em pixel art
                assertEquals(16, img.getWidth(), arquivo);
                assertEquals(32, img.getHeight(), arquivo);
            } else if (arquivo.startsWith("ret_")) {
                assertEquals(64, img.getWidth(), arquivo);
                assertEquals(64, img.getHeight(), arquivo);
            }
        }
    }

    @Test
    void osPesDosPersonagensFicamSobreOChaoDoCenario() throws Exception {
        // Última linha opaca do corpo (ignorando a sombra translúcida) é a penúltima do sprite, a mesma usada em TelaCena.
        for (String arquivo : Arte.todos()) {
            if (!arquivo.startsWith("fig_")) continue;
            java.awt.image.BufferedImage img = imagem(arquivo);
            int ultima = -1;
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    if ((img.getRGB(x, y) >>> 24) == 255) ultima = y;
                }
            }
            assertEquals(img.getHeight() - 2, ultima, "Pés fora da linha do chão em " + arquivo);
        }
    }

    @Test
    void efeitosDescrevemAtributosAfinidadeEItemAposUmaEscolha() {
        Efeitos.Foto antes = Efeitos.fotografar(jogador);
        jogador.getAtributos().alterarLideranca(1);
        jogador.getRelacionamentos().alterarAfinidade(Rotas.NPC_MILENA, 2);
        jogador.getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_CHAVE_INGLESA));
        jogador.iniciarMissao(Rotas.MISSAO_APOIO);

        List<String> efeitos = Efeitos.comparar(antes, jogador, rotas);

        assertTrue(efeitos.contains("Liderança +1"), efeitos.toString());
        assertTrue(efeitos.contains("Afinidade com " + rotas.getNpc(Rotas.NPC_MILENA).getNome() + " +2"), efeitos.toString());
        assertTrue(efeitos.stream().anyMatch(e -> e.startsWith("Item adicionado ao inventário: ")), efeitos.toString());
        assertTrue(efeitos.stream().anyMatch(e -> e.startsWith("Nova missão: ")), efeitos.toString());
    }

    @Test
    void efeitosVaziosQuandoNadaMudou() {
        assertTrue(Efeitos.comparar(Efeitos.fotografar(jogador), jogador, rotas).isEmpty());
    }

    @Test
    void efeitosMostramPerdasComSinalDeMenos() {
        Efeitos.Foto antes = Efeitos.fotografar(jogador);
        jogador.alterarReputacao(-2);
        assertTrue(Efeitos.comparar(antes, jogador, rotas).contains("Reputação −2"));
    }

    @Test
    public void instrucoesExplicamMochilaCasaESalvamento() {
        String tudo = String.join(" ", Instrucoes.paragrafos());
        assertTrue(Instrucoes.paragrafos().size() >= 8);
        assertTrue(tudo.contains("MOCHILA") && tudo.contains("Usar"));
        assertTrue(tudo.contains("CASA") && tudo.contains("Dormir"));
        assertTrue(tudo.contains("Bom-Jardim"));
    }

    @Test
    void todoItemDoCatalogoTemSpriteDe32Pixels() throws Exception {
        for (var item : rotas.getItens()) {
            java.awt.image.BufferedImage img = imagem(Arte.iconeDoItem(item.getId()));
            assertEquals(Arte.LADO_ICONE_ITEM, img.getWidth(), item.getId());
            assertEquals(Arte.LADO_ICONE_ITEM, img.getHeight(), item.getId());
        }
    }

    @Test
    void gradeDaMochilaE5x5EAgrupaItensIguais() {
        jogador.getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_GARRAFA));
        jogador.getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_RADIO));
        jogador.getInventario().adicionarItem(rotas.getItem(Rotas.ITEM_GARRAFA));

        GradeMochila g = new GradeMochila(jogador.getInventario());
        assertEquals(25, GradeMochila.CASAS);
        assertEquals(2, g.getOcupadas());
        assertEquals(Rotas.ITEM_GARRAFA, g.em(0, 0).item().getId());
        assertEquals(2, g.em(0, 0).quantidade());
        assertEquals(Rotas.ITEM_RADIO, g.em(0, 1).item().getId());
        assertNull(g.em(0, 2));
        assertNull(g.em(4, 4));
        assertEquals(1, g.indiceDe(Rotas.ITEM_RADIO));
        assertEquals(-1, g.indiceDe(Rotas.ITEM_FOTO));
    }

    @Test
    void gradeNuncaPassaDe25Casas() {
        for (int k = 0; k < 30; k++) {
            jogador.getInventario().adicionarItem(new br.colapso2071.model.Item("ITEM_X" + k, "X" + k, "teste"));
        }
        GradeMochila g = new GradeMochila(jogador.getInventario());
        assertEquals(25, g.getOcupadas());
        assertNotNull(g.em(4, 4));
    }

    @Test
    void efeitosAnunciamConquistaDesbloqueada() {
        Efeitos.Foto antes = Efeitos.fotografar(jogador);
        jogador.desbloquear(Rotas.CONQ_CONSELHO);
        assertTrue(Efeitos.comparar(antes, jogador, rotas).contains("Conquista desbloqueada: Voz e Voto"));
    }

    @Test
    void sinopseApresentaOJogoSemContarOFinal() {
        String t = String.join(" ", br.colapso2071.fx.Sinopse.curta());
        assertTrue(t.contains("Bom-Jardim") && t.contains("27 anos") && t.contains("quatro finais"));
        assertFalse(t.contains("Baltazar morre"));
    }

    @Test
    void protagonistaFalaAntesDaReacaoENaoContaComoNpcEmCena() {
        var escolha = rotas.getCena("CENA_1").getEscolhas().get(0);
        List<Pagina> reacao = Roteiro.daReacao(escolha, jogador);
        assertEquals(Pagina.JOGADOR, reacao.get(0).npcId());
        assertFalse(Roteiro.npcsEmCena(reacao).contains(Pagina.JOGADOR));
    }

    @Test
    void quemEsperavaARespostaApareceNaPortaNoDiaSeguinte() {
        List<Pagina> p = Roteiro.daEsperaNaPorta(rotas.getCena("CENA_1"), jogador);
        assertTrue(p.get(0).texto().contains("bate na porta") && p.get(0).texto().contains("Milena"));
        assertTrue(p.get(p.size() - 1).texto().contains("Você vai nos ajudar?"));
    }

    @Test
    void fotoDaFeiraExisteParaSerVistaEmTelaCheia() throws Exception {
        assertTrue(imagem("foto_feira_2030.png").getWidth() >= 200);
    }

    @Test
    void lembrancaComecaComAPerguntaDoProtagonista() {
        var l = rotas.getCena("CENA_7A").getLembranca();
        List<Pagina> p = Roteiro.daLembranca(l, jogador);
        assertEquals(Pagina.JOGADOR, p.get(1).npcId());
    }

    // ---------- Versão 4.2: tutorial de água e comida, descanso no fim do capítulo ----------

    @Test
    void tutorialEnsinaOndePegarAguaEComida() {
        Jogador jogador = new Jogador("Davi");
        String sede = String.join(" ", Roteiro.daTutorial(br.colapso2071.model.Jogo.Necessidade.SEDE, jogador).stream().map(Pagina::texto).toList());
        String fome = String.join(" ", Roteiro.daTutorial(br.colapso2071.model.Jogo.Necessidade.FOME, jogador).stream().map(Pagina::texto).toList());
        assertTrue(sede.contains("poço") && sede.contains("garrafa") && sede.contains("Davi"), sede);
        assertFalse(sede.contains("{nome}"));
        assertTrue(fome.contains("depósito") && fome.contains("marmita"), fome);
        assertTrue(sede.contains("uma vez por dia") && fome.contains("uma marmita por dia"));
    }

    @Test
    void fimDeCapituloMandaDescansarEComecaUmDiaNovo() {
        for (String id : new String[] {"CENA_1", "CENA_2", "CENA_4", "CENA_8", "CENA_9B"}) {
            assertNotNull(rotas.getCena(id).getFalaDeDescanso(), id);
        }
        Jogador jogador = new Jogador("Davi");
        jogador.registrarDecisao(new br.colapso2071.model.Decisao("CENA_2", 0));
        List<Pagina> p = Roteiro.daConclusao(rotas.getCena("CENA_2"), jogador);
        Pagina ultima = p.get(p.size() - 1);
        assertTrue(ultima.texto().contains("Começa o dia"), ultima.texto());
        assertEquals(Rotas.NPC_BALTAZAR, p.get(p.size() - 2).npcId());
    }

    // ---------- Versão 4.2: trilha sonora ----------

    @Test
    void cadaClimaTemUmaFaixaNosRecursos() throws Exception {
        for (br.colapso2071.fx.Trilha.Clima c : br.colapso2071.fx.Trilha.Clima.values()) {
            var url = br.colapso2071.fx.Trilha.class.getResource(c.getArquivo());
            assertNotNull(url, c.getArquivo());
            try (var in = url.openStream()) {
                byte[] cabeca = in.readNBytes(12);
                // WAV (RIFF....WAVE): toca no javafx-media em qualquer sistema, sem depender de codecs instalados
                String riff = new String(cabeca, 0, 4, java.nio.charset.StandardCharsets.US_ASCII);
                String wave = new String(cabeca, 8, 4, java.nio.charset.StandardCharsets.US_ASCII);
                assertEquals("RIFF", riff, c.getArquivo());
                assertEquals("WAVE", wave, c.getArquivo());
            }
        }
    }

    @Test
    void cadaFaixaAbreNoJavaSoundDoJdk() throws Exception {
        // A música toca pelo javax.sound.sampled (vem com o JDK); não depende de codecs do sistema.
        for (br.colapso2071.fx.Trilha.Clima c : br.colapso2071.fx.Trilha.Clima.values()) {
            var url = br.colapso2071.fx.Trilha.class.getResource(c.getArquivo());
            try (var in = javax.sound.sampled.AudioSystem.getAudioInputStream(new java.io.BufferedInputStream(url.openStream()))) {
                var f = in.getFormat();
                assertEquals(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED, f.getEncoding(), c.getArquivo());
                assertEquals(16, f.getSampleSizeInBits(), c.getArquivo());
                double segundos = in.getFrameLength() / f.getFrameRate();
                assertTrue(segundos > 50 && segundos < 70, c.getArquivo() + ": " + segundos + " s");
            }
        }
    }

    @Test
    void musicaAcompanhaOClimaDaHistoria() {
        assertEquals(br.colapso2071.fx.Trilha.Clima.TENSA, br.colapso2071.fx.Trilha.daCena(rotas.getCena("CENA_9A")));
        assertEquals(br.colapso2071.fx.Trilha.Clima.TENSA, br.colapso2071.fx.Trilha.daCena(rotas.getCena("CENA_4A")));
        assertEquals(br.colapso2071.fx.Trilha.Clima.TRISTE, br.colapso2071.fx.Trilha.daCena(rotas.getCena("CENA_8A")));
        assertEquals(br.colapso2071.fx.Trilha.Clima.ALEGRE, br.colapso2071.fx.Trilha.daCena(rotas.getCena("CENA_10")));
        assertEquals(br.colapso2071.fx.Trilha.Clima.ALEGRE, br.colapso2071.fx.Trilha.doFinal(rotas.getFinal("FINAL_JUSTICA")));
        assertEquals(br.colapso2071.fx.Trilha.Clima.TENSA, br.colapso2071.fx.Trilha.doFinal(rotas.getFinal("FINAL_RUIM")));
        assertEquals(br.colapso2071.fx.Trilha.Clima.NEUTRA, br.colapso2071.fx.Trilha.daCena(rotas.getCena("CENA_2A")));
        int neutras = 0;
        for (Cena c : rotas.getCenas()) {
            if (br.colapso2071.fx.Trilha.daCena(c) == br.colapso2071.fx.Trilha.Clima.NEUTRA) {
                neutras++;
            }
        }
        assertTrue(neutras * 2 > rotas.getCenas().size(), "a música neutra toca na maior parte das cenas: " + neutras);
    }

    // ---------- Versão 4.8: tocador do sistema (reserva quando o Java não enxerga a placa de som) ----------

    @Test
    void tocadorDoSistemaGravaCopiaNoVolumeCerto() throws Exception {
        var url = br.colapso2071.fx.Trilha.class.getResource(br.colapso2071.fx.Trilha.Clima.NEUTRA.getArquivo());
        var cheio = br.colapso2071.fx.TocadorSistema.copiaComVolume(url, "teste_neutra", 1.0);
        var metade = br.colapso2071.fx.TocadorSistema.copiaComVolume(url, "teste_neutra", 0.5);
        assertTrue(java.nio.file.Files.size(cheio) > 1_000_000);
        assertTrue(Math.abs(pico(cheio) / 2.0 - pico(metade)) <= pico(cheio) * 0.02, "metade do volume");
        assertEquals(cheio, br.colapso2071.fx.TocadorSistema.copiaComVolume(url, "teste_neutra", 1.0), "reaproveita a cópia");
        var aplay = br.colapso2071.fx.TocadorSistema.Programa.APLAY.comando(cheio);
        assertEquals(List.of("aplay", "-q", cheio.toString()), aplay);
        assertEquals("pw-play", br.colapso2071.fx.TocadorSistema.Programa.PW_PLAY.comando(cheio).get(0));
    }

    private static int pico(java.nio.file.Path wav) throws Exception {
        try (var in = javax.sound.sampled.AudioSystem.getAudioInputStream(wav.toFile())) {
            byte[] b = in.readAllBytes();
            var buf = java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN);
            int max = 0;
            for (int i = 0; i + 1 < b.length; i += 2) {
                max = Math.max(max, Math.abs(buf.getShort(i)));
            }
            return max;
        }
    }
}
