package dao;

import java.math.BigDecimal;
import java.sql.Date;

public class ProjetoRecord {
    private final String id;
    private final String clienteId;
    private final String prestadorId;
    private final String titulo;
    private final String tipoServico;
    private final String descricao;
    private final Date dataInicioEstimada;
    private final Date dataFimEstimada;
    private final String status;
    private final BigDecimal custoEstimadoMateriais;
    private final BigDecimal custoEstimadoMaoDeObra;
    private final String observacoesPrestador;
    private final BigDecimal valorMaoDeObra;
    private final BigDecimal custoRealMateriais;
    private final BigDecimal custoRealMaoDeObra;

    public ProjetoRecord(String id, String clienteId, String prestadorId, String titulo, String tipoServico, String descricao, Date dataInicioEstimada, Date dataFimEstimada, String status, BigDecimal custoEstimadoMateriais, BigDecimal custoEstimadoMaoDeObra, String observacoesPrestador, BigDecimal valorMaoDeObra, BigDecimal custoRealMateriais, BigDecimal custoRealMaoDeObra) {
        this.id = id;
        this.clienteId = clienteId;
        this.prestadorId = prestadorId;
        this.titulo = titulo;
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.dataInicioEstimada = dataInicioEstimada;
        this.dataFimEstimada = dataFimEstimada;
        this.status = status;
        this.custoEstimadoMateriais = custoEstimadoMateriais;
        this.custoEstimadoMaoDeObra = custoEstimadoMaoDeObra;
        this.observacoesPrestador = observacoesPrestador;
        this.valorMaoDeObra = valorMaoDeObra;
        this.custoRealMateriais = custoRealMateriais;
        this.custoRealMaoDeObra = custoRealMaoDeObra;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public String getPrestadorId() { return prestadorId; }
    public String getTitulo() { return titulo; }
    public String getTipoServico() { return tipoServico; }
    public String getDescricao() { return descricao; }
    public Date getDataInicioEstimada() { return dataInicioEstimada; }
    public Date getDataFimEstimada() { return dataFimEstimada; }
    public String getStatus() { return status; }
    public BigDecimal getCustoEstimadoMateriais() { return custoEstimadoMateriais; }
    public BigDecimal getCustoEstimadoMaoDeObra() { return custoEstimadoMaoDeObra; }
    public String getObservacoesPrestador() { return observacoesPrestador; }
    public BigDecimal getValorMaoDeObra() { return valorMaoDeObra; }
    public BigDecimal getCustoRealMateriais() { return custoRealMateriais; }
    public BigDecimal getCustoRealMaoDeObra() { return custoRealMaoDeObra; }
}
