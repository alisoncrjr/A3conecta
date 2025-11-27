package Model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.UUID;

public class ItemOrcamento {
    private final String id;
    private final StringProperty nome;
    private final StringProperty unidade;
    private final DoubleProperty quantidade;
    private final DoubleProperty valorUnitario;
    private final DoubleProperty valorTotal;

    public ItemOrcamento(String nome, String unidade, double quantidade, double valorUnitario) {
        this.id = UUID.randomUUID().toString();
        this.nome = new SimpleStringProperty(nome);
        this.unidade = new SimpleStringProperty(unidade);
        this.quantidade = new SimpleDoubleProperty(quantidade);
        this.valorUnitario = new SimpleDoubleProperty(valorUnitario);
        this.valorTotal = new SimpleDoubleProperty(quantidade * valorUnitario);


        this.quantidade.addListener((obs, oldVal, newVal) -> atualizarTotal());
        this.valorUnitario.addListener((obs, oldVal, newVal) -> atualizarTotal());
    }

    private void atualizarTotal() {
        this.valorTotal.set(getQuantidade() * getValorUnitario());
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome.get(); }
    public String getUnidade() { return unidade.get(); }
    public double getQuantidade() { return quantidade.get(); }
    public double getValorUnitario() { return valorUnitario.get(); }
    public double getValorTotal() { return valorTotal.get(); }

    // Setters
    public void setNome(String nome) { this.nome.set(nome); }
    public void setUnidade(String unidade) { this.unidade.set(unidade); }
    public void setQuantidade(double quantidade) { this.quantidade.set(quantidade); }
    public void setValorUnitario(double valorUnitario) { this.valorUnitario.set(valorUnitario); }

    // Properties
    public StringProperty nomeProperty() { return nome; }
    public StringProperty unidadeProperty() { return unidade; }
    public DoubleProperty quantidadeProperty() { return quantidade; }
    public DoubleProperty valorUnitarioProperty() { return valorUnitario; }
    public DoubleProperty valorTotalProperty() { return valorTotal; }
}