package com.sicredi.votacao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicredi.votacao.dto.response.PautaResponse;
import com.sicredi.votacao.dto.response.ResultadoVotacaoResponse;
import com.sicredi.votacao.dto.response.SessaoResponse;
import com.sicredi.votacao.dto.response.VotoResponse;
import com.sicredi.votacao.enums.OpcaoVoto;
import com.sicredi.votacao.exception.AssociadoJaVotouException;
import com.sicredi.votacao.exception.PautaNaoEncontradaException;
import com.sicredi.votacao.exception.SessaoJaExisteException;
import com.sicredi.votacao.service.PautaService;
import com.sicredi.votacao.service.SessaoService;
import com.sicredi.votacao.service.VotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {
        PautaController.class,
        SessaoController.class,
        VotoController.class
})
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PautaService pautaService;

    @MockBean
    private SessaoService sessaoService;

    @MockBean
    private VotoService votoService;

    @Test
    public void deveCadastrarPautaComStatusCreated() throws Exception {
        when(pautaService.cadastrar(any())).thenReturn(PautaResponse.builder()
                .id(1L)
                .titulo("Pauta teste")
                .descricao("Descricao teste")
                .dataCriacao(LocalDateTime.of(2026, 8, 8, 9, 0))
                .build());

        Map<String, Object> request = new HashMap<>();
        request.put("titulo", "Pauta teste");
        request.put("descricao", "Descricao teste");

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Pauta teste"));
    }

    @Test
    public void naoDeveCadastrarPautaSemTitulo() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("descricao", "Descricao teste");

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"));
    }

    @Test
    public void deveAbrirSessaoComStatusOk() throws Exception {
        when(sessaoService.abrirSessao(eq(1L), any())).thenReturn(SessaoResponse.builder()
                .id(10L)
                .pautaId(1L)
                .dataHoraAbertura(LocalDateTime.of(2026, 8, 8, 9, 0))
                .dataHoraEncerramento(LocalDateTime.of(2026, 8, 8, 9, 5))
                .build());

        Map<String, Object> request = new HashMap<>();
        request.put("duracaoMinutos", 5);

        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.pautaId").value(1));
    }

    @Test
    public void naoDeveAbrirSessaoComDuracaoZero() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("duracaoMinutos", 0);

        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"));
    }

    @Test
    public void naoDeveAbrirSessaoDuplicada() throws Exception {
        when(sessaoService.abrirSessao(eq(1L), any())).thenThrow(new SessaoJaExisteException(1L));

        Map<String, Object> request = new HashMap<>();
        request.put("duracaoMinutos", 5);

        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem", containsString("sess")));
    }

    @Test
    public void deveRegistrarVotoComStatusOk() throws Exception {
        when(votoService.votar(eq(1L), any())).thenReturn(VotoResponse.builder()
                .id(20L)
                .pautaId(1L)
                .associadoId(100L)
                .voto(OpcaoVoto.SIM)
                .dataHora(LocalDateTime.of(2026, 8, 8, 9, 1))
                .build());

        Map<String, Object> request = new HashMap<>();
        request.put("associadoId", 100L);
        request.put("voto", "SIM");

        mockMvc.perform(post("/api/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.voto").value("SIM"));
    }

    @Test
    public void naoDeveRegistrarVotoDuplicado() throws Exception {
        when(votoService.votar(eq(1L), any())).thenThrow(new AssociadoJaVotouException(100L, 1L));

        Map<String, Object> request = new HashMap<>();
        request.put("associadoId", 100L);
        request.put("voto", "SIM");

        mockMvc.perform(post("/api/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    public void deveObterResultadoComStatusOk() throws Exception {
        when(votoService.obterResultado(1L)).thenReturn(new ResultadoVotacaoResponse(
                1L,
                "Pauta teste",
                3L,
                1L,
                4L,
                "APROVADA"
        ));

        mockMvc.perform(get("/api/v1/pautas/1/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(1))
                .andExpect(jsonPath("$.resultado").value("APROVADA"));
    }

    @Test
    public void naoDeveObterResultadoQuandoPautaNaoExiste() throws Exception {
        when(votoService.obterResultado(1L)).thenThrow(new PautaNaoEncontradaException(1L));

        mockMvc.perform(get("/api/v1/pautas/1/resultado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"));
    }
}
