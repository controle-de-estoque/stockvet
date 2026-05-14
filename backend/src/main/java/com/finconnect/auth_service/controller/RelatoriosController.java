package com.finconnect.auth_service.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.finconnect.auth_service.dto.ProdutoAtivoRelatorio;
import com.finconnect.auth_service.service.RelatoriosService;

@RestController
@RequestMapping("/api/relatorios")
public class RelatoriosController {
    
    @Autowired
    private RelatoriosService relatoriosService;

    @GetMapping("/produtos-ativos")
    public ResponseEntity<Resource> downloadRelatorioProdutosAtivos() throws IOException {

        ByteArrayInputStream stream = relatoriosService.gerarRelatorioDeProdutosAtivos(criar30ProdutosMock());
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=relatorio-produtos-ativos.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }

    public List<ProdutoAtivoRelatorio> criar30ProdutosMock() {
        List<ProdutoAtivoRelatorio> produtos = new ArrayList<>();
        
        String[] categorias = {"Eletrônicos", "Escritório", "Limpeza", "Alimentos"};
        String[] unidades = {"UN", "KG", "LT", "CX"};

        for (int i = 1; i <= 30; i++) {
            produtos.add(new ProdutoAtivoRelatorio(
                "Produto Exemplo " + i,
                categorias[i % categorias.length],
                (int) (Math.random() * 100) + 1, // quantidade entre 1 e 100
                10,                              // quantidade crítica fixa
                unidades[i % unidades.length]
            ));
        }
        return produtos;
    }
}
