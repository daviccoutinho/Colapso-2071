package br.colapso2071.fx;

import br.colapso2071.model.Jogo;

import java.util.List;

/** Texto das instruções, usado no menu principal e na pausa. Não depende de JavaFX. */
public final class Instrucoes {
    /** Mostrada no menu: ajuda a conferir se a versão em execução é a mais recente. */
    public static final String VERSAO = "4.10 · 22/11/2026";
    private Instrucoes() {
    }

    /** Resumo mostrado no começo de toda partida nova, antes da primeira cena. */
    public static List<String> inicio() {
        return List.of(
                "Você vai acordar em Bom-Jardim, um povoado do sertão que já foi um ótimo lugar para viver. "
                        + "Converse com as pessoas, faça escolhas e cuide dos seus recursos.",
                "Enter, Espaço ou clique: avançar o texto. Teclas 1 a 9 ou clique: escolher uma opção. "
                        + "Opções \"Retomar conversa\" voltam a um assunto da cena anterior sem avançar a história.",
                "I: mochila em grade 5x5, com descrição e uso de itens.  C: sua casa, para dormir, passar o dia e encher a garrafa.  "
                        + "F: ficha do personagem.",
                "S: salvar.  L: carregar.  H: instruções completas.  Esc: pausa.  F11: tela cheia.",
                "Água e comida ficam na barra superior. Se uma delas chegar a zero, cuide disso antes de seguir: encha "
                        + "a garrafa no poço do quintal e beba, ou pegue a marmita no depósito do Celeiro Velho e coma "
                        + "(uma vez por dia cada). O objetivo do capítulo fica no topo da tela.");
    }

    public static List<String> paragrafos() {
        return List.of(
                "OBJETIVO — Você tem 27 anos e chega ao que restou de Bom-Jardim, no sertão de 2071, quando a água é a moeda mais cara. "
                        + "Ajude (ou não) o povoado a atravessar a seca, a milícia do Coronel Vasques e uma traição. "
                        + "Suas decisões levam a um de quatro finais.",
                "COMO JOGAR — Leia as falas e avance com Enter, Espaço ou clique. Quando aparecerem opções, escolha com o "
                        + "mouse ou com as teclas 1 a 9. Opções com cadeado exigem algo que você ainda não tem; o motivo aparece ao lado.",
                "RECURSOS — Água, comida e reputação aparecem na barra superior. Escolhas e trabalhos gastam água e "
                        + "comida; a garrafa (Água +" + Jogo.AGUA_DA_GARRAFA + "), a marmita (Comida +" + Jogo.COMIDA_DA_MARMITA + ") e o café recuperam. Não deixe nenhuma das duas chegar a zero.",
                "CONVERSAS — Algumas opções são só conversa. Se você escolher uma, as outras não se perdem: na cena "
                        + "seguinte elas aparecem como \"Retomar conversa\", para você voltar ao assunto com aquela pessoa. "
                        + "Retomar não avança a história e cada conversa só pode ser retomada uma vez.",
                "CUSTO DAS ESCOLHAS — Abaixo de cada opção aparece o que ela exige (atributo, item, afinidade), o que "
                        + "ela custa em água e comida, e se abre uma missão nova. O que você ganha em reputação e "
                        + "afinidade fica para descobrir.",
                "DIAS — Cada noite de sono e cada fim de capítulo começam um dia novo, e cada dia gasta "
                        + Math.abs(Jogo.AGUA_AO_DORMIR) + " de água e " + Math.abs(Jogo.COMIDA_AO_DORMIR) + " de comida. "
                        + "Beba da garrafa e coma uma marmita uma vez por dia. Se dormir sem responder a alguém, essa pessoa "
                        + "vai estar na sua porta no dia seguinte, esperando a resposta.",
                "ITENS ESPECIAIS — A chave inglesa não se usa na mochila: ela libera escolhas de reparo e de forçar "
                        + "fechaduras. O café torrado precisa ser preparado (gasta um pouco de água). A foto da feira abre "
                        + "em tela cheia, o caderno da Estação 7 pode ser lido e o rádio pode ser sintonizado.",
                "ÁGUA E COMIDA — A água você tira do poço do quintal da casa azul: encha a garrafa térmica (uma vez por dia) e "
                        + "beba pela mochila. A comida você pega no depósito do Celeiro Velho: uma marmita por dia, que você "
                        + "come pela mochila. Se a água ou a comida chegar a zero, "
                        + "as escolhas ficam travadas e as ações Se hidratar e Se alimentar aparecem no topo das opções. Se a "
                        + "cota do dia já foi usada, descanse até o dia seguinte. Dormir com sede ou com fome tendo água ou "
                        + "comida disponível no dia cobra reputação (−" + Math.abs(Jogo.PENALIDADE_REPUTACAO) + " cada).",
                "OBJETIVO E DIÁRIO — Cada capítulo começa com um objetivo, que fica no topo da tela, embaixo do nome do "
                        + "capítulo. No menu (Esc), a opção Diário conta o que aconteceu em cada capítulo e o que você decidiu: "
                        + "bom para quem parou de jogar por uns dias.",
                "MÚSICA — Uma música calma, de viola e sanfona, acompanha o dia a dia. Nos confrontos ela dá lugar a um "
                        + "tema tenso, no luto a uma toada triste e nos momentos de esperança a um baião. Em Configurações › Som "
                        + "dá para ligar e desligar a música, mudar o volume, escolher a saída de áudio e testar.",
                "TELA CHEIA — F11 ou Alt+Enter liga e desliga a tela cheia a qualquer momento. O jogo se ajusta ao "
                        + "tamanho da tela sem cortar nada (com faixas nas laterais se a tela não for 16:9).",
                "CARAMELO — O vira-lata do povoado aparece em algumas cenas e dorme na varanda da casa azul. Faça carinho "
                        + "nele (opção na cena ou em casa, tecla C) uma vez por dia: ele faz festa para quem tem boa reputação, "
                        + "desconfia de quem ainda não mostrou quem é e morde quem anda fazendo mal ao povoado.",
                "LEMBRANÇAS — Em cada capítulo, um personagem pode contar um pedaço do passado dele. A opção "
                        + "\"Lembrança\" aparece junto das escolhas e só abre com afinidade suficiente. Ouvir não avança a história.",
                "CAPÍTULOS — Cada cena termina com uma pergunta de quem espera a sua resposta. Cada capítulo termina "
                        + "com uma conclusão, boa ou triste conforme o que você fez, que leva ao próximo.",
                "ATRIBUTOS — Liderança, Técnico e Negociação começam pelo perfil escolhido e crescem com suas decisões. "
                        + "Eles liberam opções especiais, como consertar a bomba sozinho ou negociar com o Coronel.",
                "PESSOAS — Milena, Baltazar, Sandro, Júlio e outros lembram do que você faz. A afinidade com cada um "
                        + "aparece na ficha (tecla F) e abre ou fecha caminhos mais adiante.",
                "MOCHILA E ITENS — Abra a mochila com a tecla I. Ela é uma grade 5x5: clique num item (ou use as setas) "
                        + "para ver o nome e a opção Ver descrição (D). Itens como Garrafa Térmica, Marmita de Feijão e Café Torrado "
                        + "têm um botão para Usar (Beber, Comer, Preparar; tecla U ou duplo clique) e recuperam recursos. Marmita e café são gastos; a garrafa térmica fica vazia e continua na mochila, "
                        + "esperando ser enchida em casa. Outros, como o Rádio e a Foto da Feira, "
                        + "podem ser usados quantas vezes quiser e contam um pouco da história. Itens de pista liberam escolhas.",
                "CASA E DESCANSO — Depois da chegada, Milena cede a você a casa azul no fim da rua. Volte a ela com a tecla C. "
                        + "Dormir passa para o dia seguinte e libera uma nova garrafa e uma nova marmita, mas o dia gasta "
                        + Math.abs(Jogo.COMIDA_AO_DORMIR) + " de comida e " + Math.abs(Jogo.AGUA_AO_DORMIR)
                        + " de água. Ao fim de cada capítulo, você descansa e começa um dia novo. Da casa você também vai ao "
                        + "poço do quintal encher a garrafa e ao depósito do Celeiro Velho buscar a marmita.",
                "SALVAR — O jogo salva sozinho depois de cada decisão, item usado ou noite dormida, e também ao fechar a "
                        + "janela. A gravação é feita direto no disco: mesmo que o computador desligue de repente, \"Continuar\" "
                        + "volta para a última ação. Você também pode salvar (S) e carregar (L) a qualquer momento, em três espaços.",
                "FINAIS — A reputação no povoado decide o desfecho: A Semente do Sertão, Sombra Solitária ou Sob o Calcanhar "
                        + "da Milícia. Há ainda um quarto final, Justiça para Bom-Jardim, para quem guardar as provas e levar o "
                        + "Coronel a um julgamento de verdade. Finais e conquistas alcançados ficam registrados na galeria do menu.",
                "ATALHOS — Enter/Espaço: avançar · 1–9: escolher · I: mochila · F: ficha · C: casa · S: salvar · "
                        + "L: carregar · H: instruções · Esc ou M: pausa · F11 ou Alt+Enter: tela cheia.");
    }
}
