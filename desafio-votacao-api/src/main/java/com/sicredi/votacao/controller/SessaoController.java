package com.sicredi.votacao.controller;

import com.sicredi.votacao.dto.request.AbrirSessaoRequest;
import com.sicredi.votacao.dto.response.SessaoResponse;
import com.sicredi.votacao.service.SessaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service){
        this.service = service;
    }

    @PostMapping("/{id}/sessao")
    public ResponseEntity<SessaoResponse> abrirSesao(@PathVariable Long id,
                                                     @RequestBody(required = false)AbrirSessaoRequest request){

        if(request == null){
            request = new AbrirSessaoRequest();
        }

        return ResponseEntity.ok(service.abrirSessao(id, request));
    }
}
