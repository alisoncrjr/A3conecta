package Controller;

import Application.App;
import Model.Contrato;
import Model.ItemOrcamento;
import Model.ProjetoConstrucao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.Optional;
import services.NavigationService;

public class ContratoDetalhesController {

    @FXML private Label lblTitulo;
    @FXML private Label lblDescricao;
    @FXML private Label lblStatus;
    @FXML private TableView<ItemOrcamento> tableViewItens;
    @FXML private TableColumn<ItemOrcamento, String> colNome;
    @FXML private TableColumn<ItemOrcamento, Double> colQtd;
    @FXML private TableColumn<ItemOrcamento, Double> colValorUnit;
    @FXML private TableColumn<ItemOrcamento, Double> colValorTotal;
    @FXML private TextArea txtObservacoes;

    private Contrato contrato;
    private ProjetoConstrucao projeto;
    private ObservableList<ItemOrcamento> itensObservable;
    private NavigationService navigationService;

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
        navigationService = App.getNavigationService();
        lblTitulo.setText("Contrato: " + (contrato.getId() != null ? contrato.getId().substring(0,6) : "-"));
        lblDescricao.setText(contrato.getDescricaoServico());
        lblStatus.setText("Status: " + contrato.getStatus().toString());
        txtObservacoes.setText(contrato.getDescricaoServico());


        if (contrato.getProjetoId() != null) {
            projeto = App.getProjetosConstrucao().stream().filter(p -> contrato.getProjetoId().equals(p.getId())).findFirst().orElse(null);
        }

        if (projeto == null) {
            projeto = App.getProjetosConstrucao().stream()
                    .filter(p -> p.getCliente() != null && contrato.getCliente() != null && p.getCliente().getId().equals(contrato.getCliente().getId()))
                    .filter(p -> p.getPrestador() != null && contrato.getPrestador() != null && p.getPrestador().getId().equals(contrato.getPrestador().getId()))
                    .findFirst().orElse(null);
        }

        if (projeto != null) {

            java.util.List<ItemOrcamento> copiaItens = new java.util.ArrayList<>();
            for (ItemOrcamento i : projeto.getItensOrcamento()) {
                ItemOrcamento c = new ItemOrcamento(i.getNome(), i.getUnidade(), i.getQuantidade(), i.getValorUnitario());
                copiaItens.add(c);
            }
            itensObservable = FXCollections.observableArrayList(copiaItens);
            tableViewItens.setItems(itensObservable);
            setupColumns();
        }
    }

    private void setupColumns() {
        if (colNome != null) {
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colNome.setCellFactory(TextFieldTableCell.forTableColumn());
            colNome.setOnEditCommit(evt -> {
                ItemOrcamento item = evt.getRowValue();
                item.setNome(evt.getNewValue());
                tableViewItens.refresh();
            });
        }
        if (colQtd != null) {
            colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
            colQtd.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
            colQtd.setOnEditCommit(evt -> {
                ItemOrcamento item = evt.getRowValue();
                item.setQuantidade(evt.getNewValue());
                tableViewItens.refresh();
            });
        }
        if (colValorUnit != null) {
            colValorUnit.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
            colValorUnit.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
            colValorUnit.setOnEditCommit(evt -> {
                ItemOrcamento item = evt.getRowValue();
                item.setValorUnitario(evt.getNewValue());
                tableViewItens.refresh();
            });
        }
        if (colValorTotal != null) {
            colValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        }

        tableViewItens.setEditable(true);
    }

    @FXML
    private void handleSalvarRevisao() {
        if (projeto != null) {

            projeto.setObservacoesPrestador(txtObservacoes.getText());

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Revisão salva com sucesso.", ButtonType.OK);
            alert.setHeaderText("Salvo");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nenhum projeto vinculado a este contrato.", ButtonType.OK);
            alert.setHeaderText("Aviso");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleFechar() {

        try {

            if (navigationService != null) {
                try {
                    navigationService.goBack();
                    return;
                } catch (Exception ignored) {}
            }
            Stage stage = (Stage) lblTitulo.getScene().getWindow();
            stage.close();
        } catch (Exception ignored) {}
    }

    @FXML
    private void handleVoltarParaPainelPrestador() {
        try {
            if (App.getLoggedUser() != null && App.getLoggedUser() instanceof Model.Prestador) {
                App.getNavigationService().showPainelPrestadorScene();
            } else {

                App.getNavigationService().goBack();
            }
        } catch (Exception e) {

            try { Stage stage = (Stage) lblTitulo.getScene().getWindow(); stage.close(); } catch (Exception ignored) {}
        }
    }
}
