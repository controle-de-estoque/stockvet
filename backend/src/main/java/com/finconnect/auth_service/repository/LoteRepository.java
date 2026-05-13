package com.finconnect.auth_service.repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.entity.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, UUID> {


    List<Lote> findAllByEstoque(UUID estoque);
}
