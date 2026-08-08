package com.sicredi.votacao.exception;

public class DuracaoSessaoInvalidaException extends RuntimeException {

    public DuracaoSessaoInvalidaException() {
        super("A duração da sessão deve ser maior que zero");
    }
}
