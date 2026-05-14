package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.CessionarioResponse;
import com.finconnect.auth_service.dto.CreateCessionarioRequest;
import com.finconnect.auth_service.entity.Cessionario;
import com.finconnect.auth_service.repository.CessionarioRepository;

@Service
public class CessionarioService {

    private static final Logger logger = LoggerFactory.getLogger(CessionarioService.class);

    @Autowired
    private CessionarioRepository repository;

    public List<CessionarioResponse> findAllByEstoque(UUID estoque) {
        logger.info("Buscando cessionários do estoque: {}", estoque);

        return this.repository.findAllByEstoque(estoque).stream()
            .map(c -> new CessionarioResponse(
                c.getId(),
                c.getNome(),
                c.getEmail()
            )).toList();
    }

    public CessionarioResponse saveCessionario(CreateCessionarioRequest request) {
        logger.info("Tentando registrar cessionário");

        Cessionario cessionario = new Cessionario();
        cessionario.setNome(request.nome());
        cessionario.setEmail(request.email());
        cessionario.setAtivo(true); //cessionário é ativo por padrão
        cessionario.setEstoque(request.estoque());

        var salvo = this.repository.save(cessionario);
        return new CessionarioResponse(
            salvo.getId(),
            salvo.getNome(),
            salvo.getEmail()
        );
    }
}
