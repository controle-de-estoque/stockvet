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
import com.finconnect.auth_service.dto.CessionarioResponse;
import com.finconnect.auth_service.dto.CreateCessionarioRequest;
import com.finconnect.auth_service.service.CessionarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cessionarios")
public class CessionariosController {

    @Autowired
    private CessionarioService cessionarioService;

    @GetMapping("/{estoque}")
    public ResponseEntity<List<CessionarioResponse>> findAllCessionariosByEstoque(@PathVariable UUID estoque) {
        return ResponseEntity.ok(this.cessionarioService.findAllByEstoque(estoque));
    }

    @PostMapping()
    public ResponseEntity<CessionarioResponse> saveCessionario(@Valid @RequestBody CreateCessionarioRequest request) {
        return ResponseEntity.ok(this.cessionarioService.saveCessionario(request));
    }
}
