package com.finconnect.auth_service.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.ProdutoAtivoRelatorio;


@Service
public class RelatoriosService {
    
    public ByteArrayInputStream gerarRelatorioDeProdutosAtivos(List<ProdutoAtivoRelatorio> produtosAtivos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Relatório-produtos-ativos-" + LocalDateTime.now());

            // Criando o cabeçalho
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nome");
            headerRow.createCell(1).setCellValue("Categoria");
            headerRow.createCell(2).setCellValue("Quantidade");
            headerRow.createCell(3).setCellValue("Qtd. Crítica");

            // Preenchendo os dados
            int rowIdx = 1;
            for (ProdutoAtivoRelatorio p : produtosAtivos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.nome());
                row.createCell(1).setCellValue(p.categoria());
                row.createCell(2).setCellValue(p.quantidade() + " " + p.unidade());
                row.createCell(3).setCellValue(p.quantidadeCritica());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
