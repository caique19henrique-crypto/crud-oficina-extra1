package br.edu.poo.m0015.exception;

/** Exceção própria: lançada quando os dados de uma entidade são inválidos. */
public class ValidacaoException extends Exception {
    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
