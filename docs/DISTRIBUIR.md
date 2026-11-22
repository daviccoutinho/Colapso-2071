# Como gerar o jogo para download

O jogo é empacotado com o **jpackage** (vem com o JDK 21). O resultado já traz o Java dentro: quem baixa não
precisa instalar Java, Maven nem nada. Cada sistema gera o próprio instalador (o jpackage não gera instalador de
Windows no Linux, nem o contrário).

| Sistema | O que sai em `dist/` | Como a pessoa usa |
|---|---|---|
| Linux | `colapso2071_<versão>_amd64.deb` | dois cliques (ou `sudo apt install ./colapso2071_*.deb`); aparece no menu, em Jogos |
| Linux | `Colapso2071-<versão>-linux-portatil.tar.gz` | descompacta e roda `Colapso2071/bin/Colapso2071` |
| Windows | `Colapso2071-<versão>.exe` | instalador com atalho na área de trabalho e no menu Iniciar (precisa do WiX para gerar) |
| Windows | `Colapso2071-<versão>-windows-portatil.zip` | descompacta e roda `Colapso2071\Colapso2071.exe` |
| macOS | `Colapso2071-<versão>.dmg` | abre e arrasta para Aplicativos |

Os saves do jogo instalado ficam na pasta do usuário (`~/.local/share/colapso2071/saves` no Linux,
`%APPDATA%\Colapso2071\saves` no Windows, `~/Library/Application Support/Colapso2071/saves` no macOS), porque a
pasta onde o programa é instalado não aceita gravação. Rodando pelo Maven, continua sendo a pasta `saves` do projeto.

## Opção 1: no seu computador

Quem gera os arquivos é o `Empacotar.java`, na raiz do projeto (só Java: ele chama o jpackage do próprio JDK).
No mesmo sistema para o qual você quer o instalador:

    mvn -DskipTests package
    java Empacotar.java

No IntelliJ: painel Maven › Lifecycle › **package**, depois abra `Empacotar.java` e clique no ▶ ao lado do `main`.

- No Linux, o `.deb` precisa do pacote `fakeroot` instalado; sem ele sai só a versão portátil `.tar.gz`.
- No Windows, o instalador `.exe` precisa do WiX Toolset 3 (https://wixtoolset.org); sem ele sai só a versão
  portátil `.zip`, que também funciona.

## Opção 2: GitHub Actions (os três sistemas de uma vez)

O arquivo `.github/workflows/instaladores.yml` gera tudo nos computadores do GitHub (Linux, Windows e macOS), sem
precisar de terminal:

1. Suba o projeto para um repositório no GitHub (pelo GitHub Desktop ou pelo terminal).
2. No site do repositório: **Releases** › **Create a new release** › em "Choose a tag" digite `v4.10` e clique em
   "Create new tag" › dê um título › **Publish release**.
3. Na aba **Actions**, acompanhe o "Instaladores" (uns 10 minutos). Quando terminar, os arquivos aparecem anexados
   na release, com link de download para qualquer pessoa.

Também dá para rodar sem criar release (aba Actions › Instaladores › Run workflow); aí os arquivos ficam em "Artifacts", no
fim da página da execução.

## Onde divulgar

- **GitHub Releases**: já sai pronto pela opção 2.
- **itch.io**: site de jogos independentes; crie a página do jogo e envie o `.zip`/`.tar.gz` de cada sistema (ou o
  instalador). É gratuito e aceita jogos de estudante.

## Avisos que a pessoa pode ver

- **Windows SmartScreen** ("O Windows protegeu o computador"): acontece com todo programa sem assinatura digital.
  Clique em "Mais informações" › "Executar assim mesmo". Para sumir, só assinando o instalador (certificado pago).
- **macOS** ("não pode ser aberto porque o desenvolvedor não pode ser verificado"): clique com o botão direito no
  app › Abrir. Para sumir, só com conta de desenvolvedor da Apple (paga).
- O pacote fica com uns 60 MB compactado porque leva o Java inteiro junto.
