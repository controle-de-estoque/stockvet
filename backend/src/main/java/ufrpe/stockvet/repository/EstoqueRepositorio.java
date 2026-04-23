package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.Estoque;
import ufrpe.stockvet.models.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstoqueRepositorio extends JpaRepository<Estoque, UUID> {
    List<Estoque> findByUsuario(Usuario usuario);

}

