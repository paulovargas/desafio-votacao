package com.sicredi.votacao.service;

import com.sicredi.votacao.client.CpfVotoClient;
import com.sicredi.votacao.dto.request.RegistrarVotoRequest;
import com.sicredi.votacao.dto.response.ResultadoVotacaoResponse;
import com.sicredi.votacao.dto.response.VotoResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Sessao;
import com.sicredi.votacao.entity.Voto;
import com.sicredi.votacao.enums.OpcaoVoto;
import com.sicredi.votacao.enums.StatusCpfVoto;
import com.sicredi.votacao.exception.AssociadoJaVotouException;
import com.sicredi.votacao.exception.AssociadoNaoPodeVotarException;
import com.sicredi.votacao.exception.PautaNaoEncontradaException;
import com.sicredi.votacao.exception.SessaoEncerradaException;
import com.sicredi.votacao.exception.SessaoNaoEncontradaException;
import com.sicredi.votacao.repository.PautaRepository;
import com.sicredi.votacao.repository.SessaoRepository;
import com.sicredi.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class VotoService {

    private static final Logger logger = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoRepository sessaoRepository;
    private final CpfVotoClient cpfVotoClient;

    public VotoService(VotoRepository votoRepository,
                       PautaRepository pautaRepository,
                       SessaoRepository sessaoRepository,
                       CpfVotoClient cpfVotoClient){
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        this.sessaoRepository = sessaoRepository;
        this.cpfVotoClient = cpfVotoClient;
    }

    public VotoResponse votar(Long pautaId, RegistrarVotoRequest request){
        logger.info("Iniciando registro de voto. pautaId={}", pautaId);

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        Sessao sessao = sessaoRepository.findByPauta(pauta)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        if (LocalDateTime.now().isAfter(sessao.getFim())){
            logger.warn("Registro de voto rejeitado: sessao encerrada. pautaId={}", pautaId);
            throw new SessaoEncerradaException();
        }

        StatusCpfVoto statusCpf = cpfVotoClient.verificar(request.getCpf());

        if (StatusCpfVoto.UNABLE_TO_VOTE.equals(statusCpf)) {
            logger.warn("Registro de voto rejeitado: CPF nao habilitado para votar. pautaId={}", pautaId);
            throw new AssociadoNaoPodeVotarException(request.getCpf());
        }

        if (votoRepository.existsByPautaAndAssociadoId(pauta, request.getAssociadoId())){
            logger.warn("Registro de voto rejeitado: associado ja votou. pautaId={}", pautaId);
            throw new AssociadoJaVotouException(request.getAssociadoId(), pautaId);
        }

        Voto voto = new Voto();
        voto.setPauta(pauta);
        voto.setAssociadoId(request.getAssociadoId());
        voto.setVoto(request.getVoto());

        voto = votoRepository.save(voto);

        logger.info("Voto registrado com sucesso. votoId={}, pautaId={}", voto.getId(), pauta.getId());

        return VotoResponse.builder()
                .id(voto.getId())
                .pautaId(pauta.getId())
                .associadoId(voto.getAssociadoId())
                .voto(voto.getVoto())
                .dataHora(voto.getDataHora())
                .build();
    }

    public ResultadoVotacaoResponse obterResultado(Long pautaId){

        logger.info("Iniciando apuracao de resultado. pautaId={}", pautaId);

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

        logger.info("Resultado apurado. pautaId={}, totalVotos={}, resultado={}",
                pauta.getId(), totalVotos, resultado);

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
