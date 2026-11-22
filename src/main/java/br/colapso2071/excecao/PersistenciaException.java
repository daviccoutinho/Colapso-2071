package br.colapso2071.excecao;

/** Base das falhas de persistência (salvar, carregar, dados inválidos). */
public abstract class PersistenciaException extends Exception {
    private static final long serialVersionUID = 1L;

    protected PersistenciaException(String mensagem) {
        super(mensagem);
    }

    protected PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
