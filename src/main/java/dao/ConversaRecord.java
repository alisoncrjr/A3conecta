package dao;

import java.sql.Timestamp;

public class ConversaRecord {
    private final String id;
    private final String user1Id;
    private final String user2Id;
    private final Timestamp ultimaAtividade;

    public ConversaRecord(String id, String user1Id, String user2Id, Timestamp ultimaAtividade) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.ultimaAtividade = ultimaAtividade;
    }

    public String getId() { return id; }
    public String getUser1Id() { return user1Id; }
    public String getUser2Id() { return user2Id; }
    public Timestamp getUltimaAtividade() { return ultimaAtividade; }
}
