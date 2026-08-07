package com.sicredi.votacao.service;

import com.sicredi.votacao.dto.request.RegistrarVotoRequest;
import com.sicredi.votacao.dto.response.ResultadoVotacaoResponse;
import com.sicredi.votacao.dto.response.VotoResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Sessao;
import com.sicredi.votacao.entity.Voto;
import com.sicredi.votacao.enums.OpcaoVoto;
import com.sicredi.votacao.exception.AssociadoJaVotouException;
import com.sicredi.votacao.exception.PautaNaoEncontradaException;
import com.sicredi.votacao.exception.SessaoEncerradaException;
import com.sicredi.votacao.exception.SessaoNaoEncontradaException;
import com.sicredi.votacao.repository.PautaRepository;
import com.sicredi.votacao.repository.SessaoRepository;
import com.sicredi.votacao.repository.VotoRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class VotoService {

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoRepository sessaoRepository;

    public VotoService(VotoRepository votoRepository,
                       PautaRepository pautaRepository,
                       SessaoRepository sessaoRepository){
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        this.sessaoRepository = sessaoRepository;
    }

    public VotoResponse votar(Long pautaId, RegistrarVotoRequest request){
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        Sessao sessao = sessaoRepository.findByPauta(pauta)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        if (LocalDateTime.now().isAfter(sessao.getFim())){
            throw new SessaoEncerradaException();
        }

        if (votoRepository.existsByPautaAndAssociadoId(pauta, request.getAssociadoId())){
            throw new AssociadoJaVotouException(request.getAssociadoId(), pautaId);
        }

        Voto voto = new Voto();
        voto.setPauta(pauta);
        voto.setAssociadoId(request.getAssociadoId());
        voto.setVoto(request.getVoto());

        voto = votoRepository.save(voto);

        return VotoResponse.builder()
                .id(voto.getId())
                .pautaId(pauta.getId())
                .associadoId(voto.getAssociadoId())
                .voto(voto.getVoto())
                .dataHora(voto.getDataHora())
                .build();
    }

    public ResultadoVotacaoResponse obterResultado(Long pautaId){

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        long votosSim = votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.SIM);

        long votosNao = votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.NAO);

        long totalVotos = votosSim + votosNao;

        String resultado;

        if (votosSim > votosNao) {
            resultado = "APROVADA";
        } else if (votosNao > votosSim) {
            resultado = "REPROVADA";
        } else {
            resultado = "EMPATE";
        }

        return new ResultadoVotacaoResponse(
                pauta.getId(),
                pauta.getTitulo(),
                votosSim,
                votosNao,
                totalVotos,
                resultado
        );
    }
}
