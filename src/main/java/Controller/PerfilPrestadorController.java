package Controller;

import Application.App;
import Model.Avaliacao;
import Model.Cliente;
import Model.Prestador;
import services.NavigationService;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PerfilPrestadorController {


    @FXML private Label nomePrestadorLabel;
    @FXML private Label categoriaPrestadorLabel;
    @FXML private Label descricaoPrestadorLabel;
    @FXML private Label enderecoPrestadorLabel;
    @FXML private Label telefonePrestadorLabel;
    @FXML private Label mediaAvaliacoesLabel;
    @FXML private ImageView fotoPerfilImageView;
    @FXML private ListView<Avaliacao> listViewAvaliacoes;
    @FXML private Button contratarButton;
    @FXML private Button chatButton;
    @FXML private ChoiceBox<Integer> ratingChoiceBox;
    @FXML private TextArea ratingCommentArea;
    @FXML private Button avaliarButton;
    @FXML private Label avaliarFeedbackLabel;

    private Prestador prestador;
    private NavigationService navigationService;

    @FXML
    public void initialize() {
        navigationService = App.getNavigationService();
        listViewAvaliacoes.setCellFactory(lv -> new AvaliacaoListCell());
        if (fotoPerfilImageView != null) {
            fotoPerfilImageView.getStyleClass().add("profile-image-large");
        }
        if (ratingChoiceBox != null) {
            ratingChoiceBox.setItems(FXCollections.observableArrayList(5,4,3,2,1));
            ratingChoiceBox.setValue(5);
        }
    }


    public void setPrestador(Prestador prestador) {
        this.prestador = prestador;
        if (prestador != null) {
            nomePrestadorLabel.setText(prestador.getName());
            categoriaPrestadorLabel.setText("Categoria: " + prestador.getCategoria());
            descricaoPrestadorLabel.setText("Serviço: " + prestador.getDescricaoServico());
            enderecoPrestadorLabel.setText("Endereço: " + prestador.getEndereco());
            telefonePrestadorLabel.setText("Telefone: " + prestador.getTelefone());

            double media = prestador.getAvaliacoes().stream()
                    .mapToInt(Avaliacao::getPontuacao)
                    .average()
                    .orElse(0.0);
            mediaAvaliacoesLabel.setText(String.format("Média de Avaliações: %.1f estrelas (%d avaliações)", media, prestador.getAvaliacoes().size()));

            if (prestador.getFotoPerfilPath() != null && !prestador.getFotoPerfilPath().isEmpty()) {
                try {
                    fotoPerfilImageView.setImage(Utils.ImageUtil.loadProfileImage("/user_photos/" + prestador.getFotoPerfilPath()));
                } catch (Exception e) {
                    fotoPerfilImageView.setImage(Utils.ImageUtil.loadProfileImage(null));
                }
            } else {
                 fotoPerfilImageView.setImage(Utils.ImageUtil.loadProfileImage(null));
            }

            ObservableList<Avaliacao> observableAvaliacoes = FXCollections.observableArrayList(prestador.getAvaliacoes());
            listViewAvaliacoes.setItems(observableAvaliacoes);

            if (App.getLoggedUser() != null && App.getLoggedUser().getId().equals(prestador.getId())) {
                contratarButton.setDisable(true);
                chatButton.setDisable(true);
                contratarButton.setText("Este é o seu perfil");
                if (avaliarButton != null) avaliarButton.setDisable(true);
            } else {
                contratarButton.setDisable(false);
                chatButton.setDisable(false);
                if (App.getLoggedUser() instanceof Model.Cliente) {
                    if (avaliarButton != null) avaliarButton.setDisable(false);
                } else {
                    if (avaliarButton != null) avaliarButton.setDisable(true);
                }
            }
        }
    }

    @FXML
    private void handleAvaliarPrestador() {
        if (!(App.getLoggedUser() instanceof Model.Cliente)) {
            if (avaliarFeedbackLabel != null) avaliarFeedbackLabel.setText("Apenas clientes podem avaliar.");
            return;
        }
        if (prestador == null) return;

        Integer nota = ratingChoiceBox != null ? ratingChoiceBox.getValue() : null;
        String comentario = ratingCommentArea != null ? ratingCommentArea.getText() : "";

        if (nota == null) nota = 5;

        Model.Cliente cliente = (Model.Cliente) App.getLoggedUser();
        Model.Avaliacao nova = new Model.Avaliacao(cliente, prestador, nota, comentario, java.time.LocalDateTime.now());
        prestador.addAvaliacao(nova);

        listViewAvaliacoes.getItems().add(0, nova);

        double media = prestador.getAvaliacoes().stream().mapToInt(Model.Avaliacao::getPontuacao).average().orElse(0.0);
        mediaAvaliacoesLabel.setText(String.format("Média de Avaliações: %.1f estrelas (%d avaliações)", media, prestador.getAvaliacoes().size()));

        if (avaliarFeedbackLabel != null) avaliarFeedbackLabel.setText("Avaliação enviada com sucesso.");
        if (ratingCommentArea != null) ratingCommentArea.clear();

        if (Utils.Database.isEnabled()) {
            try {
                dao.AvaliacaoDAO.insert(cliente.getId(), prestador.getId(), nova.getPontuacao(), nova.getComentario());
                System.out.println("Avaliação salva no banco de dados.");
            } catch (Exception ex) {
                System.err.println("Erro ao salvar avaliacao no banco: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleContratar() {
        if (prestador != null && App.getLoggedUser() instanceof Model.Cliente) {
            Cliente clienteLogado = (Cliente) App.getLoggedUser();

            Model.Contrato novoContrato = new Model.Contrato(
                    clienteLogado,
                    prestador,
                    prestador.getDescricaoServico(),
                    LocalDateTime.now(),
                    Model.Contrato.ContratoStatus.PENDENTE
            );

            App.addContrato(novoContrato);

            if (Utils.Database.isEnabled()) {
                try {
                    dao.ContratoDAO.insert(novoContrato);
                    System.out.println("Contrato salvo no banco de dados.");
                } catch (Exception ex) {
                    System.err.println("Erro ao salvar contrato no banco: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            navigationService.showPainelClienteScene();
        } else if (App.getLoggedUser() == null) {
            navigationService.showLoginScene();
        }
    }

    @FXML
    private void handleVoltar() throws IOException {
        navigationService.goBack();
    }

    @FXML
    private void handleIniciarChat() {
        if (prestador != null && App.getLoggedUser() != null && !App.getLoggedUser().getId().equals(prestador.getId())) {
            navigationService.showChatScene(prestador);
        }
    }

    private static class AvaliacaoListCell extends javafx.scene.control.ListCell<Avaliacao> {
        @Override
        protected void updateItem(Avaliacao avaliacao, boolean empty) {
            super.updateItem(avaliacao, empty);
            if (empty || avaliacao == null) {
                setGraphic(null);
                setText(null);
            } else {
                Label clienteLabel = new Label(avaliacao.getCliente() != null ? avaliacao.getCliente().getName() : "Cliente");
                clienteLabel.setStyle("-fx-font-weight: bold;");

                int pontos = Math.max(0, Math.min(5, avaliacao.getPontuacao()));
                StringBuilder stars = new StringBuilder();
                for (int i = 0; i < pontos; i++) stars.append("★");
                for (int i = pontos; i < 5; i++) stars.append("☆");
                Label starsLabel = new Label(stars.toString());
                starsLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 14px;");

                Label comentario = new Label(avaliacao.getComentario() != null ? avaliacao.getComentario() : "");
                comentario.setWrapText(true);
                comentario.setMaxWidth(600);

                Label dateLabel = new Label(avaliacao.getData() != null ? avaliacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
                dateLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

                HBox header = new HBox(8, clienteLabel, starsLabel);
                header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                VBox vbox = new VBox(4, header, comentario, dateLabel);
                vbox.setPadding(new Insets(6));
                vbox.getStyleClass().add("list-cell-card");

                setGraphic(vbox);
            }
        }
    }
}