package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finconnect.auth_service.dto.SalvarUnidade;
import com.finconnect.auth_service.dto.UnidadeResponse;
import com.finconnect.auth_service.entity.Unidade;
import com.finconnect.auth_service.repository.UnidadeRepository;

@Service
public class UnidadeService {
    private static final Logger logger = LoggerFactory.getLogger(UnidadeService.class);

    @Autowired
    private UnidadeRepository unidadeRepository;

    public String salvarUnidade(SalvarUnidade request) {
        logger.info("tentando salvar unidade");

        Unidade unidade = new Unidade();
        unidade.setConsumoMinimo(request.consumoMinimo());
        unidade.setNome(request.nome());
        unidade.setEstoque(request.estoque());

        this.unidadeRepository.save(unidade);
        
        return "Unidade salva com sucesso";
    }

    public List<UnidadeResponse> buscarUnidadesPorEstoque(UUID id) {
        logger.info("buscando unidades do estoque: " + id);

        var unidades = this.unidadeRepository.findAllByEstoque(id);

        return unidades.stream()
                .map(u -> new UnidadeResponse(u.getId(), u.getNome(), u.getConsumoMinimo()))
                .toList();
    }
}
