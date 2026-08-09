package com.sicredi.votacao.dto.mobile;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MobileTelaResponse {

    private String id;
    private String tipo;
    private String titulo;
    private String descricao;
    private List<MobileCampoResponse> itens;
    private List<MobileAcaoResponse> acoes;
    private List<MobileOpcaoResponse> opcoes;
}
