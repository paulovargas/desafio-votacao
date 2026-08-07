package com.sicredi.votacao.service;


import com.sicredi.votacao.dto.request.AbrirSessaoRequest;
import com.sicredi.votacao.dto.response.SessaoResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Sessao;
import com.sicredi.votacao.exception.PautaNaoEncontradaException;
import com.sicredi.votacao.exception.SessaoJaExisteException;
import com.sicredi.votacao.repository.PautaRepository;
import com.sicredi.votacao.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;

    public SessaoService(SessaoRepository sessaoRepository, PautaRepository pautaRepository){
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
    }

    public SessaoResponse abrirSessao(Long pautaId, AbrirSessaoRequest request){

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        if(sessaoRepository.existsByPauta(pauta)){
            throw new SessaoJaExisteException(pautaId);
        }

        LocalDateTime abertura = LocalDateTime.now();

        long duracao = request.getDuracaoMinutos() == null ? 1 : request.getDuracaoMinutos();

        LocalDateTime encerramento = abertura.plusMinutes(duracao);

        Sessao sessao = new Sessao();
        sessao.setPauta(pauta);
        sessao.setInicio(abertura);
        sessao.setFim(encerramento);

        sessao = sessaoRepository.save(sessao);

        return SessaoResponse.builder()
                .id(sessao.getId())
                .pautaId(pauta.getId())
                .dataHoraAbertura(sessao.getInicio())
                .dataHoraEncerramento(sessao.getFim())
                .build();
    }
}
