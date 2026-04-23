package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.Procedimento;

import java.util.UUID;

@Repository
public interface ProcedimentoRepositorio extends JpaRepository<Procedimento, UUID> {}
