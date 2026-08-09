package com.sicredi.votacao.dto.mobile;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class MobileAcaoResponse {

    private String id;
    private String rotulo;
    private String metodo;
    private String url;
    private Map<String, Object> body;
}
