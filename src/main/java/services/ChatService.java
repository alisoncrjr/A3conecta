package services;

import Model.Conversa;
import Model.Mensagem;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatService {

    private static ChatService instance;
    private List<Conversa> todasConversas;
    private Conversa conversaAtual;
    private final List<Runnable> messageListeners = new ArrayList<>();

    private ChatService() {
        this.todasConversas = new ArrayList<>();
    }

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public Optional<Conversa> encontrarConversa(User user1, User user2) {
        return todasConversas.stream()
                .filter(c -> (c.getUser1().equals(user1) && c.getUser2().equals(user2)) ||
                        (c.getUser1().equals(user2) && c.getUser2().equals(user1)))
                .findFirst();
    }


    public void carregarConversaDoBanco(String id, User user1, User user2, LocalDateTime ultimaAtividade) {

        if (encontrarConversa(user1, user2).isPresent()) return;

        Conversa c = new Conversa(user1, user2);
        c.setId(id);
        todasConversas.add(c);
    }

    public Conversa criarNovaConversa(User user1, User user2) {
        Conversa novaConversa = new Conversa(user1, user2);

        if (Utils.Database.isEnabled()) {
            try {
                dao.ConversaRecord rec = dao.ConversaDAO.insert(user1.getId(), user2.getId());
                novaConversa.setId(rec.getId());
            } catch (Exception e) {
                System.err.println("Failed to persist conversa: " + e.getMessage());
                e.printStackTrace();
            }
        }
        todasConversas.add(novaConversa);
        notifyMessageListeners();
        return novaConversa;
    }


    public Conversa selecionarConversaOuCriar(User user1, User user2) {
        this.conversaAtual = encontrarConversa(user1, user2)
                .orElseGet(() -> criarNovaConversa(user1, user2));
        return this.conversaAtual;
    }

    public Conversa getConversaAtual() {
        return conversaAtual;
    }

    public ObservableList<Mensagem> getMensagensDaConversaAtual() {
        if (conversaAtual != null) {

            return FXCollections.observableArrayList(
                    conversaAtual.getMensagens().stream()
                            .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                            .collect(java.util.stream.Collectors.toList())
            );
        }
        return FXCollections.emptyObservableList();
    }

    public void enviarMensagem(User remetente, User destinatario, String conteudo) {
        Mensagem novaMensagem = new Mensagem(remetente, destinatario, conteudo);

        if (conversaAtual != null && conversaAtual.getOtherUser(remetente).equals(destinatario)) {
            conversaAtual.addMensagem(novaMensagem);
            if (Utils.Database.isEnabled()) {
                try {
                    dao.MensagemRecord mrec = dao.MensagemDAO.insert(conversaAtual.getId(), remetente.getId(), destinatario.getId(), conteudo);
                    novaMensagem.setId(mrec.getId());
                    if (mrec.getDataEnvio() != null) novaMensagem.setTimestamp(mrec.getDataEnvio().toLocalDateTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            notifyMessageListeners();
        } else {
            Conversa targetConversa = selecionarConversaOuCriar(remetente, destinatario);
            targetConversa.addMensagem(novaMensagem);
            this.conversaAtual = targetConversa;
            if (Utils.Database.isEnabled()) {
                try {
                    dao.MensagemRecord mrec = dao.MensagemDAO.insert(targetConversa.getId(), remetente.getId(), destinatario.getId(), conteudo);
                    novaMensagem.setId(mrec.getId());
                    if (mrec.getDataEnvio() != null) novaMensagem.setTimestamp(mrec.getDataEnvio().toLocalDateTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            notifyMessageListeners();
        }
    }

    public List<Conversa> getTodasConversasParaUsuario(User user) {
        return todasConversas.stream()
                .filter(c -> c.getUser1().equals(user) || c.getUser2().equals(user))
                .sorted((c1, c2) -> c2.getUltimaAtividade().compareTo(c1.getUltimaAtividade()))
                .collect(Collectors.toList());
    }

    public void addMensagemInicial(User remetente, User destinatario, String conteudo) {
        Conversa conversa = encontrarConversa(remetente, destinatario)
                .orElseGet(() -> criarNovaConversa(remetente, destinatario));
        Mensagem mensagem = new Mensagem(remetente, destinatario, conteudo);
        conversa.addMensagem(mensagem);
        notifyMessageListeners();
    }

    public void addMessageListener(Runnable r) {
        if (r == null) return;
        messageListeners.add(r);
    }

    public void removeMessageListener(Runnable r) {
        messageListeners.remove(r);
    }

    private void notifyMessageListeners() {
        for (Runnable r : new ArrayList<>(messageListeners)) {
            try { r.run(); } catch (Exception ignored) {}
        }
    }

    /**
     * Public trigger to notify registered listeners. Useful for forcing UI refresh
     * when other subsystems (projects/contracts) change shared data.
     */
    public void notifyListeners() {
        notifyMessageListeners();
    }

    public void markMessagesAsRead(User owner, User other) {
        if (owner == null || other == null) return;
        encontrarConversa(owner, other).ifPresent(c -> {
            boolean changed = false;
            for (Mensagem m : c.getMensagens()) {
                if (m.getDestinatario() != null && m.getDestinatario().equals(owner) && !m.isLida()) {
                    m.setLida(true);
                    changed = true;
                }
            }
            if (changed) {

                if (Utils.Database.isEnabled()) {
                    try {
                        for (Mensagem m : c.getMensagens()) {
                            if (m.getDestinatario() != null && m.getDestinatario().equals(owner) && m.isLida()) {
                                try { dao.MensagemDAO.markAsRead(m.getId()); } catch (Exception ex) { /* ignore per-message */ }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to persist read flags: " + e.getMessage());
                    }
                }
                notifyMessageListeners();
            }
        });
    }

}