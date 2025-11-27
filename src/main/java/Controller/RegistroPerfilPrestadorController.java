package Controller;

import Application.App;
import Model.Prestador;
import services.NavigationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RegistroPerfilPrestadorController {

    @FXML private TextField descricaoServicoField;
    @FXML private TextField enderecoField;
    @FXML private TextField telefoneField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private Label errorMessageLabel;

    private NavigationService navigationService;

    @FXML
    public void initialize() {
        navigationService = new NavigationService(App.getPrimaryStage());
        List<String> categorias = Arrays.asList(
                "Encanador", "Eletricista", "Pintor", "Jardineiro",
                "Arquiteto", "Pedreiro", "Diarista", "Vidraceiro",
                "Designer", "Engenheiro", "Outro"
        );
        categoriaComboBox.getItems().addAll(categorias);
        categoriaComboBox.setValue("Outro");


        if (App.getLoggedUser() instanceof Prestador) {
            Prestador prestador = (Prestador) App.getLoggedUser();
            descricaoServicoField.setText(prestador.getDescricaoServico() != null ? prestador.getDescricaoServico() : "");
            enderecoField.setText(prestador.getEndereco() != null ? prestador.getEndereco() : "");
            telefoneField.setText(prestador.getTelefone() != null ? prestador.getTelefone() : "");
            if (prestador.getCategoria() != null && categorias.contains(prestador.getCategoria())) {
                categoriaComboBox.setValue(prestador.getCategoria());
            } else if (prestador.getCategoria() != null && !prestador.getCategoria().isEmpty()) {

                categoriaComboBox.getItems().add(prestador.getCategoria());
                categoriaComboBox.setValue(prestador.getCategoria());
            }
        }

        descricaoServicoField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        enderecoField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        telefoneField.textProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
        categoriaComboBox.valueProperty().addListener((obs, oldVal, newVal) -> errorMessageLabel.setText(""));
    }

    @FXML
    private void handleFinalizarRegistro(ActionEvent event) throws IOException {
        String descricaoServico = descricaoServicoField.getText();
        String endereco = enderecoField.getText();
        String telefone = telefoneField.getText();
        String categoria = categoriaComboBox.getValue();

        if (descricaoServico.isEmpty() || endereco.isEmpty() || telefone.isEmpty() || categoria == null || categoria.isEmpty()) {
            errorMessageLabel.setText("Por favor, preencha todos os campos.");
            return;
        }

        if (!telefone.matches("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$")) {
            errorMessageLabel.setText("Telefone inválido. Use (XX) XXXXX-XXXX ou similar.");
            return;
        }

        if (App.getLoggedUser() instanceof Prestador) {
            Prestador novoPrestador = (Prestador) App.getLoggedUser();
            novoPrestador.setDescricaoServico(descricaoServico);
            novoPrestador.setEndereco(endereco);
            novoPrestador.setTelefone(telefone);
            novoPrestador.setCategoria(categoria);
            novoPrestador.setFotoPerfilPath("default_profile.png");

            App.addUser(novoPrestador);
            if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                try {
                    dao.UserDAO.insertUser(novoPrestador);
                } catch (Exception ex) {
                    System.err.println("Failed to persist new prestador: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            navigationService.showPainelPrestadorScene();
        } else {
            errorMessageLabel.setText("Erro: Usuário não é um Prestador. Redirecionando para registro.");
            App.setLoggedUser(null);
            navigationService.showRegistroScene();
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) throws IOException {
        App.setLoggedUser(null);
        navigationService.showRegistroScene();
    }
}