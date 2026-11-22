package br.colapso2071.persistencia;

import br.colapso2071.excecao.CarregamentoException;
import br.colapso2071.excecao.DadosInvalidosException;
import br.colapso2071.excecao.SaveException;
import br.colapso2071.model.Progresso;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/** Persiste finais alcançados e conquistas desbloqueadas em {@code progresso.json}. */
public class ProgressoRepositorio {
    private static final Pattern ID_VALIDO = Pattern.compile("[A-Z0-9_]+");

    private final Path arquivo;

    public ProgressoRepositorio(Path diretorio) {
        this.arquivo = diretorio.resolve("progresso.json");
        ArquivoDados.migrar(diretorio.resolve("progresso.properties"), arquivo);
    }

    public Progresso carregar() throws CarregamentoException, DadosInvalidosException {
        Progresso progresso = new Progresso();
        if (!Files.exists(arquivo)) {
            return progresso;
        }
        Properties p = ArquivoDados.ler(arquivo);
        for (String id : validar(ArquivoDados.lista(p, "finais"), "finais")) {
            progresso.registrarFinal(id);
        }
        progresso.registrarConquistas(validar(ArquivoDados.lista(p, "conquistas"), "conquistas"));
        if (p.getProperty("partidas") != null) {
            progresso.definirPartidasConcluidas(ArquivoDados.inteiro(p, "partidas", 0));
        }
        return progresso;
    }

    public void salvar(Progresso progresso) throws SaveException {
        Properties p = new Properties();
        p.setProperty("finais", String.join(",", progresso.getFinais()));
        p.setProperty("conquistas", String.join(",", progresso.getConquistas()));
        p.setProperty("partidas", String.valueOf(progresso.getPartidasConcluidas()));
        ArquivoDados.gravar(arquivo, p);
    }

    private List<String> validar(List<String> ids, String campo) throws DadosInvalidosException {
        for (String id : ids) {
            if (!ID_VALIDO.matcher(id).matches()) {
                throw new DadosInvalidosException("Id inválido em " + campo + ": '" + id + "'");
            }
        }
        return ids;
    }
}
