package com.finconnect.auth_service.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.entity.Procedimento;

@Repository
public interface ProcedimentoRepository extends JpaRepository<Procedimento, UUID> {

    List<Procedimento> findByEstoque(UUID estoqueId);

    boolean existsByEstoqueAndNomeIgnoreCaseAndAtivoTrue(UUID estoqueId, String nome);
}
