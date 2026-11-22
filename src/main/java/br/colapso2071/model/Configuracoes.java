package br.colapso2071.model;

/** Preferências do usuário, mantidas entre execuções. */
public class Configuracoes {
    private boolean salvamentoAutomatico = true;
    private boolean mostrarStatus = false;
    private String nomePadrao = "";
    private boolean musica = true;
    private int volumeMusica = 60;
    private boolean temaModerno = true;
    private boolean telaCheia = false;
    private String saidaAudio = "AUTOMATICO";

    public boolean isSalvamentoAutomatico() { return salvamentoAutomatico; }
    public void setSalvamentoAutomatico(boolean valor) { this.salvamentoAutomatico = valor; }

    /** Trilha sonora ligada. */
    public boolean isMusica() { return musica; }
    public void setMusica(boolean valor) { this.musica = valor; }

    /** Volume da música, de 0 a 100. */
    public int getVolumeMusica() { return volumeMusica; }
    public void setVolumeMusica(int valor) { this.volumeMusica = Math.max(0, Math.min(100, valor)); }

    /** Tema de interface AtlantaFX (controles modernos) por baixo da identidade visual do jogo. */
    public boolean isTemaModerno() { return temaModerno; }
    public void setTemaModerno(boolean valor) { this.temaModerno = valor; }

    /** Por onde a música sai: AUTOMATICO, JAVA_SOUND, SISTEMA ou JAVAFX. */
    public String getSaidaAudio() { return saidaAudio; }
    public void setSaidaAudio(String valor) { this.saidaAudio = valor == null || valor.isBlank() ? "AUTOMATICO" : valor; }

    /** Abrir o jogo em tela cheia (F11 alterna durante o jogo). */
    public boolean isTelaCheia() { return telaCheia; }
    public void setTelaCheia(boolean valor) { this.telaCheia = valor; }

    /** Exibir comida/água/reputação a cada cena. */
    public boolean isMostrarStatus() { return mostrarStatus; }
    public void setMostrarStatus(boolean valor) { this.mostrarStatus = valor; }

    /** Nome sugerido ao iniciar um novo jogo (vazio = nenhum). */
    public String getNomePadrao() { return nomePadrao; }
    public void setNomePadrao(String nome) { this.nomePadrao = (nome == null) ? "" : nome.trim(); }
}
