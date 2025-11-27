package Controller;

import Application.App;
import Model.Prestador;
import Model.Cliente;
import Model.User;
import services.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
 
import Utils.ImageUtil;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PainelClienteController {

    @FXML private Label labelNomeCliente;
    @FXML private ComboBox<String> comboCategorias;
    @FXML private TextField searchField;
    @FXML private ListView<Prestador> listViewPrestadores;
    @FXML private ImageView userProfileImageView;

    private NavigationService navigationService;
    private ObservableList<Prestador> observablePrestadores;
    private Cliente loggedCliente;

    @FXML
    public void initialize() {
        navigationService = App.getNavigationService();
        User currentUser = App.getLoggedUser();

        if (currentUser instanceof Cliente) {
            loggedCliente = (Cliente) currentUser;
            labelNomeCliente.setText("Olá, " + loggedCliente.getName() + "!");
            loadUserProfileImage(loggedCliente);
        } else {
            navigationService.showLoginScene();
            return;
        }

        popularCategorias();
        carregarPrestadores("Todas as Categorias");

        listViewPrestadores.setCellFactory(lv -> new PrestadorListCell());

        comboCategorias.valueProperty().addListener((obs, oldVal, newVal) -> {
            carregarPrestadores(newVal);
        });
    }

    private void loadUserProfileImage(User user) {
        String imagePath = user.getFotoPerfilPath();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/icons/default_profile.png";
        }
        String finalPath = imagePath.startsWith("/") ? imagePath : "/user_photos/" + imagePath;


        try {
            if (userProfileImageView != null) userProfileImageView.setImage(ImageUtil.loadProfileImage(finalPath));
        } catch (Exception ignored) {}
    }

    private void popularCategorias() {

        List<String> categoriasFormais = App.getPrestadores().stream()
            .map(Prestador::getCategoria)
            .filter(c -> c != null && !c.trim().isEmpty() && !"Outro".equalsIgnoreCase(c))
            .distinct()
            .collect(Collectors.toList());


        List<String> profissoes = App.getPrestadores().stream()
            .map(Prestador::getDescricaoServico)
            .filter(d -> d != null && !d.trim().isEmpty())
            .flatMap(d -> java.util.Arrays.stream(d.split("[,;]")))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .distinct()
            .collect(Collectors.toList());


        List<String> todas = categoriasFormais.stream().distinct().collect(Collectors.toList());
        for (String prof : profissoes) {
            boolean exists = todas.stream().anyMatch(t -> t.equalsIgnoreCase(prof));
            if (!exists) todas.add(prof);
        }
        todas.sort(String::compareToIgnoreCase);

        todas.add(0, "Todas as Categorias");
        comboCategorias.setItems(FXCollections.observableArrayList(todas));
        comboCategorias.getSelectionModel().selectFirst();
    }

    private void carregarPrestadores(String categoria) {
        List<Prestador> todos = App.getPrestadores();
        List<Prestador> prestadoresFiltrados;
        if (categoria == null || "Todas as Categorias".equals(categoria) || categoria.isEmpty()) {
            prestadoresFiltrados = todos;
        } else {
            String catLower = categoria.toLowerCase();
            prestadoresFiltrados = todos.stream()
                    .filter(p -> (p.getCategoria() != null && p.getCategoria().toLowerCase().equals(catLower))
                            || (p.getDescricaoServico() != null && p.getDescricaoServico().toLowerCase().equals(catLower)))
                    .collect(Collectors.toList());
        }
        observablePrestadores = FXCollections.observableArrayList(prestadoresFiltrados);
        listViewPrestadores.setItems(observablePrestadores);
    }

    @FXML
    private void handleSair() throws IOException {
        App.setLoggedUser(null);
        navigationService.showLoginScene();
    }

    @FXML
    private void handleBuscar(ActionEvent event) {
        String termo = (searchField != null) ? searchField.getText() : null;
        if (termo == null || termo.trim().isEmpty()) {

            String categoria = comboCategorias.getSelectionModel().getSelectedItem();
            carregarPrestadores(categoria);
            return;
        }
        String termoLower = termo.trim().toLowerCase();
        List<Prestador> filtrados = App.getPrestadores().stream()
                .filter(p -> p.getName().toLowerCase().contains(termoLower)
                        || p.getCategoria().toLowerCase().contains(termoLower)
                        || (p.getDescricaoServico() != null && p.getDescricaoServico().toLowerCase().contains(termoLower)))
                .collect(Collectors.toList());
        observablePrestadores = FXCollections.observableArrayList(filtrados);
        listViewPrestadores.setItems(observablePrestadores);
    }

    @FXML
    private void handleVerMeuPerfilCliente() {
        if (loggedCliente != null) {
            navigationService.showPerfilClienteScene(loggedCliente);
        }
    }

    @FXML
    private void handleGerenciarProjetos() throws IOException {
        navigationService.navigateTo("gerenciamento-projetos-view.fxml", "Gerenciamento de Projetos");
    }


    private class PrestadorListCell extends ListCell<Prestador> {
        private final HBox hBox = new HBox(15);
        private final ImageView imageView = new ImageView();
        private final Label nomeLabel = new Label();
        private final Label categoriaLabel = new Label();
        private final Label descricaoLabel = new Label();
        private final VBox textVBox = new VBox(5);
        private final Button chatButton = new Button("Conversar");
        private final Button verPerfilButton = new Button("Ver Perfil");

        public PrestadorListCell() {
            imageView.setFitWidth(60);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);
            imageView.getStyleClass().add("profile-image-small");

            nomeLabel.setFont(Font.font("System Bold", 16));
            nomeLabel.getStyleClass().add("list-item-title");
            categoriaLabel.setFont(Font.font("System", 13));
            categoriaLabel.getStyleClass().add("list-item-subtitle");
            descricaoLabel.setFont(Font.font("System", 12));
            descricaoLabel.getStyleClass().add("list-item-description");
            descricaoLabel.setWrapText(true);
            descricaoLabel.setMaxWidth(300);

            chatButton.getStyleClass().add("button-primary");
            verPerfilButton.getStyleClass().add("button-secondary");

            textVBox.getChildren().addAll(nomeLabel, categoriaLabel, descricaoLabel);
            hBox.getChildren().addAll(imageView, textVBox, verPerfilButton, chatButton);
            hBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(textVBox, javafx.scene.layout.Priority.ALWAYS);

            HBox.setMargin(verPerfilButton, new Insets(0, 10, 0, 20));
            HBox.setMargin(chatButton, new Insets(0, 10, 0, 0));

            chatButton.setOnAction(event -> {
                Prestador prestadorParaChat = getItem();
                if (prestadorParaChat != null) {
                    navigationService.showChatScene(prestadorParaChat);
                }
            });

            verPerfilButton.setOnAction(event -> {
                Prestador prestadorParaVer = getItem();
                if (prestadorParaVer != null) {
                    navigationService.showPerfilPrestadorScene(prestadorParaVer);
                }
            });
        }

        @Override
        protected void updateItem(Prestador prestador, boolean empty) {
            super.updateItem(prestador, empty);
            if (empty || prestador == null) {
                setGraphic(null);
                setText(null);
            } else {
                nomeLabel.setText(prestador.getName());
                String cat = prestador.getCategoria();
                boolean hasCat = cat != null && !cat.trim().isEmpty() && !"Outro".equalsIgnoreCase(cat);
                categoriaLabel.setText(hasCat ? cat : (prestador.getDescricaoServico() != null ? prestador.getDescricaoServico() : ""));
                descricaoLabel.setText(prestador.getDescricaoServico());

                String imagePath = prestador.getFotoPerfilPath();
                if (imagePath == null || imagePath.isEmpty()) {
                    imagePath = "/icons/default_profile.png";
                }
                String finalPath = imagePath.startsWith("/") ? imagePath : "/user_photos/" + imagePath;

                try {
                    imageView.setImage(ImageUtil.loadProfileImage(finalPath));
                } catch (Exception ignored) {}
                setGraphic(hBox);
                getStyleClass().add("list-cell-card");
            }
        }
    }
}