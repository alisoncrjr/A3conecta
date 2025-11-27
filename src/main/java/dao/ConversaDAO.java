package dao;

import Utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConversaDAO {

    public static ConversaRecord findById(String id) throws SQLException {
        String sql = "SELECT * FROM conversas WHERE id = ? LIMIT 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public static List<ConversaRecord> findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM conversas WHERE user1_id = ? OR user2_id = ?";
        List<ConversaRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public static ConversaRecord insert(String user1Id, String user2Id) throws SQLException {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO conversas (id, user1_id, user2_id, ultima_atividade) VALUES (?, ?, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, user1Id);
            ps.setString(3, user2Id);
            ps.setTimestamp(4, now);
            ps.executeUpdate();
        }
        return new ConversaRecord(id, user1Id, user2Id, now);
    }

    private static ConversaRecord mapRow(ResultSet rs) throws SQLException {
        return new ConversaRecord(
                rs.getString("id"),
                rs.getString("user1_id"),
                rs.getString("user2_id"),
                rs.getTimestamp("ultima_atividade")
        );
    }
}
