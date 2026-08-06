package com.sicredi.votacao.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SessaoResponse {

    private Long id;
    private Long pautaId;
    private LocalDateTime dataHoraAbertura;
    private LocalDateTime dataHoraEncerramento;
}
