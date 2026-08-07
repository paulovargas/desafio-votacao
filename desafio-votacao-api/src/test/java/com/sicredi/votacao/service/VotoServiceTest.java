package com.sicredi.votacao.service;

import com.sicredi.votacao.dto.request.RegistrarVotoRequest;
import com.sicredi.votacao.dto.response.ResultadoVotacaoResponse;
import com.sicredi.votacao.dto.response.VotoResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Sessao;
import com.sicredi.votacao.entity.Voto;
import com.sicredi.votacao.enums.OpcaoVoto;
import com.sicredi.votacao.repository.PautaRepository;
import com.sicredi.votacao.repository.SessaoRepository;
import com.sicredi.votacao.repository.VotoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @InjectMocks
    private VotoService votoService;

    @Test
    public void deveRegistrarVoto() {
        Pauta pauta = criarPauta();
        Sessao sessao = criarSessaoAberta(pauta);
        RegistrarVotoRequest request = criarRequestVoto(100L, OpcaoVoto.SIM);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.of(sessao));
        when(votoRepository.existsByPautaAndAssociadoId(pauta, 100L)).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> {
            Voto voto = invocation.getArgument(0);
            voto.setId(20L);
            voto.setDataHora(LocalDateTime.now());
            return voto;
        });

        VotoResponse response = votoService.votar(1L, request);

        assertEquals(Long.valueOf(20L), response.getId());
        assertEquals(Long.valueOf(1L), response.getPautaId());
        assertEquals(Long.valueOf(100L), response.getAssociadoId());
        assertEquals(OpcaoVoto.SIM, response.getVoto());
    }

    @Test(expected = RuntimeException.class)
    public void naoDeveVotarQuandoPautaNaoExiste() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        votoService.votar(1L, criarRequestVoto(100L, OpcaoVoto.SIM));
    }

    @Test(expected = RuntimeException.class)
    public void naoDeveVotarQuandoSessaoNaoExiste() {
        Pauta pauta = criarPauta();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.empty());

        votoService.votar(1L, criarRequestVoto(100L, OpcaoVoto.SIM));
    }

    @Test(expected = RuntimeException.class)
    public void naoDeveVotarQuandoSessaoEstaEncerrada() {
        Pauta pauta = criarPauta();
        Sessao sessao = new Sessao();
        sessao.setPauta(pauta);
        sessao.setInicio(LocalDateTime.now().minusMinutes(2));
        sessao.setFim(LocalDateTime.now().minusMinutes(1));

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.of(sessao));

        votoService.votar(1L, criarRequestVoto(100L, OpcaoVoto.SIM));
    }

    @Test(expected = RuntimeException.class)
    public void naoDevePermitirVotoDuplicadoNaMesmaPauta() {
        Pauta pauta = criarPauta();
        Sessao sessao = criarSessaoAberta(pauta);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.of(sessao));
        when(votoRepository.existsByPautaAndAssociadoId(pauta, 100L)).thenReturn(true);

        votoService.votar(1L, criarRequestVoto(100L, OpcaoVoto.SIM));
    }

    @Test
    public void deveRetornarResultadoAprovado() {
        Pauta pauta = criarPauta();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.SIM)).thenReturn(3L);
        when(votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.NAO)).thenReturn(1L);

        ResultadoVotacaoResponse response = votoService.obterResultado(1L);

        assertEquals(Long.valueOf(1L), response.getPautaId());
        assertEquals("Pauta teste", response.getTitulo());
        assertEquals(3L, response.getVotosSim());
        assertEquals(1L, response.getVotosNao());
        assertEquals(4L, response.getTotalVotos());
        assertEquals("APROVADA", response.getResultado());
    }

    @Test
    public void deveRetornarResultadoReprovado() {
        Pauta pauta = criarPauta();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.SIM)).thenReturn(1L);
        when(votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.NAO)).thenReturn(2L);

        ResultadoVotacaoResponse response = votoService.obterResultado(1L);

        assertEquals("REPROVADA", response.getResultado());
        assertEquals(3L, response.getTotalVotos());
    }

    @Test
    public void deveRetornarResultadoEmpatado() {
        Pauta pauta = criarPauta();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaAndVoto(pauta, OpcaoVoto.NAO)).thenReturn(2L);

        ResultadoVotacaoResponse response = votoService.obterResultado(1L);

        assertEquals("EMPATE", response.getResultado());
        assertEquals(4L, response.getTotalVotos());
    }

    @Test(expected = RuntimeException.class)
    public void naoDeveRetornarResultadoQuandoPautaNaoExiste() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        votoService.obterResultado(1L);
    }

    private Pauta criarPauta() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Pauta teste");
        return pauta;
    }

    private Sessao criarSessaoAberta(Pauta pauta) {
        Sessao sessao = new Sessao();
        sessao.setPauta(pauta);
        sessao.setInicio(LocalDateTime.now().minusMinutes(1));
        sessao.setFim(LocalDateTime.now().plusMinutes(1));
        return sessao;
    }

    private RegistrarVotoRequest criarRequestVoto(Long associadoId, OpcaoVoto opcaoVoto) {
        RegistrarVotoRequest request = new RegistrarVotoRequest();
        request.setAssociadoId(associadoId);
        request.setVoto(opcaoVoto);
        return request;
    }
}
