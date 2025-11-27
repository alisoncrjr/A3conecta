package dao;

import Utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContratoDAO {

    public static List<ContratoRecord> findByPrestadorId(String prestadorId) throws SQLException {
        String sql = "SELECT c.id, c.descricao_servico, c.cliente_id, u1.name as cliente_name, c.prestador_id, u2.name as prestador_name, c.status, c.projeto_id, c.valor_total, c.valor_recebido " +
            "FROM contratos c LEFT JOIN users u1 ON c.cliente_id = u1.id LEFT JOIN users u2 ON c.prestador_id = u2.id WHERE c.prestador_id = ?";
        List<ContratoRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prestadorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ContratoRecord(
                            rs.getString("id"),
                            rs.getString("descricao_servico"),
                            rs.getString("cliente_id"),
                            rs.getString("cliente_name"),
                            rs.getString("prestador_id"),
                            rs.getString("prestador_name"),
                            rs.getString("status"),
                            rs.getString("projeto_id"),
                            rs.getBigDecimal("valor_total"),
                            rs.getBigDecimal("valor_recebido")
                    ));
                }
            }
        }
        return list;
    }

    public static List<ContratoRecord> findByClienteId(String clienteId) throws SQLException {
        String sql = "SELECT c.id, c.descricao_servico, c.cliente_id, u1.name as cliente_name, c.prestador_id, u2.name as prestador_name, c.status, c.projeto_id, c.valor_total, c.valor_recebido " +
            "FROM contratos c LEFT JOIN users u1 ON c.cliente_id = u1.id LEFT JOIN users u2 ON c.prestador_id = u2.id WHERE c.cliente_id = ?";
        List<ContratoRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ContratoRecord(
                            rs.getString("id"),
                            rs.getString("descricao_servico"),
                            rs.getString("cliente_id"),
                            rs.getString("cliente_name"),
                            rs.getString("prestador_id"),
                            rs.getString("prestador_name"),
                            rs.getString("status"),
                            rs.getString("projeto_id"),
                            rs.getBigDecimal("valor_total"),
                            rs.getBigDecimal("valor_recebido")
                    ));
                }
            }
        }
        return list;
    }

    public static ContratoRecord findById(String id) throws SQLException {
        String sql = "SELECT c.id, c.descricao_servico, c.cliente_id, u1.name as cliente_name, c.prestador_id, u2.name as prestador_name, c.status, c.projeto_id, c.valor_total, c.valor_recebido " +
            "FROM contratos c LEFT JOIN users u1 ON c.cliente_id = u1.id LEFT JOIN users u2 ON c.prestador_id = u2.id WHERE c.id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                        return new ContratoRecord(
                            rs.getString("id"),
                            rs.getString("descricao_servico"),
                            rs.getString("cliente_id"),
                            rs.getString("cliente_name"),
                            rs.getString("prestador_id"),
                            rs.getString("prestador_name"),
                            rs.getString("status"),
                            rs.getString("projeto_id"),
                            rs.getBigDecimal("valor_total"),
                            rs.getBigDecimal("valor_recebido")
                        );
                }
            }
        }
        return null;
    }

    public static ContratoRecord insert(Model.Contrato contrato) throws SQLException {
        String sql = "INSERT INTO contratos (id, cliente_id, prestador_id, projeto_id, descricao_servico, data_contrato, status, valor_total, valor_recebido) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contrato.getId());
            ps.setString(2, contrato.getCliente() == null ? null : contrato.getCliente().getId());
            ps.setString(3, contrato.getPrestador() == null ? null : contrato.getPrestador().getId());
            ps.setString(4, contrato.getProjetoId());
            ps.setString(5, contrato.getDescricaoServico());
            if (contrato.getDataContrato() != null) {
                ps.setTimestamp(6, new java.sql.Timestamp(java.util.Date.from(contrato.getDataContrato().atZone(java.time.ZoneId.systemDefault()).toInstant()).getTime()));
            } else {
                ps.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
            }
            ps.setString(7, contrato.getStatus() == null ? "PENDENTE" : contrato.getStatus().name());
            ps.setBigDecimal(8, java.math.BigDecimal.valueOf(contrato.getValorTotal()));
            ps.setBigDecimal(9, java.math.BigDecimal.valueOf(contrato.getValorRecebido()));
            ps.executeUpdate();
        }

        return new ContratoRecord(contrato.getId(), contrato.getDescricaoServico(), contrato.getCliente() == null ? null : contrato.getCliente().getId(), contrato.getCliente() == null ? null : contrato.getCliente().getName(), contrato.getPrestador() == null ? null : contrato.getPrestador().getId(), contrato.getPrestador() == null ? null : contrato.getPrestador().getName(), contrato.getStatus() == null ? "PENDENTE" : contrato.getStatus().name(), contrato.getProjetoId(), java.math.BigDecimal.valueOf(contrato.getValorTotal()), java.math.BigDecimal.valueOf(contrato.getValorRecebido()));
    }

    public static void updateStatus(String contratoId, String status) throws SQLException {
        String sql = "UPDATE contratos SET status = ? WHERE id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, contratoId);
            ps.executeUpdate();
        }
    }

    public static void updateValores(String contratoId, double valorTotal, double valorRecebido) throws SQLException {
        String sql = "UPDATE contratos SET valor_total = ?, valor_recebido = ? WHERE id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(valorTotal));
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(valorRecebido));
            ps.setString(3, contratoId);
            ps.executeUpdate();
        }
    }

    public static void delete(String contratoId) throws SQLException {
        String sql = "DELETE FROM contratos WHERE id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contratoId);
            ps.executeUpdate();
        }
    }
}
