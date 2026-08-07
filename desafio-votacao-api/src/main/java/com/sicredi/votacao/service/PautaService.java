package com.sicredi.votacao.service;

import com.sicredi.votacao.dto.request.CriarPautaRequest;
import com.sicredi.votacao.dto.response.PautaResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.repository.PautaRepository;
import org.springframework.stereotype.Service;

@Service
public class PautaService {

    private final PautaRepository repository;

    public PautaService(PautaRepository repository){
        this.repository = repository;
    }

    public PautaResponse cadastrar(CriarPautaRequest request){

        Pauta pauta = new Pauta();

        pauta.setTitulo(request.getTitulo());
        pauta.setDescricao(request.getDescricao());

        pauta = repository.save(pauta);

        return PautaResponse.builder()
                .id(pauta.getId())
                .titulo(pauta.getTitulo())
                .descricao(pauta.getDescricao())
                .dataCriacao(pauta.getDataCriacao())
                .build();
    }
}
