package com.finconnect.auth_service.repository;

import com.finconnect.auth_service.dto.ConsumoPorPeriodoRelatorio;
import com.finconnect.auth_service.entity.MovimentacaoLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MovimentacaoLoteRepository extends JpaRepository<MovimentacaoLote, UUID> {

    @Query("SELECT new com.finconnect.auth_service.dto.ConsumoPorPeriodoRelatorio(" +
           "  m.tipo, " +
           "  p.nome, " +
           "  ml.quantidade, " +
           "  l.identificador, " +
           "  CONCAT(u.firstName, ' ', u.lastName), " +
           "  m.dataHoraMovimentacao" +
           ") " +
           "FROM MovimentacaoLote ml " +
           "JOIN ml.movimentacao m " +
           "JOIN ml.lote l " +
           "JOIN l.produto p " +
           "JOIN m.movimentadorPor u " +
           "WHERE m.estoque.id = :estoqueId " +
           "AND (CAST(:dataInicio AS localdatetime) IS NULL OR m.dataHoraMovimentacao >= :dataInicio) " +
           "AND (CAST(:dataFim AS localdatetime) IS NULL OR m.dataHoraMovimentacao <= :dataFim)")
    List<ConsumoPorPeriodoRelatorio> findConsumoPorPeriodo(
        @Param("estoqueId") UUID estoqueId, 
        @Param("dataInicio") LocalDateTime dataInicio, 
        @Param("dataFim") LocalDateTime dataFim
    );
}