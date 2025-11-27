package Controller;

import Application.App;
import Model.Cliente;
import Model.Prestador;
import Model.User;
import services.NavigationService;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private javafx.scene.control.TextField passwordVisibleField;
    @FXML private javafx.scene.control.CheckBox chkShowPassword;
    @FXML private Label errorMessageLabel;

    private NavigationService navigationService;

    @FXML
    public void initialize() {
        navigationService = new NavigationService(App.getPrimaryStage());

        emailField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));

        if (passwordVisibleField != null && chkShowPassword != null && passwordField != null) {

            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

            passwordVisibleField.visibleProperty().bind(chkShowPassword.selectedProperty());
            passwordVisibleField.managedProperty().bind(chkShowPassword.selectedProperty());
            passwordField.visibleProperty().bind(chkShowPassword.selectedProperty().not());
            passwordField.managedProperty().bind(passwordField.visibleProperty());
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Por favor, preencha email e senha.");
            return;
        }


        String emailNormalized = email.trim().toLowerCase();
        String passwordNormalized = password.trim();

            User authenticatedUser = null;


            if (Utils.Database.isEnabled()) {
                try {
                    Optional<dao.UserRecord> recOpt = dao.UserDAO.findByEmail(emailNormalized);
                    if (recOpt.isPresent()) {
                        dao.UserRecord rec = recOpt.get();
                        String stored = rec.getPasswordHash() == null ? "" : rec.getPasswordHash();

                        if (Utils.PasswordUtil.verifyPassword(stored, passwordNormalized)) {

                            Model.User existing = Application.App.findUserById(rec.getId());
                            if (existing != null) {
                                authenticatedUser = existing;
                            } else {

                                if ("CLIENTE".equalsIgnoreCase(rec.getRole())) {
                                    Cliente c = new Cliente(rec.getName(), rec.getEmail(), stored, "");
                                    c.setFotoPerfilPath(rec.getFotoPerfilPath() == null ? "default_profile.png" : rec.getFotoPerfilPath());
                                    c.setId(rec.getId());
                                    authenticatedUser = c;
                                } else {
                                    Prestador p = new Prestador(rec.getName(), rec.getEmail(), stored, "");
                                    p.setFotoPerfilPath(rec.getFotoPerfilPath() == null ? "default_profile.png" : rec.getFotoPerfilPath());
                                    p.setId(rec.getId());
                                    authenticatedUser = p;
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    errorMessageLabel.setText("Erro ao conectar ao banco: " + e.getMessage());
                    return;
                }
            }


            if (authenticatedUser == null) {
                for (User user : App.getUsers()) {
                    if (user.getEmail() != null && user.getEmail().trim().toLowerCase().equals(emailNormalized)
                            && user.checkPassword(passwordNormalized)) {
                        authenticatedUser = user;
                        break;
                    }
                }
            }

            if (authenticatedUser != null) {
                App.setLoggedUser(authenticatedUser);
                errorMessageLabel.setText("");
                if (authenticatedUser instanceof Cliente) {
                    navigationService.showPainelClienteScene();
                } else if (authenticatedUser instanceof Prestador) {
                    navigationService.showPainelPrestadorScene();
                }
            } else {
                errorMessageLabel.setText("Email ou senha inválidos.");
            }
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        navigationService.showRegistroScene();
    }

    @FXML
    private void handleRecoverPassword(MouseEvent event) {

        errorMessageLabel.setText("Instruções de recuperação enviadas (simulação).");
    }
}