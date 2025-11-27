package dao;

import java.math.BigDecimal;

public class ItemOrcamentoRecord {
    private final String id;
    private final String projetoId;
    private final String nome;
    private final String unidade;
    private final BigDecimal quantidade;
    private final BigDecimal valorUnitario;
    private final BigDecimal valorTotal;

    public ItemOrcamentoRecord(String id, String projetoId, String nome, String unidade, BigDecimal quantidade, BigDecimal valorUnitario, BigDecimal valorTotal) {
        this.id = id;
        this.projetoId = projetoId;
        this.nome = nome;
        this.unidade = unidade;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
    }

    public String getId() { return id; }
    public String getProjetoId() { return projetoId; }
    public String getNome() { return nome; }
    public String getUnidade() { return unidade; }
    public BigDecimal getQuantidade() { return quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public BigDecimal getValorTotal() { return valorTotal; }
}
