package Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Contrato {
    private String id;
    private Cliente cliente;
    private Prestador prestador;
    private String descricaoServico;
    private LocalDateTime dataContrato;
    private ContratoStatus status;
    private String projetoId;
    private double valorTotal;
    private double valorRecebido;

    public enum ContratoStatus {
        PENDENTE, EM_ANDAMENTO, FINALIZADO, CANCELADO
    }

    public Contrato(Cliente cliente, Prestador prestador, String descricaoServico, LocalDateTime dataContrato, ContratoStatus status) {
        this.id = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.prestador = prestador;
        this.descricaoServico = descricaoServico;
        this.dataContrato = dataContrato;
        this.status = status;
        this.valorTotal = 0.0;
        this.valorRecebido = 0.0;
    }


    public String getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(String projetoId) {
        this.projetoId = projetoId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Prestador getPrestador() {
        return prestador;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public LocalDateTime getDataContrato() {
        return dataContrato;
    }

    public ContratoStatus getStatus() {
        return status;
    }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public double getValorRecebido() { return valorRecebido; }
    public void setValorRecebido(double valorRecebido) { this.valorRecebido = valorRecebido; }

    public double getValorRestante() { return Math.max(0.0, valorTotal - valorRecebido); }

    // Setters
    public void setStatus(ContratoStatus status) {
        this.status = status;
    }
}