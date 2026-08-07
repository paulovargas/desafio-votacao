package com.sicredi.votacao.exception;

public class SessaoJaExisteException extends RuntimeException {

    public SessaoJaExisteException(Long pautaId){
        super("Ja existe uma sessao para a pauta: " + pautaId);
    }
}
