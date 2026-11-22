package br.colapso2071;

import br.colapso2071.model.Atributos;
import br.colapso2071.model.Condicao;
import br.colapso2071.model.Consequencia;
import br.colapso2071.model.Decisao;
import br.colapso2071.model.Item;
import br.colapso2071.model.NPC;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.StatusMissao;
import br.colapso2071.model.TipoAtributo;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Recursos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColapsoTestesSuite {

    @Test
    public void testarRecursosELimites() {
        Recursos recursos = new Recursos(10, 20, 30);

        // Testa se não desce abaixo de zero
        recursos.alterarComida(-50);
        assertEquals(0, recursos.getComida());

        // Testa o estado de vida
        assertTrue(new Recursos(5, 5, 5).estaVivo());
        assertFalse(new Recursos(0, 5, 5).estaVivo());
    }

    @Test
    public void testarAtributos() {
        Atributos atributos = new Atributos(10, 5, 2);

        atributos.alterarLideranca(5);
        assertEquals(15, atributos.getLideranca());

        atributos.alterarTecnico(-2);
        assertEquals(3, atributos.getTecnico());
    }

    @Test
    public void testarConsequenciaEstatica() {
        Jogador jogador = new Jogador("Davi");

        // Aplica uma consequência simulada de -10 em comida e +5 em reputação
        Consequencia consequencia = new Consequencia(-10, 0, 0, 5, null);
        consequencia.aplicar(jogador);

        assertEquals(40, jogador.getRecursos().getComida());
        assertEquals(5, jogador.getReputacao());
    }

    // ---------- Fase 1: perfis, atributos, condições de acesso ----------

    @Test
    public void perfilDefineOsAtributosIniciais() {
        Perfil mecanico = new Perfil("P", "Faz-tudo", "desc", 1, 3, 2);
        Jogador jogador = new Jogador("Davi", mecanico);

        assertEquals(1, jogador.getAtributos().getLideranca());
        assertEquals(3, jogador.getAtributos().getTecnico());
        assertEquals(2, jogador.getAtributos().getNegociacao());
        assertEquals(mecanico, jogador.getPerfil());
        // o construtor simples usa o perfil neutro
        assertEquals(Perfil.PADRAO, new Jogador("Davi").getPerfil());
    }

    @Test
    public void consequenciaAlteraAtributosRelacionamentosMissoesEDesbloqueios() {
        Jogador jogador = new Jogador("Davi");
        Consequencia consequencia = new Consequencia(0, 0, 0, 0, null)
                .comAtributo(TipoAtributo.TECNICO, 2)
                .comAtributo(TipoAtributo.LIDERANCA, -1)
                .comAfinidade("NPC_X", 3)
                .iniciaMissao("M_X")
                .desbloqueia("CONQ_X");
        consequencia.aplicar(jogador);

        assertEquals(3, jogador.getAtributos().getTecnico());
        assertEquals(0, jogador.getAtributos().getLideranca());
        assertEquals(3, jogador.getRelacionamentos().getAfinidade("NPC_X"));
        assertEquals(StatusMissao.EM_ANDAMENTO, jogador.getMissoes().get("M_X"));
        assertTrue(jogador.getDesbloqueios().contains("CONQ_X"));

        new Consequencia(0, 0, 0, 0, null).concluiMissao("M_X").concluiMissao("M_NUNCA_INICIADA").aplicar(jogador);
        assertEquals(StatusMissao.CONCLUIDA, jogador.getMissoes().get("M_X"));
        assertFalse(jogador.getMissoes().containsKey("M_NUNCA_INICIADA"));
    }

    @Test
    public void condicaoPorAtributoLiberaQuandoOMinimoEAtingido() {
        Jogador jogador = new Jogador("Davi"); // atributos 1/1/1
        Condicao tecnico2 = Condicao.atributoMinimo(TipoAtributo.TECNICO, 2);

        assertFalse(tecnico2.atende(jogador));
        assertEquals("Técnico 2", tecnico2.motivoDoBloqueio(jogador));

        jogador.getAtributos().alterarTecnico(1);
        assertTrue(tecnico2.atende(jogador));
        assertEquals("", tecnico2.motivoDoBloqueio(jogador));
    }

    @Test
    public void condicaoPorItemAfinidadeEDecisao() {
        Jogador jogador = new Jogador("Davi");
        Item chave = new Item("ITEM_CHAVE", "Chave", "d");
        NPC milena = new NPC("NPC_MILENA", "Milena", "p");

        Condicao porItem = Condicao.item(chave);
        Condicao porAfinidade = Condicao.afinidadeMinima(milena, 2);
        Condicao porDecisao = Condicao.decisao("CENA_7A", 0, "ter ajudado Baltazar");

        assertFalse(porItem.atende(jogador));
        assertFalse(porAfinidade.atende(jogador));
        assertFalse(porDecisao.atende(jogador));
        assertEquals("ter o item Chave", porItem.motivoDoBloqueio(jogador));
        assertEquals("afinidade 2 com Milena", porAfinidade.motivoDoBloqueio(jogador));
        assertEquals("ter ajudado Baltazar", porDecisao.motivoDoBloqueio(jogador));

        jogador.getInventario().adicionarItem(chave);
        jogador.getRelacionamentos().alterarAfinidade("NPC_MILENA", 2);
        jogador.registrarDecisao(new Decisao("CENA_7A", 0));

        assertTrue(porItem.atende(jogador));
        assertTrue(porAfinidade.atende(jogador));
        assertTrue(porDecisao.atende(jogador));
        // outra escolha na mesma cena não conta
        assertFalse(Condicao.decisao("CENA_7A", 1, "x").atende(jogador));
    }

    @Test
    public void condicoesCombinadasExigemTodosOsRequisitos() {
        Jogador jogador = new Jogador("Davi");
        Condicao combinada = Condicao.atributoMinimo(TipoAtributo.LIDERANCA, 3)
                .e(Condicao.atributoMinimo(TipoAtributo.NEGOCIACAO, 2));

        assertFalse(combinada.atende(jogador));
        jogador.getAtributos().alterarLideranca(2);
        assertFalse(combinada.atende(jogador));
        assertEquals("Negociação 2", combinada.motivoDoBloqueio(jogador));
        jogador.getAtributos().alterarNegociacao(1);
        assertTrue(combinada.atende(jogador));
        assertEquals("Liderança 3 e Negociação 2", combinada.descricao());
    }

    @Test
    public void construtorOriginalDaCondicaoContinuaFuncionando() {
        Jogador jogador = new Jogador("Davi");
        Condicao antiga = new Condicao("ITEM_X", 1);

        assertFalse(antiga.atende(jogador)); // falta o item
        jogador.getInventario().adicionarItem(new Item("ITEM_X", "X", "d"));
        assertTrue(antiga.atende(jogador));  // liderança 1 >= 1
        assertTrue(new Condicao(null, 0).atende(jogador));
    }
}
