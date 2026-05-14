package com.finconnect.auth_service.repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.entity.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, String> {

    Optional<Lote> findByIdentificadorAndEstoqueId(String identificador, UUID estoque);

    List<Lote> findAllByEstoqueId(UUID estoqueId);

    @Query("SELECT l FROM Lote l WHERE l.produto.id = :produto AND l.estoque.id = :estoque AND l.quantidadeAtual > 0 ORDER BY l.dataValidade ASC")
    List<Lote> findLotesDisponiveisFEFO(@Param("produto") UUID produto, @Param("estoque") UUID estoque);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Lote l SET l.quantidadeAtual = :quantidadeAtual WHERE l.identificador = :id")
    int updateQuantidadeAtual(@Param("id") String id, @Param("quantidadeAtual") int quantidadeAtual);
}
