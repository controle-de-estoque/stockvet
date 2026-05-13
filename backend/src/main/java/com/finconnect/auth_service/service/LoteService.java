package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.CreateLoteRequest;
import com.finconnect.auth_service.dto.LoteResponse;
import com.finconnect.auth_service.entity.Lote;
import com.finconnect.auth_service.repository.LoteRepository;

@Service
public class LoteService {

    private static final Logger logger = LoggerFactory.getLogger(LoteService.class);
    
    @Autowired
    private LoteRepository repository;

    public List<LoteResponse> findAllByEstoque(UUID estoque) {
        logger.info("Buscando lotes do estoque: {}", estoque);

        return this.repository.findAllByEstoque(estoque).stream()
            .map(l -> new LoteResponse(
                l.getIdentificador(),
                l.getDataValidade(),
                l.getQuantidadeRecebida(),
                l.getQuantidadeAtual()
            )).toList();
    }

    public LoteResponse saveLote(CreateLoteRequest request) {
        logger.info("Tentando registrar lote");

        Lote lote = new Lote(
            request.id(),
            request.dataValidade(),
            request.quantidadeRecebida(),
            request.quantidadeRecebida(), //no momento da criação do lote, a quantidade atual é a mesma recebida
            request.estoque()
        );

        var salvo = this.repository.save(lote);
        return new LoteResponse(
            salvo.getIdentificador(),
            salvo.getDataValidade(),
            salvo.getQuantidadeRecebida(),
            salvo.getQuantidadeAtual());
    }
}
