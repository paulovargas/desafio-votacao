package com.sicredi.votacao.dto.request;

import com.sicredi.votacao.enums.OpcaoVoto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RegistrarVotoRequest {

    @NotNull
    private Long associadoId;

    @NotBlank
    private String cpf;

    @NotNull
    private OpcaoVoto voto;

    public Long getAssociadoId(){
        return associadoId;
    }

    public void setAssociadoId(Long associadoId){
        this.associadoId = associadoId;
    }

    public String getCpf(){
        return cpf;
    }

    public void setCpf(String cpf){
        this.cpf = cpf;
    }

    public OpcaoVoto getVoto(){
        return voto;
    }

    public void setVoto(OpcaoVoto voto){
        this.voto = voto;
    }
}
