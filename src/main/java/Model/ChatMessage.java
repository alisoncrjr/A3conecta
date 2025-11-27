package Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessage {
    private final String id;
    private final User sender;
    private final User receiver;
    private final String messageContent;
    private final LocalDateTime timestamp;

    public ChatMessage(User sender, User receiver, String messageContent) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", sender=" + sender.getName() +
                ", receiver=" + receiver.getName() +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}