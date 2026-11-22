package br.colapso2071.persistencia;

import br.colapso2071.excecao.CarregamentoException;
import br.colapso2071.excecao.DadosInvalidosException;
import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Configuracoes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Persiste as preferências do usuário em {@code config.json}. */
public class ConfiguracoesRepositorio {
    private final Path arquivo;

    public ConfiguracoesRepositorio(Path diretorio) {
        this.arquivo = diretorio.resolve("config.json");
        ArquivoDados.migrar(diretorio.resolve("config.properties"), arquivo);
    }

    /** Lê as preferências; se o arquivo ainda não existe, devolve os valores padrão. */
    public Configuracoes carregar() throws CarregamentoException, DadosInvalidosException {
        Configuracoes config = new Configuracoes();
        if (!Files.exists(arquivo)) {
            return config;
        }
        Properties p = ArquivoDados.ler(arquivo);
        config.setSalvamentoAutomatico(ArquivoDados.booleano(p, "salvamentoAutomatico", true));
        config.setMostrarStatus(ArquivoDados.booleano(p, "mostrarStatus", false));
        config.setNomePadrao(p.getProperty("nomePadrao", ""));
        // Chaves da versão 4.2 (arquivos antigos não têm: ficam os padrões)
        config.setMusica(ArquivoDados.booleano(p, "musica", true));
        config.setTemaModerno(ArquivoDados.booleano(p, "temaModerno", true));
        config.setTelaCheia(ArquivoDados.booleano(p, "telaCheia", false));
        String saida = p.getProperty("saidaAudio", "AUTOMATICO");
        if (!java.util.List.of("AUTOMATICO", "JAVA_SOUND", "SISTEMA", "JAVAFX").contains(saida)) {
            throw new DadosInvalidosException("Saída de áudio desconhecida: " + saida);
        }
        config.setSaidaAudio(saida);
        if (p.getProperty("volumeMusica") != null) {
            int v = ArquivoDados.paraInteiro(p.getProperty("volumeMusica"), "volumeMusica");
            if (v < 0 || v > 100) {
                throw new DadosInvalidosException("Volume da música fora de 0 a 100: " + v);
            }
            config.setVolumeMusica(v);
        }
        return config;
    }

    public void salvar(Configuracoes config) throws SaveException {
        Properties p = new Properties();
        p.setProperty("salvamentoAutomatico", String.valueOf(config.isSalvamentoAutomatico()));
        p.setProperty("mostrarStatus", String.valueOf(config.isMostrarStatus()));
        p.setProperty("nomePadrao", config.getNomePadrao());
        p.setProperty("musica", String.valueOf(config.isMusica()));
        p.setProperty("volumeMusica", String.valueOf(config.getVolumeMusica()));
        p.setProperty("temaModerno", String.valueOf(config.isTemaModerno()));
        p.setProperty("telaCheia", String.valueOf(config.isTelaCheia()));
        p.setProperty("saidaAudio", config.getSaidaAudio());
        ArquivoDados.gravar(arquivo, p);
    }
}
