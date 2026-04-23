package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.Produto;
import ufrpe.stockvet.models.Usuario;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoRepositorio extends JpaRepository<Produto, UUID> {

}