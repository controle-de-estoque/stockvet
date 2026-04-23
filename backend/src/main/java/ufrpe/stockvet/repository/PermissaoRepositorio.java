package ufrpe.stockvet.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.Permissao;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissaoRepositorio extends JpaRepository<Permissao, UUID> {

    Optional<Permissao> findByNome(String nome);

    boolean existsByNome(String nome);
}
