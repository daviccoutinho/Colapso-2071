package br.colapso2071.persistencia;

import br.colapso2071.excecao.DadosInvalidosException;
import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Cena;
import br.colapso2071.model.Decisao;
import br.colapso2071.model.Item;
import br.colapso2071.model.Jogador;
import br.colapso2071.model.Jogo;
import br.colapso2071.model.Perfil;
import br.colapso2071.model.Rotas;
import br.colapso2071.model.StatusMissao;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static br.colapso2071.persistencia.ArquivoDados.*;

/**
 * Converte o estado de um {@link Jogo} de/para {@link Properties}.
 * Só guarda ids (itens, NPCs, missões, conquistas); os objetos são recuperados do catálogo de {@link Rotas}.
 */
final class SaveSerializador {
    static final int VERSAO = 2; // v2 acrescenta o perfil do protagonista; v1 ainda é lida

    private SaveSerializador() {
    }

    static Properties serializar(Jogo jogo, LocalDateTime agora) throws SaveException {
        if (!jogo.emAndamento()) {
            throw new SaveException("Não há partida em andamento para salvar.");
        }
        Jogador j = jogo.getJogador();
        Cena cena = jogo.getCenaAtual();

        Properties p = new Properties();
        p.setProperty("versao", String.valueOf(VERSAO));

        // Metadados (usados para listar os saves sem reconstruir a partida)
        p.setProperty("meta.dataHora", agora.toString());
        p.setProperty("meta.capitulo", String.valueOf(cena.getNumeroCapitulo()));
        p.setProperty("meta.tituloCapitulo", cena.getTituloCapitulo());
        p.setProperty("meta.resumo", jogo.resumoProgressao());

        // Estado
        p.setProperty("jogador.nome", j.getNome());
        p.setProperty("jogador.perfil", j.getPerfil().getId());
        p.setProperty("cena.id", cena.getId());
        p.setProperty("recursos.comida", String.valueOf(j.getRecursos().getComida()));
        p.setProperty("recursos.agua", String.valueOf(j.getRecursos().getAgua()));
        p.setProperty("recursos.energia", String.valueOf(j.getRecursos().getEnergia()));
        p.setProperty("atributos.lideranca", String.valueOf(j.getAtributos().getLideranca()));
        p.setProperty("atributos.tecnico", String.valueOf(j.getAtributos().getTecnico()));
        p.setProperty("atributos.negociacao", String.valueOf(j.getAtributos().getNegociacao()));
        p.setProperty("reputacao", String.valueOf(j.getReputacao()));
        p.setProperty("jogador.dia", String.valueOf(j.getDia()));
        p.setProperty("jogador.diaGarrafa", String.valueOf(j.getDiaGarrafaEnchida()));
        p.setProperty("conversas", String.join(",", j.getConversasRetomadas()));
        p.setProperty("jogador.diaDeposito", String.valueOf(j.getDiaDeposito()));
        p.setProperty("jogador.diaCarinho", String.valueOf(j.getDiaCarinho()));
        p.setProperty("jogador.penalidades", (j.isPenalizadoSede() ? "S" : "") + (j.isPenalizadoFome() ? "F" : ""));
        if (j.getUltimaCenaDormida() != null) {
            p.setProperty("jogador.ultimaCenaDormida", j.getUltimaCenaDormida());
        }

        p.setProperty("inventario", j.getInventario().getItens().stream()
                .map(Item::getId).collect(Collectors.joining(",")));
        p.setProperty("afinidades", j.getRelacionamentos().getAfinidades().entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.joining(",")));
        p.setProperty("missoes", j.getMissoes().entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue().name()).collect(Collectors.joining(",")));
        p.setProperty("decisoes", j.getDecisoes().stream()
                .map(d -> d.cenaId() + ":" + d.indiceEscolha()).collect(Collectors.joining(",")));
        p.setProperty("desbloqueios", String.join(",", j.getDesbloqueios()));
        return p;
    }

    static ResumoSave lerResumo(int slot, Properties p) throws DadosInvalidosException {
        validarVersao(p);
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(obrigatorio(p, "meta.dataHora"));
        } catch (DateTimeParseException e) {
            throw new DadosInvalidosException("Data/hora inválida no save: " + p.getProperty("meta.dataHora"), e);
        }
        return new ResumoSave(slot,
                obrigatorio(p, "jogador.nome"),
                dataHora,
                inteiro(p, "meta.capitulo", 1),
                obrigatorio(p, "meta.tituloCapitulo"),
                obrigatorio(p, "cena.id"),
                obrigatorio(p, "meta.resumo"));
    }

    static Jogo desserializar(Properties p, Rotas rotas) throws DadosInvalidosException {
        int versao = validarVersao(p);

        String cenaId = obrigatorio(p, "cena.id");
        Cena cena = rotas.getCena(cenaId);
        if (cena == null) {
            throw new DadosInvalidosException("Cena desconhecida no save: " + cenaId);
        }

        // Saves v1 não tinham perfil: usam o perfil neutro
        Perfil perfil = Perfil.PADRAO;
        if (versao >= 2) {
            String perfilId = obrigatorio(p, "jogador.perfil");
            perfil = rotas.getPerfil(perfilId);
            if (perfil == null) {
                throw new DadosInvalidosException("Perfil desconhecido no save: " + perfilId);
            }
        }
        Jogador j = new Jogador(obrigatorio(p, "jogador.nome"), perfil);

        // Recursos e atributos: o Jogador nasce com valores-padrão; aplica-se a diferença
        int comida = inteiro(p, "recursos.comida", 0);
        int agua = inteiro(p, "recursos.agua", 0);
        int energia = inteiro(p, "recursos.energia", 0);
        j.getRecursos().alterarComida(comida - j.getRecursos().getComida());
        j.getRecursos().alterarAgua(agua - j.getRecursos().getAgua());
        j.getRecursos().alterarEnergia(energia - j.getRecursos().getEnergia());

        // Os atributos salvos são os valores atuais; aplica-se a diferença em relação ao perfil
        j.getAtributos().alterarLideranca(inteiro(p, "atributos.lideranca", Integer.MIN_VALUE) - j.getAtributos().getLideranca());
        j.getAtributos().alterarTecnico(inteiro(p, "atributos.tecnico", Integer.MIN_VALUE) - j.getAtributos().getTecnico());
        j.getAtributos().alterarNegociacao(inteiro(p, "atributos.negociacao", Integer.MIN_VALUE) - j.getAtributos().getNegociacao());
        j.alterarReputacao(inteiro(p, "reputacao", Integer.MIN_VALUE));
        // Dia e último descanso são opcionais: saves antigos começam no dia 1
        int dia = p.getProperty("jogador.dia") == null ? 1 : inteiro(p, "jogador.dia", 1);
        String dormida = p.getProperty("jogador.ultimaCenaDormida");
        if (dormida != null && rotas.getCena(dormida) == null) {
            throw new DadosInvalidosException("Cena desconhecida no save: " + dormida);
        }
        j.restaurarDia(dia, dormida);
        for (String chave : p.getProperty("conversas") == null ? List.<String>of() : lista(p, "conversas")) {
            if (chave.equals("T:SEDE") || chave.equals("T:FOME")) {   // tutoriais já vistos
                j.registrarConversa(chave);
                continue;
            }
            if (chave.startsWith("L:")) {
                String idL = chave.substring(2);
                boolean existe = rotas.getCenas().stream()
                        .anyMatch(c -> c.getLembranca() != null && c.getLembranca().id().equals(idL));
                if (!existe) {
                    throw new DadosInvalidosException("Lembrança desconhecida no save: " + chave);
                }
                j.registrarConversa(chave);
                continue;
            }
            String[] par = par(chave, "conversas");
            Cena c = rotas.getCena(par[0]);
            int i = paraInteiro(par[1], "conversas");
            if (c == null || i < 0 || i >= c.getEscolhas().size()) {
                throw new DadosInvalidosException("Conversa inválida no save: " + chave);
            }
            j.registrarConversa(chave);
        }
        String pen = p.getProperty("jogador.penalidades", "");
        j.marcarPenalidadeSede(pen.contains("S"));
        j.marcarPenalidadeFome(pen.contains("F"));
        j.restaurarDiaDeposito(p.getProperty("jogador.diaDeposito") == null ? 0 : inteiro(p, "jogador.diaDeposito", 0));
        j.restaurarDiaCarinho(p.getProperty("jogador.diaCarinho") == null ? 0 : inteiro(p, "jogador.diaCarinho", 0));
        j.restaurarDiaGarrafa(p.getProperty("jogador.diaGarrafa") == null ? 0 : inteiro(p, "jogador.diaGarrafa", 0));

        for (String id : lista(p, "inventario")) {
            Item item = rotas.getItem(id);
            if (item == null) {
                throw new DadosInvalidosException("Item desconhecido no save: " + id);
            }
            j.getInventario().adicionarItem(item);
        }

        for (String texto : lista(p, "afinidades")) {
            String[] par = par(texto, "afinidades");
            if (rotas.getNpc(par[0]) == null) {
                throw new DadosInvalidosException("NPC desconhecido no save: " + par[0]);
            }
            j.getRelacionamentos().alterarAfinidade(par[0], paraInteiro(par[1], "afinidades"));
        }

        for (String texto : lista(p, "missoes")) {
            String[] par = par(texto, "missoes");
            if (rotas.getMissao(par[0]) == null) {
                throw new DadosInvalidosException("Missão desconhecida no save: " + par[0]);
            }
            try {
                j.restaurarMissao(par[0], StatusMissao.valueOf(par[1]));
            } catch (IllegalArgumentException e) {
                throw new DadosInvalidosException("Status de missão inválido no save: " + par[1], e);
            }
        }

        List<Decisao> decisoes = new ArrayList<>();
        for (String texto : lista(p, "decisoes")) {
            String[] par = par(texto, "decisoes");
            Cena cenaDecisao = rotas.getCena(par[0]);
            int indice = paraInteiro(par[1], "decisoes");
            if (cenaDecisao == null || indice < 0 || indice >= cenaDecisao.getEscolhas().size()) {
                throw new DadosInvalidosException("Decisão inválida no save: " + texto);
            }
            decisoes.add(new Decisao(par[0], indice));
        }
        decisoes.forEach(j::registrarDecisao);

        for (String id : lista(p, "desbloqueios")) {
            if (rotas.getConquista(id) == null) {
                throw new DadosInvalidosException("Desbloqueio desconhecido no save: " + id);
            }
            j.desbloquear(id);
        }

        Jogo jogo = new Jogo(rotas);
        jogo.restaurar(j, cena.getId());
        return jogo;
    }

    /** Aceita as versões 1 a {@link #VERSAO}; devolve a versão lida. */
    private static int validarVersao(Properties p) throws DadosInvalidosException {
        int versao = inteiro(p, "versao", 1);
        if (versao > VERSAO) {
            throw new DadosInvalidosException("Versão de save não suportada: " + versao);
        }
        return versao;
    }

}
