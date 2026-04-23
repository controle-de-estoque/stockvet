package ufrpe.stockvet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ufrpe.stockvet.models.UnidadeMedida;
import ufrpe.stockvet.service.UnidadeMedidaService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/unidades-medida")
public class UnidadeMedidaController {

    @Autowired
    private UnidadeMedidaService service;

    @PostMapping
    public ResponseEntity<UnidadeMedida> criar(@RequestBody UnidadeMedida unidadeMedida) {
        return ResponseEntity.ok(service.criar(unidadeMedida));
    }

    @GetMapping
    public ResponseEntity<List<UnidadeMedida>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.ok().build();
    }
}