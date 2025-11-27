package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Conversa {
    private String id;
    private User user1;
    private User user2;
    private List<Mensagem> mensagens;
    private LocalDateTime ultimaAtividade;

    public Conversa(User user1, User user2) {
        this.id = UUID.randomUUID().toString();
        this.user1 = user1;
        this.user2 = user2;
        this.mensagens = new ArrayList<>();
        this.ultimaAtividade = LocalDateTime.now();
    }

    public void setId(String id) { this.id = id; }

    // Getters
    public String getId() {
        return id;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }


    public List<Mensagem> getMensagens() {
        return Collections.unmodifiableList(mensagens);
    }

    public LocalDateTime getUltimaAtividade() {
        return ultimaAtividade;
    }


    public void addMensagem(Mensagem mensagem) {
        this.mensagens.add(mensagem);
        this.ultimaAtividade = mensagem.getTimestamp();
    }


    public User getOtherUser(User currentUser) {
        if (currentUser.equals(user1)) {
            return user2;
        } else if (currentUser.equals(user2)) {
            return user1;
        }
        return null;
    }
}