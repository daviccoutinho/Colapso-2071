package br.colapso2071.persistencia;

import java.util.Map;
import java.util.TreeMap;

/**
 * Leitor e escritor de JSON feito só com Java, para os arquivos do jogo (saves, configurações e progresso).
 *
 * <p>Os arquivos são um objeto JSON simples, com um campo por linha em ordem alfabética:
 * <pre>
 * {
 *   "cena.id": "CENA_3A",
 *   "jogador.nome": "Davi",
 *   "versao": 2
 * }
 * </pre>
 * Números inteiros e {@code true}/{@code false} são gravados sem aspas; o resto vira texto. Ao ler, todo valor
 * volta como texto, exatamente como foi escrito, e {@code null} é tratado como campo ausente.
 */
public final class Json {
    private Json() {
    }

    /** Monta o texto JSON do objeto, com os campos em ordem alfabética. */
    public static String escrever(Map<String, String> campos) {
        StringBuilder sb = new StringBuilder("{");
        boolean primeiro = true;
        for (Map.Entry<String, String> campo : new TreeMap<>(campos).entrySet()) {
            sb.append(primeiro ? "\n" : ",\n");
            primeiro = false;
            sb.append("  ");
            texto(sb, campo.getKey());
            sb.append(": ");
            String valor = campo.getValue();
            if (ehInteiro(valor) || valor.equals("true") || valor.equals("false")) {
                sb.append(valor);
            } else {
                texto(sb, valor);
            }
        }
        return sb.append(primeiro ? "}\n" : "\n}\n").toString();
    }

    /**
     * Lê um objeto JSON cujos valores são texto, número, booleano ou null.
     *
     * @throws IllegalArgumentException se o texto não for um objeto JSON nesse formato
     */
    public static Map<String, String> ler(String json) {
        return new Leitor(json).objeto();
    }

    private static boolean ehInteiro(String valor) {
        // sem zero à esquerda, para "007" continuar sendo texto e voltar igual
        return valor.matches("-?(0|[1-9][0-9]{0,17})");
    }

    private static void texto(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    /** Leitura caractere a caractere (descendente recursivo, só com o que os arquivos do jogo usam). */
    private static final class Leitor {
        private final String s;
        private int i;

        Leitor(String s) {
            this.s = s;
        }

        Map<String, String> objeto() {
            Map<String, String> campos = new TreeMap<>();
            pularEspacos();
            esperar('{');
            pularEspacos();
            if (olhar() == '}') {
                i++;
            } else {
                while (true) {
                    pularEspacos();
                    String chave = texto();
                    pularEspacos();
                    esperar(':');
                    pularEspacos();
                    String valor = valor();
                    if (valor != null) {
                        campos.put(chave, valor);
                    } else {
                        campos.remove(chave);
                    }
                    pularEspacos();
                    char c = proximo();
                    if (c == '}') {
                        break;
                    }
                    if (c != ',') {
                        throw erro("esperava ',' ou '}'");
                    }
                }
            }
            pularEspacos();
            if (i < s.length()) {
                throw erro("conteúdo depois do fim do objeto");
            }
            return campos;
        }

        private String valor() {
            char c = olhar();
            if (c == '"') {
                return texto();
            }
            if (c == '-' || (c >= '0' && c <= '9')) {
                return numero();
            }
            if (s.startsWith("true", i)) {
                i += 4;
                return "true";
            }
            if (s.startsWith("false", i)) {
                i += 5;
                return "false";
            }
            if (s.startsWith("null", i)) {
                i += 4;
                return null;
            }
            throw erro(c == '{' || c == '[' ? "listas e objetos dentro de objetos não são usados" : "valor inválido");
        }

        private String numero() {
            int inicio = i;
            if (olhar() == '-') {
                i++;
            }
            while (i < s.length() && "0123456789.eE+-".indexOf(s.charAt(i)) >= 0) {
                i++;
            }
            String numero = s.substring(inicio, i);
            if (!numero.matches("-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?")) {
                throw erro("número inválido: " + numero);
            }
            return numero;
        }

        private String texto() {
            esperar('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = proximo();
                if (c == '"') {
                    return sb.toString();
                }
                if (c < 0x20) {
                    throw erro("quebra de linha dentro de texto");
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char e = proximo();
                switch (e) {
                    case '"', '\\', '/' -> sb.append(e);
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        if (i + 4 > s.length()) {
                            throw erro("\\u incompleto");
                        }
                        try {
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        } catch (NumberFormatException ex) {
                            throw erro("\\u inválido");
                        }
                        i += 4;
                    }
                    default -> throw erro("escape inválido: \\" + e);
                }
            }
        }

        private void pularEspacos() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }

        private char olhar() {
            if (i >= s.length()) {
                throw erro("fim inesperado");
            }
            return s.charAt(i);
        }

        private char proximo() {
            char c = olhar();
            i++;
            return c;
        }

        private void esperar(char c) {
            if (proximo() != c) {
                i--;
                throw erro("esperava '" + c + "'");
            }
        }

        private IllegalArgumentException erro(String motivo) {
            return new IllegalArgumentException("JSON inválido na posição " + i + ": " + motivo);
        }
    }
}
