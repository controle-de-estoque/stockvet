package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.Cessionario;

import java.util.UUID;

@Repository
public interface CessionarioRepositorio extends JpaRepository<Cessionario, UUID> {
}
