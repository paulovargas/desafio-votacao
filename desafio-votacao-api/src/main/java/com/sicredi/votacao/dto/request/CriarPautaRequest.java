package com.sicredi.votacao.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CriarPautaRequest {

    @NotBlank(message = "O título é obrigatório !")
    @Size(max = 200)
    private String titulo;

    @Size(max = 1000)
    private String descricao;
}
