package Controller;

import Application.App;
import Model.ItemOrcamento;
import Model.ProjetoConstrucao;
import services.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class OrcamentoController implements Initializable {

    @FXML private Label lblNomeProjeto;
    @FXML private Label lblStatus;
    @FXML private Label lblCliente;
    @FXML private Label lblPrestador;
    @FXML private Label lblTipoServico;
    @FXML private DatePicker datePickerPrazo;
    @FXML private TextArea txtMateriaisCliente;
    @FXML private TextArea txtObservacoesPrestador;

    @FXML private TableView<ItemOrcamento> tableViewItens;
    @FXML private TableColumn<ItemOrcamento, String> colNomeItem;
    @FXML private TableColumn<ItemOrcamento, Double> colQuantidade;
    @FXML private TableColumn<ItemOrcamento, Double> colValorUnitario;
    @FXML private TableColumn<ItemOrcamento, Double> colValorTotal;

    @FXML private HBox hboxAddItens;
    @FXML private TextField txtNomeItem;
    @FXML private TextField txtQtdItem;
    @FXML private TextField txtValorUnitItem;

    @FXML private Label lblTotalMateriais;
    @FXML private TextField txtValorMaoDeObra;
    @FXML private Label lblOrcamentoTotal;
    @FXML private Label lblFeedback;
    @FXML private Button btnSalvarPrestador;
    @FXML private Button btnAprovarCliente;

    private NavigationService navigationService;
    private ProjetoConstrucao projeto;
    private ObservableList<ItemOrcamento> itensObservable = FXCollections.observableArrayList();
    private final DecimalFormat moneyFormat = new DecimalFormat("0.00");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigationService = App.getNavigationService();


        colNomeItem.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        colQuantidade.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getQuantidade()).asObject());
        colValorUnitario.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getValorUnitario()).asObject());
        colValorTotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getValorTotal()).asObject());

        tableViewItens.setItems(itensObservable);


        tableViewItens.setEditable(true);
        colNomeItem.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        colQuantidade.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
        colValorUnitario.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));

        colNomeItem.setOnEditCommit(event -> {
            ItemOrcamento it = event.getRowValue();
            it.setNome(event.getNewValue());
            atualizarTotais();
        });
        colQuantidade.setOnEditCommit(event -> {
            ItemOrcamento it = event.getRowValue();
            it.setQuantidade(event.getNewValue());
            atualizarTotais();
        });
        colValorUnitario.setOnEditCommit(event -> {
            ItemOrcamento it = event.getRowValue();
            it.setValorUnitario(event.getNewValue());
            atualizarTotais();
        });


        itensObservable.addListener((javafx.collections.ListChangeListener.Change<? extends ItemOrcamento> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (ItemOrcamento it : c.getAddedSubList()) attachItemListeners(it);
                }
            }
            atualizarTotais();
        });


        for (ItemOrcamento it : itensObservable) attachItemListeners(it);


        hboxAddItens.setVisible(false);

        if (App.getLoggedUser() != null) {

            if (App.getLoggedUser() instanceof Model.Prestador) {
                hboxAddItens.setVisible(true);
                btnSalvarPrestador.setVisible(true);
                btnAprovarCliente.setVisible(false);
            } else if (App.getLoggedUser() instanceof Model.Cliente) {
                hboxAddItens.setVisible(false);
                btnSalvarPrestador.setVisible(false);
                btnAprovarCliente.setVisible(true);
            }
        }

        txtValorMaoDeObra.setText("0.00");
    }

    public void setProjeto(ProjetoConstrucao projeto) {
        this.projeto = projeto;
        if (projeto == null) return;

        lblNomeProjeto.setText(projeto.getNomeProjeto());
        lblStatus.setText(projeto.getStatus() != null ? projeto.getStatus().name() : "N/A");
        lblCliente.setText("Cliente: " + (projeto.getCliente() != null ? projeto.getCliente().getName() : "N/A"));
        lblPrestador.setText("Prestador: " + (projeto.getPrestador() != null ? projeto.getPrestador().getName() : "N/A"));
        lblTipoServico.setText("Tipo de Serviço: " + (projeto.getTipoServico() != null ? projeto.getTipoServico() : "N/A"));

        if (projeto.getDataInicioEstimada() != null) datePickerPrazo.setValue(projeto.getDataInicioEstimada());
        txtMateriaisCliente.setText("" + (projeto.getItensOrcamento() != null ? projeto.getItensOrcamento().toString() : ""));
        txtObservacoesPrestador.setText(projeto.getObservacoesPrestador() != null ? projeto.getObservacoesPrestador() : "");

        if (projeto.getItensOrcamento() != null) {
            itensObservable.setAll(projeto.getItensOrcamento());
        }

        atualizarTotais();
    }

    private void attachItemListeners(ItemOrcamento it) {
        if (it == null) return;
        try {
            it.quantidadeProperty().addListener((obs, oldV, newV) -> atualizarTotais());
            it.valorUnitarioProperty().addListener((obs, oldV, newV) -> atualizarTotais());
            it.valorTotalProperty().addListener((obs, oldV, newV) -> atualizarTotais());
        } catch (Exception ignored) {}
    }

    @FXML
    private void handleVoltar() throws IOException {
        navigationService.goBack();
    }

    @FXML
    private void handleAdicionarItem() {
        String nome = txtNomeItem.getText();
        String qtdStr = txtQtdItem.getText();
        String valorStr = txtValorUnitItem.getText();
        if (nome == null || nome.trim().isEmpty()) {
            lblFeedback.setText("Nome do item inválido.");
            return;
        }
        try {
            double qtd = Double.parseDouble(qtdStr.replace(',', '.'));
            double valor = Double.parseDouble(valorStr.replace(',', '.'));
            ItemOrcamento novo = new ItemOrcamento(nome, "un", qtd, valor);
            itensObservable.add(novo);
            if (projeto != null) projeto.getItensOrcamento().add(novo);
            txtNomeItem.clear(); txtQtdItem.clear(); txtValorUnitItem.clear();
            lblFeedback.setText("Item adicionado.");
            atualizarTotais();
        } catch (NumberFormatException e) {
            lblFeedback.setText("Quantidade ou valor inválido.");
        }
    }

    @FXML
    private void handleAtualizarOrcamento() {
        double totalMateriais = itensObservable.stream().mapToDouble(ItemOrcamento::getValorTotal).sum();
        double maoDeObra = 0.0;
        try {
            String v = txtValorMaoDeObra.getText();
            if (v == null || v.trim().isEmpty()) v = "0";
            maoDeObra = Double.parseDouble(v.replace(',', '.'));
        } catch (NumberFormatException e) {
            lblFeedback.setText("Valor de mão de obra inválido.");
            return;
        }
        double total = totalMateriais + maoDeObra;
        lblTotalMateriais.setText(moneyFormat.format(totalMateriais));
        lblOrcamentoTotal.setText(moneyFormat.format(total));
        lblFeedback.setText("Orçamento atualizado.");
        if (projeto != null) {
            projeto.setCustoEstimadoMateriais(totalMateriais);
            projeto.setCustoEstimadoMaoDeObra(maoDeObra);
        }
    }

    private void atualizarTotais() {
        double totalMateriais = itensObservable.stream().mapToDouble(ItemOrcamento::getValorTotal).sum();
        double maoDeObra = 0.0;
        try {
            String v = txtValorMaoDeObra.getText();
            if (v == null || v.trim().isEmpty()) v = "0";
            maoDeObra = Double.parseDouble(v.replace(',', '.'));
        } catch (Exception e) {
            maoDeObra = 0.0;
        }
        double total = totalMateriais + maoDeObra;
        if (lblTotalMateriais != null) lblTotalMateriais.setText(moneyFormat.format(totalMateriais));
        if (lblOrcamentoTotal != null) lblOrcamentoTotal.setText(moneyFormat.format(total));
    }

    @FXML
    private void handleAprovarProjeto() {
        if (projeto != null) {
            projeto.setStatus(ProjetoConstrucao.StatusProjeto.APROVADO);
            lblStatus.setText(projeto.getStatus().name());
            lblFeedback.setText("Projeto aprovado. Obrigado!");
        }
    }
}
