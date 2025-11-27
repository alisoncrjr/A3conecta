package dao;

import Model.User;
import Utils.Database;
import java.util.Optional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    public static void insertUser(User u) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO users (id, name, email, password_hash, role, endereco, telefone, foto_perfil_path, cpf, cnpj, descricao_servico, categoria) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.getId());
                ps.setString(2, u.getName());
                ps.setString(3, u.getEmail());

                String hashed = Utils.PasswordUtil.hashPassword(u.getPassword());
                ps.setString(4, hashed);
                ps.setString(5, (u instanceof Model.Cliente) ? "CLIENTE" : "PRESTADOR");


                String endereco = null;
                String telefone = null;
                String foto = u.getFotoPerfilPath();
                if (u instanceof Model.Cliente) {
                    Model.Cliente c = (Model.Cliente) u;
                    endereco = c.getEndereco();
                    telefone = c.getTelefone();
                } else if (u instanceof Model.Prestador) {
                    Model.Prestador p = (Model.Prestador) u;
                    endereco = p.getEndereco();
                    telefone = p.getTelefone();
                }

                ps.setString(6, endereco);
                ps.setString(7, telefone);
                ps.setString(8, foto);


                String cpf = null;
                String cnpj = null;
                String descricao = null;
                String categoria = null;
                if (u instanceof Model.Cliente) {
                    cpf = ((Model.Cliente) u).getCpf();
                }
                if (u instanceof Model.Prestador) {
                    Model.Prestador p = (Model.Prestador) u;
                    cnpj = p.getCnpj();
                    descricao = p.getDescricaoServico();
                    categoria = p.getCategoria();
                }
                ps.setString(9, cpf);
                ps.setString(10, cnpj);
                ps.setString(11, descricao);
                ps.setString(12, categoria);
                ps.executeUpdate();
            }
        }
    }


    public static Optional<UserRecord> findById(String id) throws SQLException {
        try (Connection c = Database.getConnection()) {
            String sql = "SELECT id, name, email, role, password_hash, foto_perfil_path, categoria, descricao_servico, cnpj, cpf, endereco, telefone FROM users WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        UserRecord r = new UserRecord(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("role"),
                                rs.getString("password_hash"),
                                rs.getString("foto_perfil_path"),
                                rs.getString("categoria"),
                                rs.getString("descricao_servico"),
                                rs.getString("cnpj"),
                                rs.getString("cpf"),
                                rs.getString("endereco"),
                                rs.getString("telefone")
                        );
                        return Optional.of(r);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public static List<UserRecord> findAllRecords() throws SQLException {
        List<UserRecord> list = new ArrayList<>();
        try (Connection c = Database.getConnection()) {
            String sql = "SELECT id, name, email, role, password_hash, foto_perfil_path, categoria, descricao_servico, cnpj, cpf, endereco, telefone FROM users";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UserRecord r = new UserRecord(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("role"),
                                rs.getString("password_hash"),
                                rs.getString("foto_perfil_path"),
                                rs.getString("categoria"),
                                rs.getString("descricao_servico"),
                                rs.getString("cnpj"),
                                rs.getString("cpf"),
                                rs.getString("endereco"),
                                rs.getString("telefone")
                        );
                        list.add(r);
                    }
                }
            }
        }
        return list;
    }

    public static java.util.Optional<UserRecord> findByEmail(String email) throws SQLException {
        try (Connection c = Database.getConnection()) {
            String sql = "SELECT id, name, email, role, password_hash, foto_perfil_path, categoria, descricao_servico, cnpj, cpf, endereco, telefone FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        UserRecord r = new UserRecord(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("role"),
                                rs.getString("password_hash"),
                                rs.getString("foto_perfil_path"),
                                rs.getString("categoria"),
                                rs.getString("descricao_servico"),
                                rs.getString("cnpj"),
                                rs.getString("cpf"),
                                rs.getString("endereco"),
                                rs.getString("telefone")
                        );
                        return java.util.Optional.of(r);
                    }
                }
            }
        }
        return java.util.Optional.empty();
    }
}
