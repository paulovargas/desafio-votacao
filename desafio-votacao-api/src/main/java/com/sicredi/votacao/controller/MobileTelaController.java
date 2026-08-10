package com.sicredi.votacao.controller;

import com.sicredi.votacao.dto.mobile.MobileAcaoResponse;
import com.sicredi.votacao.dto.mobile.MobileCampoResponse;
import com.sicredi.votacao.dto.mobile.MobileOpcaoResponse;
import com.sicredi.votacao.dto.mobile.MobileTelaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile/telas")
public class MobileTelaController {

    private final String callbackBaseUrl;

    public MobileTelaController(@Value("${app.callback-base-url:}") String callbackBaseUrl) {
        this.callbackBaseUrl = removerBarraFinal(callbackBaseUrl);
    }

    @RequestMapping(value = "/nova-pauta", method = {RequestMethod.GET, RequestMethod.POST})
    public MobileTelaResponse novaPauta() {
        return MobileTelaResponse.builder()
                .id("nova-pauta")
                .tipo("FORMULARIO")
                .titulo("Nova pauta")
                .descricao("Cadastro de pauta para votacao.")
                .itens(Arrays.asList(
                        campo("titulo", "TEXTO", "Titulo", true),
                        campo("descricao", "TEXTO_LONGO", "Descricao", false)
                ))
                .acoes(Collections.singletonList(acao(
                        "cadastrar",
                        "Cadastrar",
                        "POST",
                        "/api/v1/mobile/acoes/nova-pauta",
                        Collections.emptyMap()
                )))
                .build();
    }

    @RequestMapping(value = "/abrir-sessao", method = {RequestMethod.GET, RequestMethod.POST})
    public MobileTelaResponse abrirSessao() {
        return MobileTelaResponse.builder()
                .id("abrir-sessao")
                .tipo("FORMULARIO")
                .titulo("Abrir sessao")
                .descricao("Abertura de sessao de votacao para uma pauta.")
                .itens(Arrays.asList(
                        campo("pautaId", "NUMERICO", "Id da pauta", true),
                        campo("duracaoMinutos", "NUMERICO", "Duracao em minutos", false)
                ))
                .acoes(Collections.singletonList(acao(
                        "abrir",
                        "Abrir sessao",
                        "POST",
                        "/api/v1/mobile/acoes/abrir-sessao",
                        Collections.emptyMap()
                )))
                .build();
    }

    @RequestMapping(value = "/votar", method = {RequestMethod.GET, RequestMethod.POST})
    public MobileTelaResponse votar() {
        return MobileTelaResponse.builder()
                .id("votar")
                .tipo("FORMULARIO")
                .titulo("Registrar voto")
                .descricao("Registro de voto do associado em uma pauta.")
                .itens(Arrays.asList(
                        campo("pautaId", "NUMERICO", "Id da pauta", true),
                        campo("associadoId", "NUMERICO", "Id do associado", true),
                        campo("cpf", "TEXTO", "CPF", true),
                        campoComOpcoes("voto", "SELECAO", "Voto", true, Arrays.asList("SIM", "NAO"))
                ))
                .acoes(Collections.singletonList(acao(
                        "votar",
                        "Enviar voto",
                        "POST",
                        "/api/v1/mobile/acoes/votar",
                        Collections.emptyMap()
                )))
                .build();
    }

    @RequestMapping(value = "/consultar-resultado", method = {RequestMethod.GET, RequestMethod.POST})
    public MobileTelaResponse consultarResultado() {
        return MobileTelaResponse.builder()
                .id("consultar-resultado")
                .tipo("FORMULARIO")
                .titulo("Consultar resultado")
                .descricao("Consulta do resultado da votacao por pauta.")
                .itens(Collections.singletonList(
                        campo("pautaId", "NUMERICO", "Id da pauta", true)
                ))
                .acoes(Collections.singletonList(acao(
                        "consultar",
                        "Consultar",
                        "POST",
                        "/api/v1/mobile/acoes/consultar-resultado",
                        Collections.emptyMap()
                )))
                .build();
    }

    @RequestMapping(value = "/opcoes", method = {RequestMethod.GET, RequestMethod.POST})
    public MobileTelaResponse opcoes() {
        return MobileTelaResponse.builder()
                .id("opcoes")
                .tipo("SELECAO")
                .titulo("Menu de votacao")
                .descricao("Selecione a operacao desejada.")
                .opcoes(Arrays.asList(
                        opcao("nova-pauta", "Cadastrar pauta", "/api/v1/mobile/telas/nova-pauta"),
                        opcao("abrir-sessao", "Abrir sessao", "/api/v1/mobile/telas/abrir-sessao"),
                        opcao("votar", "Registrar voto", "/api/v1/mobile/telas/votar"),
                        opcao("consultar-resultado", "Consultar resultado", "/api/v1/mobile/telas/consultar-resultado")
                ))
                .build();
    }

    private MobileCampoResponse campo(String id, String tipo, String rotulo, boolean obrigatorio) {
        return campoComOpcoes(id, tipo, rotulo, obrigatorio, null);
    }

    private MobileCampoResponse campoComOpcoes(String id, String tipo, String rotulo, boolean obrigatorio, java.util.List<String> opcoes) {
        return MobileCampoResponse.builder()
                .id(id)
                .tipo(tipo)
                .rotulo(rotulo)
                .obrigatorio(obrigatorio)
                .opcoes(opcoes)
                .build();
    }

    private MobileAcaoResponse acao(String id, String rotulo, String metodo, String url, Map<String, Object> body) {
        return MobileAcaoResponse.builder()
                .id(id)
                .rotulo(rotulo)
                .metodo(metodo)
                .url(url(url))
                .body(body)
                .build();
    }

    private MobileOpcaoResponse opcao(String id, String rotulo, String url) {
        Map<String, Object> body = new HashMap<>();
        body.put("tela", id);

        return MobileOpcaoResponse.builder()
                .id(id)
                .rotulo(rotulo)
                .metodo("POST")
                .url(url(url))
                .body(body)
                .build();
    }

    private String url(String path) {
        return callbackBaseUrl + path;
    }

    private String removerBarraFinal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        return value.trim().replaceAll("/+$", "");
    }
}
