package com.sicredi.votacao.service;

import com.sicredi.votacao.dto.request.AbrirSessaoRequest;
import com.sicredi.votacao.dto.response.SessaoResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Sessao;
import com.sicredi.votacao.repository.PautaRepository;
import com.sicredi.votacao.repository.SessaoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessaoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private SessaoService sessaoService;

    @Test
    public void deveAbrirSessaoComDuracaoDefaultDeUmMinuto() {
        Pauta pauta = criarPauta();
        AbrirSessaoRequest request = new AbrirSessaoRequest();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPauta(pauta)).thenReturn(false);
        when(sessaoRepository.save(any(Sessao.class))).thenAnswer(invocation -> {
            Sessao sessao = invocation.getArgument(0);
            sessao.setId(10L);
            return sessao;
        });

        SessaoResponse response = sessaoService.abrirSessao(1L, request);

        assertEquals(Long.valueOf(10L), response.getId());
        assertEquals(Long.valueOf(1L), response.getPautaId());
        assertNotNull(response.getDataHoraAbertura());
        assertNotNull(response.getDataHoraEncerramento());
        assertEquals(1L, Duration.between(response.getDataHoraAbertura(), response.getDataHoraEncerramento()).toMinutes());
    }

    @Test
    public void deveAbrirSessaoComDuracaoInformada() {
        Pauta pauta = criarPauta();
        AbrirSessaoRequest request = new AbrirSessaoRequest();
        request.setDuracaoMinutos(5L);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPauta(pauta)).thenReturn(false);
        when(sessaoRepository.save(any(Sessao.class))).thenAnswer(invocation -> {
            Sessao sessao = invocation.getArgument(0);
            sessao.setId(10L);
            return sessao;
        });

        SessaoResponse response = sessaoService.abrirSessao(1L, request);

        assertEquals(5L, Duration.between(response.getDataHoraAbertura(), response.getDataHoraEncerramento()).toMinutes());
    }

    @Test(expected = RuntimeException.class)
    public void naoDeveAbrirSessaoQuandoPautaNaoExiste() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        sessaoService.abrirSessao(1L, new AbrirSessaoRequest());
    }

    @Test(expected = RuntimeException.class)
    public void naoDeveAbrirSessaoDuplicadaParaMesmaPauta() {
        Pauta pauta = criarPauta();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPauta(pauta)).thenReturn(true);

        sessaoService.abrirSessao(1L, new AbrirSessaoRequest());
    }

    private Pauta criarPauta() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Pauta teste");
        return pauta;
    }
}
