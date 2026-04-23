package ufrpe.stockvet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ufrpe.stockvet.models.Estoque;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.service.EstoqueService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity criarEstoque(@RequestBody Estoque estoque) {
        try {
            return ResponseEntity.ok(estoqueService.criarEstoque(estoque, getUsuarioLogado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarEstoque(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(estoqueService.buscarEstoque(id, getUsuarioLogado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity listarEstoques() {
        List<Estoque> estoques = estoqueService.listarEstoques(getUsuarioLogado());

        if (estoques.isEmpty()) {
            return ResponseEntity.ok("nenhum estoque encontrado para esse usuário");
        }

        return ResponseEntity.ok(estoques);
    }
    @PutMapping("/{id}")
    public ResponseEntity editarEstoque(@PathVariable UUID id, @RequestBody Estoque estoque) {
        try {
            return ResponseEntity.ok(estoqueService.editarEstoque(id, estoque, getUsuarioLogado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarEstoque(@PathVariable UUID id) {
        try {
            estoqueService.deletarEstoque(id, getUsuarioLogado());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}