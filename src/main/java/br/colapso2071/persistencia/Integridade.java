package br.colapso2071.persistencia;

import br.colapso2071.excecao.DadosInvalidosException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Selo de integridade dos saves: um SHA-256 calculado sobre todas as chaves (em ordem alfabética),
 * exceto o próprio selo. Detecta edição manual ou arquivo truncado/danificado.
 * Saves antigos, sem selo, continuam sendo aceitos.
 */
final class Integridade {
    static final String CHAVE = "integridade";

    private Integridade() {
    }

    static String calcular(Properties dados) {
        StringBuilder texto = new StringBuilder();
        for (String chave : new TreeSet<>(dados.stringPropertyNames())) {
            if (!chave.equals(CHAVE)) {
                texto.append(chave).append('=').append(dados.getProperty(chave)).append('\n');
            }
        }
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(texto.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível nesta JVM", e);
        }
    }

    static void selar(Properties dados) {
        dados.setProperty(CHAVE, calcular(dados));
    }

    /** Lança exceção se houver selo e ele não bater com o conteúdo. */
    static void verificar(Properties dados, String nomeDoArquivo) throws DadosInvalidosException {
        String selo = dados.getProperty(CHAVE);
        if (selo != null && !selo.equals(calcular(dados))) {
            throw new DadosInvalidosException("Save adulterado ou danificado: " + nomeDoArquivo);
        }
    }
}
