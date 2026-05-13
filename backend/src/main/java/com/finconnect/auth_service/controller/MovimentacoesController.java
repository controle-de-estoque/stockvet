package com.finconnect.auth_service.controller;

import com.finconnect.auth_service.dto.CreateMovimentacaoEntradaRequest;
import com.finconnect.auth_service.dto.CreateMovimentacaoSaidaRequest;
import com.finconnect.auth_service.service.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacoesController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping("/entrada")
    public ResponseEntity<Void> registrarEntrada(@Valid @RequestBody CreateMovimentacaoEntradaRequest request) {
        movimentacaoService.registrarEntrada(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saida")
    public ResponseEntity<Void> registrarSaida(@Valid @RequestBody CreateMovimentacaoSaidaRequest request) {
        movimentacaoService.registrarSaida(request);
        return ResponseEntity.ok().build();
    }
}
