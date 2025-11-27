package dao;

import Utils.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemOrcamentoDAO {

    public static List<ItemOrcamentoRecord> findByProjetoId(String projetoId) throws SQLException {
        String sql = "SELECT * FROM itens_orcamento WHERE projeto_id = ?";
        List<ItemOrcamentoRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projetoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public static ItemOrcamentoRecord findById(String id) throws SQLException {
        String sql = "SELECT * FROM itens_orcamento WHERE id = ? LIMIT 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    private static ItemOrcamentoRecord mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String projetoId = rs.getString("projeto_id");
        String nome = rs.getString("nome");
        String unidade = rs.getString("unidade");
        BigDecimal quantidade = rs.getBigDecimal("quantidade");
        BigDecimal valorUnitario = rs.getBigDecimal("valor_unitario");
        BigDecimal valorTotal = rs.getBigDecimal("valor_total");
        return new ItemOrcamentoRecord(id, projetoId, nome, unidade, quantidade, valorUnitario, valorTotal);
    }

    public static ItemOrcamentoRecord insert(ItemOrcamentoRecord item) throws SQLException {
        String sql = "INSERT INTO itens_orcamento (id, projeto_id, nome, unidade, quantidade, valor_unitario, valor_total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getProjetoId());
            ps.setString(3, item.getNome());
            ps.setString(4, item.getUnidade());
            ps.setBigDecimal(5, item.getQuantidade());
            ps.setBigDecimal(6, item.getValorUnitario());
            ps.setBigDecimal(7, item.getValorTotal());
            ps.executeUpdate();
        }
        return item;
    }
}
