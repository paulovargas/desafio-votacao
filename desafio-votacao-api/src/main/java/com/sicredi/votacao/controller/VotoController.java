package com.sicredi.votacao.controller;

import com.sicredi.votacao.dto.request.RegistrarVotoRequest;
import com.sicredi.votacao.dto.response.ResultadoVotacaoResponse;
import com.sicredi.votacao.dto.response.VotoResponse;
import com.sicredi.votacao.service.VotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/pautas")
public class VotoController {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping("/{id}/votos")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable Long id,
            @RequestBody @Valid RegistrarVotoRequest request) {

        return ResponseEntity.ok(votoService.votar(id, request));
    }

    @GetMapping("/{id}/resultado")
    public ResponseEntity<ResultadoVotacaoResponse> resultado(@PathVariable Long id){
        return ResponseEntity.ok(votoService.obterResultado(id));
    }
}
