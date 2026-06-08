package com.finconnect.auth_service.controller;

import com.finconnect.auth_service.dto.ProcedimentoRequest; // DTO com os campos necessários
import com.finconnect.auth_service.entity.Procedimento;
import com.finconnect.auth_service.service.ProcedimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/procedimentos")
public class ProcedimentoController {

    @Autowired
    private ProcedimentoService procedimentoService;

    // Persistir um novo procedimento
    @PostMapping
    public ResponseEntity<Procedimento> criarProcedimento(@Valid @RequestBody ProcedimentoRequest request) {
        Procedimento novoProcedimento = procedimentoService.salvar(request);
        return ResponseEntity.ok(novoProcedimento);
    }

    // Buscar todos os procedimentos de um estoque específico
    // Filtramos apenas os ativos na camada de serviço ou aqui no controller
    @GetMapping("/estoque/{estoqueId}")
    public ResponseEntity<List<Procedimento>> listarPorEstoque(@PathVariable UUID estoqueId) {
        return ResponseEntity.ok(procedimentoService.buscarPorEstoque(estoqueId));
    }

    // Inativar um procedimento (Soft Delete)
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativarProcedimento(@PathVariable UUID id) {
        procedimentoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}