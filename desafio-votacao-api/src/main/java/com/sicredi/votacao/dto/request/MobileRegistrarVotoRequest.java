package com.sicredi.votacao.dto.request;

import com.sicredi.votacao.enums.OpcaoVoto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class MobileRegistrarVotoRequest {

    @NotNull
    @Positive
    private Long pautaId;

    @NotNull
    private Long associadoId;

    @NotBlank
    private String cpf;

    @NotNull
    private OpcaoVoto voto;

    public RegistrarVotoRequest toRegistrarVotoRequest() {
        RegistrarVotoRequest request = new RegistrarVotoRequest();
        request.setAssociadoId(associadoId);
        request.setCpf(cpf);
        request.setVoto(voto);
        return request;
    }
}
