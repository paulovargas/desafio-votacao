package com.sicredi.votacao.service;

import com.sicredi.votacao.dto.request.CriarPautaRequest;
import com.sicredi.votacao.dto.response.PautaResponse;
import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.repository.PautaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    public void deveCadastrarPauta() {
        CriarPautaRequest request = new CriarPautaRequest();
        request.setTitulo("Compra de equipamentos");
        request.setDescricao("Votacao para aprovar compra de equipamentos");

        Pauta pautaSalva = new Pauta();
        pautaSalva.setId(1L);
        pautaSalva.setTitulo(request.getTitulo());
        pautaSalva.setDescricao(request.getDescricao());
        pautaSalva.setDataCriacao(LocalDateTime.now());

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pautaSalva);

        PautaResponse response = pautaService.cadastrar(request);

        assertEquals(Long.valueOf(1L), response.getId());
        assertEquals("Compra de equipamentos", response.getTitulo());
        assertEquals("Votacao para aprovar compra de equipamentos", response.getDescricao());
        assertNotNull(response.getDataCriacao());
    }
}
