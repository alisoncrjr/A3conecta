package Controller;

import Application.App;
import Model.Cliente;
import services.NavigationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegistroPerfilClienteController {

    @FXML private TextField enderecoField;
    @FXML private TextField telefoneField;
    @FXML private Label errorMessageLabel;

    private NavigationService navigationService;

    @FXML
    public void initialize() {
        navigationService = new NavigationService(App.getPrimaryStage());
        if (App.getLoggedUser() instanceof Cliente) {
            Cliente cliente = (Cliente) App.getLoggedUser();
            enderecoField.setText(cliente.getEndereco());
            telefoneField.setText(cliente.getTelefone());
        }

        enderecoField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        telefoneField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
    }

    @FXML
    private void handleFinalizarRegistro(ActionEvent event) throws IOException {
        String endereco = enderecoField.getText();
        String telefone = telefoneField.getText();

        if (endereco.isEmpty() || telefone.isEmpty()) {
            errorMessageLabel.setText("Por favor, preencha todos os campos.");
            return;
        }

        if (!telefone.matches("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$")) {
            errorMessageLabel.setText("Telefone inválido. Use (XX) XXXXX-XXXX ou similar.");
            return;
        }


        if (App.getLoggedUser() instanceof Cliente) {
            Cliente novoCliente = (Cliente) App.getLoggedUser();
            novoCliente.setEndereco(endereco);
            novoCliente.setTelefone(telefone);
            novoCliente.setFotoPerfilPath("default_profile.png");

            App.addUser(novoCliente);

            if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                try {
                    dao.UserDAO.insertUser(novoCliente);
                } catch (Exception ex) {
                    System.err.println("Failed to persist new cliente: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            navigationService.showPainelClienteScene();
        } else {
            errorMessageLabel.setText("Erro: Usuário não é um Cliente.");
            App.setLoggedUser(null);
            navigationService.showLoginScene();
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) throws IOException {
        App.setLoggedUser(null);
        navigationService.showRegistroScene();
    }
}