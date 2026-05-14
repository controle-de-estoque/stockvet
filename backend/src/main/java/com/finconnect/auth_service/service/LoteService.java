package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.CreateLoteRequest;
import com.finconnect.auth_service.dto.LoteResponse;
import com.finconnect.auth_service.entity.Estoque;
import com.finconnect.auth_service.entity.Lote;
import com.finconnect.auth_service.entity.Produto;
import com.finconnect.auth_service.exception_handler.exceptions.LoteNotFoundException;
import com.finconnect.auth_service.exception_handler.exceptions.ResourceNotFoundException;
import com.finconnect.auth_service.repository.EstoqueRepository;
import com.finconnect.auth_service.repository.LoteRepository;
import com.finconnect.auth_service.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class LoteService {

    private static final Logger logger = LoggerFactory.getLogger(LoteService.class);
    
    @Autowired
    private LoteRepository repository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    public List<LoteResponse> findAllByEstoque(UUID estoque) {
        logger.info("Buscando lotes do estoque: {}", estoque);

        return this.repository.findAllByEstoqueId(estoque).stream()
            .map(l -> new LoteResponse(
                l.getIdentificador(),
                l.getDataValidade(),
                l.getQuantidadeRecebida(),
                l.getQuantidadeAtual(),
            l.getProduto().getId(),
            l.getEstoque().getId()
        )).toList();
    }

    public LoteResponse saveLote(CreateLoteRequest request) {
        logger.info("Tentando registrar lote");

        Produto produto = produtoRepository.findById(request.produto())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + request.produto()));
        
        Estoque estoque = estoqueRepository.findById(request.estoque())
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado ID: " + request.estoque()));

        Lote lote = new Lote(
            request.id(),
            request.dataValidade(),
            request.quantidadeRecebida(),
            request.quantidadeRecebida(), //no momento da criação do lote, a quantidade atual é a mesma recebida
            estoque,
            produto
        );

        var salvo = this.repository.save(lote);
        return new LoteResponse(
            salvo.getIdentificador(),
            salvo.getDataValidade(),
            salvo.getQuantidadeRecebida(),
            salvo.getQuantidadeAtual(),
            salvo.getProduto().getId(),
            salvo.getEstoque().getId());
    }

    public Lote findLoteByIdAndEstoque(String id, UUID estoque) {
        logger.info("Buscando lote com id: " + id);

        return this.repository.findByIdentificadorAndEstoqueId(id, estoque).orElseThrow(() -> new LoteNotFoundException("Lote não encontrado no estoque. Lote-id: " + id));
    }

    public List<Lote> findLotesDisponiveisFEFO(UUID produto, UUID estoque) {
        logger.info("Buscando lotes disponiveis (FEFO) para produto {} no estoque {}", produto, estoque);

        // FEFO: prioriza lotes com data de validade mais antiga e saldo positivo.
        return this.repository.findLotesDisponiveisFEFO(produto, estoque);
    }

    @Transactional
    public void atualizarQuantidadeAtual(String loteId, int novaQuantidade) {
        logger.info("Atualizando quantidade do lote {} para {}", loteId, novaQuantidade);

        // Evita gravar saldo negativo por inconsistencias na entrada.
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade do lote não pode ser negativa.");
        }

        Lote lote = this.repository.findById(loteId)
            .orElseThrow(() -> new LoteNotFoundException("Lote não encontrado no estoque. Lote-id: " + loteId));

        lote.setQuantidadeAtual(novaQuantidade);

        this.repository.save(lote);
    }
}
