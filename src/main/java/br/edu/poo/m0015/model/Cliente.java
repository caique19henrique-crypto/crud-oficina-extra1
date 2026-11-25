package br.edu.poo.m0015.model;

import br.edu.poo.m0015.exception.ValidacaoException;

public class Cliente {
    private int id;
    private String nome;
    private String email;
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    /** Confere os dados antes de salvar no banco. */
    public void validar() throws ValidacaoException {
        if (nome == null || nome.isBlank()) throw new ValidacaoException("O nome é obrigatório.");
        if (nome.length() > 100) throw new ValidacaoException("O nome pode ter no máximo 100 caracteres.");
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"))
            throw new ValidacaoException("E-mail inválido.");
        if (cep == null || !cep.matches("\\d{8}"))
            throw new ValidacaoException("CEP inválido: precisa ter 8 dígitos.");
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | CEP %s | %s, %s - %s/%s",
                id, nome, email, cep,
                nvl(logradouro), nvl(bairro), nvl(cidade), nvl(uf));
    }

    private static String nvl(String s) { return s == null ? "-" : s; }
}
