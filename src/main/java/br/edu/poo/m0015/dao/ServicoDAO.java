package br.edu.poo.m0015.dao;

import br.edu.poo.m0015.config.Database;
import br.edu.poo.m0015.model.Servico;
import br.edu.poo.m0015.model.StatusServico;
import br.edu.poo.m0015.model.TipoItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO implements CrudDAO<Servico> {

    private static final String SELECT_BASE =
            "SELECT s.*, c.nome AS cliente_nome FROM servico s "
            + "JOIN cliente c ON c.id = s.cliente_id ";

    @Override
    public void inserir(Servico s) throws SQLException {
        String sql = "INSERT INTO servico (cliente_id, placa, tipo, descricao, quantidade, valor_unitario, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getClienteId());
            ps.setString(2, s.getPlaca());
            ps.setString(3, s.getTipo().name());
            ps.setString(4, s.getDescricao());
            ps.setInt(5, s.getQuantidade());
            ps.setBigDecimal(6, s.getValorUnitario());
            ps.setString(7, s.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) s.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<Servico> listar() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY s.id", null);
    }

    public List<Servico> listarPorCliente(int clienteId) throws SQLException {
        return consultar(SELECT_BASE + "WHERE s.cliente_id = ? ORDER BY s.id", clienteId);
    }

    @Override
    public Servico buscarPorId(int id) throws SQLException {
        List<Servico> r = consultar(SELECT_BASE + "WHERE s.id = ?", id);
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public boolean atualizar(Servico s) throws SQLException {
        String sql = "UPDATE servico SET placa=?, tipo=?, descricao=?, quantidade=?, valor_unitario=?, status=? "
                + "WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getPlaca());
            ps.setString(2, s.getTipo().name());
            ps.setString(3, s.getDescricao());
            ps.setInt(4, s.getQuantidade());
            ps.setBigDecimal(5, s.getValorUnitario());
            ps.setString(6, s.getStatus().name());
            ps.setInt(7, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM servico WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Relatório: quantidade de itens e total em R$ por cliente (JOIN + GROUP BY). */
    public List<String> totalPorCliente() throws SQLException {
        String sql = "SELECT c.id, c.nome, COUNT(s.id) AS itens, "
                + "COALESCE(SUM(s.quantidade * s.valor_unitario), 0) AS total "
                + "FROM cliente c LEFT JOIN servico s ON s.cliente_id = c.id "
                + "GROUP BY c.id, c.nome ORDER BY c.nome";
        List<String> linhas = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                linhas.add(String.format("[%d] %s | %d item(ns) | total R$ %.2f",
                        rs.getInt("id"), rs.getString("nome"), rs.getInt("itens"), total));
            }
        }
        return linhas;
    }

    private List<Servico> consultar(String sql, Integer parametro) throws SQLException {
        List<Servico> lista = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (parametro != null) ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Servico s = new Servico();
                    s.setId(rs.getInt("id"));
                    s.setClienteId(rs.getInt("cliente_id"));
                    s.setClienteNome(rs.getString("cliente_nome"));
                    s.setPlaca(rs.getString("placa"));
                    s.setTipo(TipoItem.valueOf(rs.getString("tipo")));
                    s.setDescricao(rs.getString("descricao"));
                    s.setQuantidade(rs.getInt("quantidade"));
                    s.setValorUnitario(rs.getBigDecimal("valor_unitario"));
                    s.setStatus(StatusServico.valueOf(rs.getString("status")));
                    lista.add(s);
                }
            }
        }
        return lista;
    }
}
