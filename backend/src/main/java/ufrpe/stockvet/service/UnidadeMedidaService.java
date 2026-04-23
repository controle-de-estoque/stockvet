package ufrpe.stockvet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufrpe.stockvet.models.UnidadeMedida;
import ufrpe.stockvet.repository.UnidadeMedidaRepositorio;

import java.util.List;
import java.util.UUID;

@Service
public class UnidadeMedidaService {

    @Autowired
    private UnidadeMedidaRepositorio repositorio;

    public UnidadeMedida criar(UnidadeMedida unidadeMedida) {
        return repositorio.save(unidadeMedida);
    }

    public List<UnidadeMedida> listar() {
        return repositorio.findAll();
    }

    public void deletar(UUID id) {
        repositorio.deleteById(id);
    }
}