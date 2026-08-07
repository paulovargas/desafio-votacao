package com.sicredi.votacao.exception;

public class PautaNaoEncontradaException extends RuntimeException {

    public PautaNaoEncontradaException(Long pautaId){
        super("Pauta não encontrada: " + pautaId);
    }
}
