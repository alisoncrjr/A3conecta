package dao;

import Utils.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    public static List<ProjetoRecord> findByClienteId(String clienteId) throws SQLException {
        String sql = "SELECT * FROM projetos WHERE cliente_id = ?";
        List<ProjetoRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public static List<ProjetoRecord> findByPrestadorId(String prestadorId) throws SQLException {
        String sql = "SELECT * FROM projetos WHERE prestador_id = ?";
        List<ProjetoRecord> list = new ArrayList<>();
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

    public static ProjetoRecord findById(String id) throws SQLException {
        String sql = "SELECT * FROM projetos WHERE id = ? LIMIT 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public static List<ProjetoRecord> findAll() throws SQLException {
        String sql = "SELECT * FROM projetos";
        List<ProjetoRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private static ProjetoRecord mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String clienteId = rs.getString("cliente_id");
        String prestadorId = rs.getString("prestador_id");
        String titulo = rs.getString("titulo");
        String tipoServico = rs.getString("tipo_servico");
        String descricao = rs.getString("descricao");
        Date dataInicio = rs.getDate("data_inicio_estimada");
        Date dataFim = rs.getDate("data_fim_estimada");
        String status = rs.getString("status");
        BigDecimal custoMat = rs.getBigDecimal("custo_estimado_materiais");
        BigDecimal custoMao = rs.getBigDecimal("custo_estimado_mao_de_obra");
        BigDecimal valorMao = null;
        BigDecimal custoRealMat = null;
        BigDecimal custoRealMao = null;
        try { valorMao = rs.getBigDecimal("valor_mao_de_obra"); } catch (Exception ignored) {}
        try { custoRealMat = rs.getBigDecimal("custo_real_materiais"); } catch (Exception ignored) {}
        try { custoRealMao = rs.getBigDecimal("custo_real_mao_de_obra"); } catch (Exception ignored) {}
        String observ = rs.getString("observacoes_prestador");
        return new ProjetoRecord(id, clienteId, prestadorId, titulo, tipoServico, descricao, dataInicio, dataFim, status, custoMat, custoMao, observ, valorMao, custoRealMat, custoRealMao);
    }

    public static ProjetoRecord insert(ProjetoRecord projeto) throws SQLException {
        String sql = "INSERT INTO projetos (id, cliente_id, prestador_id, titulo, tipo_servico, descricao, data_inicio_estimada, data_fim_estimada, status, custo_estimado_materiais, custo_estimado_mao_de_obra, observacoes_prestador) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projeto.getId());
            ps.setString(2, projeto.getClienteId());
            ps.setString(3, projeto.getPrestadorId());
            ps.setString(4, projeto.getTitulo());
            ps.setString(5, projeto.getTipoServico());
            ps.setString(6, projeto.getDescricao());
            ps.setDate(7, projeto.getDataInicioEstimada());
            ps.setDate(8, projeto.getDataFimEstimada());
            ps.setString(9, projeto.getStatus());
            ps.setBigDecimal(10, projeto.getCustoEstimadoMateriais());
            ps.setBigDecimal(11, projeto.getCustoEstimadoMaoDeObra());
            ps.setString(12, projeto.getObservacoesPrestador());
            ps.executeUpdate();
        }
        return projeto;
    }

    public static void updateStatus(String projetoId, String status) throws SQLException {
        String sql = "UPDATE projetos SET status = ? WHERE id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, projetoId);
            ps.executeUpdate();
        }
    }

    public static void updateFinancials(String projetoId, java.math.BigDecimal custoEstimadoMateriais, java.math.BigDecimal custoEstimadoMaoDeObra, String observacoesPrestador, java.math.BigDecimal valorMaoDeObra, java.math.BigDecimal custoRealMateriais, java.math.BigDecimal custoRealMaoDeObra) throws SQLException {

        String sql = "UPDATE projetos SET custo_estimado_materiais = ?, custo_estimado_mao_de_obra = ?, observacoes_prestador = ?, valor_mao_de_obra = ?, custo_real_materiais = ?, custo_real_mao_de_obra = ? WHERE id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, custoEstimadoMateriais == null ? java.math.BigDecimal.ZERO : custoEstimadoMateriais);
            ps.setBigDecimal(2, custoEstimadoMaoDeObra == null ? java.math.BigDecimal.ZERO : custoEstimadoMaoDeObra);
            ps.setString(3, observacoesPrestador);
            ps.setBigDecimal(4, valorMaoDeObra == null ? java.math.BigDecimal.ZERO : valorMaoDeObra);
            ps.setBigDecimal(5, custoRealMateriais == null ? java.math.BigDecimal.ZERO : custoRealMateriais);
            ps.setBigDecimal(6, custoRealMaoDeObra == null ? java.math.BigDecimal.ZERO : custoRealMaoDeObra);
            ps.setString(7, projetoId);
            ps.executeUpdate();
        }
    }
}
