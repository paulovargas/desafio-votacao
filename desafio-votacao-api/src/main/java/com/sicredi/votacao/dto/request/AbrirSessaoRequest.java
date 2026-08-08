package com.sicredi.votacao.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class AbrirSessaoRequest {

    @Positive(message = "A duração da sessão deve ser maior que zero")
    private Long duracaoMinutos;
}
