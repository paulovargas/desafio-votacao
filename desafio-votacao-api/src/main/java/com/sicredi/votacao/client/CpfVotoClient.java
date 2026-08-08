package com.sicredi.votacao.client;

import com.sicredi.votacao.enums.StatusCpfVoto;

public interface CpfVotoClient {

    StatusCpfVoto verificar(String cpf);
}
