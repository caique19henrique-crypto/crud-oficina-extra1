package br.edu.poo.m0015;

import br.edu.poo.m0015.config.Database;
import br.edu.poo.m0015.ui.Menu;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            Database.inicializar();
        } catch (SQLException e) {
            System.out.println("Não foi possível conectar no MySQL.");
            System.out.println("1) Confira se o MySQL está ligado (serviço MySQL / MySQL80 iniciado).");
            System.out.println("2) Confira host, porta e usuário no arquivo .env.");
            System.out.println("Detalhe do erro: " + e.getMessage());
            return;
        }
        new Menu().iniciar();
    }
}
