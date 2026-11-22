package br.colapso2071.excecao;

/** Falha ao ler um save ou arquivo de preferências (slot vazio, arquivo ilegível, erro de E/S). */
public class CarregamentoException extends PersistenciaException {
    private static final long serialVersionUID = 1L;

    public CarregamentoException(String mensagem) {
        super(mensagem);
    }

    public CarregamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
