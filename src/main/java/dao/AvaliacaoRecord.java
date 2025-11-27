package dao;

import java.sql.Timestamp;

public class AvaliacaoRecord {
    private final String id;
    private final String clienteId;
    private final String prestadorId;
    private final int pontuacao;
    private final String comentario;
    private final Timestamp dataAvaliacao;

    public AvaliacaoRecord(String id, String clienteId, String prestadorId, int pontuacao, String comentario, Timestamp dataAvaliacao) {
        this.id = id;
        this.clienteId = clienteId;
        this.prestadorId = prestadorId;
        this.pontuacao = pontuacao;
        this.comentario = comentario;
        this.dataAvaliacao = dataAvaliacao;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public String getPrestadorId() { return prestadorId; }
    public int getPontuacao() { return pontuacao; }
    public String getComentario() { return comentario; }
    public Timestamp getDataAvaliacao() { return dataAvaliacao; }
}
