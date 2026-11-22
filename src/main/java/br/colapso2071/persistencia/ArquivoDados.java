package br.colapso2071.persistencia;

import br.colapso2071.excecao.CarregamentoException;
import br.colapso2071.excecao.DadosInvalidosException;
import br.colapso2071.excecao.SaveException;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Leitura/escrita dos arquivos do jogo em JSON (UTF-8) e validação de campos.
 * Na memória os campos ficam num {@link Properties} (chave e valor em texto). Arquivos antigos, gravados no formato
 * {@code chave=valor} das versões anteriores, continuam sendo lidos.
 */
final class ArquivoDados {
    private ArquivoDados() {
    }

    static Properties ler(Path arquivo) throws CarregamentoException, DadosInvalidosException {
        try {
            String texto = Files.readString(arquivo, StandardCharsets.UTF_8);
            Properties p = new Properties();
            if (texto.strip().startsWith("{")) {
                p.putAll(Json.ler(texto));
            } else {
                // formato das versões anteriores (chave=valor)
                p.load(new StringReader(texto));
            }
            return p;
        } catch (NoSuchFileException e) {
            throw new CarregamentoException("Arquivo não encontrado: " + arquivo.getFileName(), e);
        } catch (MalformedInputException | IllegalArgumentException e) {
            throw new DadosInvalidosException("Arquivo corrompido: " + arquivo.getFileName(), e);
        } catch (IOException e) {
            throw new CarregamentoException("Não foi possível ler " + arquivo.getFileName() + ": " + e.getMessage(), e);
        }
    }

    /** Grava por arquivo temporário + renomeação, para nunca deixar um save pela metade. */
    static void gravar(Path arquivo, Properties dados) throws SaveException {
        Path temporario = null;
        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            Files.createDirectories(pasta);
            temporario = Files.createTempFile(pasta, "colapso", ".tmp");
            // Grava e força os bytes no disco (fsync) antes de trocar o arquivo: se o computador desligar de
            // repente, fica o save antigo inteiro ou o novo inteiro, nunca um arquivo vazio ou pela metade.
            try (java.io.FileOutputStream saida = new java.io.FileOutputStream(temporario.toFile());
                 Writer escritor = new java.io.OutputStreamWriter(saida, StandardCharsets.UTF_8)) {
                escritor.write(Json.escrever(comoMapa(dados)));
                escritor.flush();
                saida.getFD().sync();
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new SaveException("Não foi possível gravar " + arquivo.getFileName() + ": " + e.getMessage(), e);
        } finally {
            if (temporario != null) {
                try {
                    Files.deleteIfExists(temporario);
                } catch (IOException ignorada) {
                    // nada a fazer: é só um arquivo temporário
                }
            }
        }
    }

    private static Map<String, String> comoMapa(Properties dados) {
        Map<String, String> campos = new TreeMap<>();
        for (String chave : dados.stringPropertyNames()) {
            campos.put(chave, dados.getProperty(chave));
        }
        return campos;
    }

    /** Arquivo com o nome antigo (ex.: slot1.save) passa a ter o novo (slot1.json), se o novo ainda não existir. */
    static void migrar(Path antigo, Path novo) {
        try {
            if (Files.exists(antigo) && !Files.exists(novo)) {
                Files.move(antigo, novo);
            }
        } catch (IOException e) {
            System.err.println("Não foi possível renomear " + antigo.getFileName() + ": " + e.getMessage());
        }
    }

    static String obrigatorio(Properties p, String chave) throws DadosInvalidosException {
        String valor = p.getProperty(chave);
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("Campo obrigatório ausente: " + chave);
        }
        return valor;
    }

    static int inteiro(Properties p, String chave, int minimo) throws DadosInvalidosException {
        int valor = paraInteiro(obrigatorio(p, chave), chave);
        if (valor < minimo) {
            throw new DadosInvalidosException("Valor fora do permitido em " + chave + ": " + valor);
        }
        return valor;
    }

    static int paraInteiro(String texto, String contexto) throws DadosInvalidosException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Número inválido em " + contexto + ": " + texto, e);
        }
    }

    /** "true"/"false" (estrito). Se a chave não existir, devolve o padrão. */
    static boolean booleano(Properties p, String chave, boolean padrao) throws DadosInvalidosException {
        String valor = p.getProperty(chave);
        if (valor == null) {
            return padrao;
        }
        if (valor.equals("true")) {
            return true;
        }
        if (valor.equals("false")) {
            return false;
        }
        throw new DadosInvalidosException("Valor booleano inválido em " + chave + ": " + valor);
    }

    /** Lista "a,b,c" -> ["a","b","c"]; vazio/ausente -> lista vazia. */
    static List<String> lista(Properties p, String chave) {
        String valor = p.getProperty(chave, "").trim();
        List<String> itens = new ArrayList<>();
        if (!valor.isEmpty()) {
            for (String parte : valor.split(",")) {
                itens.add(parte.trim());
            }
        }
        return itens;
    }

    /** Separa "chave:valor" em duas partes. */
    static String[] par(String texto, String contexto) throws DadosInvalidosException {
        String[] partes = texto.split(":", -1);
        if (partes.length != 2 || partes[0].isBlank() || partes[1].isBlank()) {
            throw new DadosInvalidosException("Formato inválido em " + contexto + ": " + texto);
        }
        return new String[]{partes[0].trim(), partes[1].trim()};
    }
}
