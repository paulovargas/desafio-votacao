package com.sicredi.votacao.service;

import com.sicredi.votacao.dto.request.CriarPautaRequest;
import com.sicredi.votacao.dto.response.PautaResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.repository.PautaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PautaService {

    private static final Logger logger = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository repository;

    public PautaService(PautaRepository repository){
        this.repository = repository;
    }

    public PautaResponse cadastrar(CriarPautaRequest request){

        logger.info("Iniciando cadastro de pauta");

        Pauta pauta = new Pauta();

        pauta.setTitulo(request.getTitulo());
        pauta.setDescricao(request.getDescricao());

        pauta = repository.save(pauta);

        logger.info("Pauta cadastrada com sucesso. pautaId={}", pauta.getId());

        return PautaResponse.builder()
                .id(pauta.getId())
                .titulo(pauta.getTitulo())
                .descricao(pauta.getDescricao())
                .dataCriacao(pauta.getDataCriacao())
                .build();
    }
}
