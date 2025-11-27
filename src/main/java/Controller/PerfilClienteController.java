package Controller;

import Application.App;
import Model.Cliente;
import services.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import Utils.ImageUtil;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
 



public class PerfilClienteController {

    @FXML private Label nomeClienteLabel;
    @FXML private Label emailClienteLabel;
    @FXML private Label enderecoClienteLabel;
    @FXML private Label telefoneClienteLabel;
    @FXML private ImageView fotoPerfilImageView;

    @FXML private Button voltarButton;
    @FXML private Label lblProjetosCount;
    @FXML private Label lblContratosCount;

    private Cliente cliente;
    private NavigationService navigationService;

    @FXML
    public void initialize() {
        navigationService = App.getNavigationService();
        if (fotoPerfilImageView != null) {
            fotoPerfilImageView.getStyleClass().add("profile-image-large");
        }
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            nomeClienteLabel.setText(cliente.getName());
            emailClienteLabel.setText("Email: " + cliente.getEmail());
            enderecoClienteLabel.setText("Endereço: " + cliente.getEndereco());
            telefoneClienteLabel.setText("Telefone: " + cliente.getTelefone());

            try {
                fotoPerfilImageView.setImage(ImageUtil.loadProfileImage("/user_photos/" + cliente.getFotoPerfilPath()));
            } catch (Exception ignored) {}

            if (lblProjetosCount != null) {
                long projetos = App.getProjetosConstrucaoByCliente(cliente).size();
                lblProjetosCount.setText(String.valueOf(projetos));
            }
            if (lblContratosCount != null) {
                long contratos = App.getContratos().stream().filter(c -> c.getCliente() != null && c.getCliente().getId().equals(cliente.getId())).count();
                lblContratosCount.setText(String.valueOf(contratos));
            }
        }
    }

    @FXML
    private void handleVerMeusProjetos() {
        try {
            navigationService.navigateTo("gerenciamento-projetos-view.fxml", "Meus Projetos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVerMeusContratos() {
        if (this.cliente != null) {

            services.NavigationService nav = App.getNavigationService();
            nav.showMeusContratosDialog(this.cliente);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nenhum cliente definido para exibir contratos.", ButtonType.OK);
            alert.setHeaderText("Meus Contratos");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleVoltar() throws IOException {
        navigationService.goBack();
    }
}