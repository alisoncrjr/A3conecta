package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.UUID;

public class ProjetoConstrucao {
    private String id;
    private Cliente cliente;
    private Prestador prestador;
    private String nomeProjeto;
    private String tipoServico;
    private String descricao;
    private LocalDate dataInicioEstimada;
    private LocalDate dataFimEstimada;
    private StatusProjeto status;
    private ObservableList<ItemOrcamento> itensOrcamento;
    private double valorMaoDeObra;
    private double custoEstimadoMateriais;
    private double custoEstimadoMaoDeObra;
    private double custoRealMateriais;
    private double custoRealMaoDeObra;
    private String observacoesPrestador;

    public enum StatusProjeto {
        SOLICITADO, ORCAMENTO_PENDENTE, AGUARDANDO_CLIENTE, APROVADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO
    }

    public ProjetoConstrucao(Cliente cliente, String nomeProjeto, String tipoServico, String descricao, LocalDate dataInicio, LocalDate dataFim) {
        this.id = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.nomeProjeto = nomeProjeto;
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.dataInicioEstimada = dataInicio;
        this.dataFimEstimada = dataFim;
        this.status = StatusProjeto.SOLICITADO;
        this.itensOrcamento = FXCollections.observableArrayList();
        this.valorMaoDeObra = 0.0;
        this.prestador = null;
    }


    public ProjetoConstrucao(Cliente cliente, Prestador prestador, String nomeProjeto, String tipoServico, String descricao, LocalDate dataInicio, LocalDate dataFim) {
        this(cliente, nomeProjeto, tipoServico, descricao, dataInicio, dataFim);
        this.prestador = prestador;
    }

    // Getters
    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Prestador getPrestador() { return prestador; }
    public String getNomeProjeto() { return nomeProjeto; }
    public String getTipoServico() { return tipoServico; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataInicioEstimada() { return dataInicioEstimada; }
    public LocalDate getDataFimEstimada() { return dataFimEstimada; }
    public StatusProjeto getStatus() { return status; }
    public ObservableList<ItemOrcamento> getItensOrcamento() { return itensOrcamento; }
    public double getValorMaoDeObra() { return valorMaoDeObra; }


    public String getTitulo() { return nomeProjeto; }
    public LocalDate getDataInicio() { return dataInicioEstimada; }
    public LocalDate getDataFim() { return dataFimEstimada; }

    // Setters
    public void setPrestador(Prestador prestador) { this.prestador = prestador; }
    public void setStatus(StatusProjeto status) { this.status = status; }
    public void setValorMaoDeObra(double valorMaoDeObra) { this.valorMaoDeObra = valorMaoDeObra; }
    public void setDescricao(String descricao) { this.descricao = descricao; }


    public double getCustoTotalMateriais() {
        return itensOrcamento.stream().mapToDouble(ItemOrcamento::getValorTotal).sum();
    }
    public double getCustoTotalOrcamento() {
        return getCustoTotalMateriais() + getValorMaoDeObra();
    }


    public double getCustoEstimadoMateriais() { return custoEstimadoMateriais; }
    public void setCustoEstimadoMateriais(double custoEstimadoMateriais) { this.custoEstimadoMateriais = custoEstimadoMateriais; }

    public double getCustoEstimadoMaoDeObra() { return custoEstimadoMaoDeObra; }
    public void setCustoEstimadoMaoDeObra(double custoEstimadoMaoDeObra) { this.custoEstimadoMaoDeObra = custoEstimadoMaoDeObra; }

    public double getCustoRealMateriais() { return custoRealMateriais; }
    public void setCustoRealMateriais(double custoRealMateriais) { this.custoRealMateriais = custoRealMateriais; }

    public double getCustoRealMaoDeObra() { return custoRealMaoDeObra; }
    public void setCustoRealMaoDeObra(double custoRealMaoDeObra) { this.custoRealMaoDeObra = custoRealMaoDeObra; }

    public String getObservacoesPrestador() { return observacoesPrestador; }
    public void setObservacoesPrestador(String observacoesPrestador) { this.observacoesPrestador = observacoesPrestador; }

    public void adicionarMaterialUtilizado(ItemOrcamento item) {
        if (item != null) this.itensOrcamento.add(item);
    }

    @Override
    public String toString() {
        return nomeProjeto + " (" + tipoServico + ") - Status: " + status;
    }
}