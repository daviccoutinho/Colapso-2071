package br.colapso2071.excecao;

/** Falha ao gravar ou excluir um save, as configurações ou o progresso. */
public class SaveException extends PersistenciaException {
    private static final long serialVersionUID = 1L;

    public SaveException(String mensagem) {
        super(mensagem);
    }

    public SaveException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
