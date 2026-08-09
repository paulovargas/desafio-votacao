package com.sicredi.votacao.dto.mobile;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MobileCampoResponse {

    private String id;
    private String tipo;
    private String rotulo;
    private Boolean obrigatorio;
    private List<String> opcoes;
}
