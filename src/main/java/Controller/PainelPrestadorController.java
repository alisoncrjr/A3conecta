package Controller;

import Application.App;
import Model.Contrato;
import Model.Prestador;
import Model.Cliente;
import Model.User;
import services.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import Model.Mensagem;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import Utils.ImageUtil;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.text.Font;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.List;
import java.util.stream.Collectors;


public class PainelPrestadorController {

    @FXML private Label labelNomePrestador;
    @FXML private Label lblPendentesCount;
    @FXML private ListView<Contrato> listViewContratos;
    @FXML private ListView<String> listViewMinhasAvaliacoes;
    @FXML private ImageView userProfileImageView;
    @FXML private TextField searchContractsField;
    @FXML private ListView<Object> listViewMensagensEnviadas;
    @FXML private VBox viewMensagensEnviadas;
    @FXML private VBox vboxContratos;
    @FXML private VBox vboxAvaliacoes;
    @FXML private javafx.scene.control.Button btnMensagensToggle;
    @FXML private javafx.scene.control.Button btnExcluirContrato;

    
    private static class SenderSummary {
        final Model.User sender;
        final Model.Mensagem lastMessage;
        final int unreadCount;
        SenderSummary(Model.User sender, Model.Mensagem lastMessage, int unreadCount) {
            this.sender = sender; this.lastMessage = lastMessage; this.unreadCount = unreadCount;
        }
    }

    private NavigationService navigationService;
    private Prestador loggedPrestador;

    @FXML
    public void initialize() {
        navigationService = App.getNavigationService();
        User currentUser = App.getLoggedUser();
        if (currentUser instanceof Prestador) {
            loggedPrestador = (Prestador) currentUser;
            labelNomePrestador.setText("Olá, " + loggedPrestador.getName() + "!");
            loadUserProfileImage(loggedPrestador);
            carregarContratos();
            carregarAvaliacoes();

            if (listViewMensagensEnviadas != null) {
                listViewMensagensEnviadas.setCellFactory(lv -> new ListCell<Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) { setText(null); setGraphic(null); return; }
                        SenderSummary s = (SenderSummary) item;
                        Model.Mensagem msg = s.lastMessage;
                        String remetente = s.sender != null ? s.sender.getName() : "Desconhecido";
                        String snippet = msg == null ? "" : msg.getConteudo();
                        String ts = msg == null || msg.getTimestamp() == null ? "" : msg.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        javafx.scene.layout.HBox root = new javafx.scene.layout.HBox(10);
                        javafx.scene.layout.VBox left = new javafx.scene.layout.VBox(2);
                        javafx.scene.control.Label top = new javafx.scene.control.Label(remetente + "    " + ts);
                        top.setStyle("-fx-font-weight: bold;");
                        javafx.scene.control.Label body = new javafx.scene.control.Label(snippet);
                        body.setWrapText(true);
                        left.getChildren().addAll(top, body);
                        javafx.scene.control.Label badge = new javafx.scene.control.Label();
                        badge.setStyle("-fx-background-color: #ff4b4b; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 12;");
                        if (s.unreadCount > 0) badge.setText(String.valueOf(s.unreadCount)); else { badge.setText(""); badge.setVisible(false); }
                        root.getChildren().addAll(left, badge);
                        setGraphic(root);
                    }
                });

                listViewMensagensEnviadas.setOnMouseClicked((MouseEvent ev) -> {
                    if (ev.getClickCount() == 2) {
                        SenderSummary selected = (SenderSummary) listViewMensagensEnviadas.getSelectionModel().getSelectedItem();
                        if (selected != null && selected.sender != null) {
                            services.ChatService.getInstance().markMessagesAsRead(loggedPrestador, selected.sender);
                            Platform.runLater(() -> carregarMensagensEnviadas());
                            navigationService.showChatScene(selected.sender);
                        }
                    }
                });
            }
        } else {
            navigationService.showLoginScene();
            return;
        }

        listViewContratos.setCellFactory(lv -> new ContratoListCell());

        listViewContratos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (btnExcluirContrato != null) {
                boolean show = newSel != null && newSel.getStatus() == Contrato.ContratoStatus.FINALIZADO;
                btnExcluirContrato.setVisible(show);
                btnExcluirContrato.setManaged(show);
            }
        });

        if (btnMensagensToggle != null) btnMensagensToggle.setText("Mensagens Enviadas");
        services.ChatService.getInstance().addMessageListener(() -> Platform.runLater(() -> {
            try {
                if (viewMensagensEnviadas != null && viewMensagensEnviadas.isVisible()) carregarMensagensEnviadas();
                carregarContratos();
            } catch (Exception ignored) {}
        }));
    }

    @FXML
    private void handleMostrarMensagensEnviadas() {
        if (viewMensagensEnviadas == null) return;
        boolean currentlyVisible = viewMensagensEnviadas.isVisible();
        if (currentlyVisible) {
            handleVoltarMensagens();
            if (btnMensagensToggle != null) btnMensagensToggle.setText("Mensagens Enviadas");
        } else {
            if (vboxContratos != null) { vboxContratos.setVisible(false); vboxContratos.setManaged(false); }
            if (vboxAvaliacoes != null) { vboxAvaliacoes.setVisible(false); vboxAvaliacoes.setManaged(false); }
            viewMensagensEnviadas.setVisible(true);
            viewMensagensEnviadas.setManaged(true);
            carregarMensagensEnviadas();
            if (btnMensagensToggle != null) btnMensagensToggle.setText("Fechar Mensagens");
        }
    }

    private void carregarMensagensEnviadas() {
        if (loggedPrestador == null) return;
        List<Model.Conversa> conversas = services.ChatService.getInstance().getTodasConversasParaUsuario(loggedPrestador);
        java.util.Map<String, SenderSummary> map = new java.util.HashMap<>();
        for (Model.Conversa c : conversas) {
            Model.User other = c.getOtherUser(loggedPrestador);
            if (other == null) continue;
            int unread = 0;
            Model.Mensagem last = null;
            for (Mensagem m : c.getMensagens()) {
                if (m.getDestinatario() != null && m.getDestinatario().getId().equals(loggedPrestador.getId())) {
                    if (!m.isLida()) unread++;
                }
                if (last == null || (m.getTimestamp() != null && m.getTimestamp().isAfter(last.getTimestamp()))) last = m;
            }
            map.put(other.getId(), new SenderSummary(other, last, unread + (map.containsKey(other.getId()) ? map.get(other.getId()).unreadCount : 0)));
        }
        java.util.List<SenderSummary> summaries = new java.util.ArrayList<>(map.values());
        summaries.sort((a,b) -> {
            if (a.lastMessage == null && b.lastMessage == null) return 0;
            if (a.lastMessage == null) return 1;
            if (b.lastMessage == null) return -1;
            return b.lastMessage.getTimestamp().compareTo(a.lastMessage.getTimestamp());
        });
        if (listViewMensagensEnviadas != null) {
            listViewMensagensEnviadas.setItems(FXCollections.observableArrayList(summaries));
        }
    }

    @FXML
    private void handleVoltarMensagens() {
        if (viewMensagensEnviadas != null) {
            viewMensagensEnviadas.setVisible(false);
            viewMensagensEnviadas.setManaged(false);
            if (vboxContratos != null) { vboxContratos.setVisible(true); vboxContratos.setManaged(true); }
            if (vboxAvaliacoes != null) { vboxAvaliacoes.setVisible(true); vboxAvaliacoes.setManaged(true); }
        }
    }

    private void loadUserProfileImage(User user) {
        String imagePath = user.getFotoPerfilPath();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/icons/default_profile.png";
        }
        String finalPath = imagePath.startsWith("/") ? imagePath : "/user_photos/" + imagePath;

        try {
            if (userProfileImageView != null) {
                userProfileImageView.setImage(ImageUtil.loadProfileImage(finalPath));
            }
        } catch (Exception ignored) {}
    }

    private void carregarContratos() {
        List<Contrato> contratosDoPrestador = App.getContratos().stream()
                .filter(c -> c.getPrestador() != null && c.getPrestador().getId().equals(loggedPrestador.getId()))
                .collect(Collectors.toList());
        ObservableList<Contrato> observableContratos = FXCollections.observableArrayList(contratosDoPrestador);
        listViewContratos.setItems(observableContratos);
        if (lblPendentesCount != null) {
            long pendentes = contratosDoPrestador.stream().filter(c -> c.getStatus() == Contrato.ContratoStatus.PENDENTE).count();
            lblPendentesCount.setText(String.valueOf(pendentes));
            lblPendentesCount.getStyleClass().removeAll("badge");
            if (pendentes > 0) lblPendentesCount.getStyleClass().add("badge");
        }
    }

    private void carregarAvaliacoes() {
        if (loggedPrestador != null && loggedPrestador.getAvaliacoes() != null) {
            ObservableList<String> avaliacoesStrings = FXCollections.observableArrayList();
            if (loggedPrestador.getAvaliacoes().isEmpty()) {
                avaliacoesStrings.add("Nenhuma avaliação recebida ainda.");
            } else {
                for (Model.Avaliacao avaliacao : loggedPrestador.getAvaliacoes()) {
                    String avaliacaoText = String.format("Cliente: %s\nNota: %d estrelas\nComentário: %s\nData: %s",
                            avaliacao.getCliente().getName(),
                            avaliacao.getPontuacao(),
                            avaliacao.getComentario(),
                            avaliacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    avaliacoesStrings.add(avaliacaoText);
                }
            }
            listViewMinhasAvaliacoes.setItems(avaliacoesStrings);
        }
    }

    @FXML
    private void handleVerMeuPerfil() {
        if (loggedPrestador != null) {
            navigationService.showPerfilPrestadorScene(loggedPrestador);
        }
    }

    @FXML
    private void handleSair() throws IOException {
        App.setLoggedUser(null);
        navigationService.showLoginScene();
    }

    @FXML
    private void handleChatComClienteDoContrato(ActionEvent event) {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado != null) {
            User clienteDoContrato = contratoSelecionado.getCliente();
            if (clienteDoContrato != null) {
                navigationService.showChatScene(clienteDoContrato);
            } else {
                System.out.println("Cliente do contrato não encontrado.");
            }
        } else {
            System.out.println("Por favor, selecione um contrato para iniciar o chat.");
        }
    }

    @FXML
    private void handleVerItensContrato(ActionEvent event) {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado != null) {
            navigationService.showContratoDetalhes(contratoSelecionado);
        } else {
            System.out.println("Selecione um contrato para ver detalhes.");
        }
    }

    @FXML
    private void handleExcluirContrato() {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado == null) {
            System.out.println("Selecione um contrato para excluir.");
            return;
        }
        if (contratoSelecionado.getStatus() != Contrato.ContratoStatus.FINALIZADO) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Apenas contratos finalizados podem ser excluídos.", ButtonType.OK);
            a.setHeaderText("Atenção"); a.showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir este contrato? Esta ação não pode ser desfeita.", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmar exclusão");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    App.getContratos().removeIf(c -> c.getId().equals(contratoSelecionado.getId()));
                } catch (Exception ignored) {}
                if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                    try { dao.ContratoDAO.delete(contratoSelecionado.getId()); } catch (Exception ex) { System.err.println("Failed to delete contrato: " + ex.getMessage()); }
                }
                carregarContratos();
                services.ChatService.getInstance().notifyListeners();
            }
        });
    }

    @FXML
    private void handleVerProjetoDoContrato(ActionEvent event) {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado == null) {
            System.out.println("Selecione um contrato primeiro.");
            return;
        }
        String projetoId = contratoSelecionado.getProjetoId();
        if (projetoId == null || projetoId.isEmpty()) {
            System.out.println("Contrato não está vinculado a um projeto.");
            return;
        }
        navigationService.showGerenciamentoProjetosScene(projetoId);
    }

    @FXML
    private void handleVerPerfilDoCliente(ActionEvent event) {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado != null) {
            Cliente clienteDoContrato = contratoSelecionado.getCliente();
            if (clienteDoContrato != null) {
                navigationService.showPerfilClienteScene(clienteDoContrato);
            } else {
                System.out.println("Cliente do contrato não encontrado.");
            }
        } else {
            System.out.println("Por favor, selecione um contrato para ver o perfil do cliente.");
        }
    }

    @FXML
    private void handleGerenciarProjetos() throws IOException {
        navigationService.navigateTo("gerenciamento-projetos-view.fxml", "Gerenciamento de Projetos");
    }

    @FXML
    private void handleVerDetalhesContrato() {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado != null) {
            navigationService.showContratoDetalhes(contratoSelecionado);
        } else {
            System.out.println("Selecione um contrato para ver detalhes.");
        }
    }

    @FXML
    private void handleMarcarConcluido() {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado == null) {
            System.out.println("Selecione um contrato primeiro.");
            return;
        }

        contratoSelecionado.setStatus(Contrato.ContratoStatus.FINALIZADO);
        App.getProjetosConstrucao().stream()
                .filter(p -> p.getCliente() != null && contratoSelecionado.getCliente() != null && p.getCliente().getId().equals(contratoSelecionado.getCliente().getId()))
                .filter(p -> p.getPrestador() != null && contratoSelecionado.getPrestador() != null && p.getPrestador().getId().equals(contratoSelecionado.getPrestador().getId()))
                .filter(p -> p.getStatus() == Model.ProjetoConstrucao.StatusProjeto.EM_ANDAMENTO || p.getStatus() == Model.ProjetoConstrucao.StatusProjeto.APROVADO)
                .findFirst()
                .ifPresent(p -> p.setStatus(Model.ProjetoConstrucao.StatusProjeto.CONCLUIDO));

        if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
            try {
                dao.ContratoDAO.updateStatus(contratoSelecionado.getId(), Contrato.ContratoStatus.FINALIZADO.name());
                services.ChatService.getInstance().notifyListeners();
            } catch (Exception ex) {
                System.err.println("Failed to update contrato status in DB: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        carregarContratos();
        services.ChatService.getInstance().notifyListeners();
        System.out.println("Contrato marcado como finalizado.");
    }

    @FXML
    private void handleAceitarContrato() {
        Contrato contratoSelecionado = listViewContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado == null) {
            System.out.println("Selecione um contrato para aceitar.");
            return;
        }

        if (contratoSelecionado.getStatus() != Contrato.ContratoStatus.PENDENTE) {
            System.out.println("Contrato não está pendente.");
            return;
        }

        contratoSelecionado.setStatus(Contrato.ContratoStatus.EM_ANDAMENTO);
        if (contratoSelecionado.getProjetoId() != null) {
            App.getProjetosConstrucao().stream()
                    .filter(p -> contratoSelecionado.getProjetoId().equals(p.getId()))
                    .findFirst()
                    .ifPresent(p -> p.setStatus(Model.ProjetoConstrucao.StatusProjeto.EM_ANDAMENTO));
        }
        if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
            try {
                dao.ContratoDAO.updateStatus(contratoSelecionado.getId(), Contrato.ContratoStatus.EM_ANDAMENTO.name());
                services.ChatService.getInstance().notifyListeners();
            } catch (Exception ex) {
                System.err.println("Failed to update contrato status in DB: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        carregarContratos();
        services.ChatService.getInstance().notifyListeners();
        System.out.println("Contrato aceito e em andamento.");
    }

    

    private class ContratoListCell extends ListCell<Contrato> {
        private final VBox content = new VBox(5);
        private final Label servicoLabel = new Label();
        private final Label clienteLabel = new Label();
        private final Label statusLabel = new Label();
        private final Label dataLabel = new Label();

        public ContratoListCell() {
            servicoLabel.setFont(Font.font("System Bold", 14));
            clienteLabel.setFont(Font.font("System", 12));
            statusLabel.setFont(Font.font("System Bold", 12));
            dataLabel.setFont(Font.font("System", 10));
            dataLabel.setStyle("-fx-text-fill: gray;");

            content.getChildren().addAll(servicoLabel, clienteLabel, statusLabel, dataLabel);
            content.setPadding(new Insets(8));
            content.getStyleClass().add("list-cell-card");
        }

        @Override
        protected void updateItem(Contrato contrato, boolean empty) {
            super.updateItem(contrato, empty);
            if (empty || contrato == null) {
                setGraphic(null);
                setText(null);
            } else {
                servicoLabel.setText("Serviço: " + contrato.getDescricaoServico());
                clienteLabel.setText("Cliente: " + (contrato.getCliente() != null ? contrato.getCliente().getName() : "N/A"));
                statusLabel.setText("Status: " + contrato.getStatus().toString().replace("_", " "));
                dataLabel.setText("Data: " + contrato.getDataContrato().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                switch (contrato.getStatus()) {
                    case PENDENTE:
                        statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        break;
                    case EM_ANDAMENTO:
                        statusLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                        break;
                    case FINALIZADO:
                        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        break;
                    case CANCELADO:
                        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        break;
                }
                setGraphic(content);
            }
        }
    }
}