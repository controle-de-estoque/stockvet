package ufrpe.stockvet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ufrpe.stockvet.DTO.*;
import ufrpe.stockvet.service.MovimentacaoService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    @PostMapping("/entrada")
    public ResponseEntity<RespostaMovimentacaoDTO> registrarEntrada(@RequestBody EntradaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoService.registrarEntrada(dto));
    }


    @PostMapping("/saida")
    public ResponseEntity<RespostaMovimentacaoDTO> registrarSaida(@RequestBody SaidaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoService.registrarSaida(dto));
    }

    @GetMapping
    public ResponseEntity<List<RespostaMovimentacaoDTO>> listarTodas() {
        return ResponseEntity.ok(movimentacaoService.listarTodas());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<RespostaMovimentacaoDTO>> listarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(movimentacaoService.listarPorTipo(tipo));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<RespostaMovimentacaoDTO>> listarPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        return ResponseEntity.ok(movimentacaoService.listarPorPeriodo(inicio, fim));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaMovimentacaoDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(movimentacaoService.buscarPorId(id));
    }
}