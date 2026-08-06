package com.sicredi.votacao.dto.response;

import com.sicredi.votacao.enums.OpcaoVoto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class VotoResponse {

    private Long id;
    private Long pautaId;
    private Long associadoId;
    private OpcaoVoto voto;
    private LocalDateTime dataHora;
}
