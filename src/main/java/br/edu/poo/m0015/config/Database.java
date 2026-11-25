package br.edu.poo.m0015.config;

import br.edu.poo.m0015.util.Entrada;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cuida da conexão com o MySQL (JDBC). As configurações vêm do arquivo .env.
 * Padrão: localhost, porta 3306, usuário root e SEM senha.
 * Se o MySQL da máquina exigir senha, o programa pergunta na hora (não precisa editar nada).
 */
public class Database {

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    private static final String HOST = get("DB_HOST", "localhost");
    private static final String PORT = get("DB_PORT", "3306");
    private static final String NAME = get("DB_NAME", "oficina_db");
    private static final String USER = get("DB_USER", "root");
    private static String password = get("DB_PASSWORD", "");

    private static final String PARAMS =
            "?sslMode=DISABLED&allowPublicKeyRetrieval=true&serverTimezone=America/Recife";

    private static String get(String chave, String padrao) {
        String valor = DOTENV.get(chave);
        return (valor == null || valor.isBlank()) ? padrao : valor.trim();
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + NAME + PARAMS;
        return DriverManager.getConnection(url, USER, password);
    }

    /** Cria o banco e as tabelas caso ainda não existam. */
    public static void inicializar() throws SQLException {
        if (!NAME.matches("[A-Za-z0-9_]+")) {
            throw new SQLException("DB_NAME inválido no .env (use só letras, números e _).");
        }

        criarBanco();

        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS cliente (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        cep CHAR(8) NOT NULL,
                        logradouro VARCHAR(150),
                        bairro VARCHAR(100),
                        cidade VARCHAR(100),
                        uf CHAR(2)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS servico (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        cliente_id INT NOT NULL,
                        placa CHAR(7) NOT NULL,
                        tipo VARCHAR(10) NOT NULL,
                        descricao VARCHAR(200) NOT NULL,
                        quantidade INT NOT NULL DEFAULT 1,
                        valor_unitario DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'ABERTO',
                        data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_servico_cliente FOREIGN KEY (cliente_id)
                            REFERENCES cliente (id) ON DELETE CASCADE
                    )
                    """);
        }
    }

    /**
     * Conecta no servidor (sem escolher banco) e cria o banco se precisar.
     * Se o MySQL recusar a senha (erro 1045), pergunta a senha até 3 vezes.
     */
    private static void criarBanco() throws SQLException {
        String urlSemBanco = "jdbc:mysql://" + HOST + ":" + PORT + "/" + PARAMS;
        int tentativas = 0;

        while (true) {
            try (Connection conn = DriverManager.getConnection(urlSemBanco, USER, password);
                 Statement st = conn.createStatement()) {
                st.execute("CREATE DATABASE IF NOT EXISTS `" + NAME + "` CHARACTER SET utf8mb4");
                return;
            } catch (SQLException e) {
                boolean senhaErrada = e.getErrorCode() == 1045;
                if (!senhaErrada || tentativas >= 3) throw e;
                tentativas++;
                System.out.println("O MySQL pediu senha para o usuário '" + USER + "'.");
                password = Entrada.lerTexto("Digite a senha do MySQL (Enter se não tiver): ", false, 100);
            }
        }
    }
}
