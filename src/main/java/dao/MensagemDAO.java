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

public class MensagemDAO {

    public static List<MensagemRecord> findByConversaId(String conversaId) throws SQLException {
        String sql = "SELECT * FROM mensagens WHERE conversa_id = ? ORDER BY data_envio ASC";
        List<MensagemRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conversaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public static MensagemRecord findById(String id) throws SQLException {
        String sql = "SELECT * FROM mensagens WHERE id = ? LIMIT 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public static MensagemRecord insert(String conversaId, String remetenteId, String destinatarioId, String conteudo) throws SQLException {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO mensagens (id, conversa_id, remetente_id, destinatario_id, conteudo, data_envio, lida) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, conversaId);
            ps.setString(3, remetenteId);
            ps.setString(4, destinatarioId);
            ps.setString(5, conteudo);
            ps.setTimestamp(6, now);
            ps.setBoolean(7, false);
            ps.executeUpdate();
        }
        return new MensagemRecord(id, conversaId, remetenteId, destinatarioId, conteudo, now, false);
    }

    public static void markAsRead(String mensagemId) throws SQLException {
        String sql = "UPDATE mensagens SET lida = TRUE WHERE id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mensagemId);
            ps.executeUpdate();
        }
    }

    private static MensagemRecord mapRow(ResultSet rs) throws SQLException {
        return new MensagemRecord(
                rs.getString("id"),
                rs.getString("conversa_id"),
                rs.getString("remetente_id"),
                rs.getString("destinatario_id"),
                rs.getString("conteudo"),
                rs.getTimestamp("data_envio"),
                rs.getBoolean("lida")
        );
    }
}
