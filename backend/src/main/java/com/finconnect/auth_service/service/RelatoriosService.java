package com.finconnect.auth_service.service;

import com.finconnect.auth_service.dto.ConsumoPorPeriodoRelatorio;
import com.finconnect.auth_service.dto.PeriodoRelatorio;
import com.finconnect.auth_service.dto.ProdutoAtivoRelatorio;
import com.finconnect.auth_service.entity.TipoMovimentacao;
import com.finconnect.auth_service.repository.MovimentacaoLoteRepository;
import com.finconnect.auth_service.repository.ProdutoRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RelatoriosService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoLoteRepository movimentacaoLoteRepository;
    
    public ByteArrayInputStream gerarRelatorioDeProdutosAtivos(PeriodoRelatorio request) throws IOException {
        var produtosAtivos = produtoRepository.relatorioProdutosAtivos(request.estoque());

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Relatório-produtos-ativos");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nome");
            headerRow.createCell(1).setCellValue("Categoria");
            headerRow.createCell(2).setCellValue("Quantidade");
            headerRow.createCell(3).setCellValue("Qtd. Crítica");

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

    public void produtosAtivosPdf(HttpServletResponse response, PeriodoRelatorio request) throws IOException {
        var produtosAtivos = this.produtoRepository.relatorioProdutosAtivos(request.estoque());
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-produtos-ativos.pdf");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório de Produtos Ativos", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Font fontDate = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph date = new Paragraph("Data do Relatório: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontDate);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            
            document.add(new Paragraph(" ")); 

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {3f, 2f, 1.5f, 1.5f, 1.5f}); 

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Stream.of("Nome", "Categoria", "Unidade", "Qtd.", "Qtd. Crítica").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(new Color(217, 217, 217)); 
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(columnTitle, fontHeader));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(5);
                table.addCell(header);
            });

            for (ProdutoAtivoRelatorio p : produtosAtivos) {
                table.addCell(p.nome());
                table.addCell(p.categoria());
                table.addCell(p.unidade());
                table.addCell(String.valueOf(p.quantidade()));
                table.addCell(String.valueOf(p.quantidadeCritica()));
            }

            document.add(table);
        } catch (DocumentException e) {
            throw new IOException("Erro ao gerar PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }

    public void movimentacoesPeriodoPdf(HttpServletResponse response, PeriodoRelatorio request) throws IOException {
        List<ConsumoPorPeriodoRelatorio> dados = movimentacaoLoteRepository.findConsumoPorPeriodo(request.estoque(), request.inicio(), request.fim());
        gerarPdfMovimentacoesGeneric(response, dados, "Histórico Geral de Movimentações");
    }

    public void movimentacoesPeriodoPorTipoPdf(HttpServletResponse response, PeriodoRelatorio request, TipoMovimentacao tipo) throws IOException {
        List<ConsumoPorPeriodoRelatorio> dados = movimentacaoLoteRepository.findConsumoPorPeriodo(request.estoque(), request.inicio(), request.fim())
                .stream()
                .filter(m -> m.tipo() == tipo)
                .toList();

        String titulo = "Histórico de " + (tipo == TipoMovimentacao.SAIDA ? "Saídas" : "Entradas");
        gerarPdfMovimentacoesGeneric(response, dados, titulo);
    }

    public ByteArrayInputStream gerarExcelMovimentacoes(PeriodoRelatorio request) throws IOException {
        List<ConsumoPorPeriodoRelatorio> dados = movimentacaoLoteRepository.findConsumoPorPeriodo(request.estoque(), request.inicio(), request.fim());
        return gerarExcelMovimentacoesGeneric(dados);
    }

    public ByteArrayInputStream gerarExcelMovimentacoesPorTipo(PeriodoRelatorio request, TipoMovimentacao tipo) throws IOException {
        List<ConsumoPorPeriodoRelatorio> dados = movimentacaoLoteRepository.findConsumoPorPeriodo(request.estoque(), request.inicio(), request.fim())
                .stream()
                .filter(m -> m.tipo() == tipo)
                .toList();
        return gerarExcelMovimentacoesGeneric(dados);
    }

    private void gerarPdfMovimentacoesGeneric(HttpServletResponse response, List<ConsumoPorPeriodoRelatorio> dados, String tituloRelatorio) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-movimentacoes.pdf");

        Document document = new Document(PageSize.A4.rotate()); 
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph(tituloRelatorio, fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Font fontDate = FontFactory.getFont(FontFactory.HELVETICA, 10);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            Paragraph date = new Paragraph("Emitido em: " + LocalDateTime.now().format(formatter), fontDate);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            
            document.add(new Paragraph(" ")); 

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {1.5f, 3f, 1.2f, 2f, 2.5f, 2.5f}); 

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Stream.of("Tipo", "Produto", "Qtd.", "Lote", "Movimentado Por", "Data/Hora").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(new Color(217, 217, 217));
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(columnTitle, fontHeader));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(5);
                table.addCell(header);
            });

            for (ConsumoPorPeriodoRelatorio m : dados) {
                table.addCell(m.tipo().toString());
                table.addCell(m.produto());
                table.addCell(String.valueOf(m.quantidade()));
                table.addCell(m.lote() != null ? m.lote() : "-");
                table.addCell(m.movimentadoPor());
                table.addCell(m.dataHora().format(formatter));
            }

            document.add(table);
        } catch (DocumentException e) {
            throw new IOException("Erro ao gerar PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }

    private ByteArrayInputStream gerarExcelMovimentacoesGeneric(List<ConsumoPorPeriodoRelatorio> dados) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Movimentações");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tipo");
            headerRow.createCell(1).setCellValue("Produto");
            headerRow.createCell(2).setCellValue("Quantidade");
            headerRow.createCell(3).setCellValue("Lote");
            headerRow.createCell(4).setCellValue("Movimentado Por");
            headerRow.createCell(5).setCellValue("Data/Hora");

            int rowIdx = 1;
            for (ConsumoPorPeriodoRelatorio m : dados) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(m.tipo().toString());
                row.createCell(1).setCellValue(m.produto());
                row.createCell(2).setCellValue(m.quantidade());
                row.createCell(3).setCellValue(m.lote() != null ? m.lote() : "-");
                row.createCell(4).setCellValue(m.movimentadoPor());
                row.createCell(5).setCellValue(m.dataHora().format(formatter));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}