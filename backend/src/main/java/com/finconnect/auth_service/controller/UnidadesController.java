package com.finconnect.auth_service.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finconnect.auth_service.dto.SalvarUnidade;
import com.finconnect.auth_service.dto.UnidadeResponse;
import com.finconnect.auth_service.service.UnidadeService;

@RestController
@RequestMapping("/api/produtos/unidade")
public class UnidadesController {

    @Autowired
    private UnidadeService unidadeService;

    @PostMapping
    public ResponseEntity<String> salvarUnidade(@RequestBody SalvarUnidade request) {
        return ResponseEntity.ok().body(this.unidadeService.salvarUnidade(request));
    }

    @GetMapping("/{estoque}")
    public ResponseEntity<List<UnidadeResponse>> buscarUnidadesPorEstoque(@PathVariable UUID estoque) {
        return ResponseEntity.ok(this.unidadeService.buscarUnidadesPorEstoque(estoque));
    }
}
