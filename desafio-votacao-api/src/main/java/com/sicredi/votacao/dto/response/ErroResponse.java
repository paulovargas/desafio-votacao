package com.sicredi.votacao.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErroResponse {

    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String path;

    public ErroResponse(){}

    public ErroResponse(
            LocalDateTime timestamp,
            int status,
            String erro,
            String mensagem,
            String path
    ){
        this.timestamp = timestamp;
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.path = path;
    }
}
