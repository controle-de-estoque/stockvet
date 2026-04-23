package ufrpe.stockvet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ufrpe.stockvet.DTO.PermissaoDTO;
import ufrpe.stockvet.models.Permissao;
import ufrpe.stockvet.service.PermissaoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permissoes")
@RequiredArgsConstructor
public class PermissaoController {

    private final PermissaoService permissaoService;

    @PostMapping
    public ResponseEntity<Permissao> criar(@RequestBody PermissaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissaoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<Permissao>> listarTodas() {
        return ResponseEntity.ok(permissaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permissao> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(permissaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permissao> atualizar(@PathVariable UUID id, @RequestBody PermissaoDTO dto) {
        return ResponseEntity.ok(permissaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        permissaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}