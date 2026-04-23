package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ufrpe.stockvet.models.Funcao;

import java.util.Optional;

public interface FuncaoRepositorio extends JpaRepository<Funcao, java.util.UUID> {
	Optional<Funcao> findByNameIgnoreCase(String name);
}
