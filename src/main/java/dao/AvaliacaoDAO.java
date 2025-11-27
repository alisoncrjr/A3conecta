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

public class AvaliacaoDAO {

    public static List<AvaliacaoRecord> findByPrestadorId(String prestadorId) throws SQLException {
        String sql = "SELECT * FROM avaliacoes WHERE prestador_id = ?";
        List<AvaliacaoRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prestadorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public static List<AvaliacaoRecord> findByClienteId(String clienteId) throws SQLException {
        String sql = "SELECT * FROM avaliacoes WHERE cliente_id = ?";
        List<AvaliacaoRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public static AvaliacaoRecord findById(String id) throws SQLException {
        String sql = "SELECT * FROM avaliacoes WHERE id = ? LIMIT 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public static AvaliacaoRecord insert(String clienteId, String prestadorId, int pontuacao, String comentario) throws SQLException {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO avaliacoes (id, cliente_id, prestador_id, pontuacao, comentario, data_avaliacao) VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, clienteId);
            ps.setString(3, prestadorId);
            ps.setInt(4, pontuacao);
            ps.setString(5, comentario);
            ps.setTimestamp(6, now);
            ps.executeUpdate();
        }
        return new AvaliacaoRecord(id, clienteId, prestadorId, pontuacao, comentario, now);
    }

    private static AvaliacaoRecord mapRow(ResultSet rs) throws SQLException {
        return new AvaliacaoRecord(
                rs.getString("id"),
                rs.getString("cliente_id"),
                rs.getString("prestador_id"),
                rs.getInt("pontuacao"),
                rs.getString("comentario"),
                rs.getTimestamp("data_avaliacao")
        );
    }
}
