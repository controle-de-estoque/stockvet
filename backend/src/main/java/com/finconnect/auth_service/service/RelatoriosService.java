package com.finconnect.auth_service.service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.ProdutoAtivoRelatorio;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;


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

    public void gerarRelatorioPdf(HttpServletResponse response, List<ProdutoAtivoRelatorio> produtosAtivos) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-produtos-ativos.pdf");

        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // --- Título (Centralizado e Negrito) ---
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório de Produtos Ativos", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Data do Relatório (Alinhado à direita)
            Font fontDate = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph date = new Paragraph("Data do Relatório: " + LocalDateTime.now().getDayOfMonth() + " de Maio de 2026", fontDate);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            
            document.add(new Paragraph(" ")); // Linha em branco
            document.add(new Paragraph("Este documento contém a listagem completa dos produtos ativos e seus respetivos níveis de stock.\n\n"));

            // --- Tabela (5 colunas) ---
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {3f, 2f, 1.5f, 1.5f, 1.5f}); // Proporção das colunas

            // Cabeçalho estilizado (Igual ao DOCX)
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Stream.of("Nome", "Categoria", "Unidade", "Qtd.", "Qtd. Crítica").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(new Color(217, 217, 217)); // Cinza claro D9D9D9
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(columnTitle, fontHeader));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(5);
                table.addCell(header);
            });

            // Preenchendo os dados
            for (ProdutoAtivoRelatorio p : produtosAtivos) {
                table.addCell(p.nome());
                table.addCell(p.categoria());
                table.addCell(p.unidade());
                table.addCell(String.valueOf(p.quantidade()));
                table.addCell(String.valueOf(p.quantidadeCritica()));
            }

            document.add(table);

            // --- Resumo de Inventário ---
            document.add(new Paragraph("\n"));
            Paragraph summaryHeader = new Paragraph("Resumo de Inventário", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(summaryHeader);
            
            document.add(new Paragraph("Total de itens listados: " + produtosAtivos.size()));
            
            long criticos = produtosAtivos.stream()
                    .filter(p -> p.quantidade() <= p.quantidadeCritica())
                    .count();
            document.add(new Paragraph("Itens em nível crítico/alerta: " + criticos));

        } catch (DocumentException e) {
            throw new IOException("Erro ao gerar PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
}
