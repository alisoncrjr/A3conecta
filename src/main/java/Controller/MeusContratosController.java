package Controller;

import Application.App;
import Model.Cliente;
import Model.Contrato;
import services.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.io.IOException;

public class MeusContratosController {

    @FXML private ListView<Contrato> listViewContratos;

    private NavigationService navigationService;
    private Cliente cliente;

    @FXML
    public void initialize() {
        navigationService = App.getNavigationService();
        listViewContratos.setCellFactory(lv -> new ListCell<Contrato>() {
            @Override
            protected void updateItem(Contrato item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String prestadorName = item.getPrestador() != null ? item.getPrestador().getName() : "N/A";
                    setText(String.format("%s - %s (%s)", item.getDescricaoServico(), prestadorName, item.getStatus().toString()));
                }
            }
        });
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        carregarContratosDoCliente();
    }

    private void carregarContratosDoCliente() {
        if (cliente == null) return;
        ObservableList<Contrato> itens = FXCollections.observableArrayList(
                App.getContratos().stream().filter(c -> c.getCliente() != null && c.getCliente().getId().equals(cliente.getId())).toList()
        );
        listViewContratos.setItems(itens);
    }

    @FXML
    private void handleVerDetalhes() {
        Contrato sel = listViewContratos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Selecione um contrato para ver detalhes.", ButtonType.OK);
            a.setHeaderText("Nenhum contrato selecionado");
            a.showAndWait();
            return;
        }
        navigationService.showContratoDetalhes(sel);
    }

    @FXML
    private void handleFechar() {

        if (listViewContratos != null && listViewContratos.getScene() != null && listViewContratos.getScene().getWindow() != null) {
            listViewContratos.getScene().getWindow().hide();
        } else {
            try {
                navigationService.goBack();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
