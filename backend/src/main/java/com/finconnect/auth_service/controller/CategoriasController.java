package com.finconnect.auth_service.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finconnect.auth_service.dto.CategoriaResponse;
import com.finconnect.auth_service.dto.SalvarCategoria;
import com.finconnect.auth_service.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriasController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<String> salvarCategoria(@RequestBody SalvarCategoria request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoriaService.salvarCategoria(request));
    }

    @GetMapping("/{estoque}")
    public ResponseEntity<List<CategoriaResponse>> buscarCategoriasPorEstoque(@PathVariable UUID estoque) {
        return ResponseEntity.ok(this.categoriaService.buscarCategoriasPorEstoque(estoque));
    }
}
