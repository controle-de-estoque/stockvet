package com.finconnect.auth_service.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.dto.ProdutoAtivoRelatorio;
import com.finconnect.auth_service.dto.ProdutoResponse;
import com.finconnect.auth_service.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    List<Produto> findAllByEstoque(UUID estoque);

    List<Produto> findByEstoqueAndNomeStartingWith(UUID estoque, String nome);

    boolean existsByEstoqueAndNomeIgnoreCase(UUID estoque, String nome);

    @Query("""
        SELECT new com.finconnect.auth_service.dto.ProdutoResponse(
            p.id,
            p.nome, 
            c.nome, 
            cast(p.tipo as string), 
            u.nome, 
            p.quantidadeCritica, 
            0,
            p.ativo
        ) 
        FROM Produto p 
        LEFT JOIN Categoria c ON p.categoria = c.id 
        LEFT JOIN Unidade u ON p.unidade = u.id 
        WHERE p.estoque = :estoqueId
    """)
    List<ProdutoResponse> findProdutosResponseByEstoque(@Param("estoqueId") UUID estoqueId);

    @Query("""
        SELECT new com.finconnect.auth_service.dto.ProdutoResponse(
            p.id,
            p.nome, 
            c.nome, 
            cast(p.tipo as string), 
            u.nome, 
            p.quantidadeCritica, 
            0,
            p.ativo
        ) 
        FROM Produto p 
        LEFT JOIN Categoria c ON p.categoria = c.id 
        LEFT JOIN Unidade u ON p.unidade = u.id 
        WHERE p.estoque = :estoqueId 
        AND LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
    """)
    List<ProdutoResponse> findProdutosResponseByEstoqueAndNome(
        @Param("estoqueId") UUID estoqueId, 
        @Param("nome") String nome
    );

    @Query("SELECT new com.finconnect.auth_service.dto.ProdutoAtivoRelatorio(" +
       "  p.nome, " +
       "  c.nome, " +
       "  CAST(COALESCE(SUM(l.quantidadeAtual), 0) AS int), " + // Garante 0 se não houver lote
       "  p.quantidadeCritica, " +
       "  u.nome" +
       ") " +
       "FROM Produto p " + // Começa por Produto para não perder registros
       "LEFT JOIN Lote l ON l.produto.id = p.id " + // LEFT JOIN permite produtos sem lotes
       "JOIN Categoria c ON c.id = p.categoria " +
       "JOIN Unidade u ON u.id = p.unidade " +
       "WHERE p.estoque = :estoqueId " +
       "AND p.ativo = true " +
       "GROUP BY p.id, p.nome, c.nome, p.quantidadeCritica, u.nome") // Agrupado por p.id para segurança
    List<ProdutoAtivoRelatorio> relatorioProdutosAtivos(@Param("estoqueId") UUID estoqueId);
}
