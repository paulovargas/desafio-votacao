package com.sicredi.votacao.controller;

import com.sicredi.votacao.dto.request.CriarPautaRequest;
import com.sicredi.votacao.dto.response.PautaResponse;
import com.sicredi.votacao.service.PautaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final PautaService service;

    public PautaController(PautaService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PautaResponse> cadastrar(@RequestBody @Valid CriarPautaRequest request){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.cadastrar(request));
    }

    @GetMapping
    public ResponseEntity<List<PautaResponse>> listar(){
        return ResponseEntity.ok(service.listar());
    }
}
