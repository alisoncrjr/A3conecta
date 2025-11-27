package Controller;

import Application.App;
import Model.Conversa;
import Model.Mensagem;
import Model.User;
import services.ChatService;
import services.NavigationService;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.ImageView;
import Utils.ImageUtil;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ChatController {

    @FXML private Label chatPartnerNameLabel;
    @FXML private ListView<Mensagem> chatMessagesListView;
    @FXML private TextField messageInputField;
    @FXML private Button sendMessageButton;
    @FXML private Button backButton;
    @FXML private ImageView chatPartnerImageView;

    private NavigationService navigationService;
    private ChatService chatService;
    private Conversa currentConversation;
    private User loggedUser;
    private User otherUser;

    @FXML
    public void initialize() {
        navigationService = App.getNavigationService();
        chatService = ChatService.getInstance();
        loggedUser = App.getLoggedUser();

        currentConversation = chatService.getConversaAtual();

        if (loggedUser == null || currentConversation == null) {
            System.err.println("Erro: Usuário não logado ou conversa não selecionada. Redirecionando para Login.");
            navigationService.showLoginScene();
            return;
        }

        otherUser = currentConversation.getOtherUser(loggedUser);
        chatPartnerNameLabel.setText(otherUser.getName());
        loadChatPartnerImage(otherUser);

        chatMessagesListView.setCellFactory(lv -> new MessageListCell());
        ObservableList<Mensagem> messages = chatService.getMensagensDaConversaAtual();
        chatMessagesListView.setItems(messages);


        messages.addListener((ListChangeListener<Mensagem>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    chatMessagesListView.scrollTo(messages.size() - 1);
                }
            }
        });

        if (!messages.isEmpty()) {
            chatMessagesListView.scrollTo(messages.size() - 1);
        }

        messageInputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSendMessage();
            }
        });

        sendMessageButton.getStyleClass().add("button-primary");
        backButton.getStyleClass().add("button-secondary");
        messageInputField.getStyleClass().add("text-field-custom");
        chatPartnerImageView.getStyleClass().add("profile-image-small");
    }

    private void loadChatPartnerImage(User user) {
        String imagePath = user.getFotoPerfilPath();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/icons/default_profile.png";
        }
        String finalPath = imagePath.startsWith("/") ? imagePath : "/user_photos/" + imagePath;

        try {
            chatPartnerImageView.setImage(ImageUtil.loadProfileImage(finalPath));
        } catch (Exception ignored) {}
    }

    @FXML
    private void handleSendMessage() {
        String messageContent = messageInputField.getText().trim();
        if (!messageContent.isEmpty()) {
            chatService.enviarMensagem(loggedUser, otherUser, messageContent);

            ObservableList<Mensagem> updated = chatService.getMensagensDaConversaAtual();
            chatMessagesListView.setItems(updated);
            if (!updated.isEmpty()) chatMessagesListView.scrollTo(updated.size() - 1);
            messageInputField.clear();
        }
    }

    @FXML
    private void handleBack() throws IOException {
        navigationService.goBack();
    }


    private class MessageListCell extends ListCell<Mensagem> {
        private final HBox messageContainer = new HBox();
        private final VBox textContentBox = new VBox(2);
        private final TextFlow messageBubble = new TextFlow();
        private final Label senderNameTimeLabel = new Label();
        private final Text messageText = new Text();
        private final ImageView profileImage = new ImageView();

        public MessageListCell() {
            senderNameTimeLabel.setFont(Font.font("System", 10));
            senderNameTimeLabel.setTextFill(Color.GRAY);
            messageText.setFont(Font.font("System", 12));
            messageBubble.setPadding(new Insets(8, 10, 8, 10));
            messageBubble.setMaxWidth(300);

            profileImage.setFitWidth(30);
            profileImage.setFitHeight(30);
            profileImage.setPreserveRatio(true);
            profileImage.getStyleClass().add("chat-profile-image");

            textContentBox.getChildren().addAll(senderNameTimeLabel, messageBubble);
            messageContainer.setPadding(new Insets(5, 0, 5, 0));
        }

        @Override
        protected void updateItem(Mensagem mensagem, boolean empty) {
            super.updateItem(mensagem, empty);
            if (empty || mensagem == null) {
                setGraphic(null);
            } else {
                messageBubble.getChildren().clear();
                messageContainer.getChildren().clear();

                String senderName = mensagem.getRemetente().getName();
                String time = mensagem.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm"));

                senderNameTimeLabel.setText(senderName + " - " + time);
                messageText.setText(mensagem.getConteudo());
                messageBubble.getChildren().add(messageText);


                String imagePath = mensagem.getRemetente().getFotoPerfilPath();
                if (imagePath == null || imagePath.isEmpty()) imagePath = "/icons/default_profile.png";
                String finalPath = imagePath.startsWith("/") ? imagePath : "/user_photos/" + imagePath;
                try {
                    profileImage.setImage(ImageUtil.loadProfileImage(finalPath));
                } catch (Exception ignored) {}

                if (mensagem.getRemetente().equals(loggedUser)) {
                    messageContainer.setAlignment(Pos.CENTER_RIGHT);
                    messageBubble.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 15;");
                    messageText.setFill(Color.BLACK);
                    messageContainer.getChildren().addAll(textContentBox, profileImage);
                } else {
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                    messageBubble.setStyle("-fx-background-color: #E8E8E8; -fx-background-radius: 15;");
                    messageText.setFill(Color.BLACK);
                    messageContainer.getChildren().addAll(profileImage, textContentBox);
                }
                HBox.setMargin(profileImage, new Insets(0, 5, 0, 5));

                setGraphic(messageContainer);
            }
        }
    }
}