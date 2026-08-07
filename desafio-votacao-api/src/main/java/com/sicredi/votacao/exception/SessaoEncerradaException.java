package com.sicredi.votacao.exception;

public class SessaoEncerradaException extends RuntimeException {

    public SessaoEncerradaException(){
        super("A sessão de votação está encerrada.");
    }
}
