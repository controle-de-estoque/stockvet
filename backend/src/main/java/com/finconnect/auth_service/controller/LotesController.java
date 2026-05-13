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
import com.finconnect.auth_service.dto.CreateLoteRequest;
import com.finconnect.auth_service.dto.LoteResponse;
import com.finconnect.auth_service.service.LoteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lotes")
public class LotesController {

    @Autowired
    private LoteService loteService;

    @GetMapping("/{estoque}")
    public ResponseEntity<List<LoteResponse>> findAllLotesByEstoque(@PathVariable UUID estoque) {
        return ResponseEntity.ok(this.loteService.findAllByEstoque(estoque));
    }

    @PostMapping()
    public ResponseEntity<LoteResponse> saveLote(@Valid @RequestBody CreateLoteRequest request) {
        return ResponseEntity.ok(this.loteService.saveLote(request));
    }
}
