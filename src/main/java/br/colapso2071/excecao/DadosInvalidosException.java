package br.colapso2071.excecao;

/** Os dados lidos estão corrompidos, incompletos ou são incompatíveis com o jogo (ou o slot é inválido). */
public class DadosInvalidosException extends PersistenciaException {
    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }

    public DadosInvalidosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
