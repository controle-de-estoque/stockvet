package ufrpe.stockvet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ufrpe.stockvet.models.Lote;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.service.LoteService;

import java.util.UUID;

@RestController
@RequestMapping("/lotes")
public class LoteController {

    @Autowired
    private LoteService loteService;

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }


    @PostMapping
    public ResponseEntity criarLote(@RequestBody Lote lote) {
        try {
            return ResponseEntity.ok(loteService.criarLote(lote, getUsuarioLogado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/produto/{produtoId}")
    public ResponseEntity listarLotesPorProduto(@PathVariable UUID produtoId) {
        return ResponseEntity.ok(loteService.listarLotesPorProduto(produtoId, getUsuarioLogado()));
    }

    @GetMapping
    public ResponseEntity listarLotes() {
        return ResponseEntity.ok(loteService.listarLotesPorEstoque(getUsuarioLogado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarLote(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(loteService.buscarLote(id, getUsuarioLogado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}