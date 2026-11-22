package br.colapso2071.model;

import java.util.List;

import static br.colapso2071.model.Rotas.*;

/**
 * Conteúdo narrativo de Colapso 2071: perfis, personagens, itens, missões, conquistas,
 * introdução, 10 capítulos (25 cenas) e os 4 finais. Segue o roteiro do projeto.
 *
 * <p>Convenções: as cenas "A" são de ambientação (IDs novos); as cenas "B" mantêm os IDs originais
 * CENA_1 a CENA_10. Os valores de recursos em {@code c(comida, água, energia, reputação)}.
 */
final class Historia {
    private final Rotas rotas;

    private Historia(Rotas rotas) {
        this.rotas = rotas;
    }

    static void carregar(Rotas rotas) {
        Historia h = new Historia(rotas);
        h.perfis();
        h.personagens();
        h.itens();
        h.missoes();
        h.conquistas();
        h.introducao();
        h.capitulo1();
        h.capitulo2();
        h.capitulo3();
        h.capitulo4();
        h.capitulo5();
        h.capitulo6();
        h.capitulo7();
        h.capitulo8();
        h.capitulo9();
        h.capitulo10();
        h.finais();
        h.conversas();
        h.perguntasConclusoesELembrancas();
        h.falasDoJogador();
        h.falasDeDescanso();
        h.objetivosEResumos();
        h.cenasDeContexto();
        h.caramelo();
    }

    /**
     * O Caramelo, vira-lata do povoado, aparece nos momentos em que um cachorro de verdade apareceria: late para o
     * forasteiro, avisa da milícia, fareja o diesel no Olho, guarda a cova de Damião, acorda todo mundo no incêndio
     * e deita ao pé do broto do Umbuzeiro. Nessas cenas (e em casa) dá para fazer carinho nele.
     */
    private void caramelo() {
        java.util.List<Dialogos> chegada = rotas.getCena("CENA_1A").getDialogos();
        chegada.add(2, f(NPC_CARAMELO, "Au! Au! Au! (late da porta, com as orelhas em pé)"));
        chegada.add(3, f(NPC_MILENA, "Quieto, Caramelo! (rindo) É o cachorro do povoado. Late para todo mundo que chega, mas "
                + "só morde quem não presta. Ele sabe."));
        rotas.getCena("CENA_2A").comInterludioFala(
                f(NPC_CARAMELO, "(vem atrás de você pela rua, abanando o rabo, e deita aos seus pés quando você para)"));
        java.util.List<Dialogos> cobranca = rotas.getCena("CENA_4A").getDialogos();
        cobranca.add(3, f(NPC_CARAMELO, "Au! Au! Au! (rosna para as camionetes, com o pelo do lombo eriçado)"));
        cobranca.add(4, f(NPC_DANTAS, "Segura esse cachorro, senão eu seguro."));
        cobranca.add(5, f(NPC_MILENA, "Caramelo, vem cá. Vem! (segura o lenço do pescoço dele)"));
        rotas.getCena("CENA_7A").comInterludioFala(
                f(NPC_CARAMELO, "(cheira a tampa do Olho, espirra e gane baixinho)"),
                f(NPC_MILENA, "Até o Caramelo sentiu o cheiro. Isso não foi acidente."));
        rotas.getCena("CENA_8A").comInterludioFala(
                f(NPC_CARAMELO, "(deita ao lado da cova de Damião e não sai dali o dia inteiro)"));
        java.util.List<Dialogos> fogo = rotas.getCena("CENA_9A").getDialogos();
        fogo.add(0, f(NPC_CARAMELO, "Au! Au! Au! Au! (late para o fogo e corre de porta em porta)"));
        fogo.add(1, f(NPC_MILENA, "O Caramelo acordou todo mundo! Baldes, gente! Todo mundo com balde!"));
        rotas.getCena("CENA_10").adicionarDialogo(
                f(NPC_CARAMELO, "(deita ao pé do broto do Umbuzeiro, como quem guarda alguma coisa)"));
    }

    /**
     * Objetivo de cada capítulo (mostrado na abertura e no topo da tela) e o resumo que vai para o diário.
     * Servem para quem joga pela primeira vez saber o que está em jogo e não se perder entre um dia e outro.
     */
    private void objetivosEResumos() {
        rotas.getCena("CENA_1A").comObjetivo("Conhecer Bom-Jardim e decidir se você fica para ajudar o povoado.")
                .comResumo("Você chegou a Bom-Jardim fugindo do Boqueirão. Milena contou como a cidade de nove mil pessoas virou "
                        + "um povoado de 412, que resiste graças à zona rural, e ofereceu a casa azul da professora Lúcia.");
        rotas.getCena("CENA_2A").comObjetivo("Fazer a bomba do Poço da Santa voltar a funcionar antes que a febre do poço se espalhe.")
                .comResumo("A bomba do Poço da Santa quebrou e a febre do poço chegou às crianças. O povoado precisou decidir "
                        + "como consertar a bomba e como dividir a água enquanto isso.");
        rotas.getCena("CENA_3A").comObjetivo("Atravessar a Rota dos Espinhos com Baltazar e Sandro até a Estação 7 e trazer a junta de vedação.")
                .comResumo("Você, Baltazar e Sandro atravessaram a Rota dos Espinhos até a Estação de Bombeamento 7, atrás da "
                        + "única junta de vedação capaz de salvar a bomba. Na volta, Milena pediu que você ajudasse a decidir pelo povoado.");
        rotas.getCena("CENA_4A").comObjetivo("Responder à cobrança do Coronel Vasques sem entregar o povoado.")
                .comResumo("Na Estação 7 vocês acharam o caderno de 2052: a Adutora Norte foi desligada por ordem de A. Vasques, "
                        + "e Rosa Lima, a mãe de Sandro, morreu trancada lá. Em Bom-Jardim, o Coronel chegou com a milícia "
                        + "exigindo água, sacos e as chaves da cisterna.");
        rotas.getCena("CENA_5P").comObjetivo("Descobrir quem está roubando o Celeiro Velho.")
                .comResumo("A milícia amarrou e castigou Caetano em praça pública. O povo protestou e foi calado à força. "
                        + "Depois, sacos de comida começaram a sumir do Celeiro Velho, e você seguiu o rastro.");
        rotas.getCena("CENA_6A").comObjetivo("Decidir se Bom-Jardim aceita o pacto de Júlio com o Coronel ou resiste.")
                .comResumo("Milena contou a história de Júlio e da filha dele. Júlio ofereceu um pacto com o Coronel, e o "
                        + "povoado teve de escolher entre o acordo e a resistência.");
        rotas.getCena("CENA_7A").comObjetivo("Proteger o Olho e o portão leste durante o cerco da milícia.")
                .comResumo("Alguém estragou o segundo compartimento do Olho com diesel. A milícia cercou Bom-Jardim, e Damião "
                        + "morreu no portão leste.");
        rotas.getCena("CENA_8A").comObjetivo("Descobrir quem abriu o portão leste para a milícia e decidir o que fazer com ele.")
                .comResumo("Damião foi enterrado ao amanhecer. A luz piscando no portão leste levou a um nome que ninguém "
                        + "queria ouvir: Júlio.");
        rotas.getCena("CENA_9A").comObjetivo("Salvar o Umbuzeiro-Mãe e a água do Olho, e dar a última resposta ao Coronel.")
                .comResumo("A milícia pôs fogo no Umbuzeiro-Mãe. Seu Raimundo morreu salvando a reserva do porão, Baltazar "
                        + "caiu no levante, e o Coronel fez a última oferta.");
        rotas.getCena("CENA_10A").comObjetivo("Decidir o destino de Júlio, dos presos e de quem guarda a água daqui por diante.")
                .comResumo("O povoado se reuniu à sombra do que sobrou do Umbuzeiro para julgar o que aconteceu.");
    }

    /**
     * Cenas de contexto: conversas curtas na abertura de alguns capítulos que explicam o que um jogador novo
     * ainda não sabe (o que é o Olho, por que a junta importa, quem é o Coronel, o que é o conselho).
     */
    private void cenasDeContexto() {
        rotas.getCena("CENA_2A").comInterludioFala(
                f(NPC_MILENA, "Antes de tudo, {nome}, entenda como a gente vive. A água de Bom-Jardim vem de dois lugares: o Poço da "
                        + "Santa, que tem bomba, e o Olho, a cisterna debaixo do Umbuzeiro, que é a nossa reserva."),
                f(NPC_MILENA, "Cada morador tem uma cota por dia. Você também: no quintal da casa azul tem um poço de balde. "
                        + "A água é pouca, dá uma garrafa térmica por dia. A comida sai do depósito do Celeiro Velho, uma marmita por pessoa."),
                f(NPC_JULIO, "(da porta da Casa das Cotas) E quem anota tudo sou eu. Litro que entra, litro que sai. Sem conta, "
                        + "o povoado briga por água em uma semana."),
                f(NPC_MILENA, "Se o Poço parar, só sobra o Olho. E o Olho não aguenta quatrocentas e doze pessoas por muito tempo."));
        rotas.getCena("CENA_3A").comInterludioFala(
                f(NPC_BALTAZAR, "Presta atenção, {nome}, que eu só explico uma vez. A bomba do Poço tem uma junta que segura a pressão. "
                        + "A nossa rachou. Sem ela, a bomba puxa ar em vez de água."),
                f(NPC_BALTAZAR, "A Estação 7 era da companhia de água da cidade. Fechou quando a Adutora Norte secou, em 2052. Ninguém "
                        + "volta lá desde então. Se ainda existe uma junta inteira no sertão, está lá."),
                f(NPC_SANDRO, "A minha mãe trabalhava na Estação. Ela saiu um dia para o turno e não voltou."),
                f(NPC_BALTAZAR, "(sem olhar para o menino) Por isso ele vem junto. E por isso a gente volta com ele inteiro."));
        rotas.getCena("CENA_4A").comInterludioFala(
                f(NPC_MILENA, "(quando a poeira aparece no horizonte) Camionete no sertão, hoje em dia, só quer dizer uma coisa, "
                        + "{nome}: alguém vindo cobrar. Quem não planta e não cava poço vive de cobrar de quem planta e cava."),
                f(NPC_MILENA, "Se levarem a cota, a gente passa sede. Se a gente recusar, eles voltam com mais gente. Pensa bem antes "
                        + "de abrir a boca."));
        rotas.getCena("CENA_6A").comInterludioFalaSe(Condicao.naoDecidiu("CENA_3B", 2, "ter aceitado a voz no conselho"),
                f(NPC_MILENA, "Desde a assembleia, você tem voz e voto no conselho, {nome}. Isso quer dizer que o povo espera ouvir "
                        + "você antes de decidir. E que, quando der errado, vai olhar para você também."));
        rotas.getCena("CENA_10A").comInterludioFala(
                f(NPC_MILENA, "Hoje não é vingança, {nome}. É conselho. Cada um fala, cada acusado se defende, e a decisão fica "
                        + "escrita no caderno da Casa das Cotas, para quem vier depois saber o que a gente fez."));
    }

    /** No fim de cada capítulo, alguém manda o protagonista descansar: o próximo ato começa num dia novo. */
    private void falasDeDescanso() {
        rotas.getCena("CENA_1").comFalaDeDescanso(f(NPC_MILENA, "Vai descansar, {nome}. Amanhã a gente começa cedo."));
        rotas.getCena("CENA_1B").comFalaDeDescanso(f(NPC_MILENA, "Vai descansar, {nome}. Amanhã a gente começa cedo."));
        rotas.getCena("CENA_2").comFalaDeDescanso(f(NPC_BALTAZAR, "Dorme, {nome}. Amanhã tem mais."));
        rotas.getCena("CENA_3B").comFalaDeDescanso(f(NPC_MILENA, "Agora vai dormir, {nome}. Gente cansada decide mal."));
        rotas.getCena("CENA_4").comFalaDeDescanso(f(NPC_BALTAZAR, "Descansa enquanto pode. Eles voltam."));
        rotas.getCena("CENA_5").comFalaDeDescanso(f(NPC_MILENA, "Vai dormir, {nome}. O que a gente descobriu hoje não foge até amanhã."));
        rotas.getCena("CENA_6B").comFalaDeDescanso(f(NPC_MILENA, "Dorme um pouco. A semana vai ser longa."));
        rotas.getCena("CENA_7B").comFalaDeDescanso(f(NPC_MILENA, "Vai pra casa, {nome}. Amanhã a gente enterra o Damião."));
        rotas.getCena("CENA_8").comFalaDeDescanso(f(NPC_BALTAZAR, "Vai deitar. Eu fico de vigia."));
        rotas.getCena("CENA_9B").comFalaDeDescanso(f(NPC_MILENA, "Descansa, {nome}. Amanhã a gente decide o que fazer com os vivos."));
    }

    // ------------------------------------------------------------------ auxiliares

    private Dialogos f(String npcId, String fala) {
        return new Dialogos(rotas.getNpc(npcId), fala);
    }

    private Consequencia c(int comida, int agua, int energia, int reputacao) {
        return new Consequencia(comida, agua, energia, reputacao, null);
    }

    private Consequencia c(int comida, int agua, int energia, int reputacao, String itemId) {
        return new Consequencia(comida, agua, energia, reputacao, rotas.getItem(itemId));
    }

    private Cena cena(String id, int capitulo, String titulo, String descricao) {
        Cena cena = new Cena(id, descricao).capitulo(capitulo, titulo);
        rotas.adicionar(cena);
        return cena;
    }

    private void falas(Cena cena, Dialogos... dialogos) {
        for (Dialogos d : dialogos) {
            cena.adicionarDialogo(d);
        }
    }

    // ------------------------------------------------------------------ catálogos

    private void perfis() {
        rotas.adicionar(new Perfil(PERFIL_MECANICO, "Faz-tudo de Poços",
                "Cresceu entre bombas, motores e canos. Onde algo quebra, você conserta. (Técnico 3)", 1, 3, 1));
        rotas.adicionar(new Perfil(PERFIL_NEGOCIADOR, "Mascate de Água",
                "Trocou de tudo por um pouco de água; sabe o preço de cada coisa e de cada palavra. (Negociação 3)", 1, 1, 3));
        rotas.adicionar(new Perfil(PERFIL_LIDER, "Chefe de Mutirão",
                "Organizava o povo quando a água faltava; as pessoas ouvem o que você tem a dizer. (Liderança 3)", 3, 1, 1));
    }

    private void personagens() {
        rotas.adicionar(new NPC(NPC_MILENA, "Milena", "Guardiã da cisterna e liderança informal de Bom-Jardim"));
        rotas.adicionar(new NPC(NPC_JULIO, "Júlio", "Contador de cotas do povoado"));
        rotas.adicionar(new NPC(NPC_BALTAZAR, "Baltazar", "Carpinteiro e chefe da defesa"));
        rotas.adicionar(new NPC(NPC_SANDRO, "Sandro", "Menino de 11 anos que pastoreia cabras"));
        rotas.adicionar(new NPC(NPC_RAIMUNDO, "Seu Raimundo", "Guarda do Poço da Santa"));
        rotas.adicionar(new NPC(NPC_VASQUES, "Coronel Vasques", "Chefe da milícia e antigo dono do Boqueirão"));
        rotas.adicionar(new NPC(NPC_DANTAS, "Sargento Dantas", "Cobrador da milícia"));
        rotas.adicionar(new NPC(NPC_CARAMELO, "Caramelo", "Vira-lata caramelo que adotou Bom-Jardim (e a casa azul)"));
    }

    private void itens() {
        // A chave inglesa não se "usa" na mochila: ela é exigida para reparos e para forçar fechaduras
        rotas.adicionar(new Item(ITEM_CHAVE_INGLESA, "Chave Inglesa", "Ferramenta pesada de Baltazar. Serve para reparos e para "
                + "forçar fechaduras; algumas escolhas só aparecem para quem a carrega."));
        rotas.adicionar(new Item(ITEM_PISTA_BOTAS, "Rastro das Botas", "Molde no barro de uma sandália de couro com costura dupla, "
                + "ao lado de botas pesadas. Prova de que alguém de dentro abriu o celeiro."));
        rotas.adicionar(new Item(ITEM_FICHA_BOQUEIRAO, "Ficha do Boqueirão",
                "Ficha de latão com o selo do poço do Boqueirão; liga Júlio ao Coronel.",
                Uso.permanente("Examinar", "Você vira a ficha entre os dedos. O selo do Boqueirão está gasto de tanto passar de mão em mão. "
                        + "Alguém, em algum lugar, ainda paga a água com isso.")));
        rotas.adicionar(new Item(ITEM_RADIO, "Rádio de Manivela",
                "Rádio velho, achado na Estação 7. A manivela range, mas o ponteiro ainda se mexe.",
                Uso.permanente("Sintonizar", "Você gira a manivela e passa o ponteiro de ponta a ponta. Só chiado: nenhuma estação, "
                        + "nenhuma voz. Daqui, o rádio não alcança ninguém. Talvez numa hora de necessidade, com mais gente "
                        + "girando a manivela, alguém do outro lado responda.")));
        rotas.adicionar(new Item(ITEM_GARRAFA, "Garrafa Térmica", "Garrafa térmica de inox, cheia de água fresca. Ao beber, ela fica vazia, "
                + "mas continua com você.",
                Uso.esvazia(0, Jogo.AGUA_DA_GARRAFA, 0, ITEM_GARRAFA_VAZIA, "Beber", "Você bebe devagar, do jeito que Milena ensinou. Água +"
                        + Jogo.AGUA_DA_GARRAFA + ". A garrafa ficou vazia.")));
        rotas.adicionar(new Item(ITEM_GARRAFA_VAZIA, "Garrafa Térmica (vazia)", "A mesma garrafa, agora vazia. Encha no poço do quintal "
                + "da casa azul (tecla C), uma vez por dia, ou quando alguém dividir água com você."));
        rotas.adicionar(new Item(ITEM_MARMITA, "Marmita de Feijão", "Feijão com farinha e um pedaço de abóbora, embrulhado num pano.",
                Uso.consumivel(Jogo.COMIDA_DA_MARMITA, 0, 0, "Comer", "Você come sentado na calçada, sem pressa. Comida +"
                        + Jogo.COMIDA_DA_MARMITA + ".")));
        rotas.adicionar(new Item(ITEM_CAFE, "Café Torrado", "Grãos torrados em casa, o suficiente para um bule. Precisa ser preparado: "
                + "moer, ferver um pouco de água e passar no coador de pano.",
                Uso.consumivel(15, -5, 0, "Preparar", "Você moe os grãos com uma pedra, ferve um pouco da sua água e passa o café no coador "
                        + "de pano. O cheiro enche a casa e o café forte engana a fome por umas horas. Água −5, Comida +15.")));
        rotas.adicionar(new Item(ITEM_CADERNO, "Caderno da Estação 7", "O caderno de registro da sala de controle. Prova de que a "
                + "adutora foi desligada por ordem de Vasques, em 2052.",
                Uso.leitura("Você abre o caderno. A capa está dura de poeira; as páginas, amareladas e escritas a caneta.", List.of(
                        "Estação de Bombeamento 7 · Livro de registro da sala de controle",
                        "02/03/2052 · Vazão normal na Adutora Norte: 410 litros por segundo. Pressão estável. Bom-Jardim e "
                                + "Boqueirão abastecidos. Técnica de turno: Rosa Lima.",
                        "09/03/2052 · Visita de A. Vasques, \"representante da companhia\". Pediu os mapas da adutora e a "
                                + "chave da sala de válvulas. O chefe recusou.",
                        "12/03/2052 · O chefe foi transferido. Chegaram homens armados \"para proteger o patrimônio\".",
                        "14/03/2052 · Adutora Norte desligada por ordem de A. Vasques. Motivo: realocação comercial da vazão. "
                                + "Toda a água passa a seguir para os tanques do Boqueirão.",
                        "15/03/2052 · Bom-Jardim sem pressão nos canos do centro. Perguntamos quando religa. Resposta: \"quando "
                                + "pagarem\".",
                        "17/03/2052 · Tentamos reabrir a válvula à noite. Fomos pegos. Trancaram a sala de controle por fora.",
                        "(letra tremida) Ficamos sem água para voltar. Somos quatro: Rosa, Edvaldo, Marta e Zé Pequeno. Se "
                                + "alguém achar isto, avise nossas famílias. Rosa pede: diga ao Sandro que a pressão cai quando "
                                + "alguém abre outra torneira."))));
        rotas.adicionar(new Item(ITEM_FOTO, "Foto da Feira de 2030", "Fotografia desbotada encontrada na sua casa nova.",
                Uso.imagem("foto_feira_2030.png", "A praça de Bom-Jardim num sábado de feira, em 2030: barracas de manga, caju e queijo, "
                        + "crianças em volta do Umbuzeiro, bandeirinhas de São João e, ao fundo, a lagoa cheia. Atrás, a lápis: "
                        + "\"Sábado de feira. Melhor lugar do mundo.\"")));
    }

    private void missoes() {
        rotas.adicionar(new Missao(MISSAO_APOIO, "Retribuir a Hospitalidade", "Ajudar o povoado que acolheu você."));
        rotas.adicionar(new Missao(MISSAO_TRAIDOR, "Descobrir o Traidor", "Descobrir quem está ajudando a milícia."));
        rotas.adicionar(new Missao(MISSAO_DEFESA, "Defender o Povoado", "Resistir ao cerco da milícia."));
        rotas.adicionar(new Missao(MISSAO_VIZINHOS, "Ouvir o Sertão", "Usar o rádio para descobrir se outros povoados estão vivos e dispostos a ajudar."));
    }

    private void conquistas() {
        rotas.adicionar(new Conquista(CONQ_MAO_ESTENDIDA, "Mão Estendida", "Aceitou ajudar o povoado logo na chegada."));
        rotas.adicionar(new Conquista(CONQ_OUVIDO_ATENTO, "Ouvido Atento", "Percebeu que Júlio sabia mais do que dizia."));
        rotas.adicionar(new Conquista(CONQ_AGUA_PARA_TODOS, "Água para Todos", "Consertou a bomba d'água em benefício de todos."));
        rotas.adicionar(new Conquista(CONQ_OLHOS_NA_CAATINGA, "Olhos na Caatinga", "Avisou sobre as marcas de botas dos milicianos."));
        rotas.adicionar(new Conquista(CONQ_FARO_DE_DETETIVE, "Faro de Detetive", "Investigou o sumiço dos suprimentos."));
        rotas.adicionar(new Conquista(CONQ_VOZ_DO_BOQUEIRAO, "Voz do Boqueirão", "Expôs os crimes do Coronel diante dos próprios homens dele."));
        rotas.adicionar(new Conquista(CONQ_PORTA_ABERTA, "Água de Porta Aberta", "Transformou a água em bem público, com contas à vista de todos."));
        rotas.adicionar(new Conquista(CONQ_CONSELHO, "Voz e Voto", "Ganhou um lugar no conselho de Bom-Jardim depois de trazer a água de volta."));
        rotas.adicionar(new Conquista(CONQ_LEVANTE, "Bom-Jardim se Levanta", "Esteve na praça no dia em que o povo enfrentou a milícia."));
        rotas.adicionar(new Conquista(CONQ_JUSTICA, "Justiça sem Vingança", "Levou o Coronel e a milícia a um julgamento de verdade."));
        rotas.adicionar(new Conquista(CONQ_LIGA, "Liga das Águas", "Usou o rádio para unir os povoados vizinhos em torno da água compartilhada."));
        rotas.adicionar(new Conquista(CONQ_NOVA_COROA, "Nova Coroa", "Venceu o tirano e quis ocupar o lugar dele."));
        rotas.adicionar(new Conquista(CONQ_AMIGO_CARAMELO, "Melhor Amigo", "Ganhou a confiança do Caramelo."));
        rotas.adicionar(new Conquista(CONQ_MORDIDO, "Cachorro Não Se Engana", "Levou uma mordida do Caramelo."));
        rotas.adicionar(new Conquista(CONQ_TRAVESSIA, "A Travessia", "Chegou a um dos finais de Colapso 2071."));
        rotas.adicionar(new Conquista(CONQ_SEMENTE, "Semente do Sertão", "Alcançou o melhor desfecho para o povoado."));
    }

    private void introducao() {
        rotas.adicionarParagrafoDaIntroducao("ANO DE 2071. Duas décadas depois da Grande Estiagem, o sertão vive de cotas. A seca que começou em 2049 "
                + "quebrou a transposição do São Francisco, secou as lagoas e fez da água a única moeda que ninguém falsifica.");
        rotas.adicionarParagrafoDaIntroducao("Cada pessoa tem direito a uma cota diária, e a ficha de latão é o comprovante. "
                + "Quem controla o poço controla as pessoas, e há quem tenha aprendido a gostar disso.");
        rotas.adicionarParagrafoDaIntroducao("Você é {nome}, tem vinte e sete anos e é a única pessoa que saiu viva do Boqueirão, o povoado que secou nas mãos do "
                + "Coronel Aurélio Vasques, aquele que vendia a água do poço em fichas de latão.");
        rotas.adicionarParagrafoDaIntroducao("Há semanas você caminha sem rumo pelo sertão, com um odre vazio e uma ficha de latão no bolso, "
                + "a única coisa que restou da sua antiga vida.");
        rotas.adicionarParagrafoDaIntroducao("Seu objetivo é sobreviver. Mas, em 2071, sobreviver significa escolher entre dividir a água ou guardá-la, "
                + "e cada escolha vai dizer ao sertão quem você é.");
    }

    // ------------------------------------------------------------------ capítulo 1

    private void capitulo1() {
        Cena c1a = cena("CENA_1A", 1, "A Chegada ao Povoado",
                "Você desaba na poeira seca após semanas de caminhada solitária. Seu antigo povoado, o Boqueirão, foi dizimado pelo clima "
                + "extremo e pela ganância do líder corrupto. A última coisa que você sente é o gosto de terra na boca.\n"
                + "Quando abre os olhos, está estendido em uma rede rústica, sob um telhado de telhas antigas. O ar cheira a fumaça de lenha e a terra "
                + "molhada, um cheiro que você já nem sabia que existia. Rostos cansados, mas pessoas, observam você com cautela e compaixão. "
                + "Uma mulher de cabelos longos, escuros e ondulados, com alguns fios grisalhos, leva uma caneca de lata aos seus lábios: um gole "
                + "de água racionada. Da porta, um cachorro cor de caramelo vigia você com as orelhas em pé.")
                .abreCapitulo();
        falas(c1a,
                f(NPC_MILENA, "Devagar. Água demais de uma vez faz mal a quem passou tanto tempo sem beber."),
                f(NPC_MILENA, "(para você) Você ficou três dias dormindo. Aqui é Bom-Jardim."),
                f(NPC_MILENA, "Eu sou a Milena. A rede é do Baltazar, que dormiu no chão esses dias e diz que não se importa."),
                f(NPC_MILENA, "Você chegou com a boca rachada e uma ficha de latão apertada na mão. Nem dormindo você largou. E então, qual é o seu nome?"));
        c1a.adicionarEscolha(new Escolha("Dizer o nome, agradecer e beber devagar.", "CENA_1", null,
                c(5, 5, 0, 1, ITEM_MARMITA).comAfinidade(NPC_MILENA, 1))
                .comReacao(f(NPC_MILENA, "{nome}. Nome bonito. Vou buscar um pouco de comida, você ainda está fraco.")));
        c1a.adicionarEscolha(new Escolha("Tomar a caneca das mãos dela e beber tudo de uma vez.", "CENA_1", null,
                c(0, 15, 0, -1).comAfinidade(NPC_MILENA, -1))
                .comReacao(f(NPC_MILENA, "(puxa a caneca de volta) Aqui a gente divide o que tem. É melhor aprender logo.")));
        c1a.adicionarEscolha(new Escolha("Antes de beber, perguntar onde está e quem manda ali.", "CENA_1", null,
                c(0, 0, 0, 0).comAfinidade(NPC_MILENA, 1))
                .comReacao(f(NPC_MILENA, "Aqui ninguém manda sozinho. A gente combina tudo. Dá mais trabalho, mas evita muita briga.")));

        Cena c1 = cena("CENA_1", 1, "A Chegada ao Povoado",
                "Antes do enterro, Milena deixa na beira da rede uma muda de roupa: calça de brim, camisa de algodão e as botas que foram do marido de Dona Josefa. Você troca os trapos do Boqueirão, duros de poeira e de sal de suor, e se sente gente de novo.\n"
                + "No seu segundo dia de pé, você acompanha o enterro de Dona Josefa, oitenta e um anos, que passou o mês dando a própria cota de água aos netos. Ninguém chora alto: chorar também gasta água. A cova é rasa, porque o chão é duro e os homens estão fracos.\n"
                + "Após uns dias, você já conta Bom-Jardim nos dedos: pouco mais de cem casas de alvenaria, de pintura desbotada, espalhadas por sítios, roças secas e cercas de arame. O coração do lugar é uma praça de terra em volta de um umbuzeiro enorme, de copa tão "
                + "larga que dá sombra para metade do povoado. As pessoas cumprimentam você com um aceno curto, o aceno de quem economiza até o gesto. Milena cedeu a você a casa azul no fim da rua, que foi da professora Lúcia, que deu aula na escola do povoado por trinta anos e partiu para o litoral. Ali você pode dormir e guardar suas coisas.\n"
                + "Do alto do morro, a oeste, dá para ver o que sobrou do centro de Bom-Jardim: telhados afundados, ruas tomadas pelo mato, "
                + "a torre da igreja sem sino e a caixa d'água da cidade, enferrujada, inclinada como quem desistiu.\n"
                + "No fim da tarde, você se encontra com Milena no banco de pedra, ao pé da árvore. Ela conta as coisas absurdas que têm "
                + "acontecido na comunidade e, inusitadamente, pede a sua ajuda.");
        falas(c1,
                f(NPC_RAIMUNDO, "(apoiado na cerca, olhando para o oeste) Aquilo lá era a cidade. Bom-Jardim de verdade. "
                        + "Nove mil e poucas pessoas no censo de 2048. Eu era guarda do mercado municipal."),
                f(NPC_MILENA, "Isto aqui era a zona rural, os sítios. A gente vendia manga, caju, acerola e queijo na feira do centro, "
                        + "todo sábado. Foi por causa dos quintais daqui que a cidade ganhou esse nome."),
                f(NPC_MILENA, "Quando a seca de 2049 apertou e a adutora parou, a cidade secou primeiro. Lá era tudo encanado. "
                        + "Quando a água deixou de chegar pelo cano, não tinha de onde tirar."),
                f(NPC_RAIMUNDO, "A maioria foi embora no primeiro ano: para a capital, para o litoral, para o sul. Quem tinha carro, "
                        + "parente ou algum dinheiro. Quem não tinha, ficou."),
                f(NPC_MILENA, "E muitos dos que ficaram morreram. Os velhos, os doentes, quem morava sozinho em apartamento sem cisterna. "
                        + "Teve mês de enterrar gente toda semana."),
                f(NPC_MILENA, "Aqui a gente sobreviveu porque está no lugar certo. O Poço da Santa alcança um lençol de água fundo, "
                        + "a cisterna fica debaixo das raízes do Umbuzeiro, e o baixio ainda segura umidade para plantar palma e feijão."),
                f(NPC_RAIMUNDO, "Nove mil virou quatrocentos. E a gente ainda chama isto de Bom-Jardim, por teimosia."),
                f(NPC_MILENA, "(para você) Vinte e sete anos, você me disse ontem. Com vinte e sete eu já tinha enterrado meus pais. "
                        + "A seca não escolhe idade."),
                f(NPC_MILENA, "Vou direto ao ponto, porque água é pouca e tempo também. Faz três semanas que a bomba do Poço da Santa está falhando."),
                f(NPC_MILENA, "Há duas semanas a gente vê poeira de caminhonete no horizonte, mas nunca vê quem está dentro."),
                f(NPC_MILENA, "E toda manhã falta um saco no Celeiro Velho. Eu conto. Eu sempre conto."),
                f(NPC_MILENA, "Semana passada, alguém pintou números nas portas durante a noite. Cento e doze casas, cento e doze números. "
                        + "Ninguém viu quem foi."),
                f(NPC_SANDRO, "A nossa é a 37. Minha mãe esfregou com água e sabão, mas a tinta não sai."),
                f(NPC_MILENA, "Toda madrugada passa um drone baixo, com uma luz vermelha piscando. Do governo não é: "
                        + "o governo não aparece aqui há dez anos."),
                f(NPC_MILENA, "E a cisterna baixa mais do que a gente tira. Eu marco o nível com giz na parede, "
                        + "e toda manhã a água está abaixo da marca."),
                f(NPC_SANDRO, "E as cabras do Seu Raimundo amanheceram com uma fita amarela no pescoço. Ninguém amarrou."),
                f(NPC_MILENA, "Eu sei que parece coisa sem pé nem cabeça. Mas cuido deste lugar há quinze anos e nunca vi nada assim. "
                        + "Alguém está medindo a gente."),
                f(NPC_MILENA, "Você chegou sem dever favor a ninguém. Num lugar como este, isso vale muito. Pode me ajudar?"));
        c1.adicionarEscolha(new Escolha("Claro que ajudo, preciso retribuir o que fizeram por mim.", "CENA_2A", null,
                c(-10, -10, -10, 2).encheGarrafa(rotas.getItem(ITEM_GARRAFA)).comAfinidade(NPC_MILENA, 2).iniciaMissao(MISSAO_APOIO).desbloqueia(CONQ_MAO_ESTENDIDA))
                .comReacao(f(NPC_MILENA, "(sorri pela primeira vez) Então amanhã cedo. O sol aqui não espera ninguém.")));
        c1.adicionarEscolha(new Escolha("Não, não quero me envolver.", "CENA_2A", null,
                c(10, 10, 10, -5).comAfinidade(NPC_MILENA, -2))
                .comReacao(f(NPC_MILENA, "(dá de ombros, sem raiva) Cada um sabe o que pode carregar. A rede continua sua enquanto você precisar.")));
        c1.adicionarEscolha(new Escolha("Pedir um dia para entender o povoado antes de responder.", "CENA_1B", null,
                c(5, 5, 5, 0).comAfinidade(NPC_MILENA, 1))
                .comReacao(f(NPC_MILENA, "Um dia. É justo. Mas amanhã o sol nasce sem pedir licença.")));

        // Quem pede um dia para pensar conhece o povoado antes de responder a Milena
        Cena c1b = cena("CENA_1B", 1, "A Chegada ao Povoado",
                "Você acorda antes do sol, com o barulho de baldes. A fila da cisterna já dobra a praça: crianças com "
                + "galões do tamanho delas, velhos apoiados na cerca, ninguém falando para não gastar saliva. Cada família "
                + "leva a cota marcada a giz na parede.\nNo Poço da Santa, a bomba tosse, para, tosse de novo. Duas "
                + "mulheres revezam a manivela de emergência até as mãos ficarem vermelhas. Na beira da estrada, os netos "
                + "de Dona Josefa arrumam pedras em volta da cova da avó.\nÀ tarde, você anda pelos sítios. Vê roças de "
                + "palma secando, cercas consertadas com arame de cerca velha, cento e doze portas com números pintados por "
                + "alguém que ninguém viu. Na casa azul, atrás do espelho, você encontra uma foto antiga da feira, de "
                + "quando a lagoa ainda enchia.");
        falas(c1b,
                f(NPC_RAIMUNDO, "(na porta do porão, mastigando um talo de capim) Veio conhecer o lugar antes de decidir? Coisa de gente "
                        + "esperta. Ou de gente com medo."),
                f(NPC_RAIMUNDO, "Aqui embaixo fica a reserva do mês que vem. Eu guardo a chave. E, de vez em quando, um favor ou outro."),
                f(NPC_MILENA, "(no fim da tarde, sentada no banco de pedra) Viu a fila de manhã? Viu as portas? Ouviu a bomba tossindo?"),
                f(NPC_MILENA, "Eu não vou fingir que é fácil. Mas também não vou fingir que a gente não precisa de você."));
        c1b.adicionarEscolha(new Escolha("Ajudar: depois de ver tudo isso, não dá para virar as costas.", "CENA_2A", null,
                c(-10, -10, -10, 3, ITEM_FOTO).encheGarrafa(rotas.getItem(ITEM_GARRAFA)).comAfinidade(NPC_MILENA, 2)
                        .iniciaMissao(MISSAO_APOIO).desbloqueia(CONQ_MAO_ESTENDIDA))
                .comReacao(f(NPC_MILENA, "Quem pensa antes de prometer costuma cumprir. Amanhã cedo, na bomba.")));
        c1b.adicionarEscolha(new Escolha("Recusar: é problema demais para quem acabou de chegar.", "CENA_2A", null,
                c(10, 10, 10, -5, ITEM_FOTO).comAfinidade(NPC_MILENA, -2))
                .comReacao(f(NPC_MILENA, "(guarda a resposta sem discutir) Pelo menos você olhou antes de dizer não. A rede continua sua.")));
    }

    // ------------------------------------------------------------------ capítulo 2

    private void capitulo2() {
        Cena c2a = cena("CENA_2A", 2, "A Febre do Poço",
                "Milena apresenta o povoado como quem apresenta a própria família: com orgulho e com queixas. Você conhece o Umbuzeiro-Mãe, "
                + "de mais de duzentos anos, e a cisterna que se esconde sob as raízes, o Olho. Conhece o Poço da Santa, cuja bomba tosse "
                + "como um velho. E conhece os homens que mantêm tudo de pé.")
                .abreCapitulo().comFechamento("Você não disse a ninguém de onde veio.");
        c2a
                .comInterludio("Os dias seguintes têm um ritmo que você aprende depressa: a fila da cisterna antes do sol, o trabalho até o "
                + "calor ficar insuportável, o silêncio das três da tarde, quando nem os cachorros latem.")
                .comInterludio("Na Casa das Cotas há um caderno com o nome de cada morador. Este ano, onze nomes foram riscados a lápis. Ao "
                + "lado de alguns, Júlio escreveu \"partiu\". Ao lado de outros, só uma data.")
                .comInterludioSe(Condicao.decisao("CENA_1", 0, "ter aceitado ajudar").ou(Condicao.decisao("CENA_1B", 0, "ter aceitado ajudar")),
                        "Você começa a trabalhar no dia seguinte. Carrega pedra, limpa calha, conserta balde. As pessoas ainda não "
                + "sabem seu sobrenome, mas já sabem que você não some quando o serviço aperta.")
                .comInterludioSe(Condicao.decisao("CENA_1", 1, "ter recusado ajudar").ou(Condicao.decisao("CENA_1B", 1, "ter recusado ajudar")),
                        "Você disse não a Milena, e o povoado não esqueceu. Ninguém é grosseiro; é pior: as conversas param quando "
                + "você chega, e a sua caneca é sempre a última a ser cheia.");
        falas(c2a,
                f(NPC_MILENA, "Aquele ali, de martelo, é o Baltazar. E aquele que parece que está sempre fazendo conta é o Júlio."),
                f(NPC_JULIO, "Sempre fazendo conta, com o devido respeito, porque alguém precisa. Cada litro que entra, cada litro que sai. "
                        + "O número não cansa. Só as pessoas."),
                f(NPC_BALTAZAR, "(olha suas mãos) Mão calejada. Gente de trabalho. Serve."),
                f(NPC_JULIO, "Do Boqueirão, imagino. Só de lá se chega com essa cor de terra nas botas."),
                f(NPC_MILENA, "(seca) O Júlio percebe tudo. É um dom. Às vezes é um defeito."));
        c2a.adicionarEscolha(new Escolha("“Como você sabia que eu vim do Boqueirão?”", "CENA_2", null,
                c(0, 0, 0, 0).comAfinidade(NPC_JULIO, -1).comAtributo(TipoAtributo.NEGOCIACAO, 1).desbloqueia(CONQ_OUVIDO_ATENTO))
                .comReacao(f(NPC_JULIO, "(sorri sem pressa) O barro do Boqueirão tem uma cor que não se vê em outro lugar, e eu observo todo mundo que passa por aqui. "
                        + "Contar é isso: prestar atenção."))
                .comNarracao("Você guarda a lição: quem presta atenção sabe mais do que diz."));
        c2a.adicionarEscolha(new Escolha("Deixar passar e apertar a mão dele.", "CENA_2", null,
                c(0, 0, 0, 0).comAfinidade(NPC_JULIO, 1))
                .comReacao(f(NPC_JULIO, "Um bom começo, então.")));
        c2a.adicionarEscolha(new Escolha("Puxar conversa com Baltazar sobre a oficina e o martelo.", "CENA_2", null,
                c(0, 0, 0, 0, ITEM_CAFE).comAfinidade(NPC_BALTAZAR, 1))
                .comReacao(f(NPC_BALTAZAR, "(vira o martelo na mão) Era do meu pai. O cabo já foi trocado quatro vezes. A cabeça é a original."))
                .comNarracao("É a frase mais longa que Baltazar diz a você até agora."));

        Cena c2 = cena("CENA_2", 2, "A Febre do Poço",
                "Na manhã seguinte, a bomba do Poço da Santa para de vez. O motor tosse, bufa e silencia, e o silêncio se espalha pelo povoado "
                + "mais rápido que o boato. Em duas horas, a fila da cisterna dobra a esquina. Ao meio-dia, a febre do poço, como alguém já "
                + "batizou, é a febre de todos: medo, empurra-empurra e criança chorando de sede.");
        falas(c2,
                f(NPC_BALTAZAR, "O eixo quebrou. Dá para consertar, mas leva dois dias."),
                f(NPC_JULIO, "Se a cisterna ficar aberta a todos, o Olho esvazia antes de o conserto terminar. Eu fiz a conta. Com o devido respeito, "
                        + "alguém precisa ser racional."),
                f(NPC_RAIMUNDO, "(baixo, junto ao seu ouvido) Se quiser uma garrafa só para você, a chave do porão fica comigo. "
                        + "Tudo tem preço, forasteiro. O meu é pequeno."),
                f(NPC_MILENA, "Eu não mando em você. Mas todo mundo está olhando."));
        c2.adicionarEscolha(new Escolha("Consertar para todos.", "CENA_3A", null,
                c(-5, -5, -20, 5, ITEM_CHAVE_INGLESA).comAfinidade(NPC_BALTAZAR, 1).comAtributo(TipoAtributo.TECNICO, 1)
                        .concluiMissao(MISSAO_APOIO).desbloqueia(CONQ_AGUA_PARA_TODOS))
                .comReacao(f(NPC_BALTAZAR, "(entrega a chave inglesa) Gostei da sua atitude. Fique com ela.")));
        c2.adicionarEscolha(new Escolha("Subornar o guarda por água exclusiva.", "CENA_3A", null,
                c(0, 20, 0, -5).comAfinidade(NPC_MILENA, -2))
                .comReacao(f(NPC_RAIMUNDO, "Negócio bom é o que ninguém vê.")));
        c2.adicionarEscolha(new Escolha("Reparar o eixo sozinho, com a sua perícia de técnico.", "CENA_3A",
                Condicao.atributoMinimo(TipoAtributo.TECNICO, 2),
                c(-5, -5, -10, 5, ITEM_CHAVE_INGLESA).comAfinidade(NPC_BALTAZAR, 1).comAtributo(TipoAtributo.TECNICO, 1)
                        .concluiMissao(MISSAO_APOIO).desbloqueia(CONQ_AGUA_PARA_TODOS))
                .comReacao(f(NPC_BALTAZAR, "Rápido e bem feito. Mão boa. (entrega a chave inglesa) Fique com ela."))
                .comNarracao("Você reconhece o defeito antes mesmo de abrir a carcaça: o eixo está empenado. Em poucas horas a bomba volta "
                        + "a tossir, depois a engolir, depois a cantar."));
        c2.adicionarEscolha(new Escolha("Convocar uma assembleia rápida: dividir os turnos de conserto e as filas.", "CENA_3A",
                Condicao.atributoMinimo(TipoAtributo.LIDERANCA, 2),
                c(-5, -5, -15, 6, ITEM_CHAVE_INGLESA).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1)
                        .comAtributo(TipoAtributo.LIDERANCA, 1).concluiMissao(MISSAO_APOIO).desbloqueia(CONQ_AGUA_PARA_TODOS))
                .comReacao(f(NPC_MILENA, "(alto, para a fila) Dois turnos, quatro baldes por casa, e quem furar a fila vai limpar a cisterna!"),
                        f(NPC_BALTAZAR, "(entrega a chave inglesa) Bem organizado. Gostei.")));
    }

    // ------------------------------------------------------------------ capítulo 3

    private void capitulo3() {
        Cena c3a = cena("CENA_3A", 3, "Rota dos Espinhos",
                "Com ou sem a sua ajuda, Baltazar deu um jeito no eixo, mas o remendo não aguenta uma semana. Para durar, falta uma junta de "
                + "vedação, e a única que presta está na Estação de Bombeamento 7, a dois dias de Bom-Jardim pela Rota dos Espinhos.\n"
                + "Você sai antes do sol com Baltazar e Sandro. A Caatinga é uma extensão de galhos cinzentos e cactos de braços erguidos, "
                + "como se o mato inteiro estivesse se rendendo. Ao meio-dia, o ar treme sobre o chão e o menino começa a andar mais devagar.")
                .abreCapitulo().comPontoDeSalvamento().comFechamento("Baltazar testa você sem dizer que está testando.");
        c3a
                .comInterludio("Enquanto a bomba fica parada, a febre do poço cobra seu preço. O filho de Damião, de dois anos, arde em febre "
                + "por três noites. Na quarta, Damião é pego tirando um balde da cisterna depois do toque de recolher.")
                .comInterludio("A assembleia corta a cota dele por uma semana. É a regra, e todos sabem que a regra existe porque sem ela o "
                + "Olho secaria em um mês. Milena vota pelo corte e, naquela noite, leva metade da própria água para a casa de "
                + "Damião, sem contar a ninguém. A criança sobrevive. Milena passa três dias com os lábios rachados.")
                .comInterludioSe(Condicao.decisao("CENA_2", 1, "ter subornado o guarda"),
                        "Você bebe da garrafa do porão de Seu Raimundo, escondido, e escuta pela parede o choro do filho de "
                + "Damião. A água está fresca. Não desce bem.")
                .comInterludioSe(Condicao.decisao("CENA_2", 0, "ter consertado a bomba para todos"),
                        "Damião passa pela sua porta antes do amanhecer e deixa, sem bater, um punhado de feijão embrulhado num pano. "
                + "Não há bilhete.");
        falas(c3a,
                f(NPC_SANDRO, "Minha mãe trabalhava na Estação 7 antes de fecharem. Ela me ensinou a ler os medidores de pressão."),
                f(NPC_BALTAZAR, "Guarde o fôlego."),
                f(NPC_SANDRO, "Mas, seu Baltazar, minha língua está grudando no céu da boca..."),
                f(NPC_BALTAZAR, "(sem olhar para você) Cada um com a sua."),
                f(NPC_SANDRO, "Quando eu crescer, vou estudar engenharia e fazer a água voltar para a lagoa."),
                f(NPC_BALTAZAR, "(quase sorri) Então estude. Agora ande."));
        c3a.adicionarEscolha(new Escolha("Dividir a água da sua garrafa com Sandro.", "CENA_3", null,
                c(0, -10, 0, 1).comAfinidade(NPC_SANDRO, 2).comAfinidade(NPC_BALTAZAR, 1).comAtributo(TipoAtributo.LIDERANCA, 1))
                .comReacao(f(NPC_SANDRO, "Você é o forasteiro mais legal que eu já conheci. De três, é o melhor.")));
        c3a.adicionarEscolha(new Escolha("Guardar a sua água: cada um cuida da sua.", "CENA_3", null,
                c(0, 0, 0, -1).comAfinidade(NPC_SANDRO, -1))
                .comNarracao("Baltazar lança um olhar comprido. Nenhuma palavra."));
        c3a.adicionarEscolha(new Escolha("Ensinar Sandro a mastigar uma folha de umbu para enganar a sede.", "CENA_3", null,
                c(0, 0, 0, 1).comAfinidade(NPC_SANDRO, 1).comAfinidade(NPC_BALTAZAR, 1))
                .comReacao(f(NPC_SANDRO, "Azedo! Mas a língua desgrudou! Como você sabia disso?"),
                        f(NPC_BALTAZAR, "(sem olhar) Meu avô fazia a mesma coisa.")));

        Cena c3 = cena("CENA_3", 3, "Rota dos Espinhos",
                "No leito de um rio morto, perto da estação, Baltazar se agacha e passa os dedos na terra. Há marcas de botas, e são de "
                + "milicianos: solas fundas, passo curto, de gente que carrega peso e não tem pressa. A trilha segue em direção a Bom-Jardim.");
        falas(c3,
                f(NPC_SANDRO, "Essa sola afundou no chão. É gente pesada. Gente armada."),
                f(NPC_BALTAZAR, "Foi ontem. Umas vinte e quatro pessoas. (aponta com o queixo) Foram em direção ao povoado."),
                f(NPC_SANDRO, "Será que eles ainda estão por aí?"),
                f(NPC_BALTAZAR, "Foram embora. Mas vão voltar."),
                f(NPC_SANDRO, "A gente vai contar para a Dona Milena, não vai? Não vai?"));
        c3.adicionarEscolha(new Escolha("Avisar Dona Milena.", "CENA_3B", null,
                c(0, 0, -5, 5).comAfinidade(NPC_MILENA, 2).comAfinidade(NPC_BALTAZAR, 1).desbloqueia(CONQ_OLHOS_NA_CAATINGA))
                .comReacao(f(NPC_MILENA, "(ouve em silêncio, depois põe a mão no seu ombro) Obrigada por trazer o medo até mim "
                        + "em vez de deixar ele chegar sozinho.")));
        c3.adicionarEscolha(new Escolha("Ficar calado para não se envolver.", "CENA_3B", null,
                c(0, 0, 0, -2).comAfinidade(NPC_BALTAZAR, -1).comAfinidade(NPC_SANDRO, -1))
                .comReacao(f(NPC_SANDRO, "Você não vai contar?"),
                        f(NPC_BALTAZAR, "(balança a cabeça uma vez) Silêncio custa caro.")));
        c3.adicionarEscolha(new Escolha("Seguir a trilha até a estação e vasculhar os destroços.", "CENA_3B", null,
                c(0, -5, -10, 2, ITEM_RADIO).comAfinidade(NPC_BALTAZAR, 1).comAfinidade(NPC_SANDRO, 1).iniciaMissao(MISSAO_VIZINHOS))
                .comReacao(f(NPC_SANDRO, "Olha! Um rádio! Está chiando, mas parece funcionar!"),
                        f(NPC_BALTAZAR, "(gira a manivela devagar) Ainda funciona. Guarde bem."))
                .comNarracao("Entre ferros retorcidos e fios de cobre arrancados, você encontra um rádio de manivela embrulhado em plástico. "
                        + "A carcaça está rachada, mas o ponteiro ainda se mexe."));


        Cena c3b = cena("CENA_3B", 3, "Rota dos Espinhos",
                "De volta a Bom-Jardim, vocês passam dois dias deitados no chão da casa de máquinas, encaixando a junta "
                + "de vedação da Estação 7. No fim do segundo dia, a bomba do Poço da Santa tosse, engole e depois canta, "
                + "firme, como não cantava havia meses. A água sobe limpa e constante. Pela primeira vez no ano, a fila da "
                + "cisterna anda sem empurra-empurra. Baltazar entrega a você o caderno da Estação 7: \"Guarde. Um dia alguém vai ter que ler isto em voz alta.\"\nNaquela noite, Milena convoca a assembleia na praça. Mais de "
                + "trezentas pessoas sentam no chão de terra, em volta do Umbuzeiro, com lamparinas no colo.")
                .comFechamento("A partir de hoje, a sua palavra pesa na assembleia de Bom-Jardim. É uma honra, e é um peso: tudo o que o "
                + "conselho decidir também vai ter o seu nome.");
        falas(c3b,
                f(NPC_BALTAZAR, "(limpando a graxa das mãos) A junta aguenta dez anos. Talvez mais. Foi {nome} que achou a peça certa no "
                        + "meio do ferro-velho."),
                f(NPC_RAIMUNDO, "Três semanas de bomba engasgando e quem resolve é alguém que chegou outro dia. Tem gente aqui que devia "
                        + "ter vergonha. Eu incluso."),
                f(NPC_MILENA, "(de pé no banco de pedra) Quem trouxe a água de volta tem direito de falar sobre ela. Proponho que "
                        + "{nome} tenha voz e voto no conselho do povoado, como quem nasceu aqui."),
                f(NPC_JULIO, "Com o devido respeito, Milena, é cedo. Ninguém sabe direito quem é essa pessoa."),
                f(NPC_MILENA, "Eu sei o que essa pessoa fez. É o que me basta. Quem é a favor?"));
        c3b.adicionarEscolha(new Escolha("Aceitar e prometer que toda decisão do conselho sobre a água será anunciada na praça.", "CENA_4A", null,
                c(0, 0, 0, 3, ITEM_CADERNO).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_JULIO, -1).comAtributo(TipoAtributo.LIDERANCA, 1).desbloqueia(CONQ_CONSELHO))
                .comReacao(f(NPC_MILENA, "Então está dito, na frente de todo mundo. E eu vou cobrar."))
                .comNarracao("Mais de trezentas mãos se levantam. Júlio levanta a dele por último, devagar."));
        c3b.adicionarEscolha(new Escolha("Aceitar em silêncio, só com um aceno de cabeça.", "CENA_4A", null,
                c(0, 0, 0, 0, ITEM_CADERNO).desbloqueia(CONQ_CONSELHO))
                .comReacao(f(NPC_MILENA, "Aceno também é palavra. Bem-vindo ao conselho.")));
        c3b.adicionarEscolha(new Escolha("Recusar: \"Eu só conserto coisas. Decidam vocês.\"", "CENA_4A", null,
                c(0, 0, 0, -2, ITEM_CADERNO).comAfinidade(NPC_MILENA, -1))
                .comReacao(f(NPC_MILENA, "Consertar também é decidir. A cadeira fica aqui. Quando precisar, você senta."),
                        f(NPC_JULIO, "(sorri de leve) Prudente.")));
    }

    // ------------------------------------------------------------------ capítulo 4

    private void capitulo4() {
        Cena c4a = cena("CENA_4A", 4, "A Chegada da Cobrança",
                "À tarde, uma coluna de poeira sobe no horizonte e o povoado inteiro para de respirar. Três camionetes blindadas com chapas "
                + "de ferro entram em Bom-Jardim. Do primeiro veículo desce um sargento magro, com um livro de contas debaixo do braço. "
                + "Do segundo desce um homem de terno de linho branco, sem uma mancha de poeira, como se o sol trabalhasse para ele.")
                .abreCapitulo().comAutosave();
        c4a
                .comInterludio("Na Estação de Bombeamento 7, vocês encontram mais do que a junta de vedação. A sala de controle está intacta, "
                + "coberta de poeira, com um caderno de registro aberto na última página.")
                .comInterludio("A letra é firme: \"14/03/2052. Adutora Norte desligada por ordem de A. Vasques. Motivo: realocação comercial "
                + "da vazão.\" Abaixo, outra letra, tremida: \"Ficamos sem água para voltar. Se alguém achar isto, avise nossas "
                + "famílias.\" No chão, ao lado da porta trancada por fora, há quatro crachás.")
                .comInterludio("Um deles diz: Rosa Lima, técnica de pressão. Sandro pega o crachá, olha muito tempo para a foto e o guarda no "
                + "bolso da camisa sem dizer uma palavra. Era a mãe dele. A seca não esvaziou a região sozinha: alguém fechou a "
                + "torneira e esperou o preço subir.")
                .comInterludioSe(Condicao.decisao("CENA_3", 2, "ter vasculhado a estação"),
                        "O rádio que você achou estava na mesma sala. Talvez fosse o rádio em que Rosa pediu socorro, sem ninguém para "
                + "ouvir.");
        c4a.comInterludioFala(
                f(NPC_SANDRO, "(ajoelhado ao lado da porta, com um crachá na mão) É a minha mãe. Rosa Lima. É ela."),
                f(NPC_BALTAZAR, "(tira o chapéu) Sinto muito, menino."),
                f(NPC_SANDRO, "Ela não foi embora. Ela não abandonou a gente. Trancaram ela aqui dentro."),
                f(NPC_BALTAZAR, "(guarda o caderno de registro dentro da camisa) Isto aqui vai voltar com a gente. Alguém vai ter que ler "
                        + "em voz alta."));
        falas(c4a,
                f(NPC_DANTAS, "Bom-Jardim. Quatrocentas e doze pessoas, exatamente como na contagem que o... que a gente levantou de longe, Coronel."),
                f(NPC_VASQUES, "Sargento. (sorri para os moradores) Boa tarde, minha gente. Não se assustem. Eu não vim tomar nada. "
                        + "Eu vim administrar."),
                f(NPC_MILENA, "(baixo, para você) Os números nas portas, o drone, as fitas nas cabras... Eram eles contando a gente."),
                f(NPC_DANTAS, "(baixo, para o Coronel) Eles estão olhando pras armas, senhor."),
                f(NPC_VASQUES, "Então guarde as armas, Sargento. A gente conversa melhor com as mãos vazias."),
                f(NPC_VASQUES, "(repara em você) Ora, ora. Fugiu do Boqueirão e foi dar justamente aqui. O sertão é grande, "
                        + "mas a gente sempre se reencontra."),
                f(NPC_MILENA, "Quem é esse homem?"),
                f(NPC_VASQUES, "Um amigo antigo. Vamos ao que interessa: tributo é civilização."));
        c4a.adicionarEscolha(new Escolha("Encarar o Coronel: “O Boqueirão ainda lembra do que o senhor fez.”", "CENA_4", null,
                c(0, 0, -5, 2).comAfinidade(NPC_MILENA, 1).comAtributo(TipoAtributo.LIDERANCA, 1))
                .comReacao(f(NPC_VASQUES, "(ri baixo) Lembra, é? Então lembre também que quem lembra demais acaba pagando por isso.")));
        c4a.adicionarEscolha(new Escolha("Baixar a cabeça e recuar para trás de Milena.", "CENA_4", null,
                c(0, 0, 0, 0))
                .comReacao(f(NPC_VASQUES, "Sensato. Você sempre foi.")));
        c4a.adicionarEscolha(new Escolha("Ficar em silêncio e gravar cada detalhe do Coronel e de Dantas.", "CENA_4", null,
                c(0, 0, 0, 1).comAtributo(TipoAtributo.NEGOCIACAO, 1).comAfinidade(NPC_DANTAS, 1))
                .comNarracao("O terno nunca amassa. O sargento olha para os moradores antes de olhar para o chão. "
                        + "Você guarda os dois detalhes como quem guarda fósforos."));

        Cena c4 = cena("CENA_4", 4, "A Chegada da Cobrança",
                "Vasques senta no banco de pedra do Umbuzeiro como se a árvore fosse dele, e abre as mãos. Os moradores se aglomeram em silêncio.")
                .comFechamento("Por trás da calma de Júlio, você sente algo que não consegue nomear. Depois, o dia passa de um jeito estranho, "
                        + "sem resposta clara, e o pôr do sol chega.");
        falas(c4,
                f(NPC_VASQUES, "Eu sou razoável. Quarenta por cento da água do Olho. Dez sacos do Celeiro Velho. "
                        + "E as chaves da cisterna ficam com a Guarda, por segurança. De vocês, claro."),
                f(NPC_BALTAZAR, "Segurança de quem?"),
                f(NPC_JULIO, "Com o devido respeito, Coronel, quatrocentas pessoas não fecham a conta com sessenta por cento. "
                        + "Talvez um prazo..."),
                f(NPC_MILENA, "Quem decide aqui é o povoado. Você tem até o pôr do sol."),
                f(NPC_VASQUES, "Até o pôr do sol. E o voto do novo conselheiro? Dizem que agora você fala por eles. Quero ouvir."));
        c4.adicionarEscolha(new Escolha("Entregar a cota exigida: sem briga, sem sangue.", "CENA_5P", null,
                c(-5, -15, -5, -2).comAfinidade(NPC_MILENA, -1).comAfinidade(NPC_JULIO, 1))
                .comReacao(f(NPC_VASQUES, "Viu? Civilização.")));
        c4.adicionarEscolha(new Escolha("Negociar: oferecer trabalho e um prazo em vez de água.", "CENA_5P",
                Condicao.atributoMinimo(TipoAtributo.NEGOCIACAO, 2),
                c(-5, -5, -10, 2).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_JULIO, 1).comAtributo(TipoAtributo.NEGOCIACAO, 1))
                .comReacao(f(NPC_VASQUES, "Prazo... eu gosto de prazo. Trinta dias."),
                        f(NPC_JULIO, "(sorri de lado) Muito bem.")));
        c4.adicionarEscolha(new Escolha("Recusar: a água do Olho é do povoado.", "CENA_5P", null,
                c(0, 0, -5, 3).comAfinidade(NPC_MILENA, 2).comAfinidade(NPC_BALTAZAR, 1).comAtributo(TipoAtributo.LIDERANCA, 1))
                .comReacao(f(NPC_VASQUES, "Pois recuse. Tudo se paga com juros.")));
        c4.adicionarEscolha(new Escolha("Propor que o povoado vote em voz alta e o Coronel ouça o resultado.", "CENA_5P",
                Condicao.atributoMinimo(TipoAtributo.LIDERANCA, 2),
                c(-5, 0, -5, 4).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1).comAtributo(TipoAtributo.LIDERANCA, 1))
                .comReacao(f(NPC_MILENA, "Quem é contra? (silêncio) Quem é a favor de recusar? (dezenas de mãos se levantam)"),
                        f(NPC_VASQUES, "(sorri sem calor) Democracia. Que charmoso.")));
    }

    // ------------------------------------------------------------------ capítulo 5

    private void capitulo5() {
        // O castigo de Caetano acontece diante do jogador (antes era só narrado)
        Cena c5p = cena("CENA_5P", 5, "O Rastro do Furto",
                "Na manhã seguinte à cobrança, a milícia volta para \"conferir o pagamento\". Caetano, um rapaz de "
                + "dezenove anos, filho de Dona Socorro, está na fila da cisterna quando um miliciano empurra a mãe dele "
                + "para passar na frente. Caetano cospe nas botas do homem.\nA praça inteira para. Vasques, sentado no "
                + "banco de pedra, não se levanta.")
                .abreCapitulo()
                .comInterludioSe(Condicao.decisao("CENA_4", 0, "ter entregado a cota"),
                        "Como o povoado aceitou pagar o tributo, a Guarda volta no dia seguinte e leva o dobro do combinado. "
                + "Ninguém tem coragem de reclamar. A mãe de Caetano passa por você na fila e não responde ao seu bom-dia.")
                .comInterludioSe(Condicao.decisao("CENA_4", 2, "ter recusado o tributo"),
                        "Recusar teve preço: dois barris do Olho aparecem furados de bala na manhã seguinte. Ninguém viu quem foi. "
                + "Todos sabem.");
        falas(c5p,
                f(NPC_VASQUES, "(sem levantar a voz) Sargento Dantas. Amarre esse rapaz no Umbuzeiro. Sem chapéu, sem água, até o pôr do "
                        + "sol."),
                f(NPC_DANTAS, "Coronel... ele é só um menino. Foi só um cuspe."),
                f(NPC_VASQUES, "Por isso mesmo. Que a árvore ensine o que os pais não ensinaram. Agora, Sargento."),
                f(NPC_DANTAS, "(passa a corda pelo peito de Caetano e dá o nó no tronco, sem olhar para ninguém) Desculpa, rapaz."),
                f(NPC_MILENA, "(atravessando a praça, com uma panela na mão) Solta ele! Bom-Jardim não deixa ninguém amarrado no sol!"),
                f(NPC_BALTAZAR, "(abre o canivete) Eu corto essa corda."),
                f(NPC_VASQUES, "(para os milicianos) Se alguém tocar na corda, atirem no chão. Se insistirem, não atirem no chão."));
        c5p.adicionarEscolha(new Escolha("Ficar na frente da árvore com Milena e Baltazar, batendo panela com o povo.", "CENA_5A", null,
                c(0, 0, -10, 2).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1))
                .comFalaDoJogador("Solta ele! Caetano! Caetano!")
                .comReacao(f(NPC_MILENA, "Mais alto! Que o sertão inteiro escute!"),
                        f(NPC_VASQUES, "(cansado) Sargento. Mostre a eles."))
                .comNarracao("Os milicianos atiram para o alto, depois para o chão, perto dos seus pés. Baltazar leva uma coronhada no "
                        + "rosto e cai de joelhos. Um cano de fuzil encosta no seu peito e empurra você contra o tronco. Dois "
                        + "homens são arrastados para a carroceria de uma camionete. Quando a praça esvazia, o Coronel anuncia: por "
                        + "três dias, a cota de água de todos cai pela metade. Caetano fica amarrado até o pôr do sol. O protesto "
                        + "não mudou nada."));
        c5p.adicionarEscolha(new Escolha("Assistir de longe, sem se mexer.", "CENA_5A", null,
                c(0, 0, 0, 0))
                .comFalaDoJogador("(você fica parado, de punhos fechados, longe da árvore)")
                .comNarracao("De longe, você vê tudo: as panelas, os tiros para o alto, a coronhada em Baltazar, os dois homens "
                        + "arrastados para a camionete. O Coronel corta a cota de todos pela metade por três dias. Caetano fica "
                        + "amarrado até o pôr do sol. O protesto não mudou nada."));
        c5p.adicionarEscolha(new Escolha("Voltar para casa e fechar a porta.", "CENA_5A", null,
                c(0, 0, 5, -2))
                .comFalaDoJogador("(você entra em casa e passa a tranca na porta)")
                .comNarracao("Pela janela fechada, você ouve as panelas, depois os tiros, depois o silêncio. À tarde, alguém bate na "
                        + "sua porta para avisar que a cota foi cortada pela metade. Você não abre."));
        c5p.adicionarEscolha(new Escolha("Esperar a praça esvaziar e levar a sua água escondido para Caetano.", "CENA_5A",
                Condicao.item(rotas.getItem(ITEM_GARRAFA)),
                c(0, -10, -5, 3).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_DANTAS, 1))
                .comFalaDoJogador("(baixinho, encostando a garrafa nos lábios dele) Bebe devagar, Caetano. Devagar.")
                .comReacao(f(NPC_DANTAS, "(de guarda, olha para o outro lado) Eu não vi nada. Anda logo."))
                .comNarracao("Caetano bebe sem conseguir falar. O protesto da tarde não mudou nada, mas ele chega vivo ao pôr do sol."));

        Cena c5a = cena("CENA_5A", 5, "O Rastro do Furto",
                "Dias depois, Milena destranca o Celeiro Velho e fica parada na porta, imóvel. As prateleiras estão varridas, o chão foi "
                + "esfregado e o cheiro de grão sumiu. Faltam dezoito sacos, e a porta está inteira, sem um arranhão.")
                .comPontoDeSalvamento();
        c5a.comInterludio("Naquela noite, quando soltam Caetano, ele não consegue ficar de pé. Sobrevive, mas as mãos tremem até "
                        + "hoje. Os dois homens arrastados para a camionete voltam dois dias depois, sem falar sobre onde "
                        + "estiveram. Baltazar fica com o olho roxo por uma semana e não reclama. Depois disso, ninguém mais cospe "
                        + "em bota nenhuma, e ninguém mais bate panela.");
        falas(c5a,
                f(NPC_MILENA, "Dezoito sacos. Dezoito! Não foi rato, não foi vento, e a porta está inteira."),
                f(NPC_JULIO, "Foi a Guarda, Milena. De noite, com a calma de quem tem pressa. Eu refiz o inventário duas vezes. A conta não mente."),
                f(NPC_BALTAZAR, "(examina a fechadura) Não foi arrombada. Usaram uma chave."),
                f(NPC_SANDRO, "Quem tem chave? Só a Dona Milena, o Seu Júlio e o Seu Raimundo, do porão."),
                f(NPC_RAIMUNDO, "(sem erguer os olhos do cachimbo apagado) Eu guardo o porão, não o celeiro. Mas ninguém pergunta o que eu vejo."),
                f(NPC_JULIO, "Eu não gosto do rumo dessa pergunta, menino. (a voz, por um segundo, perde o açúcar)"));
        c5a.adicionarEscolha(new Escolha("Propor um inventário aberto: cotas e chaves conferidas na frente de todos.", "CENA_5", null,
                c(0, 0, -5, 1).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_JULIO, -1).comAtributo(TipoAtributo.LIDERANCA, 1))
                .comReacao(f(NPC_MILENA, "Isso. Conta feita à vista de todos."),
                        f(NPC_JULIO, "(uma pausa curta demais) Claro. Transparência.")));
        c5a.adicionarEscolha(new Escolha("Concordar com Júlio: só pode ter sido a milícia.", "CENA_5", null,
                c(0, 0, 0, 0).comAfinidade(NPC_JULIO, 1))
                .comReacao(f(NPC_JULIO, "Sensato. Com o devido respeito, ainda bem que alguém aqui pensa.")));
        c5a.adicionarEscolha(new Escolha("Pedir que Raimundo abra o porão e mostre a chave antes de acusar alguém.", "CENA_5", null,
                c(0, 0, -5, 1).encheGarrafa(rotas.getItem(ITEM_GARRAFA)).comAfinidade(NPC_RAIMUNDO, 1))
                .comReacao(f(NPC_RAIMUNDO, "(entrega a chave sem reclamar) Pode olhar. Só não repara na bagunça."),
                        f(NPC_MILENA, "Você é do tipo que confere antes de gritar. Gosto disso.")));

        c5a.adicionarEscolha(new Escolha("Forçar a gaveta da Casa das Cotas com a chave inglesa.", "CENA_5",
                Condicao.item(rotas.getItem(ITEM_CHAVE_INGLESA)),
                c(0, 0, -5, 1).iniciaMissao(MISSAO_TRAIDOR))
                .comFalaDoJogador("Com licença, Júlio. Esta gaveta vai abrir agora.")
                .comNarracao("A fechadura cede na segunda volta da chave inglesa. Dentro da gaveta, embrulhada num pano, há uma cópia "
                        + "da chave do Celeiro Velho, ainda com rebarba de lima. Júlio não está em casa para explicar."));

        Cena c5 = cena("CENA_5", 5, "O Rastro do Furto",
                "Antes do amanhecer, você volta ao Celeiro Velho com Sandro e uma lamparina. Atrás do prédio, a terra conta uma história que "
                + "a porta calou. Há marcas pesadas de botas de milicianos, até a janela dos fundos. E há outra trilha, leve e regular, "
                + "de alpargatas, que entra no celeiro e sai sem carregar nada.");
        falas(c5,
                f(NPC_SANDRO, "Duas pegadas. Uma pesada, uma leve. A leve não está carregando nada!"),
                f(NPC_BALTAZAR, "(chega em silêncio) Quem pisa leve abriu a porta. Quem pisa pesado levou os sacos."),
                f(NPC_SANDRO, "Então alguém daqui abriu a porta para eles?"),
                f(NPC_BALTAZAR, "Alguém."),
                f(NPC_SANDRO, "(baixinho) É sandália de couro, com costura dupla. Só o Seu Júlio usa dessas."),
                f(NPC_BALTAZAR, "Não diga isso em voz alta. Primeiro a prova, depois o nome."));
        c5.adicionarEscolha(new Escolha("Investigar o sumiço.", "CENA_6A", null,
                c(-5, 0, -10, 5, ITEM_PISTA_BOTAS).comAfinidade(NPC_SANDRO, 1).comAtributo(TipoAtributo.TECNICO, 1)
                        .iniciaMissao(MISSAO_TRAIDOR).desbloqueia(CONQ_FARO_DE_DETETIVE))
                .comReacao(f(NPC_SANDRO, "Eu ajudo! Eu sou ótimo em passar despercebido.")));
        c5.adicionarEscolha(new Escolha("Deixar para lá e poupar energia.", "CENA_6A", null,
                c(5, 0, 10, -3).comAfinidade(NPC_BALTAZAR, -1))
                .comReacao(f(NPC_BALTAZAR, "(balança a cabeça) Cansaço não é desculpa.")));
        c5.adicionarEscolha(new Escolha("Ligar o rádio de manivela e escutar se a milícia comenta o roubo.", "CENA_6A",
                Condicao.item(rotas.getItem(ITEM_RADIO)),
                c(0, 0, -5, 3, ITEM_PISTA_BOTAS).comAfinidade(NPC_SANDRO, 1).iniciaMissao(MISSAO_TRAIDOR)
                        .desbloqueia(CONQ_FARO_DE_DETETIVE))
                .comReacao(f(NPC_SANDRO, "Shhh! Tem gente falando baixo!"))
                .comNarracao("No meio do chiado, uma voz de rádio de campo diz: \"...a chave do celeiro está conosco desde a lua passada.\" "
                        + "Você anota a hora e a frequência."));
    }

    // ------------------------------------------------------------------ capítulo 6

    private void capitulo6() {
        Cena c6a = cena("CENA_6A", 6, "Pacto no Serrote",
                "Júlio chama você à Casa das Cotas ao entardecer. É uma sala pequena, de paredes forradas de cadernos, com uma balança de latão "
                + "e um cheiro de tinta. Ele serve café de chicória, quente, no único copo inteiro da casa. Fala baixo, e cada frase parece "
                + "ter sido ensaiada diante do espelho.")
                .abreCapitulo().comAutosave();
        c6a
                .comInterludio("Milena conta, numa noite sem lua, que Júlio chegou a Bom-Jardim oito anos atrás com uma menina de seis anos, "
                + "Lia. A menina não passou do primeiro verão. Júlio nunca falou dela. No caderno de cotas, a linha de Lia "
                + "continua lá, sem risco.")
                .comInterludio("Duas noites depois, você encontra o Sargento Dantas sozinho na beira da estrada, bêbado de cachaça de umbu. "
                + "Ele não está armado. Diz que a mulher e os dois filhos dele moram no Boqueirão, \"sob a proteção do "
                + "Coronel\", e que todo homem da Guarda tem alguém lá. \"A gente não serve ao Coronel. A gente paga o resgate "
                + "todo dia.\" Antes de ir embora, olha para as próprias mãos e diz que ainda sente nelas a corda do Caetano.")
                .comInterludioSe(Condicao.item(rotas.getItem(ITEM_PISTA_BOTAS)),
                        "Você mostra a Baltazar o molde da sandália de costura dupla que tirou no barro do celeiro. Ele não diz nome "
                + "nenhum. Só guarda o molde dentro da caixa de ferramentas e passa a dormir com a porta da oficina aberta.");
        c6a.comInterludioFala(
                f(NPC_DANTAS, "(sentado na beira da estrada, com uma garrafa de cachaça de umbu) Você é o do Boqueirão. Senta. Não "
                        + "estou armado."),
                f(NPC_DANTAS, "Minha mulher e meus dois filhos moram lá. \"Sob a proteção do Coronel\". Todo homem da Guarda tem alguém "
                        + "lá."),
                f(NPC_DANTAS, "A gente não serve ao Coronel. A gente paga o resgate todo dia."),
                f(NPC_DANTAS, "(olha para as próprias mãos) Ainda sinto a corda do Caetano nelas."));
        falas(c6a,
                f(NPC_JULIO, "Sente, por favor. É só chicória, mas é quente."),
                f(NPC_JULIO, "Eu gosto de você. Por isso falo sem enfeite: Bom-Jardim perde. Não é pessimismo, é aritmética."),
                f(NPC_JULIO, "Há gente no Serrote do Gavião que fala em nome do Coronel, mas pensa mais devagar que ele. "
                        + "Eles aceitariam um acordo reservado."),
                f(NPC_JULIO, "Garantem a nossa cisterna. Em troca, a gente deixa de... atrapalhar."),
                f(NPC_JULIO, "(girando o copo vazio) Eu vi o Boqueirão secar por dentro, sabe? Primeiro o poço, depois os vizinhos. Eu não vou ver de novo."),
                f(NPC_JULIO, "Preciso que você vá comigo. O povo confia em você. Em mim, confia menos do que eu merecia."));
        c6a.adicionarEscolha(new Escolha("Ouvir tudo e decidir depois, sem ir ao Serrote.", "CENA_6", null,
                c(0, 0, 0, 0))
                .comReacao(f(NPC_JULIO, "Prudente. Eu admiro.")));
        c6a.adicionarEscolha(new Escolha("Ir ao Serrote por conta própria e observar de longe.", "CENA_6", null,
                c(0, 0, -5, 1, ITEM_FICHA_BOQUEIRAO))
                .comNarracao("Do alto de uma pedra, você vê Júlio entregar um papel dobrado a um homem de uniforme. Quando ele se afasta, "
                        + "você acha no chão uma ficha de latão com o selo do poço do Boqueirão."));
        c6a.adicionarEscolha(new Escolha("Pedir um dia para consultar Milena e Baltazar antes de responder.", "CENA_6", null,
                c(0, 0, 0, 2).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1))
                .comReacao(f(NPC_JULIO, "(o sorriso estreita um milímetro) Claro. Um dia. Os números esperam."))
                .comNarracao("Quando você volta ao Umbuzeiro, Milena já sabe que algo está errado. Ela só pergunta: \"Quer café ou quer verdade?\""));

        Cena c6 = cena("CENA_6", 6, "Pacto no Serrote",
                "Amanhece com cheiro de chuva que não vai cair. Júlio encontra você sozinho, ao pé do Umbuzeiro, e põe uma ficha de latão "
                + "na sua mão: o convite. O mensageiro espera na pedra do Serrote. Você pensa na boca ressecada do Boqueirão e na caneca de Milena.");
        falas(c6,
                f(NPC_JULIO, "É agora. Uma palavra sua e hoje à noite chegam doze barris e quarenta sacos. Um aperto de mão."),
                f(NPC_JULIO, "Em troca, ninguém mexe nas cercas, ninguém dá o alarme, e o Baltazar... descansa."),
                f(NPC_JULIO, "Eu entendo o seu olhar. Mas o que vale uma consciência limpa diante de quatrocentos estômagos?"),
                f(NPC_JULIO, "Pense numa criança sem sede. Só isso. Pense nela e depois me responda."));
        c6.adicionarEscolha(new Escolha("Recusar o acordo.", "CENA_6B", null,
                c(0, 0, 0, 5).comAfinidade(NPC_JULIO, -3))
                .comReacao(f(NPC_JULIO, "(a máscara de calma estala por meio segundo) Que pena. Que pena mesmo.")));
        c6.adicionarEscolha(new Escolha("Aceitar a proposta sombria.", "CENA_6B", null,
                c(20, 20, 20, -10).comAfinidade(NPC_JULIO, 3))
                .comReacao(f(NPC_JULIO, "Sábia escolha. Você vai ver: a gente sobrevive.")));
        c6.adicionarEscolha(new Escolha("Fingir que aceita e passar a vigiar Júlio de perto.", "CENA_6B",
                Condicao.atributoMinimo(TipoAtributo.NEGOCIACAO, 3),
                c(5, 5, -5, 2).comAfinidade(NPC_JULIO, 1).comAfinidade(NPC_BALTAZAR, 1))
                .comReacao(f(NPC_JULIO, "Eu sabia. Eu sabia que você entendia de aritmética.")));


        Cena c6b = cena("CENA_6B", 6, "Pacto no Serrote",
                "Você e Baltazar sabem que o Coronel vai voltar, e que a cerca sozinha não segura ninguém. Naquela noite, "
                + "os dois batem de porta em porta, de sítio em sítio, chamando para a assembleia. Algumas famílias já "
                + "amarram trouxas para fugir para as ruínas do centro.\nA praça enche devagar. Há medo em todos os rostos, "
                + "e há raiva também, desde o dia em que Caetano ficou amarrado na árvore.");
        falas(c6b,
                f(NPC_BALTAZAR, "(de pé, diante de todos, falando mais do que nunca) Eu não sei falar bonito. A milícia volta. Com cerca "
                        + "e com gente, a gente segura. Só com cerca, não segura."),
                f(NPC_RAIMUNDO, "E segura com o quê, Baltazar? Com enxada contra fuzil? Eu tenho neto."),
                f(NPC_JULIO, "Com o devido respeito, a conta não fecha. Resistir é enterrar metade de nós."),
                f(NPC_SANDRO, "(baixinho, ao seu lado) Fala da minha mãe. Fala da Estação."));
        c6b.adicionarEscolha(new Escolha("Mostrar o caderno da Estação 7: foi Vasques quem desligou a adutora e matou a cidade.", "CENA_7A", null,
                c(0, 0, -5, 3).comAfinidade(NPC_BALTAZAR, 1).comAfinidade(NPC_SANDRO, 1).comAtributo(TipoAtributo.LIDERANCA, 1).iniciaMissao(MISSAO_DEFESA))
                .comReacao(f(NPC_RAIMUNDO, "(lê a página, e a mão treme) Meu irmão morreu esperando essa água. Morreu esperando."),
                        f(NPC_MILENA, "Quem é a favor de resistir?"))
                .comNarracao("As mãos se levantam devagar, depois de uma vez. Cento e oitenta pessoas entram no mutirão de defesa: "
                        + "homens, mulheres, velhos que ainda aguentam carregar areia. É menos da metade do povoado. É o suficiente "
                        + "para começar."));
        c6b.adicionarEscolha(new Escolha("Falar dos mortos: Dona Josefa, o filho de Damião com febre, Caetano amarrado na árvore.", "CENA_7A", null,
                c(0, 0, 0, 0))
                .comReacao(f(NPC_MILENA, "Quem é a favor de resistir?"))
                .comNarracao("Cento e vinte pessoas se oferecem para o mutirão de defesa. Outras abaixam a cabeça. Ninguém as culpa em "
                        + "voz alta."));
        c6b.adicionarEscolha(new Escolha("Dizer que cada família deve decidir sozinha se fica ou vai embora.", "CENA_7A", null,
                c(0, 0, 0, -3).comAfinidade(NPC_BALTAZAR, -1))
                .comReacao(f(NPC_BALTAZAR, "(olha para você por muito tempo) Entendi."))
                .comNarracao("Naquela noite, onze famílias partem para as ruínas do centro. Na cerca leste, ficam Baltazar e algumas "
                        + "dezenas de pessoas."));
    }

    // ------------------------------------------------------------------ capítulo 7

    private void capitulo7() {
        Cena c7a = cena("CENA_7A", 7, "O Cerco",
                "A milícia não manda recado: manda silêncio. Há três dias não se vê poeira no horizonte, e é isso que assusta. Baltazar passa "
                + "a noite inteira trançando mandacaru e arame farpado nas cercas de contenção. Milena mede a água da cisterna em baldes, como "
                + "quem conta dinheiro antes de uma viagem sem volta. As crianças dormem juntas, no chão do galpão, de mãos dadas.")
                .abreCapitulo().comPontoDeSalvamento();
        c7a
                .comInterludio("Numa madrugada, o cheiro chega antes da notícia: diesel. Alguém abriu o segundo compartimento do Olho e "
                + "despejou combustível na água. Um terço da reserva do povoado está perdida. Quem fez isso tinha a chave.")
                .comInterludio("Milena passa a manhã inteira tirando baldes da água estragada, como se pudesse salvá-la. Quando para, senta "
                + "no chão da cisterna e chora pela primeira vez desde que você chegou. Ninguém a interrompe.")
                .comInterludioSe(Condicao.decisao("CENA_6", 1, "ter aceitado o pacto de Júlio"),
                        "Os barris do acordo chegaram, como Júlio prometeu. A água tem o mesmo gosto de diesel da cisterna. Dois "
                + "velhos adoecem depois de beber. Você também bebeu.")
                .comInterludioSe(Condicao.decisao("CENA_6", 0, "ter recusado o pacto de Júlio"),
                        "Depois que você recusou o acordo, Júlio deixou de cumprimentar você. O drone passa toda noite, cada vez mais "
                + "baixo, e às vezes para em cima da sua casa.");
        c7a.comInterludioFala(
                f(NPC_MILENA, "(de joelhos dentro da cisterna, com um balde de água escura) Diesel. Um terço. Um terço da água de todo "
                        + "mundo."),
                f(NPC_BALTAZAR, "Quem fez isso tinha a chave."),
                f(NPC_MILENA, "(a voz falha pela primeira vez) Tinha a nossa chave, Baltazar. A nossa."));
        falas(c7a,
                f(NPC_BALTAZAR, "Eles vêm amanhã, ou depois. A cerca tem que aguentar."),
                f(NPC_MILENA, "Tem mais coisa para proteger do que parede. Tem gente."),
                f(NPC_RAIMUNDO, "(arrastando um colchão) Se alguém precisar de chave, eu tenho. Dessa vez, de graça."),
                f(NPC_MILENA, "(olha para ele, surpresa) Seu Raimundo, anote a data: o senhor deu uma coisa de graça.")); 
        c7a.adicionarEscolha(new Escolha("Ajudar Baltazar a reforçar as cercas.", "CENA_7", null,
                c(0, 0, -10, 1).comAfinidade(NPC_BALTAZAR, 1).comAtributo(TipoAtributo.TECNICO, 1))
                .comReacao(f(NPC_BALTAZAR, "(entrega um martelo) Bata onde eu bater.")));
        c7a.adicionarEscolha(new Escolha("Ajudar Milena a preparar a cisterna e abrigar os mais velhos.", "CENA_7", null,
                c(0, -5, 0, 1).encheGarrafa(rotas.getItem(ITEM_GARRAFA)).comAfinidade(NPC_MILENA, 1).comAtributo(TipoAtributo.LIDERANCA, 1))
                .comReacao(f(NPC_MILENA, "Uma mão a mais vale por duas, quando a outra mão ainda treme.")));
        c7a.adicionarEscolha(new Escolha("Organizar turnos de vigia e ensinar às crianças a rota de fuga da cisterna.", "CENA_7",
                Condicao.atributoMinimo(TipoAtributo.LIDERANCA, 2),
                c(0, 0, -5, 2).comAfinidade(NPC_SANDRO, 1).comAfinidade(NPC_MILENA, 1).comAtributo(TipoAtributo.LIDERANCA, 1))
                .comReacao(f(NPC_SANDRO, "Primeiro o degrau podre, depois a pedra solta, depois o escuro. Eu decorei!"),
                        f(NPC_MILENA, "Se alguém decorar isso melhor que ele, eu entrego o povoado.")));

        Cena c7 = cena("CENA_7", 7, "O Cerco",
                "O ataque vem ao alvorecer, com um rugido de motores. Uma camionete arranca a cerca leste do chão, puxando-a por cabos de aço. "
                + "Tiros riscam o ar. A voz de Dantas, deformada por um megafone, atravessa o povoado.");
        falas(c7,
                f(NPC_DANTAS, "Rendam-se! O Coronel tem paciência, mas ela não é infinita!"),
                f(NPC_BALTAZAR, "{nome}. Venha para cá. Agora."),
                f(NPC_MILENA, "As crianças já estão na cisterna! Quem fecha a porta de baixo?"),
                f(NPC_SANDRO, "(voz fina, de dentro do galpão) Eu fico aqui! Eu não choro! Eu não choro!"),
                f(NPC_DANTAS, "(no megafone, a voz falhando) É só entregar a água! Ninguém precisa se machucar!"));
        c7.adicionarEscolha(new Escolha("Lutar ao lado de Baltazar.", "CENA_7B", null,
                c(-10, -10, -20, 5).comAfinidade(NPC_BALTAZAR, 3).comAtributo(TipoAtributo.LIDERANCA, 1).iniciaMissao(MISSAO_DEFESA))
                .comReacao(f(NPC_BALTAZAR, "Orgulho."))
                .comNarracao("Vindo de Baltazar, é quase um discurso."));
        c7.adicionarEscolha(new Escolha("Defender a cisterna e as crianças ao lado de Milena.", "CENA_7B",
                Condicao.afinidadeMinima(rotas.getNpc(NPC_MILENA), 1),
                c(-5, -5, -10, 4).comAfinidade(NPC_MILENA, 2).comAtributo(TipoAtributo.LIDERANCA, 1).iniciaMissao(MISSAO_DEFESA))
                .comReacao(f(NPC_MILENA, "Cada porta que fecha, uma vida que fica.")));
        c7.adicionarEscolha(new Escolha("Esconder-se no fundo da cisterna e esperar passar.", "CENA_7B", null,
                c(0, 0, 5, -6).comAfinidade(NPC_BALTAZAR, -2).comAfinidade(NPC_MILENA, -2))
                .comNarracao("No escuro, você ouve os tiros e o choro contido de Sandro. Ninguém diz nada. Ninguém precisa."));
        c7.adicionarEscolha(new Escolha("Girar a manivela do rádio e pedir ajuda aos povoados vizinhos.", "CENA_7B",
                Condicao.item(rotas.getItem(ITEM_RADIO)),
                c(0, 0, -5, 3).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1).iniciaMissao(MISSAO_DEFESA))
                .comReacao(f(NPC_MILENA, "(entre dois tiros) Fale devagar! Diga o nome do povoado, a posição e o que está faltando!"))
                .comNarracao("Do outro lado do chiado, uma voz de mulher responde: \"Poço Fundo ouviu. Estamos saindo com o que temos.\""));


        Cena c7b = cena("CENA_7B", 7, "O Cerco",
                "A camionete da frente engata os cabos de aço na cerca leste e acelera. O arame geme, os mourões saem do "
                + "chão um a um. Do outro lado, o mutirão segue o que Baltazar ensinou: baldes de areia no motor, cordas "
                + "puxando a cerca de volta, forcados empurrando o arame contra os pneus.\nOs tiros começam quando o motor "
                + "engasga. Caetano, com as mãos ainda trêmulas, enche os baldes de pedra sem levantar a cabeça. Uma mulher "
                + "do sítio de cima amarra um pano no braço ferido do marido e volta para a corda. Damião segura o mourão "
                + "do portão com o corpo inteiro.\nUma bala o acerta no peito. Ele não larga o mourão.")
                .comInterludioSe(Condicao.decisao("CENA_6B", 0, "ter mostrado o caderno da Estação 7"),
                        "Na cerca há cento e oitenta pessoas, cada uma no posto que treinou: as da areia, as das cordas, as dos "
                        + "estilingues, as que levam os feridos para a cisterna.")
                .comInterludioSe(Condicao.decisao("CENA_6B", 1, "ter falado dos mortos"),
                        "Na cerca há cento e vinte pessoas. Faltam braços nas cordas, e todos sabem disso.")
                .comInterludioSe(Condicao.decisao("CENA_6B", 2, "ter deixado cada família decidir"),
                        "Na cerca há Baltazar, você e algumas dezenas de pessoas. Onde deveria haver gente, há buracos.");
        falas(c7b,
                f(NPC_DANTAS, "(no megafone) Última chance! Abram o portão e ninguém mais se machuca!"),
                f(NPC_BALTAZAR, "Areia no motor! Agora! Puxa a corda, puxa!"),
                f(NPC_MILENA, "(gritando do alto da cisterna) Estão contornando pelo baixio! Lado sul! Lado sul!"),
                f(NPC_RAIMUNDO, "(arrastando um saco de areia, sem fôlego) Mais areia! Quem tiver braço, pega um balde!"));
        c7b.adicionarEscolha(new Escolha("Correr até Damião e arrastá-lo para trás da cerca.", "CENA_8A", null,
                c(0, 0, -10, 2).comAfinidade(NPC_MILENA, 1))
                .comNarracao("Você o arrasta pelo chão de terra, debaixo de tiro. Ele morre no caminho, com a mão agarrada à sua "
                        + "camisa. A última palavra dele é o nome do filho. Ao cair da tarde, a camionete atolada é abandonada e a "
                        + "milícia recua."));
        c7b.adicionarEscolha(new Escolha("Segurar a linha ao lado de Baltazar enquanto a cerca aguenta.", "CENA_8A", null,
                c(0, 0, -10, 0))
                .comReacao(f(NPC_BALTAZAR, "Segura! Segura!"))
                .comNarracao("A cerca aguenta. Quando a poeira baixa e a milícia recua, Damião está caído junto ao portão, ainda "
                        + "abraçado ao mourão. Ninguém conseguiu chegar até ele a tempo."));
        c7b.adicionarEscolha(new Escolha("Recuar para a cisterna antes que a cerca caia.", "CENA_8A", null,
                c(0, 0, 5, -3).comAfinidade(NPC_BALTAZAR, -1))
                .comNarracao("Do fundo da cisterna, você ouve a cerca ceder, e depois voltar a ficar de pé, sem você. Damião morre "
                        + "junto ao portão."));
    }

    // ------------------------------------------------------------------ capítulo 8

    private void capitulo8() {
        Cena c8a = cena("CENA_8A", 8, "A Máscara Cai",
                "O primeiro ataque recua com o cair da tarde. O povoado respira, conta os feridos, bebe a água de todos. Sandro, agarrado "
                + "a uma cabra, puxa a sua manga com a cara de quem viu um fantasma.")
                .abreCapitulo().comAutosave();
        c8a
                .comInterludio("Damião morreu na cerca leste, abraçado ao mourão do portão por onde a água entra. Tinha "
                + "trinta e um anos. O filho dele, o mesmo que sobreviveu à febre, tem dois.")
                .comInterludio("O enterro é ao amanhecer. O chão está tão duro que quatro homens levam três horas para abrir a cova. Milena "
                + "lê os nomes do caderno de cotas, um por um, e para no de Damião. Ninguém sabe o que dizer. Sandro segura o "
                + "crachá da mãe no bolso.")
                .comInterludioSe(Condicao.decisao("CENA_7", 2, "ter se escondido na cisterna"),
                        "Ninguém fala com você no enterro. Quando você tenta pegar uma pá, Baltazar a tira da sua mão sem olhar para "
                + "você.")
                .comInterludioSe(Condicao.decisao("CENA_7", 0, "ter lutado ao lado de Baltazar"),
                        "Baltazar, com o braço enfaixado, segura o seu ombro na beira da cova. É o único gesto de carinho que você o "
                + "vê fazer.");
        c8a.comInterludioFala(
                f(NPC_MILENA, "(com o caderno de cotas aberto, na beira da cova) Damião Ferreira. Trinta e um anos. Pai do Davizinho."),
                f(NPC_MILENA, "(fecha o caderno) Não vou riscar o nome dele. Vou escrever do lado: defendeu o portão."));
        falas(c8a,
                f(NPC_SANDRO, "O Seu Júlio está mandando sinal com um espelho, lá no portão leste! Para o mato! Eu juro!"),
                f(NPC_BALTAZAR, "Mostre. (já está de pé)"),
                f(NPC_MILENA, "Menino, você tem certeza do que viu?"),
                f(NPC_SANDRO, "Duas piscadas curtas e uma comprida. Igual quando o Seu Júlio conta de dez em dez."),
                f(NPC_MILENA, "(passa a mão no rosto, devagar) Meu Deus. Ele contou a minha casa inteira."));
        c8a.adicionarEscolha(new Escolha("Seguir Júlio sozinho, pelo corredor das cercas.", "CENA_8", null,
                c(0, 0, -5, 1))
                .comNarracao("O barro frio, o cheiro de fumaça, a lamparina de Júlio balançando adiante, como um olho."));
        c8a.adicionarEscolha(new Escolha("Chamar Baltazar e Milena antes de fazer qualquer coisa.", "CENA_8", null,
                c(0, 0, 0, 2, ITEM_CAFE).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1))
                .comReacao(f(NPC_BALTAZAR, "Certo. Juntos.")));
        c8a.adicionarEscolha(new Escolha("Mandar Sandro vigiar o portão e soprar o apito se algo se mexer.", "CENA_8", null,
                c(0, 0, 0, 1).comAfinidade(NPC_SANDRO, 1))
                .comReacao(f(NPC_SANDRO, "Um apito longo para perigo, dois curtos para alegria. Eu criei o código!")));

        Cena c8 = cena("CENA_8", 8, "A Máscara Cai",
                "No portão leste, Júlio abre o trinco com a calma de quem sempre soube a combinação. Atrás da cerca, a silhueta de Dantas "
                + "espera, e mais atrás ainda uma camionete com os faróis apagados. Quando Júlio vira e vê você, ele não se assusta. "
                + "Ele sorri, e pela primeira vez o sorriso não parece ensaiado.");
        falas(c8,
                f(NPC_JULIO, "Não precisa me olhar assim. Eu só contei. Quem decidia era o Coronel. No Boqueirão eu contei cada gota que ele "
                        + "vendeu. Sabe o que eu aprendi? Que quem conta, sobrevive."),
                f(NPC_JULIO, "Eu avisei o Coronel das cercas, do Celeiro, do Olho. Dei a chave. Dei a contagem. E faria de novo."),
                f(NPC_JULIO, "Mas você não precisa pagar o preço. Atrás do Serrote tem um comboio de barris cheios. Venha comigo. "
                        + "Ninguém precisa saber."),
                f(NPC_DANTAS, "(da sombra, baixo) Se vai, vá logo, contador. A paciência do Coronel tem limite."),
                f(NPC_MILENA, "(chegando, a voz quebrada) Júlio..."));
        c8.adicionarEscolha(new Escolha("Enfrentar Júlio diante de todos, com as provas.", "CENA_9A",
                Condicao.item(rotas.getItem(ITEM_PISTA_BOTAS)),
                c(0, 0, -10, 5).comAfinidade(NPC_JULIO, -5).comAtributo(TipoAtributo.LIDERANCA, 1).concluiMissao(MISSAO_TRAIDOR))
                .comReacao(f(NPC_JULIO, "Você não entende. Eu salvei todo mundo de uma morte lenta."),
                        f(NPC_MILENA, "Você contou a nossa sede para os nossos carrascos.")));
        c8.adicionarEscolha(new Escolha("Segurar Júlio e entregá-lo a Baltazar, sem violência, para proteger o portão.", "CENA_9A", null,
                c(0, 0, -5, 3).comAfinidade(NPC_BALTAZAR, 1).comAfinidade(NPC_JULIO, -3).concluiMissao(MISSAO_TRAIDOR))
                .comReacao(f(NPC_BALTAZAR, "(prende as mãos dele) Muito bem. Sem sangue.")));
        c8.adicionarEscolha(new Escolha("Aceitar a oferta e seguir Júlio pela escuridão.", "CENA_9A", null,
                c(15, 25, 10, -8).comAfinidade(NPC_JULIO, 4).comAfinidade(NPC_MILENA, -3))
                .comReacao(f(NPC_JULIO, "Sabia que você ia entender."),
                        f(NPC_SANDRO, "(ao longe) Para onde você vai?"))
                .comNarracao("O comboio de barris é de verdade. A culpa, também. Antes do amanhecer você volta, com as mãos cheias e o peito "
                        + "vazio, a tempo de ver o fogo subir."));
        c8.adicionarEscolha(new Escolha("Lembrar a Júlio os nomes dos mortos do Boqueirão e perguntar quem ele quer ser.", "CENA_9A",
                Condicao.atributoMinimo(TipoAtributo.NEGOCIACAO, 3),
                c(0, 0, -5, 4).comAfinidade(NPC_JULIO, 1).comAfinidade(NPC_MILENA, 1).concluiMissao(MISSAO_TRAIDOR))
                .comReacao(f(NPC_JULIO, "(a voz falha) Você decorou todos eles?"),
                        f(NPC_MILENA, "(baixo) Deixa ele ouvir até o fim.")));
    }

    // ------------------------------------------------------------------ capítulo 9

    private void capitulo9() {
        Cena c9a = cena("CENA_9A", 9, "Batalha pelo Umbuzeiro",
                "O céu muda de cor antes de você entender por quê. Uma flecha de fogo cruza a noite e se crava na copa do Umbuzeiro-Mãe. "
                + "Em segundos, a árvore mais antiga de Bom-Jardim vira uma tocha. Faíscas descem sobre os telhados e os varais, e o último "
                + "reduto do povoado está em chamas.")
                .abreCapitulo().comPontoDeSalvamento().comFechamento("Não há tempo para tudo.");
        c9a
                .comInterludio("Depois do portão leste, nada é mais como antes. Baltazar dorme sentado, com o martelo no colo. Seu Raimundo, "
                + "que por trinta anos cobrou por cada favor, passa as noites em claro guardando o porão de graça, \"porque lá "
                + "embaixo está o mês que vem\".")
                .comInterludioSe(Condicao.decisao("CENA_8", 2, "ter seguido Júlio"),
                        "Você voltou com barris cheios e as mãos tremendo. Ninguém pergunta de onde veio a água. Milena só olha para "
                + "você uma vez, demorado, e depois passa a contar os seus baldes em separado.")
                .comInterludioSe(Condicao.decisao("CENA_8", 0, "ter exposto Júlio"),
                        "Júlio está preso na Casa das Cotas, entre os próprios cadernos. À noite, você ouve quando ele fala sozinho "
                + "com alguém que não está lá. Diz o nome Lia.")
                .comInterludioSe(Condicao.decisao("CENA_8", 1, "ter entregado Júlio a Baltazar"),
                        "Júlio está preso na Casa das Cotas, entre os próprios cadernos. À noite, você ouve quando ele fala sozinho "
                + "com alguém que não está lá. Diz o nome Lia.");
        falas(c9a,
                f(NPC_MILENA, "Se a árvore morre, o Olho seca em um ano. Se o Olho seca, o povoado vai junto."),
                f(NPC_BALTAZAR, "Vamos abrir um aceiro, cortar os galhos. Depressa."),
                f(NPC_DANTAS, "(ao longe, no megafone) Eu não mandei queimar! Alguém mandou queimar?!"),
                f(NPC_RAIMUNDO, "(ofegante, com um balde em cada mão) Eu tenho a chave do porão e uma mangueira velha lá dentro. Alguém me ajuda a arrastar?"));
        c9a.adicionarEscolha(new Escolha("Usar a água estocada para apagar o fogo, esvaziando parte da reserva.", "CENA_9", null,
                c(0, -25, -5, 4).comAfinidade(NPC_MILENA, 1))
                .comReacao(f(NPC_MILENA, "(vendo a água descer no tronco) Água que apaga fogo não se perde.")));
        c9a.adicionarEscolha(new Escolha("Abrir um aceiro, cortando as galhadas ao lado de Baltazar.", "CENA_9",
                Condicao.decisao("CENA_7A", 0, "ter ajudado Baltazar com as cercas"),
                c(0, 0, -15, 4).comAfinidade(NPC_BALTAZAR, 1))
                .comReacao(f(NPC_BALTAZAR, "Pegue um machado. Vamos.")));
        c9a.adicionarEscolha(new Escolha("Pegar as reservas e levar Sandro e as crianças pela Caatinga.", "CENA_9", null,
                c(10, 10, -5, 0).comAfinidade(NPC_SANDRO, 1))
                .comReacao(f(NPC_SANDRO, "(chorando, abraçado a uma cabra) E a árvore?"))
                .comNarracao("Na primeira curva, o clarão se apaga atrás de vocês e você volta, com as crianças e as reservas, a tempo de "
                        + "ver o Coronel entrar no pátio."));
        c9a.adicionarEscolha(new Escolha("Mandar Sandro ao rádio chamar os vizinhos enquanto você segura o fogo.", "CENA_9",
                Condicao.item(rotas.getItem(ITEM_RADIO)),
                c(0, -10, -10, 5).comAfinidade(NPC_SANDRO, 1).comAfinidade(NPC_MILENA, 1))
                .comReacao(f(NPC_SANDRO, "Poço Fundo! Poço Fundo, respondam! A árvore está queimando!"))
                .comNarracao("Pouco antes de o fogo ceder, o rádio chia uma resposta: motores ao longe, muitos."));

        c9a.adicionarEscolha(new Escolha("Abrir o registro velho da cisterna com a chave inglesa e molhar a raiz por baixo.", "CENA_9",
                Condicao.item(rotas.getItem(ITEM_CHAVE_INGLESA)),
                c(0, -5, -10, 3).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1))
                .comFalaDoJogador("Baltazar, me ajuda com este registro! Se ele abrir, a água sobe pela raiz!")
                .comReacao(f(NPC_BALTAZAR, "(apoia o peso do corpo na chave junto com você) Agora!"))
                .comNarracao("O registro, emperrado há anos, range e gira. A água sobe pelo cano velho e encharca a terra em volta da "
                        + "raiz. O fogo chega, chia e não pega."));

        Cena c9 = cena("CENA_9", 9, "Batalha pelo Umbuzeiro",
                "Quando o fogo cede, o Coronel Vasques atravessa o pátio com o terno branco manchado de fuligem e as mãos abertas, como quem "
                + "vem em paz. Atrás dele, no chão do porão, está Seu Raimundo: morreu abraçado à mangueira velha, com as mãos queimadas, "
                + "e salvou a reserva que vai alimentar o povoado no próximo mês. Os homens do Coronel estão cansados, Dantas já nem mira direito, e o Umbuzeiro, mesmo ferido, continua em pé. "
                + "É o momento mais silencioso do jogo.");
        falas(c9,
                f(NPC_VASQUES, "Chega de fogo, minha gente. Eu não gosto de destruição. É um desperdício. Entreguem o Olho e levem seus vivos."),
                f(NPC_VASQUES, "(olhando para você) Você sabe o que eu faço bem. O Boqueirão sabe. Eu administro a sede. E a sede sempre obedece."),
                f(NPC_MILENA, "E se a gente não entregar?"),
                f(NPC_VASQUES, "Então a árvore cai sozinha, e eu espero na sombra do que sobrar."),
                f(NPC_DANTAS, "(muito baixo, só para o Coronel) Senhor... essa gente nunca nos fez nada."),
                f(NPC_VASQUES, "(sem virar o rosto) Sargento. Cale-se."),
                f(NPC_BALTAZAR, "(ferido, encostado numa cerca) {nome}. É a última chance."));
        c9.adicionarEscolha(new Escolha("Enfrentar Vasques e expor os crimes do Boqueirão diante de todos, inclusive dos homens dele.", "CENA_9B",
                Condicao.atributoMinimo(TipoAtributo.LIDERANCA, 3),
                c(0, 0, -10, 4).concluiMissao(MISSAO_DEFESA).desbloqueia(CONQ_VOZ_DO_BOQUEIRAO))
                .comReacao(f(NPC_VASQUES, "(o riso falha por um instante) Quem lhe deu o direito de lembrar?"),
                        f(NPC_DANTAS, "(baixa a arma) Coronel... é verdade isso que estão dizendo?")));
        c9.adicionarEscolha(new Escolha("Aceitar o acordo do Coronel: entregar o Olho para poupar vidas.", "CENA_9B", null,
                c(5, 10, 0, -6).comAfinidade(NPC_MILENA, -2).concluiMissao(MISSAO_DEFESA))
                .comReacao(f(NPC_VASQUES, "Inteligente. Uma mão lava a outra.")));
        c9.adicionarEscolha(new Escolha("Deixar Milena decidir.", "CENA_9B", null,
                c(0, 0, 0, 2).comAfinidade(NPC_MILENA, 2).concluiMissao(MISSAO_DEFESA))
                .comReacao(f(NPC_MILENA, "(endireita as costas) Então escute, Coronel. Escute bem, porque eu só falo uma vez.")));
        c9.adicionarEscolha(new Escolha("Mostrar a ficha do Boqueirão aos homens do Coronel e perguntar quem ainda a recebe.", "CENA_9B",
                Condicao.item(rotas.getItem(ITEM_FICHA_BOQUEIRAO)),
                c(0, 0, -5, 5).comAfinidade(NPC_MILENA, 1).concluiMissao(MISSAO_DEFESA))
                .comReacao(f(NPC_DANTAS, "(olha a ficha, depois as próprias mãos) Esse selo... eu cobrava ele."),
                        f(NPC_VASQUES, "Sargento. Guarde essa arma."))
                .comNarracao("Um a um, alguns homens de uniforme baixam o cano. Ninguém ouviu o Coronel perder o controle até agora."));


        Cena c9b = cena("CENA_9B", 9, "Batalha pelo Umbuzeiro",
                "Ninguém combinou. Uma mulher do sítio de baixo sai de casa com uma enxada. Depois o vizinho, com uma "
                + "foice. Depois os filhos dele, com pedras. Em poucos minutos, duzentas, trezentas pessoas atravessam a "
                + "praça na direção das camionetes, com o que têm na mão.\nOs milicianos atiram. Gente cai, e quem vem "
                + "atrás passa por cima e continua. Baltazar sobe na carroceria da primeira camionete para arrancar o "
                + "atirador da metralhadora. É o começo de uma revolta que Bom-Jardim vai contar por muitos anos.")
                .comInterludioSe(Condicao.decisao("CENA_9", 0, "ter exposto o Coronel"),
                        "Os homens do Coronel ainda estão olhando uns para os outros, depois do que você contou. É nesse segundo "
                        + "de dúvida que Bom-Jardim se levanta.")
                .comInterludioSe(Condicao.decisao("CENA_9", 3, "ter mostrado a ficha"),
                        "A ficha do Boqueirão ainda passa de mão em mão entre os milicianos. É nesse segundo de dúvida que "
                        + "Bom-Jardim se levanta.")
                .comInterludioSe(Condicao.decisao("CENA_9", 2, "ter deixado Milena decidir"),
                        "Milena diz não ao Coronel, alto, para a praça inteira ouvir. Atrás dela, o povo começa a se mexer.")
                .comInterludioSe(Condicao.decisao("CENA_9", 1, "ter aceitado o acordo"),
                        "Você aceitou entregar o Olho. Milena não aceitou, e o povo também não. Quando os milicianos vão até a "
                        + "cisterna, encontram duzentas pessoas paradas na frente dela.");
        falas(c9b,
                f(NPC_MILENA, "(de pé no banco de pedra) Bom-Jardim! Quem ainda tem perna, levanta! Hoje a água não sai daqui!"),
                f(NPC_BALTAZAR, "(pega o martelo) {nome}. Comigo. Até o fim."),
                f(NPC_SANDRO, "Eu vou junto!"),
                f(NPC_MILENA, "Você vai para a cisterna com as crianças, Sandro. É uma ordem."),
                f(NPC_DANTAS, "(no meio dos milicianos) Não atirem neles! Não atirem!"),
                f(NPC_VASQUES, "(sem levantar a voz) Atirem. Em todos."));
        c9b.adicionarEscolha(new Escolha("Subir na camionete atrás de Baltazar.", "CENA_10A", null,
                c(0, 0, -15, 3).comAfinidade(NPC_SANDRO, 1).desbloqueia(CONQ_LEVANTE))
                .comReacao(f(NPC_BALTAZAR, "(caído no chão da carroceria, com a mão no seu ombro) Tá tudo bem... Cuida deles. Juntos."),
                        f(NPC_SANDRO, "(fugindo da cisterna, correndo pela praça) Seu Baltazar! Seu Baltazar!"),
                        f(NPC_MILENA, "(segura Sandro pelo braço) Não olha, menino. Não olha."))
                .comNarracao("Baltazar se joga na sua frente. O tiro que era para você o acerta no peito. Ainda assim, ele arranca a "
                        + "arma das mãos do atirador e a joga longe da carroceria. Morre sentado no chão da camionete, com a mão no "
                        + "seu ombro. A última coisa que diz é: \"Juntos.\""));
        c9b.adicionarEscolha(new Escolha("Organizar quem avança, para ninguém ficar sozinho no meio da praça.", "CENA_10A", null,
                c(0, 0, -10, 0).desbloqueia(CONQ_LEVANTE))
                .comReacao(f(NPC_MILENA, "(depois que a fumaça baixa) {nome}... é o Baltazar. Lá em cima da camionete."),
                        f(NPC_SANDRO, "(sobe na carroceria e pega o martelo da mão dele) Ele ainda está segurando. Ele não soltou."))
                .comNarracao("Você não vê Baltazar cair. Vê depois, quando a fumaça baixa: ele está sobre a carroceria da primeira "
                        + "camionete, com o martelo ainda na mão e o atirador desarmado no chão. Os olhos dele estão abertos, "
                        + "voltados para o Umbuzeiro."));
        c9b.adicionarEscolha(new Escolha("Proteger-se atrás do tronco queimado do Umbuzeiro.", "CENA_10A", null,
                c(0, 0, 0, -3).comAfinidade(NPC_BALTAZAR, -1))
                .comReacao(f(NPC_BALTAZAR, "(caindo da camionete, olhando na sua direção) ...juntos."),
                        f(NPC_MILENA, "(ajoelhada ao lado dele) Baltazar! Alguém! Alguém me ajuda aqui!"))
                .comNarracao("Do tronco queimado você vê tudo: Baltazar subindo na camionete, o tiro, a queda. Antes de fechar os "
                        + "olhos, ele olha para o lado onde você está."));
    }

    // ------------------------------------------------------------------ capítulo 10

    private void capitulo10() {
        Cena c10a = cena("CENA_10A", 10, "O Julgamento de 2071",
                "A poeira baixa. Do Umbuzeiro restou um tronco escuro e, na base, um broto verde do tamanho de um dedo. O povoado se reúne "
                + "à sombra do que sobrou para decidir duas coisas: o destino de Júlio e quem, daqui por diante, guarda a água. Seja pelo que "
                + "você fez ou pelo que deixou de fazer, todos os olhos se voltam para você.")
                .abreCapitulo().comAutosave();
        c10a
                .comInterludio("O levante custou caro. Baltazar, Seu Raimundo e mais vinte e nove moradores morreram entre o cerco, o "
                + "incêndio e a praça. Foram enterrados às pressas, ao amanhecer, porque o calor não espera. Milena pediu "
                + "que os dois milicianos mortos também fossem enterrados com nome: \"Alguém, em algum lugar, está "
                + "esperando por eles.\"")
                .comInterludio("Bom-Jardim tinha quatrocentas e doze pessoas quando a milícia chegou. Agora são trezentas e oitenta e "
                + "uma.")
                .comInterludioSe(Condicao.decisao("CENA_9", 1, "ter aceitado o acordo"),
                        "O levante não bastou. Pelo acordo que você aceitou, a milícia ficou com o Olho e com metade das "
                        + "camionetes. Vasques ainda manda na água, mas agora sabe que Bom-Jardim se levanta.")
                .comInterludioSe(Condicao.decisao("CENA_9", 0, "ter exposto o Coronel"),
                        "A milícia foi expulsa. Vasques fugiu pelo Serrote na última camionete, com o para-brisa estourado. "
                        + "Bom-Jardim ficou com três caminhões, onze fuzis e uma liberdade que ninguém sabe ainda como guardar.")
                .comInterludioSe(Condicao.decisao("CENA_9", 2, "ter deixado Milena decidir"),
                        "A milícia foi expulsa. Vasques fugiu pelo Serrote na última camionete, com o para-brisa estourado. "
                        + "Bom-Jardim ficou com três caminhões, onze fuzis e uma liberdade que ninguém sabe ainda como guardar.")
                .comInterludioSe(Condicao.decisao("CENA_9", 3, "ter mostrado a ficha"),
                        "A milícia foi expulsa. Vasques fugiu pelo Serrote na última camionete, com o para-brisa estourado. "
                        + "Bom-Jardim ficou com três caminhões, onze fuzis e uma liberdade que ninguém sabe ainda como guardar.")
                .comInterludioSe(Condicao.decisao("CENA_9", 0, "ter exposto o Coronel"),
                        "Depois que você expôs o Coronel, Dantas pegou uma camionete e foi para o Boqueirão buscar a mulher e os "
                + "filhos. Outros cinco homens foram com ele. Ninguém sabe se chegaram.")
                .comInterludioSe(Condicao.decisao("CENA_9", 3, "ter mostrado a ficha do Boqueirão"),
                        "Depois da ficha, Dantas pegou uma camionete e foi para o Boqueirão buscar a mulher e os filhos. Outros cinco "
                + "homens foram com ele. Ninguém sabe se chegaram.")
                .comInterludioSe(Condicao.decisao("CENA_9", 1, "ter entregado o Olho"),
                        "O Coronel levou o Olho, como combinado. Dantas foi com ele, de cabeça baixa. Na saída, deixou cair no chão um "
                + "papel dobrado: o nome da mulher e dos filhos dele, e a palavra \"desculpa\".");
        falas(c10a,
                f(NPC_MILENA, "Antes de falar de castigo, vamos falar das cotas. Quem vai guardar a água daqui para frente?"),
                f(NPC_SANDRO, "(com o martelo de Baltazar nas mãos) O Seu Júlio está preso na Casa das Cotas. O seu Baltazar disse que era para esperar a assembleia."),
                f(NPC_JULIO, "(amarrado, a voz baixa) Só peço que contem direito, desta vez."),
                f(NPC_MILENA, "O Seu Raimundo cobrou a vida inteira por favores que deviam ser de graça. No fim, deu a única coisa que não tinha preço. "
                        + "Que a gente não precise de outro morto para aprender."),
                f(NPC_SANDRO, "Dona Milena, e se a gente não precisasse de guarda? E se a água fosse de todo mundo e todo mundo visse?"),
                f(NPC_MILENA, "Menino, você acabou de dizer a coisa mais sensata dessa assembleia."),
                f(NPC_MILENA, "{nome}, ninguém aqui viu o Coronel de perto como você. O povoado vai ouvir o que você disser."));
        c10a.adicionarEscolha(new Escolha("Júlio responde diante do povoado e trabalha na reconstrução; toda cota passa a ser registrada "
                + "em praça pública.", "CENA_10B", null,
                c(0, 0, -5, 3).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_SANDRO, 1).desbloqueia(CONQ_PORTA_ABERTA))
                .comReacao(f(NPC_MILENA, "Água de porta aberta.")));
        c10a.adicionarEscolha(new Escolha("Exilar Júlio no sertão, sem água.", "CENA_10B", null,
                c(0, 0, 0, 0))
                .comNarracao("Ninguém diz uma palavra. O lugar de Baltazar, na primeira fila da assembleia, está vazio."));
        c10a.adicionarEscolha(new Escolha("Poupar Júlio em troca de tudo o que ele sabe sobre o Coronel.", "CENA_10B",
                Condicao.item(rotas.getItem(ITEM_FICHA_BOQUEIRAO)),
                c(0, 0, 0, 1).comAfinidade(NPC_JULIO, 2))
                .comReacao(f(NPC_JULIO, "Tudo. Até os números.")));
        c10a.adicionarEscolha(new Escolha("Propor-se como o novo Guardião da Cota: a água passaria pela sua mão.", "CENA_10B",
                Condicao.atributoMinimo(TipoAtributo.LIDERANCA, 3),
                c(10, 10, 0, -4).comAfinidade(NPC_MILENA, -3).desbloqueia(CONQ_NOVA_COROA))
                .comReacao(f(NPC_MILENA, "(devagar) Foi assim que o Boqueirão começou.")));

        Cena c10b = cena("CENA_10B", 10, "O Julgamento de 2071",
                "Uma semana depois, Caetano termina de gravar a última letra na pedra que fica ao lado do tronco do "
                + "Umbuzeiro. São trinta e um nomes. Os primeiros são de Dona Josefa, de Damião, de Seu Raimundo e de "
                + "Baltazar. Em cima da pedra, alguém deixou o martelo de Baltazar, com o cabo trocado pela quinta vez.");
        falas(c10b,
                f(NPC_SANDRO, "(lendo devagar, com o crachá da mãe no pescoço) Josefa Alves. Damião Ferreira. Raimundo Sousa. Baltazar "
                        + "Rocha."),
                f(NPC_SANDRO, "Ele nunca me disse o sobrenome dele. Eu tive que perguntar para a Dona Milena."),
                f(NPC_MILENA, "Trinta anos consertando tudo em Bom-Jardim, e ninguém nunca consertou nada para ele."),
                f(NPC_SANDRO, "(passa o dedo nas letras) A Dona Josefa ia gostar daqui. Dá para ver o morro e o Umbuzeiro."));
        c10b.adicionarEscolha(new Escolha("Falar do que Baltazar ensinou: que ninguém segura a cerca sozinho.", "CENA_10", null,
                c(0, 0, 0, 2).comAfinidade(NPC_SANDRO, 1).comAfinidade(NPC_MILENA, 1))
                .comReacao(f(NPC_MILENA, "Juntos. Era a palavra dele."))
                .comNarracao("Quando você termina, ninguém aplaude. As pessoas se aproximam da pedra, uma por uma, e encostam a mão no "
                        + "nome de alguém."));
        c10b.adicionarEscolha(new Escolha("Pôr a mão na pedra e ficar em silêncio.", "CENA_10", null,
                c(0, 0, 0, 0))
                .comNarracao("O silêncio dura muito tempo. Ninguém reclama. Sandro põe a mão ao lado da sua."));
        c10b.adicionarEscolha(new Escolha("Dizer que já fez o bastante e que vai embora assim que puder.", "CENA_10", null,
                c(0, 0, 0, -2).comAfinidade(NPC_SANDRO, -1))
                .comReacao(f(NPC_SANDRO, "(aperta o martelo contra o peito) Ele não ia embora.")));

        Cena c10 = cena("CENA_10", 10, "O Julgamento de 2071",
                "À noite, sob o tronco queimado, você abre o caderno de cotas que Júlio deixou para trás. Cada linha é uma vida contada em "
                + "litros. Você pensa no Boqueirão, em Milena, em Sandro, e na única conta que importa e que nenhum caderno registra: "
                + "o que você fez com o que lhe foi dado. Amanhece. A poeira baixou. O destino do sertão está selado.");
        c10.adicionarEscolha(new Escolha("Ver meu destino", Jogo.ID_VERIFICAR_FINAL, null, c(0, 0, 0, 0)));
        c10a.adicionarEscolha(new Escolha("Convidar os povoados do rádio para uma liga de águas, com contas abertas entre todos.", "CENA_10B",
                Condicao.item(rotas.getItem(ITEM_RADIO)),
                c(0, 0, 0, 4).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_BALTAZAR, 1)
                        .concluiMissao(MISSAO_VIZINHOS).desbloqueia(CONQ_LIGA))
                .comReacao(f(NPC_MILENA, "(olha o rádio) Um povoado que ouve os outros não morre sozinho."),
                        f(NPC_SANDRO, "O seu Baltazar ia dizer só uma palavra: juntos.")));
        // Opção 5 (Jogo.ESCOLHA_TRIBUNAL): leva ao final da justiça
        c10a.adicionarEscolha(new Escolha("Propor um julgamento de verdade: Júlio, os milicianos presos e o próprio Coronel diante de um tribunal, "
                + "com o caderno da Estação 7 como prova.", "CENA_10B",
                Condicao.item(rotas.getItem(ITEM_CADERNO)).e(Condicao.naoDecidiu("CENA_9", 1, "não ter entregado o Olho ao Coronel")),
                c(0, 0, 0, 5).comAfinidade(NPC_MILENA, 1).comAfinidade(NPC_SANDRO, 1).desbloqueia(CONQ_JUSTICA))
                .comReacao(f(NPC_MILENA, "Julgamento? Com juiz, defesa e tudo? Para o Coronel?"),
                        f(NPC_SANDRO, "(com o crachá da mãe na mão) Eu quero que ele escute o nome dela. Na frente de todo mundo."),
                        f(NPC_MILENA, "(depois de um longo silêncio) Se a gente fizer com ele o que ele fez com Caetano, vira ele. Vamos fazer "
                                + "do jeito certo."))
                .comNarracao("O conselho aprova por maioria. Pelo rádio, a Liga das Águas promete ajudar a encontrar o Coronel. "
                        + "Dantas, que voltou do Boqueirão com a família, se oferece para guiar a busca, e para responder pelo que "
                        + "ele mesmo fez."));
    }

    // ------------------------------------------------------------------ perguntas, conclusões e lembranças

    /**
     * Pergunta principal de cada cena (repetida antes das opções), conclusão de cada capítulo (narrada depois da
     * última escolha, ligando ao próximo) e uma lembrança de personagem por capítulo, liberada pela afinidade.
     */
    private void perguntasConclusoesELembrancas() {
        rotas.getCena("CENA_1A").comPergunta(f(NPC_MILENA, "E então? Vai me dizer o seu nome, ou eu vou ter que adivinhar?"));
        rotas.getCena("CENA_1B").comPergunta(f(NPC_MILENA, "Um dia inteiro, {nome}. Você já viu o bastante. E então: vai nos ajudar?"));
        rotas.getCena("CENA_1B")
                .comConclusao("Naquela noite, você dorme na casa azul com a foto da feira em cima da mesa. Pela janela, a luz vermelha "
                        + "do drone passa baixa sobre os telhados, devagar, como quem conta.")
                .comConclusaoSe(Condicao.decisao("CENA_1B", 0, "ter aceitado ajudar"),
                        "Milena demora a ir embora do banco de pedra. Quando vai, leva no rosto uma coisa que ninguém em "
                        + "Bom-Jardim via fazia tempo: alívio.")
                .comConclusaoSe(Condicao.decisao("CENA_1B", 1, "ter recusado"),
                        "Milena apaga a lamparina sem dizer nada. Você fica acordado muito tempo, olhando a foto da praça cheia, "
                        + "pensando se fez a coisa certa.")
                .comConclusao("Na manhã seguinte, a bomba do Poço da Santa para de vez. E a sede chega antes do sol.");
        rotas.getCena("CENA_1").comPergunta(f(NPC_MILENA, "Então, {nome}? Você vai nos ajudar?"));
        rotas.getCena("CENA_2A").comPergunta(f(NPC_MILENA, "Esse é o povo com quem você vai trabalhar. Tem alguma coisa para dizer a eles?"));
        rotas.getCena("CENA_2").comPergunta(f(NPC_MILENA, "A fila não espera, {nome}. O que a gente faz com essa bomba?"));
        rotas.getCena("CENA_3A").comPergunta(f(NPC_SANDRO, "Minha língua está colando... Você vai dividir comigo ou não?"));
        rotas.getCena("CENA_3").comPergunta(f(NPC_SANDRO, "A gente conta para a Dona Milena? Ou segue o rastro até a estação?"));
        rotas.getCena("CENA_3B").comPergunta(f(NPC_MILENA, "Quem é a favor levanta a mão. E você, {nome}: aceita o lugar no conselho?"));
        rotas.getCena("CENA_4A").comPergunta(f(NPC_VASQUES, "E você, do Boqueirão? Vai ficar só me olhando?"));
        rotas.getCena("CENA_4").comPergunta(f(NPC_VASQUES, "Até o pôr do sol. Qual é o voto do conselheiro?"));
        rotas.getCena("CENA_5P").comPergunta(f(NPC_MILENA, "{nome}! O povo está com o Caetano. Você vem?"));
        rotas.getCena("CENA_5A").comPergunta(f(NPC_MILENA, "Alguém daqui abriu aquela porta. Como a gente descobre quem foi sem virar uma briga de vizinhos?"));
        rotas.getCena("CENA_5").comPergunta(f(NPC_SANDRO, "E agora? A gente vai atrás dessas pegadas ou não?"));
        rotas.getCena("CENA_6A").comPergunta(f(NPC_JULIO, "Posso contar com você, {nome}?"));
        rotas.getCena("CENA_6").comPergunta(f(NPC_JULIO, "Uma palavra sua. Sim ou não?"));
        rotas.getCena("CENA_6B").comPergunta(f(NPC_MILENA, "O povo está esperando, {nome}. O que você vai dizer a eles?"));
        rotas.getCena("CENA_7A").comPergunta(f(NPC_MILENA, "Eles vêm amanhã ou depois. Onde você vai estar quando chegarem?"));
        rotas.getCena("CENA_7").comPergunta(f(NPC_BALTAZAR, "{nome}! Onde você vai lutar?"));
        rotas.getCena("CENA_7B").comPergunta(f(NPC_BALTAZAR, "O portão está cedendo! Damião caiu! O que você vai fazer?"));
        rotas.getCena("CENA_8A").comPergunta(f(NPC_SANDRO, "Você acredita em mim? O que a gente faz agora?"));
        rotas.getCena("CENA_8").comPergunta(f(NPC_MILENA, "{nome}... o que você vai fazer com ele?"));
        rotas.getCena("CENA_9A").comPergunta(f(NPC_MILENA, "O fogo está chegando na raiz! Como a gente salva o Umbuzeiro?"));
        rotas.getCena("CENA_9").comPergunta(f(NPC_VASQUES, "Última oferta. Qual é a resposta de Bom-Jardim?"));
        rotas.getCena("CENA_9B").comPergunta(f(NPC_BALTAZAR, "{nome}! É agora! Você vem?"));
        rotas.getCena("CENA_10A").comPergunta(f(NPC_MILENA, "O povoado vai ouvir você. O que a gente faz com o Júlio, com os presos e com o que sobrou desta história?"));
        rotas.getCena("CENA_10B").comPergunta(f(NPC_MILENA, "Ele confiava em você, {nome}. O povo está esperando. O que você vai dizer?"));
        rotas.getCena("CENA_10").comPergunta(f(NPC_MILENA, "Bom-Jardim vai seguir em frente. E você?"));
        rotas.getCena("CENA_1")
                .comConclusao("Naquela noite, você dorme pela primeira vez na casa azul da professora Lúcia. Pela janela, a luz "
                        + "vermelha do drone passa baixa sobre os telhados, duas vezes, devagar, como quem conta.")
                .comConclusaoSe(Condicao.decisao("CENA_1", 0, "ter aceitado ajudar"),
                        "Milena aperta a sua mão antes de ir embora. \"Amanhã cedo, na bomba.\" É a primeira vez em semanas que "
                        + "alguém em Bom-Jardim dorme com um pouco de esperança.")
                .comConclusaoSe(Condicao.decisao("CENA_1", 1, "ter recusado"),
                        "Milena não insiste. Você fica acordado muito tempo, ouvindo o vento bater na porta numerada, pensando se "
                        + "fez a coisa certa.")
                .comConclusao("Na manhã seguinte, a bomba do Poço da Santa para de vez. E a sede chega antes do sol.");
        rotas.getCena("CENA_2")
                .comConclusao("O dia termina com o sol baixo e a fila da cisterna andando outra vez.")
                .comConclusaoSe(Condicao.decisao("CENA_2", 0, "ter consertado para todos"),
                        "A bomba volta a bombear, remendada, e a praça inteira bebe. Ninguém agradece em voz alta, mas à noite "
                        + "aparece um prato de cuscuz na sua porta.")
                .comConclusaoSe(Condicao.decisao("CENA_2", 1, "ter subornado o guarda"),
                        "Você bebe a sua garrafa do porão escondido, enquanto a fila espera a bomba. Ninguém viu. Você viu.")
                .comConclusaoSe(Condicao.decisao("CENA_2", 2, "ter consertado sozinho"),
                        "Você conserta o eixo sozinho, com as mãos sangrando. Baltazar observa de longe e, no fim, acena uma vez "
                        + "com a cabeça.")
                .comConclusaoSe(Condicao.decisao("CENA_2", 3, "ter convocado a assembleia"),
                        "Os turnos funcionam. Pela primeira vez em semanas, ninguém briga na fila.")
                .comConclusao("Mas Baltazar sabe que aquilo é remendo. Sem uma junta de vedação nova, a bomba não passa do mês. A única "
                        + "peça que serve está na Estação de Bombeamento 7, a dois dias de caminhada pela caatinga.");
        rotas.getCena("CENA_3B")
                .comConclusao("A assembleia termina tarde. As lamparinas se apagam uma por uma, e você volta para casa com o caderno da "
                        + "Estação 7 debaixo do braço.")
                .comConclusaoSe(Condicao.decisao("CENA_3B", 0, "ter aceitado com promessa"),
                        "Agora há uma promessa sua anunciada em praça pública. Bom-Jardim vai cobrar.")
                .comConclusaoSe(Condicao.decisao("CENA_3B", 2, "ter recusado o conselho"),
                        "A cadeira no conselho fica vazia, esperando. Milena não tira ninguém de lá.")
                .comConclusao("Três dias depois, uma coluna de poeira sobe no horizonte. Não é vento. São camionetes.");
        rotas.getCena("CENA_4")
                .comConclusao("O sol se põe. As camionetes vão embora devagar, como quem sabe que vai voltar.")
                .comConclusaoSe(Condicao.decisao("CENA_4", 0, "ter entregado a cota"),
                        "Os barris do Olho saem de Bom-Jardim na carroceria. Milena conta cada um, em voz alta, até o último. "
                        + "Depois fica em silêncio por muito tempo.")
                .comConclusaoSe(Condicao.decisao("CENA_4", 1, "ter negociado"),
                        "O Coronel aceita o prazo, sorrindo. Ninguém acredita que ele vá cumprir.")
                .comConclusaoSe(Condicao.decisao("CENA_4", 2, "ter recusado"),
                        "O Coronel não discute. Só olha para a cisterna, e depois para você, e guarda as duas coisas na memória.")
                .comConclusaoSe(Condicao.decisao("CENA_4", 3, "ter proposto o voto"),
                        "O povoado votou, e o Coronel ouviu. Ele foi embora sem uma palavra, e isso assusta mais do que qualquer "
                        + "ameaça.")
                .comConclusao("Na madrugada seguinte, o Celeiro Velho amanhece com dezoito sacos a menos. E a porta está inteira.");
        rotas.getCena("CENA_5")
                .comConclusao("As pegadas no barro começam a secar. Logo vão sumir, mas o que elas mostraram, não.")
                .comConclusaoSe(Condicao.decisao("CENA_5", 0, "ter investigado"),
                        "Você guarda o molde da sandália de costura dupla. Já sabe de quem é. Só não sabe ainda o que fazer com "
                        + "isso.")
                .comConclusaoSe(Condicao.decisao("CENA_5", 1, "ter deixado para lá"),
                        "Você deixa para lá. À noite, outro saco some.")
                .comConclusaoSe(Condicao.decisao("CENA_5", 2, "ter ouvido o rádio"),
                        "A frase do rádio não sai da sua cabeça: a chave do celeiro está com eles desde a lua passada.")
                .comConclusao("Dois dias depois, Júlio convida você para um café na Casa das Cotas. Ele nunca convidou ninguém.");
        rotas.getCena("CENA_6B")
                .comConclusao("A assembleia termina com as lamparinas apagando e o medo bem acordado.")
                .comConclusaoSe(Condicao.decisao("CENA_6B", 0, "ter mostrado o caderno"),
                        "Pela primeira vez, Bom-Jardim sabe o nome de quem matou a cidade. Raiva e coragem às vezes são a mesma "
                        + "coisa.")
                .comConclusaoSe(Condicao.decisao("CENA_6B", 2, "ter deixado cada um decidir"),
                        "De madrugada, você ouve carroças saindo para as ruínas do centro. Baltazar fica sozinho na oficina, "
                        + "afiando o machado.")
                .comConclusao("Na semana seguinte, o drone volta toda noite, cada vez mais baixo. O Coronel está contando de novo, e "
                        + "desta vez vai cobrar à força.");
        rotas.getCena("CENA_7B")
                .comConclusao("Ao cair da tarde, a camionete atolada é abandonada e a milícia recua. Bom-Jardim resistiu ao primeiro "
                        + "ataque.")
                .comConclusao("Mas ninguém comemora. No portão leste, Damião está deitado sob um lençol, e o filho dele de dois anos "
                        + "pergunta por que o pai está dormindo no chão.")
                .comConclusaoSe(Condicao.decisao("CENA_7B", 0, "ter tentado salvar Damião"),
                        "A sua camisa ainda tem o sangue dele. Você não troca de roupa naquela noite.")
                .comConclusaoSe(Condicao.decisao("CENA_7B", 2, "ter recuado"),
                        "Quando você sai da cisterna, as pessoas desviam o olhar. Ninguém diz nada. Não precisa.")
                .comConclusao("Naquela noite, Sandro vê uma luz piscando no portão leste. Duas piscadas curtas e uma comprida.");
        rotas.getCena("CENA_8")
                .comConclusao("A noite termina com o portão leste trancado e um nome que ninguém queria ouvir: Júlio.")
                .comConclusaoSe(Condicao.decisao("CENA_8", 0, "ter exposto Júlio"),
                        "Júlio é preso na Casa das Cotas, entre os próprios cadernos. Milena tranca a porta e chora do lado de "
                        + "fora, sem ninguém ver.")
                .comConclusaoSe(Condicao.decisao("CENA_8", 1, "ter entregado Júlio a Baltazar"),
                        "Baltazar amarra as mãos de Júlio sem apertar demais. \"Sem sangue\", ele repete, como uma reza.")
                .comConclusaoSe(Condicao.decisao("CENA_8", 2, "ter seguido Júlio"),
                        "Você atravessa a escuridão atrás de Júlio, com barris cheios e a consciência vazia.")
                .comConclusaoSe(Condicao.decisao("CENA_8", 3, "ter lembrado os mortos"),
                        "Júlio chora pela primeira vez em oito anos e entrega a chave do celeiro sem que ninguém peça.")
                .comConclusao("Antes do amanhecer, alguém joga fogo no capim seco perto do Umbuzeiro. A milícia voltou, e desta vez "
                        + "quer a árvore.");
        rotas.getCena("CENA_9B")
                .comConclusao("Quando a fumaça baixa, a praça de Bom-Jardim está cheia de gente sentada no chão, sem forças para "
                        + "levantar. Baltazar está morto. Muitos outros também.")
                .comConclusaoSe(Condicao.decisao("CENA_9", 1, "ter aceitado o acordo"),
                        "O levante não bastou. As camionetes saem levando o Olho, e o Coronel acena da janela como quem se "
                        + "despede de amigos. Mas Bom-Jardim se levantou uma vez, e todos sabem que pode se levantar de novo.")
                .comConclusaoSe(Condicao.naoDecidiu("CENA_9", 1, "não ter aceitado o acordo"),
                        "A milícia foi expulsa. Na estrada do Serrote, a última camionete some na poeira, levando um Coronel "
                        + "ferido e humilhado. Algumas pessoas choram. Outras riem. Muitas fazem as duas coisas ao mesmo tempo.")
                .comConclusao("Amanhã, Bom-Jardim vai ter que decidir o que fazer com os vivos: com Júlio, com os milicianos presos e "
                        + "consigo mesma.");
        rotas.getCena("CENA_1").comLembranca(new Lembranca("milena_posto", rotas.getNpc(NPC_MILENA), 1, "Milena: o posto de saúde",
                "Milena, você fala do posto de saúde como quem conheceu de perto. Trabalhou lá?",
                List.of(f(NPC_MILENA, "Minha mãe era enfermeira do posto de saúde do centro. Quando a água parou de chegar, ela continuou indo "
                        + "trabalhar a pé. Onze quilômetros, todo dia."),
                        f(NPC_MILENA, "Eu ia junto. Aprendi a fazer curativo antes de aprender a fazer conta."),
                        f(NPC_MILENA, "Em 2063 veio uma febre que o posto não tinha remédio para tratar. Ela cuidou de todo mundo, e depois "
                        + "pegou também."),
                        f(NPC_MILENA, "Meu pai durou mais um ano. Dizem que foi do coração. Eu acho que foi de saudade."),
                        f(NPC_MILENA, "(passa a mão nos fios grisalhos) Estes aqui apareceram naquele ano. Eu tinha vinte e sete."))));
        rotas.getCena("CENA_2").comLembranca(new Lembranca("julio_prefeitura", rotas.getNpc(NPC_JULIO), 1, "Júlio: as contas da prefeitura",
                "Júlio, você conta tudo com tanto cuidado... Sempre trabalhou com números?",
                List.of(f(NPC_JULIO, "Eu era contador da prefeitura de Bom-Jardim. Fui eu que fiz a lista de quem ia embora, para mandar à "
                        + "capital."),
                        f(NPC_JULIO, "Nove mil, depois seis, depois três. Cada número era uma família, e eu escrevia todos com a mesma letra."),
                        f(NPC_JULIO, "Quando acabou o dinheiro da prefeitura, fui para o Boqueirão com a minha filha. Lá, quem fazia as contas "
                        + "era o Coronel."),
                        f(NPC_JULIO, "Aprendi uma coisa: quem faz a conta nunca entra na conta. Foi assim que eu sobrevivi. (baixa os olhos) "
                        + "Até agora."))));
        rotas.getCena("CENA_3A").comLembranca(new Lembranca("baltazar_iracema", rotas.getNpc(NPC_BALTAZAR), 1, "Baltazar: Iracema",
                "Baltazar, quem te ensinou a consertar tudo desse jeito?",
                List.of(f(NPC_BALTAZAR, "(sem tirar os olhos da trilha) Eu era mecânico do serviço de água da cidade. Vinte anos cuidando de "
                        + "bomba, cano e registro."),
                        f(NPC_BALTAZAR, "Quando cortaram a adutora, me mandaram embora com uma carta de agradecimento."),
                        f(NPC_BALTAZAR, "Minha mulher, Iracema, falava pelos dois. Eu nunca precisei falar muito."),
                        f(NPC_BALTAZAR, "Ela morreu em 2055. Desde então eu falo o necessário. Conserto as coisas daqui porque ela gostava deste "
                        + "lugar."))));
        rotas.getCena("CENA_4A").comLembranca(new Lembranca("sandro_pai", rotas.getNpc(NPC_SANDRO), 2, "Sandro: o pai em São Paulo",
                "Sandro, você nunca fala do seu pai. Cadê ele?",
                List.of(f(NPC_SANDRO, "(enquanto a poeira se aproxima) Meu pai foi trabalhar em São Paulo quando eu tinha oito anos. Disse que "
                        + "ia mandar dinheiro e buscar a gente."),
                        f(NPC_SANDRO, "Ele mandou duas cartas pelo caminhão da feira. Depois o caminhão parou de vir."),
                        f(NPC_SANDRO, "Minha mãe foi trabalhar na Estação 7 e também não voltou. Agora sou eu e a minha avó Celina."),
                        f(NPC_SANDRO, "Eu guardo as duas cartas debaixo do colchão. Já li tanto que sei de cor."))));
        rotas.getCena("CENA_5").comLembranca(new Lembranca("raimundo_expedito", rotas.getNpc(NPC_RAIMUNDO), 1, "Seu Raimundo: o irmão Expedito",
                "Seu Raimundo, por que o senhor cobra por tudo? Nem sempre foi assim, foi?",
                List.of(f(NPC_RAIMUNDO, "Fui guarda do mercado municipal por vinte e dois anos. Conhecia cada feirante pelo nome."),
                        f(NPC_RAIMUNDO, "Meu irmão, Expedito, tinha uma banca de queijo. Quando a cidade esvaziou, ele não quis sair. Disse que "
                        + "alguém tinha que ficar."),
                        f(NPC_RAIMUNDO, "Ficou esperando a água voltar pelo cano. Morreu esperando, no apartamento, em 2053. Só achei ele três "
                        + "dias depois."),
                        f(NPC_RAIMUNDO, "Foi aí que eu comecei a cobrar por tudo. Achei que, se tudo tivesse preço, nada mais ia me pegar "
                        + "desprevenido. (ri sem graça) Não funcionou."))));
        rotas.getCena("CENA_6A").comLembranca(new Lembranca("dantas_vaqueiro", rotas.getNpc(NPC_DANTAS), 1, "Sargento Dantas: o vaqueiro",
                "Sargento, o senhor não parece gostar do que faz. O que o senhor era antes do Coronel?",
                List.of(f(NPC_DANTAS, "Eu era vaqueiro antes disso tudo. Tinha gado no Serrote, quando ainda tinha pasto."),
                        f(NPC_DANTAS, "O Coronel apareceu com água e com fuzil. Ofereceu trabalho e uma casa no Boqueirão para a minha família."),
                        f(NPC_DANTAS, "Na primeira cobrança, achei que estava protegendo a minha gente. Na terceira, entendi que a minha gente "
                        + "era refém."),
                        f(NPC_DANTAS, "Minha filha mais velha, Rita, tem a idade do Sandro. Ela acha que eu sou soldado. (olha para as mãos) "
                        + "Ainda posso ser. Só não sei de que lado."))));
        rotas.getCena("CENA_7A").comLembranca(new Lembranca("milena_mochila", rotas.getNpc(NPC_MILENA), 5, "Milena: a noite em que quase foi embora",
                "Ouvi dizer que você quase foi embora de Bom-Jardim, Milena. É verdade?",
                List.of(f(NPC_MILENA, "Em 2066 eu arrumei uma mochila. Tinha carona num caminhão para o litoral e um emprego num hospital."),
                        f(NPC_MILENA, "Na noite de sair, a cisterna rachou. Fiquei até de manhã tapando a rachadura com cimento."),
                        f(NPC_MILENA, "Quando o caminhão buzinou, eu estava com a mão suja de cimento e trinta pessoas na fila. Não fui."),
                        f(NPC_MILENA, "Às vezes penso na mulher que eu seria. Acho que estaria viva e tranquila. E sozinha."),
                        f(NPC_MILENA, "(olha para a cerca) Não me arrependo. Mas estou com medo, {nome}. Não conte a ninguém."))));
        rotas.getCena("CENA_8A").comLembranca(new Lembranca("sandro_cracha", rotas.getNpc(NPC_SANDRO), 3, "Sandro: o crachá da mãe",
                "Sandro, você nunca larga esse crachá. Como ela era, a sua mãe?",
                List.of(f(NPC_SANDRO, "(tira o crachá do bolso) Minha mãe saía de casa às cinco da manhã. Voltava com cheiro de graxa e me "
                        + "trazia um parafuso de presente."),
                        f(NPC_SANDRO, "Eu tenho uma lata cheia de parafusos. Cento e doze. Contei ontem."),
                        f(NPC_SANDRO, "Ela me ensinou que, quando a pressão do cano cai, é porque alguém abriu outra torneira em algum lugar."),
                        f(NPC_SANDRO, "Então foi isso, né? O Coronel abriu outra torneira. (guarda o crachá) Quando eu crescer, vou fechar a "
                        + "torneira dele."))));
        rotas.getCena("CENA_9A").comLembranca(new Lembranca("baltazar_juntos", rotas.getNpc(NPC_BALTAZAR), 4, "Baltazar: juntos",
                "Baltazar, amanhã pode ser o último dia de muita gente. Tem alguma coisa que você queira dizer?",
                List.of(f(NPC_BALTAZAR, "(na véspera, sentado na oficina, afiando o machado) Iracema dizia uma palavra só, quando eu queria "
                        + "resolver tudo sozinho: juntos."),
                        f(NPC_BALTAZAR, "Demorei trinta anos para aprender."),
                        f(NPC_BALTAZAR, "Se eu não estiver aqui amanhã, o martelo fica com o Sandro. O cabo está gasto. Ele que troque."),
                        f(NPC_BALTAZAR, "(olha para você) Você chegou de longe e ficou. Isso é raro. Cuide deles."))));
        rotas.getCena("CENA_10A").comLembranca(new Lembranca("julio_lia", rotas.getNpc(NPC_JULIO), 3, "Júlio: Lia",
                "Júlio, eu vi a linha da Lia no caderno, sem risco. Me conta dela?",
                List.of(f(NPC_JULIO, "(na Casa das Cotas, sem tirar os olhos do caderno) Lia tinha seis anos. Gostava de contar as estrelas e "
                        + "errava de propósito, para começar de novo."),
                        f(NPC_JULIO, "Ela adoeceu no primeiro verão daqui. Faltou água limpa. Faltou remédio. Faltou tudo."),
                        f(NPC_JULIO, "Eu achei que, se a conta fechasse, ninguém mais ia perder uma filha. Fiz a conta do Coronel e esqueci "
                        + "que cada número era uma pessoa."),
                        f(NPC_JULIO, "Não peço perdão. Peço só que a linha dela no caderno continue sem risco."))));
    }

    // ------------------------------------------------------------------ falas do protagonista

    /** O que o protagonista diz em cada escolha: aparece como fala dele, antes da reação dos outros personagens. */
    private void falasDoJogador() {
        rotas.getCena("CENA_1A").getEscolhas().get(0).comFalaDoJogador("Meu nome é {nome}. Obrigado... de verdade. Vou beber devagar.");
        rotas.getCena("CENA_1A").getEscolhas().get(1).comFalaDoJogador("(você arranca a caneca das mãos dela) Me dá isso aqui.");
        rotas.getCena("CENA_1A").getEscolhas().get(2).comFalaDoJogador("Antes de qualquer coisa: onde eu estou? Quem manda aqui?");
        rotas.getCena("CENA_1").getEscolhas().get(0).comFalaDoJogador("Claro que eu ajudo. Vocês me tiraram da morte. Preciso retribuir.");
        rotas.getCena("CENA_1").getEscolhas().get(1).comFalaDoJogador("Não. Eu não quero me envolver em nada disso.");
        rotas.getCena("CENA_1").getEscolhas().get(2).comFalaDoJogador("Me dá um dia, Milena. Quero entender este lugar antes de prometer qualquer coisa.");
        rotas.getCena("CENA_1B").getEscolhas().get(0).comFalaDoJogador("Eu vi a fila, vi as portas, ouvi a bomba. Não dá para virar as costas. Conte comigo.");
        rotas.getCena("CENA_1B").getEscolhas().get(1).comFalaDoJogador("Desculpa, Milena. É problema demais para quem acabou de chegar.");
        rotas.getCena("CENA_2A").getEscolhas().get(0).comFalaDoJogador("Como você sabia que eu vim do Boqueirão?");
        rotas.getCena("CENA_2A").getEscolhas().get(1).comFalaDoJogador("(você deixa passar e aperta a mão dele) Prazer, Júlio.");
        rotas.getCena("CENA_2A").getEscolhas().get(2).comFalaDoJogador("Baltazar, esse martelo tem história, não tem?");
        rotas.getCena("CENA_2").getEscolhas().get(0).comFalaDoJogador("Vamos consertar essa bomba para todo mundo. Agora.");
        rotas.getCena("CENA_2").getEscolhas().get(1).comFalaDoJogador("(baixinho, para Seu Raimundo) Quanto custa uma garrafa só para mim?");
        rotas.getCena("CENA_2").getEscolhas().get(2).comFalaDoJogador("Deixa comigo. Eu conheço esse tipo de eixo.");
        rotas.getCena("CENA_2").getEscolhas().get(3).comFalaDoJogador("Escutem todos! Vamos dividir os turnos: uns no conserto, outros organizando a fila.");
        rotas.getCena("CENA_3A").getEscolhas().get(0).comFalaDoJogador("Toma, Sandro. Bebe um pouco da minha.");
        rotas.getCena("CENA_3A").getEscolhas().get(1).comFalaDoJogador("Cada um cuida da sua água, Sandro. Guarda a tua.");
        rotas.getCena("CENA_3A").getEscolhas().get(2).comFalaDoJogador("Mastiga esta folha de umbu, Sandro. Ela engana a sede.");
        rotas.getCena("CENA_3").getEscolhas().get(0).comFalaDoJogador("Vamos voltar e contar tudo para a Dona Milena.");
        rotas.getCena("CENA_3").getEscolhas().get(1).comFalaDoJogador("A gente não viu nada, entendeu? Não é problema nosso.");
        rotas.getCena("CENA_3").getEscolhas().get(2).comFalaDoJogador("A trilha vai até a estação. Vamos ver o que tem lá.");
        rotas.getCena("CENA_3B").getEscolhas().get(0).comFalaDoJogador("Eu aceito. E prometo: toda decisão sobre a água vai ser anunciada aqui, na praça, para todo mundo.");
        rotas.getCena("CENA_3B").getEscolhas().get(1).comFalaDoJogador("(você aceita em silêncio, com um aceno de cabeça)");
        rotas.getCena("CENA_3B").getEscolhas().get(2).comFalaDoJogador("Eu só conserto coisas, Milena. Decidam vocês.");
        rotas.getCena("CENA_4A").getEscolhas().get(0).comFalaDoJogador("O Boqueirão ainda lembra do que o senhor fez, Coronel.");
        rotas.getCena("CENA_4A").getEscolhas().get(1).comFalaDoJogador("(você baixa a cabeça e recua para trás de Milena)");
        rotas.getCena("CENA_4A").getEscolhas().get(2).comFalaDoJogador("(você fica em silêncio e guarda cada detalhe)");
        rotas.getCena("CENA_4").getEscolhas().get(0).comFalaDoJogador("Vamos entregar a cota. Sem briga, sem sangue.");
        rotas.getCena("CENA_4").getEscolhas().get(1).comFalaDoJogador("Coronel, a gente pode oferecer trabalho e um prazo em vez de água.");
        rotas.getCena("CENA_4").getEscolhas().get(2).comFalaDoJogador("Não. A água do Olho é do povoado.");
        rotas.getCena("CENA_4").getEscolhas().get(3).comFalaDoJogador("Que o povoado vote em voz alta. E o senhor ouça o resultado.");
        rotas.getCena("CENA_5A").getEscolhas().get(0).comFalaDoJogador("Vamos fazer um inventário aberto: cada cota e cada chave conferidas na frente de todos.");
        rotas.getCena("CENA_5A").getEscolhas().get(1).comFalaDoJogador("O Júlio tem razão. Só pode ter sido a milícia.");
        rotas.getCena("CENA_5A").getEscolhas().get(2).comFalaDoJogador("Seu Raimundo, abre o porão e mostra a chave antes de a gente acusar alguém.");
        rotas.getCena("CENA_5").getEscolhas().get(0).comFalaDoJogador("Essas pegadas vão a algum lugar. Vamos atrás delas.");
        rotas.getCena("CENA_5").getEscolhas().get(1).comFalaDoJogador("Deixa para lá, Sandro. Não tenho força para isso hoje.");
        rotas.getCena("CENA_5").getEscolhas().get(2).comFalaDoJogador("Espera. Vou ligar o rádio. Quem sabe eles comentam alguma coisa.");
        rotas.getCena("CENA_6A").getEscolhas().get(0).comFalaDoJogador("Eu ouço, Júlio. Mas não vou ao Serrote com você.");
        rotas.getCena("CENA_6A").getEscolhas().get(1).comFalaDoJogador("(você decide ir ao Serrote sozinho, sem avisar ninguém)");
        rotas.getCena("CENA_6A").getEscolhas().get(2).comFalaDoJogador("Me dá um dia. Quero falar com a Milena e com o Baltazar antes.");
        rotas.getCena("CENA_6").getEscolhas().get(0).comFalaDoJogador("Não, Júlio. Eu não vou vender ninguém.");
        rotas.getCena("CENA_6").getEscolhas().get(1).comFalaDoJogador("Está bem. Eu aceito. Que Deus me perdoe.");
        rotas.getCena("CENA_6").getEscolhas().get(2).comFalaDoJogador("(você finge que aceita) Pode contar comigo, Júlio.");
        rotas.getCena("CENA_6B").getEscolhas().get(0).comFalaDoJogador("Olhem este caderno. Foi o Coronel quem desligou a adutora em 2052. Foi ele quem matou a nossa cidade.");
        rotas.getCena("CENA_6B").getEscolhas().get(1).comFalaDoJogador("Lembrem da Dona Josefa. Do filho do Damião com febre. Do Caetano amarrado naquela árvore.");
        rotas.getCena("CENA_6B").getEscolhas().get(2).comFalaDoJogador("Cada família sabe o que é melhor para si. Quem quiser ir, vá. Quem quiser ficar, fique.");
        rotas.getCena("CENA_7A").getEscolhas().get(0).comFalaDoJogador("Baltazar, vamos reforçar essas cercas.");
        rotas.getCena("CENA_7A").getEscolhas().get(1).comFalaDoJogador("Milena, eu ajudo com a cisterna e com os mais velhos.");
        rotas.getCena("CENA_7A").getEscolhas().get(2).comFalaDoJogador("Vamos organizar os turnos de vigia. E as crianças precisam decorar a rota de fuga.");
        rotas.getCena("CENA_7").getEscolhas().get(0).comFalaDoJogador("Baltazar! Estou com você!");
        rotas.getCena("CENA_7").getEscolhas().get(1).comFalaDoJogador("Milena, eu fico com as crianças e a cisterna!");
        rotas.getCena("CENA_7").getEscolhas().get(2).comFalaDoJogador("(você corre para o fundo da cisterna e se esconde)");
        rotas.getCena("CENA_7").getEscolhas().get(3).comFalaDoJogador("Alguém me dá o rádio! Vou pedir ajuda!");
        rotas.getCena("CENA_7B").getEscolhas().get(0).comFalaDoJogador("Damião! Aguenta firme, eu estou indo!");
        rotas.getCena("CENA_7B").getEscolhas().get(1).comFalaDoJogador("Segura a linha, Baltazar! Ninguém sai daqui!");
        rotas.getCena("CENA_7B").getEscolhas().get(2).comFalaDoJogador("(você recua para a cisterna antes que a cerca caia)");
        rotas.getCena("CENA_8A").getEscolhas().get(0).comFalaDoJogador("Eu vou atrás dele. Sozinho.");
        rotas.getCena("CENA_8A").getEscolhas().get(1).comFalaDoJogador("Sandro, chama o Baltazar e a Dona Milena. Agora.");
        rotas.getCena("CENA_8A").getEscolhas().get(2).comFalaDoJogador("Sandro, fica de olho no portão. Se alguma coisa se mexer, apita.");
        rotas.getCena("CENA_8").getEscolhas().get(0).comFalaDoJogador("Júlio, acabou. Todo mundo vai ver as provas.");
        rotas.getCena("CENA_8").getEscolhas().get(1).comFalaDoJogador("Baltazar, segura ele. Sem violência.");
        rotas.getCena("CENA_8").getEscolhas().get(2).comFalaDoJogador("(você aceita a oferta e segue Júlio pela escuridão)");
        rotas.getCena("CENA_8").getEscolhas().get(3).comFalaDoJogador("Júlio, você lembra dos nomes dos mortos do Boqueirão? Quem você quer ser agora?");
        rotas.getCena("CENA_9A").getEscolhas().get(0).comFalaDoJogador("Usem a água estocada! Salvem a árvore!");
        rotas.getCena("CENA_9A").getEscolhas().get(1).comFalaDoJogador("Baltazar, vamos abrir um aceiro! Corta essas galhadas!");
        rotas.getCena("CENA_9A").getEscolhas().get(2).comFalaDoJogador("Sandro, pega as crianças. A gente vai embora pela caatinga.");
        rotas.getCena("CENA_9A").getEscolhas().get(3).comFalaDoJogador("Sandro, corre para o rádio e chama os vizinhos! Eu seguro o fogo!");
        rotas.getCena("CENA_9").getEscolhas().get(0).comFalaDoJogador("Coronel, eu vou contar a todos o que o senhor fez no Boqueirão. Inclusive aos seus homens.");
        rotas.getCena("CENA_9").getEscolhas().get(1).comFalaDoJogador("Está bem, Coronel. Leve o Olho. Mas deixe essa gente viver.");
        rotas.getCena("CENA_9").getEscolhas().get(2).comFalaDoJogador("Milena, a decisão é sua.");
        rotas.getCena("CENA_9").getEscolhas().get(3).comFalaDoJogador("(você ergue a ficha de latão) Quem de vocês ainda recebe isto?");
        rotas.getCena("CENA_9B").getEscolhas().get(0).comFalaDoJogador("Baltazar, estou contigo!");
        rotas.getCena("CENA_9B").getEscolhas().get(1).comFalaDoJogador("Ninguém avança sozinho! Em grupos!");
        rotas.getCena("CENA_9B").getEscolhas().get(2).comFalaDoJogador("(você se joga atrás do tronco queimado)");
        rotas.getCena("CENA_10A").getEscolhas().get(0).comFalaDoJogador("O Júlio vai responder diante de todos e trabalhar na reconstrução. E toda cota passa a ser registrada "
                + "aqui, na praça.");
        rotas.getCena("CENA_10A").getEscolhas().get(1).comFalaDoJogador("Que ele vá embora para o sertão. Sem água.");
        rotas.getCena("CENA_10A").getEscolhas().get(2).comFalaDoJogador("Vamos poupar o Júlio, se ele contar tudo o que sabe sobre o Coronel.");
        rotas.getCena("CENA_10A").getEscolhas().get(3).comFalaDoJogador("Eu cuido da cota daqui para a frente. A água passa pela minha mão.");
        rotas.getCena("CENA_10A").getEscolhas().get(4).comFalaDoJogador("Vamos chamar os povoados do rádio. Uma liga de águas, com contas abertas entre todos.");
        rotas.getCena("CENA_10A").getEscolhas().get(5).comFalaDoJogador("Eu proponho um julgamento de verdade: o Júlio, os presos e o próprio Coronel, com juiz, defesa e o "
                + "caderno da Estação 7 como prova.");
        rotas.getCena("CENA_10B").getEscolhas().get(0).comFalaDoJogador("O Baltazar me ensinou que ninguém segura a cerca sozinho. Juntos. Era a palavra dele.");
        rotas.getCena("CENA_10B").getEscolhas().get(1).comFalaDoJogador("(você põe a mão na pedra e fica em silêncio)");
        rotas.getCena("CENA_10B").getEscolhas().get(2).comFalaDoJogador("Eu já fiz o bastante. Assim que puder, vou embora.");
        rotas.getCena("CENA_9B").getEscolhas().get(0).comNarracaoAntes();
        rotas.getCena("CENA_9B").getEscolhas().get(1).comNarracaoAntes();
        rotas.getCena("CENA_9B").getEscolhas().get(2).comNarracaoAntes();
    }

    // ------------------------------------------------------------------ conversas retomáveis

    /**
     * Opções de conversa: se o jogador não as escolher, elas reaparecem na cena seguinte para ele voltar ao
     * assunto. Só opções de conversa, que não mudam o rumo da história.
     */
    private void conversas() {
        rotas.getCena("CENA_1A").getEscolhas().get(2).conversa("Perguntar a Milena quem decide as coisas em Bom-Jardim.");
        rotas.getCena("CENA_2A").getEscolhas().get(0).conversa("Procurar Júlio e perguntar como ele sabia que você veio do Boqueirão.");
        rotas.getCena("CENA_2A").getEscolhas().get(1).conversa("Voltar a falar com Júlio e apertar a mão dele.",
                f(NPC_JULIO, "(aperta a sua mão, demorado) Melhor tarde do que nunca. Um bom começo, então."));
        rotas.getCena("CENA_2A").getEscolhas().get(2).conversa("Puxar conversa com Baltazar sobre a oficina e o martelo.");
        rotas.getCena("CENA_3A").getEscolhas().get(2).conversa("Mostrar a Sandro o truque da folha de umbu para enganar a sede.");
        rotas.getCena("CENA_5A").getEscolhas().get(2).conversa("Pedir a Seu Raimundo para ver o porão e as chaves.",
                f(NPC_RAIMUNDO, "(entrega a chave, desconfiado) Agora que todo mundo já desconfia de mim, você vem perguntar? Pode olhar."),
                f(NPC_MILENA, "Antes tarde. Conferir nunca é tarde."));
        rotas.getCena("CENA_6A").getEscolhas().get(2).conversa("Contar a Milena e Baltazar sobre a proposta de Júlio.",
                f(NPC_MILENA, "(ouve tudo sem interromper) Quer café ou quer verdade? Eu fico com a verdade. Obrigada por me contar."),
                f(NPC_BALTAZAR, "Ele está com medo. Gente com medo vende qualquer coisa."));
        rotas.getCena("CENA_8A").getEscolhas().get(2).conversa("Combinar com Sandro o código do apito.");
    }

    // ------------------------------------------------------------------ finais

    private void finais() {
        rotas.adicionar(new Final("FINAL_JUSTICA", "Justiça para Bom-Jardim",
                "O Coronel Vasques e a milícia respondem pelo que fizeram, diante de um tribunal de verdade.",
                "Três semanas depois do levante, Dantas e uma comitiva da Liga das Águas encontram Vasques escondido numa "
                + "fazenda do Boqueirão. Ele é trazido vivo, sem um arranhão. \"Ninguém aqui vai virar o que ele foi\", diz "
                + "Milena, e ninguém discorda.\nA juíza itinerante do Tribunal Regional, doutora Helena Matos, chega numa "
                + "camionete de vidro rachado, com um defensor público para Vasques. \"Até ele tem direito a defesa\", ela "
                + "diz na praça, e repete até o povo aceitar. O julgamento dura três dias, debaixo do Umbuzeiro queimado, "
                + "aberto a quem quiser ouvir.\nAs provas são lidas em voz alta: o caderno da Estação 7, os quatro crachás, "
                + "a ficha de latão do Boqueirão. Sandro diz o nome da mãe olhando para o Coronel. Caetano mostra as mãos "
                + "que ainda tremem. Dantas testemunha contra o Coronel e contra si mesmo.\nVasques é condenado pela morte "
                + "dos técnicos da Estação 7, pelo corte da adutora que matou a cidade, pela tortura de Caetano e pelas "
                + "mortes do cerco e da praça. Vai cumprir pena na capital. As camionetes, os fuzis e a água que ele vendia "
                + "voltam para os povoados.\nCada miliciano é julgado pelo que fez, e não pela farda. Os que atiraram na "
                + "praça cumprem pena. Os que serviram com a família refém, como Dantas, recebem pena menor e trabalham na "
                + "reconstrução da Estação 7. Júlio confessa tudo e passa os anos seguintes cuidando das contas públicas, "
                + "sob os olhos de todos.\nUm ano depois, a adutora é religada. A água chega primeiro à zona rural e, "
                + "depois de muito tempo, volta a correr nos canos do centro, entre as ruínas. Na pedra dos nomes, alguém "
                + "acrescenta uma linha: \"Aqui a justiça foi feita sem vingança.\"\nMilena resume tudo numa frase, na "
                + "noite em que a primeira torneira do centro volta a pingar: \"Justiça não devolve ninguém. Mas impede que "
                + "aconteça de novo.\""));
        rotas.adicionar(new Final("FINAL_BOM", "A Semente do Sertão",
                "Você desmascarou o traidor e expulsou a milícia. O povoado sobrevive e prospera.",
                "Na praça de Bom-Jardim, uma lousa de madeira registra todo litro que entra e todo litro que sai, e qualquer pessoa pode "
                + "conferir. Sandro, que sempre foi bom de conta na escola, cuida da lousa e é o primeiro a avisar quando a conta não fecha. "
                + "O portão novo foi levantado com o martelo de Baltazar, que hoje fica pendurado na Casa das Cotas, ao alcance de quem precisar.\n"
                + "O broto do Umbuzeiro virou arbusto antes do fim do inverno. Quando a primeira chuva de 2072 cai, Milena sai para o terreiro, "
                + "estende a caneca de lata e não diz nada. Nos quintais, alguém replanta muda de manga e de acerola, e pela primeira vez em vinte anos se fala em feira de sábado. Ao lado do Umbuzeiro há uma pedra com trinta e um nomes: Josefa, Damião, Raimundo, Baltazar e os outros que não viram a chuva. Do horizonte vêm mensageiros de outros povoados, querendo saber como se faz. "
                + "Você responde com a frase que Milena lhe ensinou: “A água não se possui. A água se divide.”"));
        rotas.adicionar(new Final("FINAL_NEUTRO", "Sombra Solitária",
                "O povoado ruiu, mas você guardou recursos suficientes para fugir pela Caatinga como um nômade.",
                "Bom-Jardim não aguenta o segundo inverno de seca. Uns partem para o litoral, outros se espalham pelas vilas do norte, "
                + "e o Olho, sem a árvore, vira um buraco escuro. Você sai numa madrugada sem lua, com odres cheios e a cabra de Sandro "
                + "amarrada atrás, porque o menino pediu e você não soube dizer não.\n"
                + "Você vira andarilho. Conserta bombas em troca de água, escuta histórias em troca de pão, e de longe ouve boatos de que "
                + "Milena ainda vive e de que o Coronel administra uma cisterna que não é dele. Na mochila, o caderno de cotas de Júlio "
                + "continua com as páginas finais em branco. Talvez um dia você as preencha."));
        rotas.adicionar(new Final("FINAL_RUIM", "Sob o Calcanhar da Milícia",
                "O traidor venceu. A milícia dominou a água e você se tornou um prisioneiro do novo regime.",
                "Júlio, de terno emprestado, ocupa a Casa das Cotas como Contador-Geral. Vasques mudou o nome do povoado para Posto Quatro "
                + "e nunca mais precisou levantar a voz. Você agora tem um número riscado a ferro no pulso, e toda manhã entra na fila do "
                + "Poço da Santa para receber uma ficha com o selo do Boqueirão.\n"
                + "Milena não aparece mais na praça. Sandro carrega baldes para a Guarda sem fazer perguntas, e é isso que mais dói. "
                + "À noite, no galpão, alguém sussurra a conta do dia, e você descobre que nunca mais vai deixar de ser contado. "
                + "Na palma da mão, escondida, a ficha de latão do Boqueirão ainda tem as bordas afiadas."));
    }
}
