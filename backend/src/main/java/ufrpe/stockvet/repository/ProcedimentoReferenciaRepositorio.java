package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.ProcedimentoReferencia;

import java.util.UUID;

@Repository
public interface ProcedimentoReferenciaRepositorio extends JpaRepository<ProcedimentoReferencia, UUID> {

}