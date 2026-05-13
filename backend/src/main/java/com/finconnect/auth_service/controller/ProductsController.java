package com.finconnect.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.finconnect.auth_service.dto.ProdutoResponse;
import com.finconnect.auth_service.dto.SalvarProduto;
import com.finconnect.auth_service.service.ProductsService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/produtos")
public class ProductsController {

    @Autowired
    private ProductsService productsService;
    
    @PostMapping()
    public ResponseEntity<String> salvarProduto(@RequestBody SalvarProduto request) {
        return ResponseEntity.ok().body(this.productsService.salvarProduto(request));
    }

    @PatchMapping("/desativar/{id}")
    public ResponseEntity<String> desativarProduto(@PathVariable UUID id) {
        return ResponseEntity.ok().body(this.productsService.desativarProduto(id));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<List<ProdutoResponse>> findAllProdutosFromEstoque(@PathVariable UUID id) {
        return ResponseEntity.ok().body(this.productsService.findAllProdutosFromEstoque(id));
    }

    @GetMapping("/{id}/{nome}")
    public ResponseEntity<List<ProdutoResponse>> findAllProdutosFromEstoque(@PathVariable UUID id, @PathVariable String nome) {
        return ResponseEntity.ok().body(this.productsService.findAllProdutosFromEstoqueComecandoComNome(id, nome));
    }
}