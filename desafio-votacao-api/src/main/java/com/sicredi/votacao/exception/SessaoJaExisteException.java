package com.sicredi.votacao.exception;

public class SessaoJaExisteException extends RuntimeException {

    public SessaoJaExisteException(Long pautaId){
        super("Já existe uma sessão para a pauta: " + pautaId);
    }
}
