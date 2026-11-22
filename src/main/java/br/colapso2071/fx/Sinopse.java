package br.colapso2071.fx;

import java.util.List;

/** Sinopse curta (sem spoilers) mostrada no menu, em "Sinopse e Créditos". Não depende de JavaFX. */
public final class Sinopse {
    private Sinopse() {
    }

    public static List<String> curta() {
        return List.of(
                "SINOPSE",
                "Sertão, 2071. A Grande Estiagem esvaziou as cidades e transformou a água na moeda mais cara do Nordeste. "
                        + "Bom-Jardim, que um dia teve nove mil habitantes, feira de sábado e lagoa cheia, virou ruína. "
                        + "Só resistiu a antiga zona rural: pouco mais de quatrocentas pessoas em volta de um umbuzeiro, "
                        + "de um poço que ainda alcança água e de uma cisterna guardada por Milena.",
                "Você tem 27 anos e é a única pessoa que saiu viva do Boqueirão, um povoado destruído pela seca e pelo "
                        + "Coronel Vasques, um homem que vende água com fuzil. Você chega a Bom-Jardim desmaiado, com uma "
                        + "ficha de latão apertada na mão, e acorda numa rede, com uma caneca de água racionada nos lábios.",
                "Logo fica claro que alguma coisa está errada: números pintados nas portas, um drone que passa de "
                        + "madrugada, sacos sumindo do celeiro, a bomba do poço morrendo. A milícia do Coronel está a caminho, "
                        + "e alguém dentro do povoado está ajudando.",
                "Em dez capítulos, você vai ganhar (ou perder) a confiança de Milena, de Baltazar, do menino Sandro e de "
                        + "um povo inteiro; descobrir quem matou a cidade; e decidir se Bom-Jardim se rende, foge ou se levanta. "
                        + "Cada escolha custa água, comida e afinidade, e volta mais tarde. Há quatro finais.",
                "CRÉDITOS",
                "Colapso 2071 · Jogo narrativo interativo em Java (JavaFX).",
                "Projeto de EXA863 – MI Programação, UEFS.");
    }
}
