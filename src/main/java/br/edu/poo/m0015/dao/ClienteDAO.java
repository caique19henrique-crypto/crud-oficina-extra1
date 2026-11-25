package br.edu.poo.m0015.dao;

import br.edu.poo.m0015.config.Database;
import br.edu.poo.m0015.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements CrudDAO<Cliente> {

    @Override
    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, email, cep, logradouro, bairro, cidade, uf) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, c);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM cliente ORDER BY id")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cliente WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public boolean atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome=?, email=?, cep=?, logradouro=?, bairro=?, cidade=?, uf=? "
                + "WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            preencher(ps, c);
            ps.setInt(8, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void preencher(PreparedStatement ps, Cliente c) throws SQLException {
        ps.setString(1, c.getNome());
        ps.setString(2, c.getEmail());
        ps.setString(3, c.getCep());
        ps.setString(4, c.getLogradouro());
        ps.setString(5, c.getBairro());
        ps.setString(6, c.getCidade());
        ps.setString(7, c.getUf());
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        c.setCep(rs.getString("cep"));
        c.setLogradouro(rs.getString("logradouro"));
        c.setBairro(rs.getString("bairro"));
        c.setCidade(rs.getString("cidade"));
        c.setUf(rs.getString("uf"));
        return c;
    }
}
