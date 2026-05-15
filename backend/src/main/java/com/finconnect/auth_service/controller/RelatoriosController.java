package com.finconnect.auth_service.controller;

import com.finconnect.auth_service.dto.PeriodoRelatorio;
import com.finconnect.auth_service.entity.TipoMovimentacao;
import com.finconnect.auth_service.service.RelatoriosService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/relatorios")
public class RelatoriosController {
    
    @Autowired
    private RelatoriosService relatoriosService;

    @PostMapping("/produtos-ativos/excel")
    public ResponseEntity<Resource> downloadRelatorioProdutosAtivos(@Valid @RequestBody PeriodoRelatorio request) throws IOException {
        ByteArrayInputStream stream = relatoriosService.gerarRelatorioDeProdutosAtivos(request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=relatorio-produtos-ativos.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }

    @PostMapping("/produtos-ativos/pdf")
    public void downloadProdutosAtivospdf(HttpServletResponse response, @Valid @RequestBody PeriodoRelatorio request) throws IOException {
        relatoriosService.produtosAtivosPdf(response, request);
    }

    @PostMapping("/historico-movimentacoes/pdf")
    public void downloadHistoricoMovimentacoesPdf(HttpServletResponse response, @Valid @RequestBody PeriodoRelatorio request) throws IOException {
        relatoriosService.movimentacoesPeriodoPdf(response, request);
    }

    @PostMapping("/historico-saida/pdf")
    public void downloadHistoricoSaidaPdf(HttpServletResponse response, @Valid @RequestBody PeriodoRelatorio request) throws IOException {
        relatoriosService.movimentacoesPeriodoPorTipoPdf(response, request, TipoMovimentacao.SAIDA);
    }

    @PostMapping("/historico-movimentacoes/excel")
    public ResponseEntity<Resource> downloadHistoricoMovimentacoesExcel(@Valid @RequestBody PeriodoRelatorio request) throws IOException {
        ByteArrayInputStream stream = relatoriosService.gerarExcelMovimentacoes(request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=historico-movimentacoes.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }

    @PostMapping("/historico-saida/excel")
    public ResponseEntity<Resource> downloadHistoricoSaidaExcel(@Valid @RequestBody PeriodoRelatorio request) throws IOException {
        ByteArrayInputStream stream = relatoriosService.gerarExcelMovimentacoesPorTipo(request, TipoMovimentacao.SAIDA);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=historico-saidas.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }
}