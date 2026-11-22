package br.colapso2071.view;

import br.colapso2071.model.Perfil;

import java.util.Collection;

/**
 * View do menu inicial: apenas exibe telas e lê o que o usuário digitar.
 * Não decide o que fazer com a entrada (isso é do controller).
 */
public class MenuView {
    private final Console console;

    public MenuView(Console console) {
        this.console = console;
    }

    /** Exibe o menu e devolve o texto digitado, ou null se a entrada terminou. */
    public String lerOpcaoMenu() {
        console.println("==========================================");
        console.println("            COLAPSO 2071                  ");
        console.println("==========================================");
        console.println("1. Novo Jogo");
        console.println("2. Continuar");
        console.println("3. Carregar Jogo");
        console.println("4. Excluir Save");
        console.println("5. Configurações");
        console.println("6. Finais e Conquistas");
        console.println("7. Instruções");
        console.println("8. Créditos");
        console.println("9. Sair");
        console.print("Escolha uma opção: ");
        return console.lerLinha();
    }

    /** Pede o nome do jogador (com sugestão, se houver); devolve null se a entrada terminou. */
    public String lerNomeJogador(String nomePadrao) {
        if (nomePadrao == null || nomePadrao.isBlank()) {
            console.print("Digite o nome do seu jogador: ");
        } else {
            console.print("Digite o nome do seu jogador [Enter = " + nomePadrao + "]: ");
        }
        return console.lerLinha();
    }

    /**
     * Mostra os perfis do protagonista e lê a escolha (número). "V" volta ao menu.
     * Devolve o texto digitado, ou null se a entrada terminou.
     */
    public String lerPerfil(Collection<Perfil> perfis) {
        console.println("\n--- CRIAÇÃO DO PROTAGONISTA ---");
        console.println("Como você sobreviveu até aqui? Seu perfil define seus atributos iniciais.");
        int numero = 1;
        for (Perfil p : perfis) {
            console.println(numero++ + ". " + p.getNome() + " - " + p.getDescricao());
            console.println("      Liderança " + p.getLideranca() + " | Técnico " + p.getTecnico()
                    + " | Negociação " + p.getNegociacao());
        }
        console.print("Escolha seu perfil [1-" + perfis.size() + ", V = voltar]: ");
        return console.lerLinha();
    }

    public void exibirPerfilInvalido() {
        console.println("Perfil inválido.");
    }

    public void exibirInstrucoes() {
        console.println("\n--- INSTRUÇÕES ---");
        console.println("Colapso 2071 é um jogo narrativo de sobrevivência.");
        console.println("Gerencie seus recursos (comida e água) e tome escolhas sábias para sobreviver.");
        console.println("Cada escolha muda sua reputação, seus atributos e suas relações com os personagens.");
        console.println("Algumas opções só se liberam com certos itens, atributos, afinidades ou decisões anteriores.");
        console.println("Use F durante a partida para ver sua ficha: atributos, inventário, missões e decisões.");
        console.println("Em certas cenas você pode salvar a partida (opção S) em um de 3 slots;");
        console.println("o jogo também salva automaticamente em momentos importantes.");
        console.println("Use M durante a partida para voltar ao menu principal.");
        aguardarEnter();
    }

    public void exibirCreditos() {
        console.println("\n--- CRÉDITOS ---");
        console.println("Desenvolvido para o projeto Colapso 2071.");
        console.println("Desenvolvido por Davi Carvalho Coutinho de Jesus");
        aguardarEnter();
    }

    public void exibirSaindo() {
        console.println("Saindo do jogo...");
    }

    public void exibirOpcaoInvalida() {
        console.println("Opção inválida.");
    }

    private void aguardarEnter() {
        console.println("Pressione ENTER para voltar...");
        console.lerLinha();
    }
}
