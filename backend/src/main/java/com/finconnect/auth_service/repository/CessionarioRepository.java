package com.finconnect.auth_service.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.entity.Cessionario;

@Repository
public interface CessionarioRepository extends JpaRepository<Cessionario, UUID> {

    List<Cessionario> findAllByEstoque(UUID estoque);
}
