package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.UnidadeMedida;

import java.util.UUID;

@Repository
public interface UnidadeMedidaRepositorio extends JpaRepository<UnidadeMedida, UUID> {

}