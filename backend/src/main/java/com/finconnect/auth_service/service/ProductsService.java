package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finconnect.auth_service.dto.CategoriaResponse;
import com.finconnect.auth_service.dto.ProdutoResponse;
import com.finconnect.auth_service.dto.SalvarCategoria;
import com.finconnect.auth_service.dto.SalvarEstoque;
import com.finconnect.auth_service.dto.SalvarProduto;
import com.finconnect.auth_service.dto.SalvarUnidade;
import com.finconnect.auth_service.dto.UnidadeResponse;
import com.finconnect.auth_service.entity.Categoria;
import com.finconnect.auth_service.entity.Estoque;
import com.finconnect.auth_service.entity.Produto;
import com.finconnect.auth_service.entity.Unidade;
import com.finconnect.auth_service.exception_handler.exceptions.CategoriaJaExisteException;
import com.finconnect.auth_service.exception_handler.exceptions.ProdutoJaExisteException;
import com.finconnect.auth_service.repository.CategoriaRepository;
import com.finconnect.auth_service.repository.EstoqueRepository;
import com.finconnect.auth_service.repository.ProdutoRepository;
import com.finconnect.auth_service.repository.UnidadeRepository;

@Service
public class ProductsService {
    private static final Logger logger = LoggerFactory.getLogger(ProductsService.class);
    
    @Autowired
    private ProdutoRepository productsRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    public String salvarUnidade(SalvarUnidade request) {
        logger.info("tentando salvar unidade");

        Unidade unidade = new Unidade();
        unidade.setConsumoMinimo(request.consumoMinimo());
        unidade.setNome(request.nome());
        unidade.setEstoque(request.estoque());
    

        this.unidadeRepository.save(unidade);
        
        return "Unidade salva com sucesso";
    }

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

    public List<CategoriaResponse> buscarCategoriasPorEstoque(UUID id) {
        logger.info("buscando categorias do estoque: " + id);

        var categorias = this.categoriaRepository.findAllByEstoque(id);
        
        return categorias.stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNome()))
                .toList();
    }

    public List<UnidadeResponse> buscarUnidadesPorEstoque(UUID id) {
        logger.info("buscando unidades do estoque: " + id);

        var unidades = this.unidadeRepository.findAllByEstoque(id);

        return unidades.stream()
                .map(u -> new UnidadeResponse(u.getId(), u.getNome(), u.getConsumoMinimo()))
                .toList();
    }
}
