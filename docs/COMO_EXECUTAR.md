# Como executar

Requisitos: JDK 21 e Maven.

## Gerar o jogo para download

Instaladores para Linux (.deb), Windows (.exe) e macOS (.dmg), com o Java embutido: veja `docs/DISTRIBUIR.md`
(`Empacotar.java`, ou o GitHub Actions em `.github/workflows/instaladores.yml`).

## Console
Execute a classe `Main` (no IntelliJ ou por `mvn compile` e `java -cp target/classes Main`).

## Menu JavaFX
No terminal do sistema, dentro da pasta do projeto:

    mvn javafx:run

Precisa de ambiente gráfico (variável `DISPLAY`). Em IntelliJ instalado por Flatpak o JavaFX não abre,
porque o sandbox não acessa a tela; use o terminal do sistema.

## Testes

    mvn test

São 186 testes JUnit 5 em `src/test/java/br/colapso2071/`:
`UnidadesTestesSuite` (24), `HistoriaTestesSuite` (72), `MvcTestesSuite` (16),
`PersistenciaTestesSuite` (34), `ColapsoTestesSuite` (9) e `FxLogicaTestesSuite` (31).

## Bibliotecas (versão 4.2)

O `pom.xml` agora inclui, além de `javafx-controls`:

- `org.openjfx:javafx-media` (trilha sonora com `MediaPlayer`);
- `io.github.mkpaz:atlantafx-base:2.0.1` (tema moderno dos controles, PrimerDark).

Na primeira execução o Maven baixa as duas. Se o sistema não tocar áudio ou o tema não carregar, o jogo segue
sem música ou com o tema padrão (o erro vai para o terminal). Música, volume e tema ficam em Configurações.

As quatro faixas (`src/main/resources/br/colapso2071/fx/som/*.wav`: neutra, tensa, triste e alegre) foram compostas para o jogo e não usam
amostras de terceiros.

## Atalhos durante a partida

| Tecla | Ação |
|-------|------|
| Enter / Espaço / clique | avançar o texto |
| 1–9 | escolher uma opção |
| I | mochila em grade 5x5: setas ou clique selecionam, U usa, D mostra a descrição |
| C | casa: encher a garrafa no poço do quintal, pegar a marmita no depósito, dormir na rede |
| H | instruções |
| F | ficha (atributos, missões, afinidades, decisões) |
| S | salvar em qualquer momento (pede confirmação ao sobrescrever) |
| L | carregar um save |
| F11 / Alt+Enter | liga e desliga a tela cheia (o jogo se ajusta à tela, sem cortar nada) |
| Esc / M | pausa: continuar, salvar, diário e objetivo, instruções, carregar, reiniciar, menu principal ou sair |

O jogo grava a partida no salvamento automático depois de cada ação e ao fechar a janela (escrita atômica com
`fsync`), então "Continuar" volta para a última decisão mesmo se o computador desligar de repente.
No console, a tecla D mostra o diário.

## Conferir a versão

O menu mostra a versão (ex.: "Versão 4.1 · 21/11/2026") e o título da janela traz "Bom-Jardim".
Se aparecerem nomes antigos (Cacimba Nova, Maria, Jonas, Tiquinho), você está rodando uma cópia antiga:
apague a pasta antiga do projeto, extraia o zip novo e rode `mvn clean javafx:run`.

## Avisos no terminal (Linux)

Os avisos abaixo não impedem o jogo de funcionar.

- **Música:** as faixas são WAV (PCM, mono, 22 kHz). Configurações › Som › Saída de áudio escolhe por onde o som sai;
  no automático o jogo tenta, nesta ordem: o áudio do Java (`javax.sound.sampled`), o tocador do sistema
  (`pw-play`, `paplay`, `aplay` ou `afplay`, chamado num processo separado) e o `MediaPlayer` do JavaFX. O botão
  "Testar som" mostra qual saída está tocando ou o motivo de não tocar.
  - "No line matching interface Clip" quer dizer que o Java não enxerga a placa de som (comum com PipeWire ou com JDK
    sem as bibliotecas de som); nesse caso o tocador do sistema assume.
  - "Could not create player!" é o JavaFX sem GStreamer/codecs; fica só como última opção.
  - O tocador do sistema tenta **todos** os que existirem (`pw-play`, depois `paplay`, depois `aplay`): o `pw-play`
    pode estar instalado sem o PipeWire rodando (Linux Mint usa PulseAudio) e sair com código 1. O erro de cada um
    aparece no terminal entre parênteses.
  - Se o jogo roda dentro de um Flatpak (por exemplo, pelo terminal do VS Code ou do IntelliJ instalados como
    Flatpak), o Java fica sem acesso ao servidor de som: o `paplay` responde "Conexão recusada", o `pw-play` "Host
    está desligado" e o `flatpak-spawn --host` "ServiceUnknown". O terminal (e Configurações › Som › Testar) mostra o
    comando para liberar, com o id do programa. Para o VS Code em Flatpak:

        flatpak override --user --socket=pulseaudio com.visualstudio.code

    Depois feche e abra o VS Code de novo. Outra saída é rodar o jogo por um terminal comum do sistema
    (`mvn javafx:run`), fora do editor. Os avisos "Gtk-Message: Failed to load module" também vêm daí: o Flatpak não
    tem os módulos GTK do sistema; fora dele eles somem.
  - Para conferir o som fora do jogo: `paplay /usr/share/sounds/alsa/Front_Center.wav` ou
    `aplay /usr/share/sounds/alsa/Front_Center.wav`.
- **"A restricted method ... has been called" / "native method ... has been bound":** avisos do Java 22+ porque o
  JavaFX carrega bibliotecas nativas. O `pom.xml` passa `--enable-native-access=javafx.graphics,javafx.media`.
- **"sun.misc.Unsafe::allocateMemory":** aviso do Java 23+ sobre código interno do JavaFX 21. O `pom.xml` tem um
  perfil que só liga no JDK 23 ou mais novo e passa `--sun-misc-unsafe-memory-access=allow` (essa opção não existe
  no JDK 21, por isso não fica fixa).
- **"Gtk-Message: Failed to load module xapp-gtk3-module / canberra-gtk-module":** a área de trabalho (Linux Mint /
  Cinnamon) pede, pela variável `GTK_MODULES`, módulos opcionais do GTK que não estão instalados. São inofensivos.
  O `pom.xml` força o GTK 3 (`-Djdk.gtk.version=3`), que é a versão para a qual esses módulos existem. Se continuar aparecendo, instale os módulos:
  `sudo apt install libcanberra-gtk-module libcanberra-gtk3-module xapp`.
