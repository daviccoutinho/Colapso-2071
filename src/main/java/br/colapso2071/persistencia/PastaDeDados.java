package br.colapso2071.persistencia;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

/**
 * Onde o jogo guarda saves, configurações e progresso.
 *
 * <ul>
 *   <li>Rodando pelo Maven ou pela IDE: a pasta {@code saves} do projeto (como sempre foi).</li>
 *   <li>Instalado (pelo instalador gerado com jpackage): a pasta de dados do usuário, porque a pasta do programa
 *       ({@code /opt}, {@code C:\Program Files}) não aceita gravação.
 *       Linux: {@code ~/.local/share/colapso2071/saves}; Windows: {@code %APPDATA%\Colapso2071\saves};
 *       macOS: {@code ~/Library/Application Support/Colapso2071/saves}.</li>
 *   <li>{@code -Dcolapso.saves=/um/caminho} força outra pasta.</li>
 * </ul>
 */
public final class PastaDeDados {
    private PastaDeDados() {
    }

    public static Path saves() {
        return saves(System.getProperty("colapso.saves"), System.getProperty("jpackage.app-path"),
                System.getProperty("os.name", ""), System.getProperty("user.home", "."), System.getenv());
    }

    /** Versão testável: recebe tudo o que normalmente vem do sistema. */
    static Path saves(String forcada, String appPath, String so, String home, Map<String, String> env) {
        if (forcada != null && !forcada.isBlank()) {
            return Path.of(forcada);
        }
        if (appPath == null || appPath.isBlank()) {
            return Path.of("saves");
        }
        return dadosDoUsuario(so, home, env).resolve("saves");
    }

    static Path dadosDoUsuario(String so, String home, Map<String, String> env) {
        String s = so.toLowerCase(Locale.ROOT);
        if (s.contains("win")) {
            String appData = env.get("APPDATA");
            return (appData != null && !appData.isBlank() ? Path.of(appData) : Path.of(home, "AppData", "Roaming"))
                    .resolve("Colapso2071");
        }
        if (s.contains("mac")) {
            return Path.of(home, "Library", "Application Support", "Colapso2071");
        }
        String xdg = env.get("XDG_DATA_HOME");
        return (xdg != null && !xdg.isBlank() ? Path.of(xdg) : Path.of(home, ".local", "share")).resolve("colapso2071");
    }
}
