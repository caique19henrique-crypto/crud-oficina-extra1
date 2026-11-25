package br.edu.poo.m0015.dao;

import java.sql.SQLException;
import java.util.List;

/** Contrato do CRUD: toda DAO do sistema implementa estas 5 operações. */
public interface CrudDAO<T> {
    void inserir(T obj) throws SQLException;

    List<T> listar() throws SQLException;

    T buscarPorId(int id) throws SQLException;

    boolean atualizar(T obj) throws SQLException;

    boolean deletar(int id) throws SQLException;
}
