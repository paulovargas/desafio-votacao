package com.sicredi.votacao.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class MobileConsultarResultadoRequest {

    @NotNull
    @Positive
    private Long pautaId;
}
