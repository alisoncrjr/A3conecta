package Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Mensagem {
    private String id;
    private User remetente;
    private User destinatario;
    private String conteudo;
    private LocalDateTime timestamp;
    private boolean lida;

    public Mensagem(User remetente, User destinatario, String conteudo) {
        this.id = UUID.randomUUID().toString();
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
        this.timestamp = LocalDateTime.now();
        this.lida = false;
    }

    // Getters
    public String getId() {
        return id;
    }

    public User getRemetente() {
        return remetente;
    }

    public User getDestinatario() {
        return destinatario;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isLida() {
        return lida;
    }

    // Setter
    public void setLida(boolean lida) {
        this.lida = lida;
    }


    public void setId(String id) { this.id = id; }
    public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }
}