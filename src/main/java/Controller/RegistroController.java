package Controller;

import Application.App;
import Model.Cliente;
import Model.Prestador;
import Model.User;
import Utils.ValidationUtil;
import services.NavigationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class RegistroController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> userTypeChoiceBox;
    @FXML private TextField cpfCnpjField;
    @FXML private Label cpfCnpjLabel;
    @FXML private Label errorMessageLabel;

    private NavigationService navigationService;

    @FXML
    public void initialize() {
        navigationService = new NavigationService(App.getPrimaryStage());
        userTypeChoiceBox.getItems().addAll("Cliente", "Prestador");
        userTypeChoiceBox.setValue("Cliente");

        userTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Cliente".equals(newVal)) {
                cpfCnpjLabel.setText("CPF:");
                cpfCnpjField.setPromptText("Digite seu CPF");
            } else {
                cpfCnpjLabel.setText("CNPJ:");
                cpfCnpjField.setPromptText("Digite seu CNPJ");
            }
            errorMessageLabel.setText("");
            cpfCnpjField.setText("");
        });

        cpfCnpjLabel.setText("CPF:");
        cpfCnpjField.setPromptText("Digite seu CPF");

        nameField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        emailField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        cpfCnpjField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        userTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
    }

    @FXML
    private void handleProximo(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String userType = userTypeChoiceBox.getValue();
        String cpfCnpj = cpfCnpjField.getText().replaceAll("[^0-9]", "");


        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || userType == null || cpfCnpj.isEmpty()) {
            errorMessageLabel.setText("Por favor, preencha todos os campos.");
            return;
        }


        if (!ValidationUtil.validarEmail(email)) {
            errorMessageLabel.setText("Por favor, insira um e-mail válido.");
            return;
        }

        String passwordError = ValidationUtil.validarSenha(password);
        if (passwordError != null) {
            errorMessageLabel.setText(passwordError);
            return;
        }

        if ("Cliente".equals(userType)) {
            if (!ValidationUtil.validarCPF(cpfCnpj)) {
                errorMessageLabel.setText("CPF inválido. Verifique os números.");
                return;
            }
        } else {
            if (!ValidationUtil.validarCNPJ(cpfCnpj)) {
                errorMessageLabel.setText("CNPJ inválido. Verifique os números.");
                return;
            }
        }

        for (User user : App.getUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                errorMessageLabel.setText("Este e-mail já está cadastrado.");
                return;
            }
            if (user instanceof Cliente && ((Cliente) user).getCpf().equals(cpfCnpj)) {
                errorMessageLabel.setText("Este CPF já está cadastrado.");
                return;
            }
            if (user instanceof Prestador && ((Prestador) user).getCnpj().equals(cpfCnpj)) {
                errorMessageLabel.setText("Este CNPJ já está cadastrado.");
                return;
            }
        }

        User tempUser;
        if ("Cliente".equals(userType)) {
            tempUser = new Cliente(name, email, password, cpfCnpj, "", "");
        } else {
            tempUser = new Prestador(name, email, password, cpfCnpj, "", "", "", "", "default_profile.png");
        }
        App.setLoggedUser(tempUser);

        if ("Cliente".equals(userType)) {
            navigationService.showRegistroPerfilClienteScene();
        } else {
            navigationService.showRegistroPerfilPrestadorScene();
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) throws IOException {
        App.setLoggedUser(null);
        navigationService.showLoginScene();
    }
}