package br.colapso2071.model;

public class Final {
    private final String id;
    private final String titulo;
    private final String mensagemEncerramento;
    private final String epilogo;

    public Final(String id, String titulo, String mensagemEncerramento) {
        this(id, titulo, mensagemEncerramento, "");
    }

    public Final(String id, String titulo, String mensagemEncerramento, String epilogo) {
        this.id = id;
        this.titulo = titulo;
        this.mensagemEncerramento = mensagemEncerramento;
        this.epilogo = epilogo;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getMensagemEncerramento() { return mensagemEncerramento; }
    /** Texto longo exibido depois do desfecho (pode ser vazio). */
    public String getEpilogo() { return epilogo; }
}
