package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.ItemMovimentacao;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemMovimentacaoRepositorio extends JpaRepository<ItemMovimentacao, UUID> {
    List<ItemMovimentacao> findByLoteId(UUID loteId);

    List<ItemMovimentacao> findByMovimentacaoId(UUID movimentacaoId);
}
