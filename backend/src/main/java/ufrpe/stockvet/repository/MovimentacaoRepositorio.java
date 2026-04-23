package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufrpe.stockvet.models.Movimentacao;

import java.util.UUID;

@Repository
public interface MovimentacaoRepositorio extends JpaRepository<Movimentacao, UUID> {

}