package com.sicredi.votacao.service;


import com.sicredi.votacao.dto.request.AbrirSessaoRequest;
import com.sicredi.votacao.dto.response.SessaoResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Sessao;
import com.sicredi.votacao.exception.DuracaoSessaoInvalidaException;
import com.sicredi.votacao.exception.PautaNaoEncontradaException;
import com.sicredi.votacao.exception.SessaoJaExisteException;
import com.sicredi.votacao.repository.PautaRepository;
import com.sicredi.votacao.repository.SessaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class SessaoService {

    private static final Logger logger = LoggerFactory.getLogger(SessaoService.class);

    private final SessaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;

    public SessaoService(SessaoRepository sessaoRepository, PautaRepository pautaRepository){
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
    }

    public SessaoResponse abrirSessao(Long pautaId, AbrirSessaoRequest request){

        long duracao = obterDuracaoMinutos(request);
        logger.info("Iniciando abertura de sessao. pautaId={}, duracaoMinutos={}", pautaId, duracao);

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        if(sessaoRepository.existsByPauta(pauta)){
            logger.warn("Abertura de sessao rejeitada: sessao ja existente. pautaId={}", pautaId);
            throw new SessaoJaExisteException(pautaId);
        }

        LocalDateTime abertura = LocalDateTime.now();

        LocalDateTime encerramento = abertura.plusMinutes(duracao);

        Sessao sessao = new Sessao();
        sessao.setPauta(pauta);
        sessao.setInicio(abertura);
        sessao.setFim(encerramento);

        sessao = sessaoRepository.save(sessao);

        logger.info("Sessao aberta com sucesso. sessaoId={}, pautaId={}, encerramento={}",
                sessao.getId(), pauta.getId(), sessao.getFim());

        return SessaoResponse.builder()
                .id(sessao.getId())
                .pautaId(pauta.getId())
                .dataHoraAbertura(sessao.getInicio())
                .dataHoraEncerramento(sessao.getFim())
                .build();
    }

    private long obterDuracaoMinutos(AbrirSessaoRequest request) {
        if (request == null || request.getDuracaoMinutos() == null) {
            return 1L;
        }

        if (request.getDuracaoMinutos() <= 0) {
            logger.warn("Duracao de sessao invalida informada. duracaoMinutos={}", request.getDuracaoMinutos());
            throw new DuracaoSessaoInvalidaException();
        }

        return request.getDuracaoMinutos();
    }
}
