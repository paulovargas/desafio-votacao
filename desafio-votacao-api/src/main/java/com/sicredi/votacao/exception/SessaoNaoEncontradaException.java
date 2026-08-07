package com.sicredi.votacao.exception;

public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(Long pautaId){
        super("Sessão não encontrada para a pauta : " + pautaId);
    }
}
