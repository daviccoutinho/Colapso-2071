# Arquitetura do Colapso 2071

Pacote raiz: `br.colapso2071`. Padrão MVC com camada de persistência separada.

| Pasta | Função | Arquivos para mostrar |
|---|---|---|
| `model/` | Regras e dados do jogo | `Cena`, `Escolha`, `Condicao`, `Rotas` |
| `view/` | Entrada e saída no console | `Console` (único `Scanner`), `MenuView` |
| `controller/` | Fluxo e ligação entre model e view | `MenuController`, `JogoController`, `SaveController` |
| `persistencia/` | Salvar e carregar arquivos | `SaveRepository`, `SaveSerializador`, `Integridade` |
| `excecao/` | Exceções personalizadas | `PersistenciaException` e filhas |
| `fx/` | Interface JavaFX (Fase 3) | `ColapsoApp`, `PartidaFx`, `TelaCena`, `TelaMenu`, `ContextoFx` |

## Fluxo de execução
1. `Main` cria o `Console` e chama `Aplicacao.criar(...)`.
2. `Aplicacao` monta Model, repositórios, views e controllers (Facade).
3. `MenuController` lê a opção de 1 a 9 e chama o controller correspondente.
4. A versão JavaFX usa `fx/Lancador` → `ColapsoApp`, reaproveitando model e repositórios.

## Partida em JavaFX
- `PartidaFx` faz o papel do `JogoController` do console: usa o mesmo `Jogo` (model), os mesmos repositórios
  e as mesmas regras de autosave, salvamento manual, conquistas e final.
- `Roteiro` transforma a cena em páginas curtas (título, narração, falas) e `Arte` escolhe cenário e sprites;
  as duas classes não dependem de JavaFX e têm testes (`FxLogicaTestesSuite`).
- `TelaCena` só desenha: cenário, personagens, barra de recursos, caixa de diálogo e escolhas
  (teclas 1..N, Enter/Espaço avança, F ficha, S salvar, M menu).
- Telas auxiliares: `TelaNovoJogo`, `TelaFicha`, `TelaFinal`, `TelaSaves` (também para salvar).

## Arquivos gerados em `saves/`
- `slot1.json` a `slot3.json`, `autosave.json` e os `.bak` de backup
- `config.json`: salvamento automático, mostrar status, nome padrão, música, tema e tela cheia
- `progresso.json`: finais, conquistas e partidas concluídas

## Persistência
- Formato JSON em UTF-8 (um objeto com um campo por linha, em ordem alfabética), lido e escrito pela classe `Json`,
  feita só com Java, sem biblioteca externa. Escrita atômica (arquivo temporário e depois move).
- Saves das versões anteriores (`slot1.save`, `config.properties`, no formato `chave=valor`) são renomeados para
  `.json` na primeira abertura e continuam sendo lidos.
- O save guarda só ids; o jogo é reconstruído a partir de `Rotas`.
- Selo SHA-256 (`Integridade`) detecta alteração; backup `.bak` permite recuperação.
- Save v2 é o atual; o v1 ainda é lido.

## Limitações conhecidas
- O código JavaFX (telas e `PartidaFx`) não tem teste automatizado; só `Roteiro` e `Arte` têm.
- A fonte pixelada ainda não é empacotada; o JavaFX usa fontes do sistema.
- O selo detecta, mas não impede adulteração.
- A história está definida em código.

## Feedback visual das escolhas (Efeitos)

`fx/Efeitos` guarda uma foto do estado do jogador antes de cada escolha e, depois dela, lista em frases
curtas o que mudou (atributos, recursos, reputação, afinidades, itens e missões). `PartidaFx` entrega essa
lista, junto com avisos de salvamento, à `TelaCena`, que a exibe como um aviso no canto da tela por alguns
segundos. A classe não depende de JavaFX e tem testes em `FxLogicaTestesSuite`. A ficha (`TelaFicha`)
também lista as últimas decisões tomadas.

## Conteúdo narrativo ampliado

- Personagens: Milena (guardiã da cisterna) e Júlio (contador de cotas), além de Baltazar, Sandro, Raimundo, Vasques e Dantas.
- Diálogos: cada cena tem de 4 a 7 falas, com vozes distintas (Baltazar fala pouco, Júlio usa números, Dantas hesita).
- Rádio de Manivela: item achado na Estação 7 (cena 3). Ele libera novas escolhas nas cenas 5, 7, 9A e 10A e a missão "Ouvir o Sertão", que termina na conquista "Liga das Águas".
- Novas opções com condições: liderança, negociação, item e decisões anteriores abrem caminhos diferentes (assembleia, voto em voz alta, fingir aceitar o acordo, lembrar os mortos do Boqueirão).

## Bom-Jardim, casa e itens usáveis

- O povoado se chama **Bom-Jardim**. Na cena 1, Milena e Sandro contam como era antes da Grande Estiagem
  (quintais com frutas, lagoa cheia, feira de sábado, São João, escola e cooperativa); a Foto da Feira de 2030
  e a casa da professora Lúcia retomam essa memória.
- **Itens usáveis** (`model/Uso`): cada `Item` pode ter um uso. Consumíveis (Cantil de Água, Marmita de Feijão,
  Café Torrado) recuperam recursos e somem; permanentes (Rádio, Foto, Chave Inglesa, Ficha) mostram uma mensagem.
  `Jogo.usarItem(id)` aplica o efeito; a `TelaMochila` mostra o botão Usar e agrupa itens repetidos.
- **Casa e dormir**: a partir da cena 1 o jogador tem a casa azul (`Jogo.temCasa`). `Jogo.dormir()` avança o dia,
  recupera energia e gasta um pouco de comida e água; só pode dormir uma vez por cena. Dia e último descanso entram
  no save como chaves opcionais (`jogador.dia`, `jogador.ultimaCenaDormida`), então saves antigos continuam válidos.
- **Instruções** (`fx/Instrucoes`): texto único usado no menu principal, na pausa e pela tecla H.

## Mochila em grade 5x5 e sprites de itens

`fx/GradeMochila` (sem JavaFX, testada) organiza o inventário numa matriz 5x5: itens iguais ocupam uma casa com
quantidade. `TelaMochila` desenha a matriz com o sprite de cada item (`item_*.png`, 32x32), destaca a casa selecionada e mostra ao lado o item, o botão Usar e a opção Ver descrição.
`Arte.iconeDoItem(id)` liga o id do item ao arquivo do sprite.

## Ajustes de roteiro

- Tiquinho passou a se chamar Sandro; o açude passou a ser a lagoa de Bom-Jardim.
- Na cena 1, Milena relata os acontecimentos estranhos (números pintados nas portas, drone de madrugada, cisterna
  baixando além do consumo, fitas nas cabras). Na cena 4A ela liga esses sinais à contagem da milícia.
- A memória dos bons tempos de Bom-Jardim fica no passeio pelo povoado (cena 2A).
- Falas que tratavam o sertanejo como quem não conhece o mar ou não estudou foram retiradas; Sandro agora fala dos
  medidores de pressão que a mãe ensinou a ler e do plano de estudar engenharia.
- No começo de cada partida aparece a tela "Como jogar".

## Interlúdios e garrafa térmica

- **Interlúdios** (`model/Interludio`, `Cena.comInterludio` e `Cena.comInterludioSe`): cada capítulo, do 2 ao 10,
  abre com parágrafos narrados sobre os dias que se passaram. Alguns só aparecem conforme decisões anteriores
  (`Condicao.decisao`, `Condicao.item`), então as escolhas voltam a pesar mais tarde. `Roteiro` (JavaFX) e
  `JogoView` (console) mostram o interlúdio depois do título do capítulo.
- Acontecimentos novos: o enterro de Dona Josefa, a febre do filho de Damião, o registro da Estação 7 que mostra
  que Vasques mandou desligar a adutora em 2052, o crachá de Rosa Lima (mãe de Sandro), Caetano amarrado por Dantas, a mando de Vasques, ao
  Umbuzeiro (o protesto do povoado é reprimido e não muda nada), a família de Dantas refém no Boqueirão, o diesel no Olho, a morte de Damião na cerca leste e a de
  Seu Raimundo salvando o porão no incêndio. O povoado termina com 85 dos 94 moradores.
- **Garrafa térmica** (`Uso.esvazia`): ao beber, `Jogo.usarItem` troca a garrafa cheia pela vazia, que continua
  na mochila. `Jogo.encherGarrafa` enche no filtro de casa uma vez por dia (`jogador.diaGarrafa` no save), e
  algumas escolhas (`Consequencia.encheGarrafa`) representam alguém dividindo água com o jogador.

## A cidade de Bom-Jardim e as conversas retomáveis

- **Ambientação:** Bom-Jardim era uma cidade pequena (cerca de 9 mil habitantes no censo de 2048). Com a seca de 2049
  e o corte da adutora em 2052, o centro, todo encanado, secou primeiro e virou ruína; a maioria se mudou no primeiro
  ano e muitos dos que ficaram morreram. O "povoado" do jogo é a antiga zona rural, que sobreviveu por estar bem
  localizada: o Poço da Santa alcança um lençol fundo, a cisterna fica sob o Umbuzeiro e o baixio guarda umidade.
  Milena, Seu Raimundo e Sandro contam isso no capítulo 1. O protagonista tem 27 anos (`Jogador.IDADE`).
- **Conversas retomáveis:** `Escolha.conversa(textoRetomar, reacao...)` marca uma opção como conversa. As opções de
  conversa não escolhidas na cena anterior aparecem como "Retomar conversa" (`Jogo.conversasPendentes`);
  `Jogo.retomarConversa` aplica o efeito sem avançar de cena e sem contar como decisão. Cada conversa é retomada uma
  vez (`conversas` no save). Disponível na interface JavaFX; no menu de console as cenas seguem como antes.

## Estrutura atual da história (25 cenas, 24 decisões)

| Cap. | Cenas | Novidade |
|------|-------|----------|
| 1 | 1A, 1 | A cidade de ~9 mil habitantes e a zona rural que sobreviveu (cerca de 412 pessoas, 112 casas). |
| 2 | 2A, 2 | Conserto da bomba. |
| 3 | 3A, 3, **3B** | **A Junta e a Assembleia**: o jogador traz a peça que salva a bomba e Milena propõe que ele tenha voz e voto no conselho (conquista "Voz e Voto"). Isso justifica as decisões coletivas a partir do capítulo 4. |
| 4–5 | 4A, 4, 5A, 5 | Cobrança da milícia; Caetano amarrado por Dantas; protesto reprimido. |
| 6 | 6A, 6, **6B** | **O Chamado**: jogador e Baltazar pedem apoio ao povo em assembleia; 180, 120 ou poucas dezenas entram no mutirão de defesa, conforme o argumento. |
| 7 | 7A, 7, **7B** | **A Cerca Leste**: o confronto em detalhe; Damião morre no portão. O tamanho do mutirão aparece na batalha. |
| 8 | 8A, 8 | A traição de Júlio. |
| 9 | 9A, 9, **9B** | **O Levante**: o povo se ergue contra a milícia; Baltazar morre em todos os caminhos. |
| 10 | 10A, **10B**, 10 | Julgamento; **A Pedra dos Nomes** (31 mortos, entre eles Josefa, Damião, Raimundo e Baltazar). |

Nos testes, `HistoriaTestesSuite.comNovas` converte as sequências antigas (19 decisões) para a estrutura atual,
inserindo uma opção padrão nas cinco cenas novas (0 solidária, 1 neutra, 2 egoísta).

## Pergunta da cena, conclusão do capítulo, necessidades, lembranças e o final da justiça

- **Pergunta principal** (`Cena.comPergunta`): cada cena termina com a pergunta de quem espera a resposta
  (ex.: "Você vai nos ajudar?"). Ela é a última página antes das opções e reaparece quando o jogador volta de uma
  conversa retomada, de uma lembrança, da mochila ou da casa (`Roteiro.daPergunta`).
- **Conclusão do capítulo** (`Cena.comConclusao` / `comConclusaoSe`): a última cena dos capítulos 1 a 9 tem uma
  conclusão narrada depois da escolha, com parágrafos que dependem do que o jogador fez e um gancho para o
  capítulo seguinte (`Roteiro.daConclusao`, `JogoView.exibirConclusao`).
- **Necessidades** (`Jogo.necessidades`): energia, água ou comida em zero travam as escolhas. Sono se resolve
  dormindo em casa (permitido mesmo já tendo dormido na cena, se o jogador estiver exausto); sede, bebendo da
  garrafa térmica (a casa tem uma; com sede, dá para encher mesmo já tendo enchido no dia); fome, buscando uma
  marmita no depósito (`Jogo.buscarComida`, uma por dia, ou sempre que houver fome) e comendo pela mochila.
  No console, `Jogo.atenderNecessidades` faz isso automaticamente e mostra o que foi feito.
- **Lembranças** (`model/Lembranca`): uma por capítulo, cada uma de um personagem (Milena, Júlio, Baltazar,
  Sandro, Raimundo e Dantas). Aparecem como opção "Lembrança" e só abrem com afinidade mínima; ouvir não avança a
  história, dá +1 de afinidade e fica no save (`conversas`, prefixo `L:`).
- **Final da justiça** (`FINAL_JUSTICA`): na cena 10A, quem tem o Caderno da Estação 7 (recebido na assembleia
  do capítulo 3) e não entregou o Olho ao Coronel pode propor um julgamento de verdade
  (`Jogo.ESCOLHA_TRIBUNAL`). O epílogo mostra o processo: captura sem violência, juíza, defensor público, provas,
  testemunhas, condenação de Vasques e penas individuais para os milicianos, conforme o que cada um fez.

## Versão 4.0: dia para pensar, custos visíveis, alertas e elenco enxuto

- **CENA_1B · Um Dia em Bom-Jardim:** quem pede um dia para pensar (CENA_1, opção 2) não pula para o capítulo 2:
  passa o dia no povoado e só então responde a Milena. As condições "aceitou/recusou ajudar" usam
  `Condicao.ou` para valer nas duas cenas. São 26 cenas; a partida tem 24 ou 25 decisões.
- **Custo e efeito de cada opção:** `Consequencia.resumo` e `Escolha.resumoParaJogador` descrevem o que a opção
  exige e muda (água, comida, energia, reputação, atributos, afinidades, itens, missões). A linha aparece abaixo
  da opção no JavaFX (`TelaCena.Opcao.detalhe`) e no console (`JogoView.lerAcao`).
- **Alertas de necessidade:** "É necessário descansar / se hidratar / se alimentar" aparece numa faixa fixa
  (`TelaCena.alertar`) e as ações que resolvem (dormir, encher e beber, buscar e comer) entram no topo da lista
  de opções, para o jogador nunca ficar preso (correção do travamento na CENA_5).
- **Conquistas:** `Efeitos.comparar` anuncia "Conquista desbloqueada: …" junto dos outros efeitos.
- **Elenco:** Sandro e Seu Raimundo deixaram de falar em cenas em que não eram relevantes; o Coronel não exige mais
  "voluntários" (a cobrança é água, sacos e as chaves da cisterna).
- **Sinopse:** `fx/Sinopse` (menu "Sinopse e Créditos") e `docs/SINOPSE.md` (sinopse, personagens, resumo por
  capítulo e finais).

## Versão 4.1: o protagonista fala, eventos em cena e itens com uso próprio

- **Fala do protagonista:** `Escolha.comFalaDoJogador` dá a cada opção uma resposta dita pelo protagonista, mostrada
  antes da reação dos outros (`Pagina.JOGADOR`: retrato e nome do jogador na caixa de diálogo; no console,
  "Nome: fala"). As lembranças começam com a pergunta do protagonista (`Lembranca.perguntaDoJogador`, ex.: "Ouvi
  dizer que você quase foi embora de Bom-Jardim, Milena. É verdade?").
- **Eventos que acontecem em cena:** o castigo de Caetano virou a cena jogável CENA_5P (abre o capítulo 5), com
  Vasques dando a ordem, Dantas obedecendo, Milena e Baltazar protestando e quatro escolhas. Interlúdios ganharam
  falas (`Cena.comInterludioFala`): Sandro com o crachá da mãe, a confissão de Dantas, Milena diante do diesel, o
  enterro de Damião. Na morte de Baltazar, a narração vem antes das últimas palavras dele (`comNarracaoAntes`).
- **Consumo diário:** cada noite de sono e cada fim de capítulo começam um dia novo e gastam 20 de água e 20 de
  comida; garrafa e marmita recuperam 20. Se o jogador dormir no meio de uma cena, quem esperava a resposta está na
  porta no dia seguinte (`Roteiro.daEsperaNaPorta`).
- **Opções:** mostram só o que exigem, o que custam (água, comida, energia) e missões novas; ganhos ficam escondidos.
- **Itens:** `Uso` tem verbo (Beber, Comer, Preparar, Ler, Ver, Sintonizar), páginas (caderno da Estação 7, lido
  numa tela de texto) e imagem (Foto da Feira de 2030, em tela cheia via `TelaImagem`). O café precisa de água para
  ser preparado; o rádio só pega chiado fora das cenas; a chave inglesa não tem uso na mochila e é exigida em
  escolhas de reparo e de forçar fechaduras (CENA_5A e CENA_9A).
- **Avisos:** cartões no canto da tela para item adicionado (com botão "Abrir inventário"), conquista desbloqueada e
  necessidades (descansar, se hidratar, se alimentar); o protagonista aparece empoeirado na primeira cena, antes de
  trocar os trapos do Boqueirão pela roupa que Milena separa.

## Versão 4.2: água e comida ensinadas, sem energia, salvamento contínuo, objetivo, diário e trilha sonora

- **Sem energia:** `Jogo.Necessidade` tem só SEDE e FOME. O descanso continua: no fim de cada capítulo alguém manda
  o protagonista dormir (`Cena.comFalaDeDescanso`), e a conclusão termina com "Você volta para a casa azul e dorme.
  Começa o dia N." (`Roteiro.daConclusao` e `JogoView.exibirConclusao`).
- **Água e comida:** a garrafa enche no filtro de casa e a marmita sai do depósito do Celeiro Velho, cada um uma vez
  por dia. Com água ou comida em zero, as escolhas travam e as ações "Se hidratar" / "Se alimentar" (ou "Descansar até
  amanhã", se a cota do dia já foi usada) aparecem no topo. Na primeira vez, Milena ensina o caminho
  (`Roteiro.daTutorial`, marcado no save como `T:SEDE` / `T:FOME`).
- **Penalidade:** dormir com água ou comida em zero tendo como resolver no mesmo dia custa reputação
  (`Jogo.PENALIDADE_REPUTACAO = -2` cada). Quem já usou a cota do dia descansa sem penalidade. As mensagens saem de
  `Jogo.consumirPenalidades()` para os avisos da tela e do console.
- **Salvamento contínuo:** `PartidaFx.salvarContinuo()` grava o salvamento automático a cada troca de tela depois de
  uma ação; `ColapsoApp` grava ao fechar a janela, em `stop()` e num *shutdown hook*. `ArquivoDados.gravar`
  escreve num temporário, chama `FileDescriptor.sync()` e faz `ATOMIC_MOVE`: um desligamento abrupto deixa o arquivo
  antigo ou o novo, nunca um pela metade (e há o backup).
- **Avisos somem:** cartões saem em 5 s e a faixa vermelha de necessidade some em 6 s (as ações continuam na lista).
- **Primeira vez no jogo:** cada capítulo tem objetivo (`Cena.comObjetivo`, na abertura e no topo da tela) e resumo
  (`Cena.comResumo`). `Jogo.diario()` junta os resumos dos capítulos vividos com as decisões do jogador (Esc → Diário;
  tecla D no console). Cenas de contexto (falas na abertura dos capítulos 2, 3, 4, 6 e 10) explicam o Poço e o Olho,
  a cota diária, a junta de vedação, a cobrança e o conselho.
- **Trilha sonora:** `Trilha` (sem JavaFX, testada) escolhe o clima por capítulo, cena e final; `Musica` toca com
  `MediaPlayer` em loop e troca de faixa com *fade*. Três faixas compostas para o jogo: baião em Sol
  mixolídio (sanfona, zabumba, triângulo), toada em Lá menor (viola em Karplus-Strong, pífano) e ostinato em Ré frígio
  (cordas graves, rabeca, chapa). Loops sem emenda (a cauda do reverb volta para o começo) e volume equilibrado
  (cerca de −19 a −20 LUFS).
- **Interface:** tema AtlantaFX PrimerDark por baixo da identidade visual do jogo (`Tema` e `Estilo`), transição de 250 ms entre telas
  (`Navegador`), objetivo do capítulo no topo, e opções de música, volume e tema em Configurações.

## Versão 4.3: música neutra, tela cheia, interface redesenhada e lugares do dia a dia

- **Água e comida:** a garrafa cheia dá Água +45 e a marmita Comida +45 (`Jogo.AGUA_DA_GARRAFA` e
  `Jogo.COMIDA_DA_MARMITA`); o café dá Comida +15. A água agora vem do poço de balde no quintal da casa azul (uma
  garrafa por dia). A penalidade de reputação vale só para quem escolhe dormir com sede ou fome tendo como resolver
  no mesmo dia; a passagem de capítulo não penaliza.
- **Música neutra:** quarta faixa (`neutra`, viola dedilhada e sanfona de fundo em Ré mixolídio). `Trilha.daCena`
  toca a neutra por padrão; a tensa só nos confrontos (CENA_4A, 5P, 7, 7B, 9A, 9, 9B), a triste no luto (8A, 10A) e a
  alegre nos respiros (3B, 10).
- **Tela cheia:** `Navegador` desenha todas as telas numa área fixa de 1280×720 dentro de um `Group` escalado para
  caber na janela (mesma proporção, faixas pretas). F11 ou Alt+Enter alternam; a tela cheia continua ao trocar de
  tela; Esc deixou de sair da tela cheia (era a tecla da pausa); os diálogos (`Navegador.confirmar/avisar`) têm a
  janela como dona, para não sumirem atrás dela. Opção "Tela cheia" em Configurações.
- **Interface:** menu principal com véu escuro, poeira dourada animada, cartão de opções com ícone, detalhe e tecla
  (`Componentes.botaoMenu`), rodapé de atalhos. Pausa, mochila, ficha, textos, saves, galeria e configurações abrem
  num cartão sobre a última cena desfocada (`Navegador.ultimaCena`, `Componentes.fundoDesfocado`). Diálogos no
  estilo do jogo.
- **Personagens:** retratos com sombra suave e respiração lenta; os bonecos na cena também respiram, cada um no seu
  ritmo.
- **Lugares:** três cenários novos em pixel art: a casa azul por dentro (fundo da tela
  da casa e do amanhecer), o poço do quintal (mostrado ao encher a garrafa) e o depósito do Celeiro Velho de dia
  (mostrado ao buscar a marmita), via `PartidaFx.mostrarLocal`. A placa da chegada agora diz BOM-JARDIM · POP. 412.

## Versão 4.4: personagens redesenhados e o Caramelo

- **Retratos novos**: busto de frente, 80×104, numa moldura de madeira
  laranja com fundo de papel envelhecido. Rampas de pele quentes com mudança de matiz (sombra para o vinho, luz para o
  laranja), olhos com cílio grosso e brilho branco, nariz desenhado só com sombra, boca em três tons. Cada personagem
  tem cabelo, chapéu e roupa próprios: Milena de cabelos longos e escuros com fios grisalhos, Baltazar com chapéu de
  couro e barba branca, Sandro de cachos com o crachá da mãe, Júlio de óculos e colete, Seu Raimundo careca e de
  bigode, o Coronel de terno e panamá brancos, Dantas de quepe e barba por fazer, e os três protagonistas.
- **Corpo inteiro novo**: figuras em três quartos, cabeça grande, contorno
  azul-noite (vinho na pele), três tons por material e luz da direita; os NPCs olham para a esquerda, para o
  protagonista. Os pés continuam na linha 77 do sprite e a sombra no chão é translúcida.
- **Na cena:** os retratos emoldurados ficam inteiros acima da caixa de diálogo (`TelaCena.Y_RETRATO`).
- **Caramelo** (`Rotas.NPC_CARAMELO`): vira-lata do povoado, com retrato e sprite em pé, latindo e sentado (o latido
  é só visual, sem som: o boneco troca para a pose de latido). Aparece na chegada, na
  apresentação do povoado, na cobrança da milícia, no diesel do Olho, no enterro de Damião, no incêndio e no final.
  `Jogo.fazerCarinho()` (uma vez por dia, salvo em `jogador.diaCarinho`): reputação ≥ 5 → festa (reputação +1,
  afinidade +1, conquista "Melhor Amigo"); entre 0 e 4 → desconfiado; negativa → mordida (reputação −1, conquista
  "Cachorro Não Se Engana"). Disponível nas cenas em que ele aparece e na casa; no console, tecla P.

## Versão 4.5: personagens no estilo dos RPGs de fazenda em pixel art

- **Retratos 64×64**, fundo transparente: rosto redondo, olhos
  grandes com íris em dois tons, pupila e brilho, bochechas coradas, nariz e boca pequenos, cabelo em mechas grossas
  com faixa de brilho e contorno marrom-escuro. O retrato aparece num quadro de madeira à direita da caixa de texto,
  com o nome embaixo (`TelaCena.cartaoRetrato`); o do protagonista aparece quando ele fala.
- **Bonecos 16×32**, de frente, cabeça grande, dois ou três tons por material, montados
  a partir de mapas de pixels (rosto, corpo, corpo com saia, corpo de criança, cabelos e chapéus). Ampliados 4×, como
  o cenário, ficam na proporção das casas; os pés na penúltima linha, a sombra na última. O Caramelo tem 24×17.

## Versão 4.6: música em WAV

- As quatro faixas passaram de MP3 para WAV (PCM 16 bits, mono, 22 050 Hz). No Linux, o `MediaPlayer` só abre MP3 se houver uma `libavcodec` compatível; WAV é lido pelo próprio
  javafx-media. O `pom.xml` passa `--enable-native-access=javafx.graphics,javafx.media` para o Java não avisar sobre o
  código nativo do JavaFX.
- `Musica` passou a tocar pelo Java Sound (`Clip` em loop, volume pelo `MASTER_GAIN`, fade cruzado numa thread
  própria "musica"). Se a placa não aceitar 22 kHz mono, o PCM é reamostrado para 44,1 kHz estéreo
  (`Musica.para44kEstereo`). O `MediaPlayer` do JavaFX virou reserva.

## Versão 4.8: som que funciona no Linux, personagens de frente e animados, mais casas

- **Som:** `Musica` tem três saídas (`Musica.Saida`): Java Sound, tocador do sistema e JavaFX Media. No automático,
  usa a primeira que tocar. `TocadorSistema` procura `pw-play`, `paplay`, `aplay` ou `afplay` no `PATH`, toca o WAV
  num processo separado e repete quando acaba; o volume vai numa cópia do WAV gravada na altura certa (de 10 em 10%)
  em `/tmp/colapso2071-som`. Configurações › Som: música ligada, volume, saída de áudio (salva em
  `config.saidaAudio`), "Testar som" e a saída em uso.
- **Avisos:** perfil Maven `jdk23-ou-mais` (`--sun-misc-unsafe-memory-access=allow` só no JDK 23+) .
- **Bonecos:** todos de frente para a câmera, inclusive o Caramelo (sentado, latindo de frente). Contorno colorido
  (cada borda pega um tom bem escuro da região vizinha), cinto, luz na calça e no bico do sapato, braços separados
  por sombra em vez de linha preta, óculos do Júlio redesenhados. Cada boneco tem uma versão de olhos fechados
  (`fig_*_pisca.png`) e pisca de 2,5 a 6 s; a respiração ficou mais sutil (2 px).
- **Retratos animados:** cada retrato tem boca aberta (`ret_*_fala.png`) e olhos fechados (`ret_*_pisca.png`).
  Enquanto o texto aparece, a boca abre e fecha; de vez em quando o personagem pisca. As animações param quando a
  tela sai (`sceneProperty`), para não acumular.
- **Cenários:** casario ao fundo (`cenas2.casario`/`casinha`): casinhas coloridas de fachada simples ou com
  platibanda na praça, no poço, na chegada e, à noite, com janelas acesas atrás do Celeiro Velho.

## Versão 4.9: tocador do sistema mais teimoso

- `TocadorSistema.candidatos()` lista todos os tocadores encontrados (e, dentro de um Flatpak, os do sistema via
  `flatpak-spawn --host`); `Musica` tenta um por um até algum tocar. O erro de cada tocador (saída do processo) vai
  para a mensagem. As cópias de volume ficam em `~/.cache/colapso2071/som` (visível também fora do Flatpak).
- `pom.xml`: `-Djdk.gtk.version=3`, para o JavaFX não cair no GTK 2 (que é o que gera "Failed to load module").

## Versão 4.11: só Java

- **Saves em JSON:** `persistencia/Json` lê e escreve JSON sem biblioteca externa; `ArquivoDados` (antes
  `ArquivoProperties`) usa esse formato e ainda aceita os arquivos antigos. Testes novos cobrem acentos, aspas,
  números, arquivo mal formado e a migração dos saves antigos.
- **Instaladores em Java:** `Empacotar.java`, na raiz, chama o jpackage do JDK (`ToolProvider`) e gera o `.tar.gz`
  (com permissão de execução), o `.zip`, o `.deb`, o `.rpm`, o `.exe` e o `.dmg` conforme o sistema. Substitui os
  scripts de terminal; o GitHub Actions roda `java Empacotar.java`.
- **Visual em Java:** a folha de estilos foi trocada por `fx/Estilo`. As telas continuam marcando os nós com nomes de
  estilo (`cartao`, `botao-menu`, `cena-caixa`...); `Estilo` acompanha a árvore de cada cena, junta as regras que
  valem para o nó (nome de estilo, estado — mouse por cima, foco, clicado, desabilitado, selecionado — e, em algumas,
  o ancestral) e aplica com a API do JavaFX (`setBackground`, `setBorder`, `setFont`, `setTextFill`, `setEffect`).
  Regras mais específicas vencem; entre iguais, vale a declarada depois. A fonte é herdada do ancestral mais próximo
  que define uma.

