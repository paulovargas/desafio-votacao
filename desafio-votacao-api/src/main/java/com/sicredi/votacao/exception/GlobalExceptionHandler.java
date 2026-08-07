package com.sicredi.votacao.exception;

import com.sicredi.votacao.dto.response.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handlePautaNaoEncontrada(
            PautaNaoEncontradaException exception,
            WebRequest request){

        return criarResposta(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleSessaoNaoEncontrada(
            SessaoNaoEncontradaException exception,
            WebRequest request){

        return criarResposta(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessaoJaExisteException.class)
    public ResponseEntity<ErroResponse> handleSessaoJaExiste(
            SessaoJaExisteException exception,
            WebRequest request){

        return criarResposta(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessaoEncerradaException.class)
    public ResponseEntity<ErroResponse> handleSessaoEncerrada(
            SessaoEncerradaException exception,
            WebRequest request){

        return criarResposta(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(AssociadoJaVotouException.class)
    public ResponseEntity<ErroResponse> handleAssociadoJaVotou(
            AssociadoJaVotouException exception,
            WebRequest request){

        return criarResposta(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    private ResponseEntity<ErroResponse> criarResposta(
            HttpStatus status,
            String mensagem,
            WebRequest request){

        ErroResponse response = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getDescription(false)
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
