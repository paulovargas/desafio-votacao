package com.sicredi.votacao.controller;

import com.sicredi.votacao.dto.request.CriarPautaRequest;
import com.sicredi.votacao.dto.request.MobileAbrirSessaoRequest;
import com.sicredi.votacao.dto.request.MobileConsultarResultadoRequest;
import com.sicredi.votacao.dto.request.MobileRegistrarVotoRequest;
import com.sicredi.votacao.dto.response.PautaResponse;
import com.sicredi.votacao.dto.response.ResultadoVotacaoResponse;
import com.sicredi.votacao.dto.response.SessaoResponse;
import com.sicredi.votacao.dto.response.VotoResponse;
import com.sicredi.votacao.service.PautaService;
import com.sicredi.votacao.service.SessaoService;
import com.sicredi.votacao.service.VotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/mobile/acoes")
public class MobileAcaoController {

    private final PautaService pautaService;
    private final SessaoService sessaoService;
    private final VotoService votoService;

    public MobileAcaoController(PautaService pautaService,
                                SessaoService sessaoService,
                                VotoService votoService) {
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.votoService = votoService;
    }

    @PostMapping("/nova-pauta")
    public ResponseEntity<PautaResponse> cadastrarPauta(@RequestBody @Valid CriarPautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pautaService.cadastrar(request));
    }

    @PostMapping("/abrir-sessao")
    public ResponseEntity<SessaoResponse> abrirSessao(@RequestBody @Valid MobileAbrirSessaoRequest request) {
        return ResponseEntity.ok(sessaoService.abrirSessao(request.getPautaId(), request));
    }

    @PostMapping("/votar")
    public ResponseEntity<VotoResponse> votar(@RequestBody @Valid MobileRegistrarVotoRequest request) {
        return ResponseEntity.ok(votoService.votar(request.getPautaId(), request.toRegistrarVotoRequest()));
    }

    @PostMapping("/consultar-resultado")
    public ResponseEntity<ResultadoVotacaoResponse> consultarResultado(
            @RequestBody @Valid MobileConsultarResultadoRequest request) {
        return ResponseEntity.ok(votoService.obterResultado(request.getPautaId()));
    }
}
