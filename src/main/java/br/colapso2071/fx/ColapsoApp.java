package br.colapso2071.fx;

import br.colapso2071.excecao.PersistenciaException;
import br.colapso2071.fx.AcaoMenu.Estilo;
import br.colapso2071.persistencia.ResumoSave;
import br.colapso2071.persistencia.ResultadoCarga;
import br.colapso2071.persistencia.SaveRepository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Aplicação JavaFX: monta o contexto (Model + repositórios da Fase 2) e controla a navegação.
 * A partida em si fica em {@link PartidaFx}; aqui só se escolhe o que mostrar no menu.
 */
public final class ColapsoApp extends Application {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ContextoFx ctx;
    private Navegador nav;

    @Override
    public void start(Stage palco) {
        ctx = new ContextoFx(br.colapso2071.persistencia.PastaDeDados.saves(), Clock.systemDefaultZone());
        nav = new Navegador(palco);
        Tema.aplicar(ctx.configuracoes().isTemaModerno());
        Musica.setSaida(Musica.Saida.de(ctx.configuracoes().getSaidaAudio()));
        Musica.setVolume(ctx.configuracoes().getVolumeMusica());
        Musica.setLigada(ctx.configuracoes().isMusica());
        palco.setTitle("Colapso 2071 · Bom-Jardim · versão " + Instrucoes.VERSAO);
        mostrarMenu();
        // Fechar a janela (ou o sistema encerrar o programa) grava a partida em andamento
        palco.setOnCloseRequest(e -> {
            PartidaFx.salvarAoFechar();
            Musica.parar();
        });
        Runtime.getRuntime().addShutdownHook(new Thread(PartidaFx::salvarAoFechar));
        palco.show();
        if (ctx.configuracoes().isTelaCheia()) {
            palco.setFullScreen(true);
        }
    }

    private void mostrarMenu() {
        Musica.tocar(Trilha.doMenu());
        Navegador.esquecerCena();
        Optional<ResumoSave> recente = ctx.saves().maisRecente();
        String sub = recente
                .map(r -> r.nomeJogador() + " · Cap. " + r.capitulo() + " · " + r.dataHora().format(DATA))
                .orElse(null);

        List<AcaoMenu> acoes = new ArrayList<>();
        acoes.add(acao("1", "Novo Jogo", this::novoJogo));
        acoes.add(acao("2", "Continuar", this::continuar));
        acoes.add(acao("3", "Carregar Jogo", () -> mostrarSaves("Carregar jogo", true, this::carregar)));
        acoes.add(acao("4", "Excluir Save", () -> mostrarSaves("Excluir save", false, this::excluir)));
        acoes.add(acao("5", "Configurações", () -> nav.mostrar(new TelaConfig(ctx, this::mostrarMenu).construir())));
        acoes.add(acao("6", "Finais e Conquistas", () -> nav.mostrar(
                new TelaGaleria(ctx.rotas(), ctx.progresso(), this::mostrarMenu).construir())));
        acoes.add(acao("7", "Instruções", () -> nav.mostrar(new TelaTexto("Instruções", Instrucoes.paragrafos(),
                this::mostrarMenu).construir())));
        acoes.add(acao("8", "Sinopse e Créditos", () -> nav.mostrar(new TelaTexto("Colapso 2071", Sinopse.curta(),
                this::mostrarMenu).construir())));
        acoes.add(acao("9", "Sair", nav::fechar));

        // Opção Continuar fica sem destaque quando não há save; a ação avisa o motivo.
        nav.mostrar(new TelaMenu(acoes, sub, ctx.avisoDeInicio()).construir());
    }

    private static AcaoMenu acao(String atalho, String rotulo, Runnable r) {
        return AcaoMenu.de(atalho, rotulo, TelaMenu.estiloDe(atalho), r);
    }

    private void mostrarSaves(String titulo, boolean aceitaAuto, java.util.function.IntConsumer aoEscolher) {
        nav.mostrar(new TelaSaves(ctx, titulo, aceitaAuto, aoEscolher, this::mostrarMenu).construir());
    }

    private void novoJogo() {
        nav.mostrar(new TelaNovoJogo(ctx.rotas(), ctx.configuracoes().getNomePadrao(),
                (nome, perfil) -> novaPartida().novaPartida(nome, perfil), this::mostrarMenu).construir());
    }

    private void continuar() {
        Optional<ResumoSave> recente = ctx.saves().maisRecente();
        if (recente.isEmpty()) {
            aviso("Continuar", "Não há nenhum save para continuar.");
            return;
        }
        carregar(recente.get().slot());
    }

    /** Lê o save (com selo e backup) e entra direto na cena em que o jogador parou. */
    private void carregar(int slot) {
        try {
            ResultadoCarga r = ctx.saves().carregarComRecuperacao(slot);
            if (r.recuperadoDoBackup()) {
                aviso("Save recuperado", "O save principal estava danificado; foi usado o backup.");
            }
            novaPartida().retomar(r.jogo());
        } catch (PersistenciaException e) {
            aviso("Não foi possível carregar", e.getMessage());
        }
    }

    private PartidaFx novaPartida() {
        return new PartidaFx(ctx, nav, this::mostrarMenu);
    }

    private void excluir(int slot) {
        if (Navegador.confirmar("Excluir o save " + SaveRepository.nomeDoSlot(slot) + "? Isso não pode ser desfeito.")) {
            try {
                ctx.saves().excluir(slot);
            } catch (PersistenciaException e) {
                aviso("Não foi possível excluir", e.getMessage());
            }
        }
        mostrarSaves("Excluir save", false, this::excluir);
    }

    private void aviso(String titulo, String texto) {
        Navegador.avisar(titulo, texto);
    }

    @Override
    public void stop() {
        PartidaFx.salvarAoFechar();
        Musica.parar();
        Platform.exit();
    }
}
