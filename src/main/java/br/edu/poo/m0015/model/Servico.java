package br.edu.poo.m0015.model;

import br.edu.poo.m0015.exception.ValidacaoException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Um item da oficina: pode ser uma PECA ou um SERVICO, ligado a um cliente e a uma placa. */
public class Servico {

    // aceita placa antiga (ABC1234) e Mercosul (ABC1D23)
    private static final Pattern PLACA = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    private int id;
    private int clienteId;
    private String clienteNome; // preenchido nas consultas com JOIN
    private String placa;
    private TipoItem tipo;
    private String descricao;
    private int quantidade;
    private BigDecimal valorUnitario;
    private StatusServico status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public TipoItem getTipo() { return tipo; }
    public void setTipo(TipoItem tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
    public StatusServico getStatus() { return status; }
    public void setStatus(StatusServico status) { this.status = status; }

    public BigDecimal getTotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    /** Confere os dados antes de salvar no banco. */
    public void validar() throws ValidacaoException {
        if (clienteId <= 0) throw new ValidacaoException("O cliente é obrigatório.");
        if (placa == null || !PLACA.matcher(placa).matches())
            throw new ValidacaoException("Placa inválida (ex: ABC1234 ou ABC1D23).");
        if (tipo == null) throw new ValidacaoException("O tipo é obrigatório (PECA ou SERVICO).");
        if (descricao == null || descricao.isBlank())
            throw new ValidacaoException("A descrição é obrigatória.");
        if (descricao.length() > 200)
            throw new ValidacaoException("A descrição pode ter no máximo 200 caracteres.");
        if (quantidade < 1 || quantidade > 999)
            throw new ValidacaoException("A quantidade deve ficar entre 1 e 999.");
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidacaoException("O valor unitário deve ser maior que zero.");
        if (status == null) throw new ValidacaoException("O status é obrigatório.");
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | placa %s | %d x R$ %.2f = R$ %.2f | %s | Cliente: %s (id %d)",
                id, tipo, descricao, placa, quantidade, valorUnitario, getTotal(), status,
                clienteNome == null ? "-" : clienteNome, clienteId);
    }
}
