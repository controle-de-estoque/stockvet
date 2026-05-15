package com.finconnect.auth_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.dto.ConsumoPorPeriodoRelatorio;
import com.finconnect.auth_service.entity.Movimentacao;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

	List<Movimentacao> findAllByEstoqueIdOrderByDataHoraMovimentacaoDesc(UUID estoqueId);

	@Query("SELECT new com.finconnect.auth_service.dto.ConsumoPorPeriodoRelatorio(" +
        "  m.tipo, p.nome, ml.quantidade, l.identificador, " +
        "  CONCAT(u.firstName, ' ', u.lastName), m.dataHoraMovimentacao) " +
        "FROM MovimentacaoLote ml " +
        "JOIN ml.movimentacao m " +
        "JOIN ml.lote l " +
        "JOIN l.produto p " +
        "JOIN m.movimentadorPor u " +
        "WHERE m.estoque.id = :estoqueId " +
        "AND (:dataInicio IS NULL OR m.dataHoraMovimentacao >= :dataInicio) " +
        "AND (:dataFim IS NULL OR m.dataHoraMovimentacao <= :dataFim)")
    List<ConsumoPorPeriodoRelatorio> findHistoricoMovimentacoes(
        @Param("estoqueId") UUID estoqueId, 
        @Param("dataInicio") LocalDateTime dataInicio, 
        @Param("dataFim") LocalDateTime dataFim
    );
}
