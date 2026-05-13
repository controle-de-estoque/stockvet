package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finconnect.auth_service.dto.ProdutoResponse;
import com.finconnect.auth_service.dto.SalvarEstoque;
import com.finconnect.auth_service.dto.SalvarProduto;
import com.finconnect.auth_service.entity.Estoque;
import com.finconnect.auth_service.entity.Produto;
import com.finconnect.auth_service.exception_handler.exceptions.ProdutoJaExisteException;
import com.finconnect.auth_service.repository.EstoqueRepository;
import com.finconnect.auth_service.repository.ProdutoRepository;

@Service
public class ProductsService {
    private static final Logger logger = LoggerFactory.getLogger(ProductsService.class);
    
    @Autowired
    private ProdutoRepository productsRepository;
    
    @Autowired
    private EstoqueRepository estoqueRepository;

    public String salvarProduto(SalvarProduto request) {
        logger.info("tentando salvar produto");

        String nomeNormalizado = request.nome().trim();

        if (this.productsRepository.existsByEstoqueAndNomeIgnoreCase(request.estoque(), nomeNormalizado)) {
            throw new ProdutoJaExisteException("Já existe um produto com esse nome neste estoque");
        }

        Produto produto = new Produto();
        produto.setNome(nomeNormalizado);
        produto.setCategoria(request.categoria());
        produto.setTipo(request.tipo());
        produto.setUnidade(request.unidade());
        produto.setEstoque(request.estoque());
        produto.setAtivo(true);

        try {
            this.productsRepository.save(produto);
        } catch (DataIntegrityViolationException ex) {
            throw new ProdutoJaExisteException("Já existe um produto com esse nome neste estoque");
        }
        
        return "Produto salvo com sucesso";
    }

    public String desativarProduto(UUID id) {
        logger.info("desativando produto: " + id);

        Produto produto = this.productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (!produto.isAtivo()) {
            return "Produto já está inativo";
        }

        produto.setAtivo(false);
        this.productsRepository.save(produto);

        return "Produto desativado com sucesso";
    }

    public UUID salvarEstoque(SalvarEstoque request) {
        logger.info("tentando salvar estoque");

        Estoque estoque = new Estoque();
        estoque.setNome(request.nome());

        return this.estoqueRepository.save(estoque).getId();
    }

    public List<ProdutoResponse> findAllProdutosFromEstoque(UUID id) {
        logger.info("buscando produtos do estoque: " + id);

        var produtos = this.productsRepository.findProdutosResponseByEstoque(id);
        
        return produtos;
    }

    public List<ProdutoResponse> findAllProdutosFromEstoqueComecandoComNome(UUID id, String nome) {
        logger.info("buscando produtos do estoque comecando com : " + nome);

        var produtos = this.productsRepository.findProdutosResponseByEstoqueAndNome(id, nome);
    
        return produtos;
    }
}
