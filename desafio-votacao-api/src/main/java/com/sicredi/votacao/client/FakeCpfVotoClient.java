package com.sicredi.votacao.client;

import com.sicredi.votacao.enums.StatusCpfVoto;
import com.sicredi.votacao.exception.CpfInvalidoException;
import org.springframework.stereotype.Component;

@Component
public class FakeCpfVotoClient implements CpfVotoClient {

    @Override
    public StatusCpfVoto verificar(String cpf) {
        String cpfNormalizado = normalizar(cpf);

        if (cpfNormalizado.length() != 11) {
            throw new CpfInvalidoException(cpf);
        }

        int ultimoDigito = Character.getNumericValue(cpfNormalizado.charAt(cpfNormalizado.length() - 1));

        return ultimoDigito % 2 == 0
                ? StatusCpfVoto.ABLE_TO_VOTE
                : StatusCpfVoto.UNABLE_TO_VOTE;
    }

    private String normalizar(String cpf) {
        if (cpf == null) {
            return "";
        }

        return cpf.replaceAll("\\D", "");
    }
}
