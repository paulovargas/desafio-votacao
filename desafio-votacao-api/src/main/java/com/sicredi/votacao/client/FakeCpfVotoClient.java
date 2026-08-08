package com.sicredi.votacao.client;

import com.sicredi.votacao.enums.StatusCpfVoto;
import com.sicredi.votacao.exception.CpfInvalidoException;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class FakeCpfVotoClient implements CpfVotoClient {

    private final Random random = new Random();

    @Override
    public StatusCpfVoto verificar(String cpf) {
        if (random.nextBoolean()) {
            throw new CpfInvalidoException(cpf);
        }

        return random.nextBoolean()
                ? StatusCpfVoto.ABLE_TO_VOTE
                : StatusCpfVoto.UNABLE_TO_VOTE;
    }
}
