-- O programa cria isso sozinho ao iniciar, mas o script fica aqui como referência.
CREATE DATABASE IF NOT EXISTS oficina_db CHARACTER SET utf8mb4;
USE oficina_db;

CREATE TABLE IF NOT EXISTS cliente (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    cep         CHAR(8)      NOT NULL,
    logradouro  VARCHAR(150),
    bairro      VARCHAR(100),
    cidade      VARCHAR(100),
    uf          CHAR(2)
);

CREATE TABLE IF NOT EXISTS servico (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id      INT            NOT NULL,
    placa           CHAR(7)        NOT NULL,
    tipo            VARCHAR(10)    NOT NULL,            -- PECA ou SERVICO
    descricao       VARCHAR(200)   NOT NULL,
    quantidade      INT            NOT NULL DEFAULT 1,
    valor_unitario  DECIMAL(10,2)  NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'ABERTO',
    data_registro   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_servico_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE CASCADE
);
