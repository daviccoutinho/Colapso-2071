package br.colapso2071.fx;

import br.colapso2071.excecao.PersistenciaException;
import br.colapso2071.model.Configuracoes;
import br.colapso2071.model.Progresso;
import br.colapso2071.model.Rotas;
import br.colapso2071.persistencia.ConfiguracoesRepositorio;
import br.colapso2071.persistencia.ProgressoRepositorio;
import br.colapso2071.persistencia.SaveRepository;

import java.nio.file.Path;
import java.time.Clock;

/**
 * Objetos de domínio e persistência compartilhados pelas telas JavaFX.
 * Reaproveita o Model e os repositórios da Fase 2 sem alteração.
 */
public final class ContextoFx {
    private final Rotas rotas = new Rotas();
    private final SaveRepository saves;
    private final ConfiguracoesRepositorio configuracoesRepo;
    private final ProgressoRepositorio progressoRepo;
    private Configuracoes configuracoes = new Configuracoes();
    private Progresso progresso = new Progresso();
    private String avisoDeInicio = "";

    public ContextoFx(Path pasta, Clock clock) {
        this.saves = new SaveRepository(pasta, rotas, clock);
        this.configuracoesRepo = new ConfiguracoesRepositorio(pasta);
        this.progressoRepo = new ProgressoRepositorio(pasta);
        try {
            configuracoes = configuracoesRepo.carregar();
        } catch (PersistenciaException e) {
            avisoDeInicio = "Preferências ignoradas: " + e.getMessage();
        }
        try {
            progresso = progressoRepo.carregar();
        } catch (PersistenciaException e) {
            avisoDeInicio = "Registro de finais ignorado: " + e.getMessage();
        }
    }

    public Rotas rotas() { return rotas; }
    public SaveRepository saves() { return saves; }
    public ConfiguracoesRepositorio configuracoesRepo() { return configuracoesRepo; }
    public ProgressoRepositorio progressoRepo() { return progressoRepo; }
    public Configuracoes configuracoes() { return configuracoes; }
    public Progresso progresso() { return progresso; }
    public String avisoDeInicio() { return avisoDeInicio; }
}
