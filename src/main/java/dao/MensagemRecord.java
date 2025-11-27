package dao;

import java.sql.Timestamp;

public class MensagemRecord {
    private final String id;
    private final String conversaId;
    private final String remetenteId;
    private final String destinatarioId;
    private final String conteudo;
    private final Timestamp dataEnvio;
    private final boolean lida;

    public MensagemRecord(String id, String conversaId, String remetenteId, String destinatarioId, String conteudo, Timestamp dataEnvio, boolean lida) {
        this.id = id;
        this.conversaId = conversaId;
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.conteudo = conteudo;
        this.dataEnvio = dataEnvio;
        this.lida = lida;
    }

    public String getId() { return id; }
    public String getConversaId() { return conversaId; }
    public String getRemetenteId() { return remetenteId; }
    public String getDestinatarioId() { return destinatarioId; }
    public String getConteudo() { return conteudo; }
    public Timestamp getDataEnvio() { return dataEnvio; }
    public boolean isLida() { return lida; }
}
