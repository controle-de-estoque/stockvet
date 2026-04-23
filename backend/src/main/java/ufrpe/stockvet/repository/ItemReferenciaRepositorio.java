package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.ItemReferencia;

import java.util.UUID;

@Repository
public interface ItemReferenciaRepositorio extends JpaRepository<ItemReferencia, UUID> {}