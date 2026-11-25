# Oficina Mecânica - CRUD de Clientes e Peças/Serviços

Projeto do Desafio Extra: aplicação Java de console com CRUD completo de duas entidades
relacionadas (**Cliente 1 : N Peças/Serviços**), banco **MySQL** via JDBC e consumo da API pública
**ViaCEP** (https://viacep.com.br).

Pacote raiz do projeto: `br.edu.poo.m0015`.

## O que o sistema faz

- **Clientes:** cadastrar, listar, buscar por ID, atualizar e excluir.
- **Peças e serviços:** cadastrar, listar todos, listar por cliente, atualizar e excluir. Cada item
  tem o tipo (`PECA` ou `SERVICO`), a placa do veículo, descrição, quantidade, valor unitário e
  status (`ABERTO`, `EM_ANDAMENTO` ou `CONCLUIDO`). Todo item pertence a um cliente.
- **Relatório:** total gasto por cliente (quantidade de itens e soma em R$).
- **API externa (ViaCEP):** ao cadastrar ou atualizar um cliente, o CEP é consultado e o endereço
  (rua, bairro, cidade, UF) é preenchido automaticamente. A API é pública e **não precisa de chave**.
- **Validações:** campos obrigatórios, e-mail válido e único, CEP com 8 dígitos, placa válida
  (formato antigo ou Mercosul), quantidade e valor maiores que zero, tipo e status válidos, cliente
  precisa existir para cadastrar item. Os dados são conferidos na leitura do teclado e de novo nas
  classes de modelo (`validar()`), que lançam `ValidacaoException`. Erros de banco e de rede são tratados.
- Excluir um cliente apaga também os itens dele (`ON DELETE CASCADE`), com confirmação.

## Conceitos de POO usados

Classes e objetos, encapsulamento (atributos privados com getters/setters), interface genérica
(`CrudDAO<T>`, implementada por `ClienteDAO` e `ServicoDAO`), enums (`TipoItem` e `StatusServico`),
exceção própria (`ValidacaoException`), tratamento de exceções (`try/catch/finally` com
try-with-resources), coleções (`List`/`ArrayList`) e organização em camadas.

## Tecnologias

Java 17, Maven, MySQL 8, JDBC (mysql-connector-j 9.6.0), Gson, dotenv-java, `java.net.http.HttpClient`.

## Pré-requisitos

- JDK 17 ou superior
- Maven (o IntelliJ já traz um embutido)
- **MySQL instalado e ligado** na máquina (padrão: `localhost:3306`)
- Conexão com a internet (para consultar a ViaCEP e para o Maven baixar as dependências)

## Como rodar

1. Clone o repositório:
   ```
   git clone <link-do-repositorio>
   cd crud-oficina-extra
   ```
2. Deixe o MySQL ligado. **Não precisa configurar mais nada:** o arquivo `.env` já vem no
   repositório com `localhost`, usuário `root` e **sem senha**.
3. Rode o projeto:
   - **IntelliJ:** File > Open, escolha a pasta (ou o `pom.xml`), espere o Maven baixar as
     dependências e execute a classe `br.edu.poo.m0015.Main`.
   - **Terminal:** `mvn compile exec:java`

O programa **cria o banco `oficina_db` e as tabelas sozinho** na primeira execução, então não precisa
rodar nenhum script antes. O arquivo `schema.sql` está no repositório só como referência.

### E se o MySQL da máquina tiver senha?

Sem problema: se o MySQL recusar o acesso, o programa **pergunta a senha no console** na hora
(Enter se não tiver) e segue normalmente. Também dá pra deixar fixo no `.env`, em `DB_PASSWORD=`.

## Estrutura

```
src/main/java/br/edu/poo/m0015/
  Main.java                    ponto de entrada
  config/Database.java         conexão JDBC + criação do banco/tabelas + leitura do .env
  model/                       Cliente, Servico, TipoItem (enum) e StatusServico (enum)
  dao/                         CrudDAO (interface), ClienteDAO e ServicoDAO (SQL com PreparedStatement)
  exception/                   ValidacaoException
  service/ViaCepService        consumo da API ViaCEP
  util/Entrada.java            leitura e validação do teclado
  ui/Menu.java                 menus interativos
```

## Problemas comuns

- **Não foi possível conectar no MySQL:** o serviço do MySQL não está ligado (no Windows, abra
  "Serviços" e inicie o MySQL80) ou a porta é diferente de 3306 (ajuste `DB_PORT` no `.env`).
- **Não consegui consultar a ViaCEP:** sem internet; o cadastro continua, só com o endereço em branco.
- **Acentos estranhos no Windows:** rode com `-Dfile.encoding=UTF-8` ou use o terminal do IntelliJ.
