package dao;

public class ContratoRecord {
    private final String id;
    private final String descricao;
    private final String clienteId;
    private final String clienteName;
    private final String prestadorId;
    private final String prestadorName;
    private final String status;
    private final String projetoId;
    private final java.math.BigDecimal valorTotal;
    private final java.math.BigDecimal valorRecebido;

    public ContratoRecord(String id, String descricao, String clienteId, String clienteName, String prestadorId, String prestadorName, String status, String projetoId, java.math.BigDecimal valorTotal, java.math.BigDecimal valorRecebido) {
        this.id = id;
        this.descricao = descricao;
        this.clienteId = clienteId;
        this.clienteName = clienteName;
        this.prestadorId = prestadorId;
        this.prestadorName = prestadorName;
        this.status = status;
        this.projetoId = projetoId;
        this.valorTotal = valorTotal == null ? java.math.BigDecimal.ZERO : valorTotal;
        this.valorRecebido = valorRecebido == null ? java.math.BigDecimal.ZERO : valorRecebido;
    }

    public String getId() { return id; }
    public String getDescricao() { return descricao; }
    public String getClienteId() { return clienteId; }
    public String getClienteName() { return clienteName; }
    public String getPrestadorId() { return prestadorId; }
    public String getPrestadorName() { return prestadorName; }
    public String getStatus() { return status; }
    public String getProjetoId() { return projetoId; }
    public java.math.BigDecimal getValorTotal() { return valorTotal; }
    public java.math.BigDecimal getValorRecebido() { return valorRecebido; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", descricao, prestadorName != null ? prestadorName : "N/A", status);
    }
}
