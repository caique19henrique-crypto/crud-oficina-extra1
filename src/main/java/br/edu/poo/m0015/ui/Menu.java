package br.edu.poo.m0015.ui;

import br.edu.poo.m0015.dao.ClienteDAO;
import br.edu.poo.m0015.dao.ServicoDAO;
import br.edu.poo.m0015.exception.ValidacaoException;
import br.edu.poo.m0015.model.Cliente;
import br.edu.poo.m0015.model.Servico;
import br.edu.poo.m0015.model.StatusServico;
import br.edu.poo.m0015.model.TipoItem;
import br.edu.poo.m0015.service.ViaCepService;
import br.edu.poo.m0015.util.Entrada;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Menu {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final ViaCepService viaCep = new ViaCepService();

    public void iniciar() {
        int op;
        do {
            System.out.println("\n===== OFICINA MECÂNICA =====");
            System.out.println("1 - Clientes");
            System.out.println("2 - Peças e serviços");
            System.out.println("0 - Sair");
            op = Entrada.lerInt("Escolha: ");
            switch (op) {
                case 1 -> menuClientes();
                case 2 -> menuServicos();
                case 0 -> System.out.println("Até mais!");
                default -> System.out.println("Opção inválida.");
            }
        } while (op != 0);
    }

    // ===================== CLIENTES =====================

    private void menuClientes() {
        int op;
        do {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar por ID");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Excluir");
            System.out.println("0 - Voltar");
            op = Entrada.lerInt("Escolha: ");
            try {
                switch (op) {
                    case 1 -> cadastrarCliente();
                    case 2 -> listarClientes();
                    case 3 -> buscarCliente();
                    case 4 -> atualizarCliente();
                    case 5 -> excluirCliente();
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (SQLException e) {
                tratarErroBanco(e);
            } catch (ValidacaoException e) {
                System.out.println("Dados inválidos: " + e.getMessage());
            }
        } while (op != 0);
    }

    private void cadastrarCliente() throws SQLException, ValidacaoException {
        Cliente c = new Cliente();
        c.setNome(Entrada.lerTexto("Nome: ", true, 100));
        c.setEmail(Entrada.lerEmail("E-mail: ", true));
        c.setCep(Entrada.lerCep("CEP: ", true));
        preencherEndereco(c);
        c.validar();
        clienteDAO.inserir(c);
        System.out.println("Cliente cadastrado com ID " + c.getId() + ".");
    }

    /** Usa a API ViaCEP para completar o endereço do cliente. */
    private void preencherEndereco(Cliente c) {
        try {
            ViaCepService.Endereco e = viaCep.buscar(c.getCep());
            if (e == null) {
                System.out.println("CEP não encontrado na ViaCEP. O endereço ficará em branco.");
                return;
            }
            c.setLogradouro(e.logradouro);
            c.setBairro(e.bairro);
            c.setCidade(e.cidade);
            c.setUf(e.uf);
            System.out.println("Endereço encontrado: " + e.logradouro + ", " + e.bairro
                    + " - " + e.cidade + "/" + e.uf);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) Thread.currentThread().interrupt();
            System.out.println("Não consegui consultar a ViaCEP agora (" + ex.getMessage()
                    + "). O endereço ficará em branco.");
        }
    }

    private void listarClientes() throws SQLException {
        List<Cliente> lista = clienteDAO.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        lista.forEach(System.out::println);
    }

    private void buscarCliente() throws SQLException {
        Cliente c = clienteDAO.buscarPorId(Entrada.lerInt("ID do cliente: "));
        System.out.println(c == null ? "Cliente não encontrado." : c);
    }

    private void atualizarCliente() throws SQLException, ValidacaoException {
        Cliente c = clienteDAO.buscarPorId(Entrada.lerInt("ID do cliente: "));
        if (c == null) {
            System.out.println("Cliente não encontrado.");
            return;
        }
        System.out.println("Atual: " + c);
        System.out.println("(Enter em branco mantém o valor atual)");

        String nome = Entrada.lerTexto("Novo nome: ", false, 100);
        String email = Entrada.lerEmail("Novo e-mail: ", false);
        String cep = Entrada.lerCep("Novo CEP: ", false);

        if (!nome.isEmpty()) c.setNome(nome);
        if (!email.isEmpty()) c.setEmail(email);
        if (!cep.isEmpty()) {
            c.setCep(cep);
            preencherEndereco(c);
        }

        c.validar();
        System.out.println(clienteDAO.atualizar(c) ? "Cliente atualizado." : "Nada foi atualizado.");
    }

    private void excluirCliente() throws SQLException {
        int id = Entrada.lerInt("ID do cliente: ");
        Cliente c = clienteDAO.buscarPorId(id);
        if (c == null) {
            System.out.println("Cliente não encontrado.");
            return;
        }
        System.out.println(c);
        if (Entrada.confirmar("Excluir esse cliente? As peças e serviços dele também serão apagados")) {
            clienteDAO.deletar(id);
            System.out.println("Cliente excluído.");
        } else {
            System.out.println("Cancelado.");
        }
    }

    // ===================== PEÇAS E SERVIÇOS =====================

    private void menuServicos() {
        int op;
        do {
            System.out.println("\n--- PEÇAS E SERVIÇOS ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar todos");
            System.out.println("3 - Listar por cliente");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Excluir");
            System.out.println("6 - Relatório: total por cliente");
            System.out.println("0 - Voltar");
            op = Entrada.lerInt("Escolha: ");
            try {
                switch (op) {
                    case 1 -> cadastrarServico();
                    case 2 -> imprimir(servicoDAO.listar());
                    case 3 -> imprimir(servicoDAO.listarPorCliente(Entrada.lerInt("ID do cliente: ")));
                    case 4 -> atualizarServico();
                    case 5 -> excluirServico();
                    case 6 -> relatorio();
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (SQLException e) {
                tratarErroBanco(e);
            } catch (ValidacaoException e) {
                System.out.println("Dados inválidos: " + e.getMessage());
            }
        } while (op != 0);
    }

    private void cadastrarServico() throws SQLException, ValidacaoException {
        int clienteId = Entrada.lerInt("ID do cliente dono do veículo: ");
        if (clienteDAO.buscarPorId(clienteId) == null) {
            System.out.println("Cliente não encontrado. Cadastre o cliente primeiro.");
            return;
        }
        Servico s = new Servico();
        s.setClienteId(clienteId);
        s.setPlaca(Entrada.lerPlaca("Placa do veículo: ", true));
        s.setTipo(Entrada.lerEnum("Tipo (PECA ou SERVICO): ", TipoItem.class, true));
        s.setDescricao(Entrada.lerTexto("Descrição (ex: troca de óleo, pastilha de freio): ", true, 200));
        s.setQuantidade(Entrada.lerIntFaixa("Quantidade (1 a 999): ", 1, 999));
        s.setValorUnitario(Entrada.lerValor("Valor unitário (R$): ", true));
        s.setStatus(StatusServico.ABERTO);
        s.validar();
        servicoDAO.inserir(s);
        System.out.println("Cadastrado com ID " + s.getId() + ". Total: R$ " + s.getTotal() + ".");
    }

    private void atualizarServico() throws SQLException, ValidacaoException {
        Servico s = servicoDAO.buscarPorId(Entrada.lerInt("ID do item: "));
        if (s == null) {
            System.out.println("Item não encontrado.");
            return;
        }
        System.out.println("Atual: " + s);
        System.out.println("(Enter em branco mantém o valor atual)");

        String placa = Entrada.lerPlaca("Nova placa: ", false);
        TipoItem tipo = Entrada.lerEnum("Novo tipo (PECA ou SERVICO): ", TipoItem.class, false);
        String desc = Entrada.lerTexto("Nova descrição: ", false, 200);
        Integer qtd = Entrada.lerIntFaixaOpcional("Nova quantidade (1 a 999): ", 1, 999);
        BigDecimal valor = Entrada.lerValor("Novo valor unitário (R$): ", false);
        StatusServico status = Entrada.lerEnum(
                "Novo status (ABERTO, EM_ANDAMENTO ou CONCLUIDO): ", StatusServico.class, false);

        if (!placa.isEmpty()) s.setPlaca(placa);
        if (tipo != null) s.setTipo(tipo);
        if (!desc.isEmpty()) s.setDescricao(desc);
        if (qtd != null) s.setQuantidade(qtd);
        if (valor != null) s.setValorUnitario(valor);
        if (status != null) s.setStatus(status);

        s.validar();
        System.out.println(servicoDAO.atualizar(s) ? "Item atualizado." : "Nada foi atualizado.");
    }

    private void excluirServico() throws SQLException {
        int id = Entrada.lerInt("ID do item: ");
        Servico s = servicoDAO.buscarPorId(id);
        if (s == null) {
            System.out.println("Item não encontrado.");
            return;
        }
        System.out.println(s);
        if (Entrada.confirmar("Excluir esse item?")) {
            servicoDAO.deletar(id);
            System.out.println("Item excluído.");
        } else {
            System.out.println("Cancelado.");
        }
    }

    private void relatorio() throws SQLException {
        List<String> linhas = servicoDAO.totalPorCliente();
        if (linhas.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        linhas.forEach(System.out::println);
    }

    // ===================== AUXILIARES =====================

    private void imprimir(List<Servico> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum item encontrado.");
            return;
        }
        lista.forEach(System.out::println);
    }

    private void tratarErroBanco(SQLException e) {
        if (e.getErrorCode() == 1062) { // chave duplicada
            System.out.println("Erro: já existe um cliente com esse e-mail.");
        } else {
            System.out.println("Erro no banco de dados: " + e.getMessage());
        }
    }
}
