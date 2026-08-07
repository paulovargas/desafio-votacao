package com.sicredi.votacao.exception;

public class AssociadoJaVotouException extends RuntimeException {

    public AssociadoJaVotouException(Long associadoId, Long pautaId){

        super("O associado " + associadoId + " já votou na pauta " + pautaId);
    }
}
