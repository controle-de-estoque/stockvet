package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.Estoque;
import ufrpe.stockvet.models.Lote;
import ufrpe.stockvet.models.Produto;
import ufrpe.stockvet.models.Usuario;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoteRepositorio extends JpaRepository<Lote, UUID> {
    List<Lote> findByProdutoIdAndEstoqueUsuarioOrderByDataValidadeAsc(UUID produtoId, Usuario usuario);

    List<Lote> findByEstoqueUsuario(Usuario usuario);
}