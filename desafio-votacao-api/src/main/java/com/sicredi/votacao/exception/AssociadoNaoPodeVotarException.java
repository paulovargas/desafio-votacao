package com.sicredi.votacao.exception;

public class AssociadoNaoPodeVotarException extends RuntimeException {

    public AssociadoNaoPodeVotarException(String cpf) {
        super("Associado não habilitado para votar. cpf=" + cpf);
    }
}
