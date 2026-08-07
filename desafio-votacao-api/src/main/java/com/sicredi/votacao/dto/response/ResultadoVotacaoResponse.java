package com.sicredi.votacao.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultadoVotacaoResponse {

    private Long pautaId;
    private String titulo;
    private long votosSim;
    private long votosNao;
    private long totalVotos;
    private String resultado;

    public ResultadoVotacaoResponse (){}

    public ResultadoVotacaoResponse(
            Long pautaId,
            String titulo,
            long votosSim,
            long votosNao,
            long totalVotos,
            String resultado){
        this.pautaId = pautaId;
        this.titulo = titulo;
        this.votosSim = votosSim;
        this.votosNao = votosNao;
        this.totalVotos = totalVotos;
        this.resultado = resultado;
    }
}
