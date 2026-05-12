package com.finconnect.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finconnect.auth_service.dto.CategoriaResponse;
import com.finconnect.auth_service.dto.ProdutoResponse;
import com.finconnect.auth_service.dto.SalvarCategoria;
import com.finconnect.auth_service.dto.SalvarProduto;
import com.finconnect.auth_service.dto.SalvarUnidade;
import com.finconnect.auth_service.dto.UnidadeResponse;
import com.finconnect.auth_service.service.ProductsService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/produtos")
public class ProductsController {

    @Autowired
    private ProductsService productsService;
    
    @PostMapping("/unidade")
    public ResponseEntity<String> salvarUnidade(@RequestBody SalvarUnidade request) {
        return ResponseEntity.ok().body(this.productsService.salvarUnidade(request));
    }

    @PostMapping("/categoria")
    public ResponseEntity<String> salvarCategoria(@RequestBody SalvarCategoria request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productsService.salvarCategoria(request));
    }

    @GetMapping("/categoria/{estoque}")
    public ResponseEntity<List<CategoriaResponse>> buscarCategoriasPorEstoque(@PathVariable UUID estoque) {
        return ResponseEntity.ok(this.productsService.buscarCategoriasPorEstoque(estoque));
    }

    @GetMapping("/unidade/{estoque}")
    public ResponseEntity<List<UnidadeResponse>> buscarUnidadesPorEstoque(@PathVariable UUID estoque) {
        return ResponseEntity.ok(this.productsService.buscarUnidadesPorEstoque(estoque));
    }

    @PostMapping()
    public ResponseEntity<String> salvarProduto(@RequestBody SalvarProduto request) {
        return ResponseEntity.ok().body(this.productsService.salvarProduto(request));
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