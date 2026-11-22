package br.colapso2071.fx;

import br.colapso2071.model.Cena;
import br.colapso2071.model.Rotas;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Liga o que a história pede à arte pronta: qual cenário aparece em cada cena, qual sprite
 * representa cada personagem. Só devolve nomes de arquivo; não depende de JavaFX, então é testável.
 */
public final class Arte {
    private static final Map<String, String> NPCS = Map.of(
            Rotas.NPC_MILENA, "fig_milena.png",
            Rotas.NPC_JULIO, "fig_julio.png",
            Rotas.NPC_BALTAZAR, "fig_baltazar.png",
            Rotas.NPC_SANDRO, "fig_sandro.png",
            Rotas.NPC_RAIMUNDO, "fig_raimundo.png",
            Rotas.NPC_VASQUES, "fig_vasques.png",
            Rotas.NPC_DANTAS, "fig_dantas.png",
            Rotas.NPC_CARAMELO, "fig_caramelo.png");

    private static final Map<String, String> PERFIS = Map.of(
            Rotas.PERFIL_MECANICO, "fig_faz_tudo.png",
            Rotas.PERFIL_NEGOCIADOR, "fig_mascate.png",
            Rotas.PERFIL_LIDER, "fig_chefe.png");

    /** Cenário de cada capítulo (1 a 10). */
    private static final Map<Integer, String> FUNDO_DO_CAPITULO = Map.of(
            1, "cena_praca.png",
            2, "cena_poco.png",
            3, "cena_chegada.png",
            4, "cena_praca.png",
            5, "cena_celeiro.png",
            6, "cena_menu.png",
            7, "cena_poco.png",
            8, "cena_celeiro.png",
            9, "cena_praca.png",
            10, "cena_praca.png");

    private static final Map<String, String> RETRATOS_NPC = Map.of(
            Rotas.NPC_MILENA, "ret_milena.png",
            Rotas.NPC_JULIO, "ret_julio.png",
            Rotas.NPC_BALTAZAR, "ret_baltazar.png",
            Rotas.NPC_SANDRO, "ret_sandro.png",
            Rotas.NPC_RAIMUNDO, "ret_raimundo.png",
            Rotas.NPC_VASQUES, "ret_vasques.png",
            Rotas.NPC_DANTAS, "ret_dantas.png",
            Rotas.NPC_CARAMELO, "ret_caramelo.png");

    private static final Map<String, String> RETRATOS_PERFIL = Map.of(
            Rotas.PERFIL_MECANICO, "ret_faz_tudo.png",
            Rotas.PERFIL_NEGOCIADOR, "ret_mascate.png",
            Rotas.PERFIL_LIDER, "ret_chefe.png");

    /** Linha do chão (px do cenário 320×180) onde os personagens pisam; todos os cenários seguem isso. */
    public static final int PISO_Y = 127;

    private Arte() {
    }

    /** Arquivo do sprite do personagem, ou null se não houver arte para ele. */
    public static String sprite(String npcId) {
        return NPCS.get(npcId);
    }

    /** Sprite do jogador conforme o perfil; perfis desconhecidos usam o Faz-tudo. */
    public static String spriteDoPerfil(String perfilId) {
        return PERFIS.getOrDefault(perfilId, "fig_faz_tudo.png");
    }

    /** Retrato de busto (64×64) usado no quadro ao lado da caixa de diálogo, ou null se não houver. */
    public static String retrato(String npcId) {
        return RETRATOS_NPC.get(npcId);
    }

    /** Variação de um retrato ou boneco: "ret_milena.png" + "fala" → "ret_milena_fala.png". */
    public static String variacao(String arquivo, String sufixo) {
        if (arquivo == null || !arquivo.endsWith(".png")) {
            return arquivo;
        }
        return arquivo.substring(0, arquivo.length() - 4) + "_" + sufixo + ".png";
    }

    /** Variações que existem para os retratos (boca aberta, olhos fechados) e bonecos (olhos fechados). */
    public static final java.util.List<String> VARIACOES_RETRATO = java.util.List.of("fala", "pisca");

    /** Retrato do jogador conforme o perfil; perfis desconhecidos usam o Faz-tudo. */
    public static String retratoDoPerfil(String perfilId) {
        return RETRATOS_PERFIL.getOrDefault(perfilId, "ret_faz_tudo.png");
    }

    public static String fundo(Cena cena) {
        if ("CENA_1A".equals(cena.getId())) {
            return "cena_chegada.png"; // quando o protagonista acorda, ainda nos arredores do povoado
        }
        return FUNDO_DO_CAPITULO.getOrDefault(cena.getNumeroCapitulo(), "cena_praca.png");
    }

    public static String fundoDoFinal(String finalId) {
        return switch (finalId) {
            case "FINAL_BOM", "FINAL_JUSTICA" -> "cena_final_bom.png";
            case "FINAL_RUIM" -> "cena_final_ruim.png";
            default -> "cena_menu.png";
        };
    }

    /** Sprite 32x32 do item na mochila: ITEM_CHAVE_INGLESA → item_chave_inglesa.png. */
    public static String iconeDoItem(String itemId) {
        String base = itemId.startsWith("ITEM_") ? itemId.substring(5) : itemId;
        return "item_" + base.toLowerCase(java.util.Locale.ROOT) + ".png";
    }

    public static final int LADO_ICONE_ITEM = 32;

    /** Caramelo latindo (quando é a vez dele "falar") e sentado (em casa). */
    public static final String CARAMELO_LATINDO = "fig_caramelo_latindo.png";
    public static final String CARAMELO_SENTADO = "fig_caramelo_sentado.png";

    /** Lugares do dia a dia: a casa azul por dentro, o poço do quintal e o depósito do Celeiro Velho. */
    public static final java.util.List<String> LUGARES = java.util.List.of("cena_casa.png", "cena_quintal.png", "cena_deposito.png");

    /** Ícones das telas redesenhadas (casa, pausa, mochila). */
    public static final java.util.List<String> ICONES_INTERFACE = java.util.List.of(
            "ico_rede.png", "ico_casa.png", "ico_mochila.png", "ico_diario.png", "ico_disquete.png");

    /** Todos os arquivos que esta classe pode devolver (usado nos testes). */
    public static Set<String> todos() {
        Set<String> s = new LinkedHashSet<>();
        s.addAll(NPCS.values());
        s.addAll(PERFIS.values());
        s.addAll(FUNDO_DO_CAPITULO.values());
        s.addAll(RETRATOS_NPC.values());
        s.addAll(RETRATOS_PERFIL.values());
        s.add("cena_final_bom.png");
        s.add("cena_final_ruim.png");
        s.add("cena_menu.png");
        s.add("cena_chegada.png");
        s.addAll(LUGARES);
        s.addAll(ICONES_INTERFACE);
        // variações para a animação: boca aberta e piscar (retratos) e piscar (bonecos de gente)
        for (String r : new java.util.ArrayList<>(s)) {
            if (r.startsWith("ret_")) {
                for (String v : VARIACOES_RETRATO) {
                    s.add(variacao(r, v));
                }
            } else if (r.startsWith("fig_") && !r.startsWith("fig_caramelo")) {
                s.add(variacao(r, "pisca"));
            }
        }
        s.add(CARAMELO_LATINDO);
        s.add(CARAMELO_SENTADO);
        return s;
    }
}
