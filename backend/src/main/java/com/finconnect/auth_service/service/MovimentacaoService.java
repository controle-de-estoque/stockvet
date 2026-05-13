package com.finconnect.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.CreateLoteRequest;
import com.finconnect.auth_service.dto.LoteResponse;
import com.finconnect.auth_service.entity.Movimentacao;
import com.finconnect.auth_service.dto.CreateMovimentacaoEntradaRequest;
import com.finconnect.auth_service.repository.MovimentacaoRepository;

@Service
public class MovimentacaoService {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoService.class);

    @Autowired
    private MovimentacaoRepository repository;

    @Autowired
    private LoteService loteService;

    public void saveMovimentacao(CreateMovimentacaoEntradaRequest request) {
        logger.info("Tentando registrar movimentação");

        //cada movimentação de entrada gera o registro de um novo lote
        LoteResponse lote = this.loteService.saveLote(new CreateLoteRequest(request.loteId(), request.dataValidade(), request.quantidade(), request.estoque(), request.produto()));

    }
}
