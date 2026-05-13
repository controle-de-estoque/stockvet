package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finconnect.auth_service.dto.CategoriaResponse;
import com.finconnect.auth_service.dto.SalvarCategoria;
import com.finconnect.auth_service.entity.Categoria;
import com.finconnect.auth_service.exception_handler.exceptions.CategoriaJaExisteException;
import com.finconnect.auth_service.repository.CategoriaRepository;

@Service
public class CategoriaService {
    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    public String salvarCategoria(SalvarCategoria request) {
        logger.info("tentando salvar categoria");

        String nomeNormalizado = request.nome().trim();

        if (this.categoriaRepository.existsByEstoqueAndNomeIgnoreCase(request.estoque(), nomeNormalizado)) {
            throw new CategoriaJaExisteException("Já existe uma categoria com esse nome neste estoque");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(nomeNormalizado);
        categoria.setEstoque(request.estoque());

        try {
            this.categoriaRepository.save(categoria);
        } catch (DataIntegrityViolationException ex) {
            throw new CategoriaJaExisteException("Já existe uma categoria com esse nome neste estoque");
        }
        
        return "Categoria salva com sucesso";
    }

    public List<CategoriaResponse> buscarCategoriasPorEstoque(UUID id) {
        logger.info("buscando categorias do estoque: " + id);

        var categorias = this.categoriaRepository.findAllByEstoque(id);
        
        return categorias.stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNome()))
                .toList();
    }
}
