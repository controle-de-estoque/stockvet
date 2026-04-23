package ufrpe.stockvet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufrpe.stockvet.models.Estoque;
import ufrpe.stockvet.models.Lote;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.repository.LoteRepositorio;

import java.util.List;
import java.util.UUID;

@Service
public class LoteService {

    @Autowired
    private LoteRepositorio loteRepositorio;

    @Autowired
    private EstoqueService estoqueService;

    public Lote criarLote(Lote lote, Usuario usuario) {

        Estoque estoque = estoqueService.buscarEstoque(lote.getEstoque().getId(), usuario);
        lote.setEstoque(estoque);
        return loteRepositorio.save(lote);
    }

    public List<Lote> listarLotesPorProduto(UUID produtoId, Usuario usuario) {

        return loteRepositorio.findByProdutoIdAndEstoqueUsuarioOrderByDataValidadeAsc(produtoId, usuario);
    }

    public List<Lote> listarLotesPorEstoque(Usuario usuario) {
        return loteRepositorio.findByEstoqueUsuario(usuario);
    }

    public Lote buscarLote(UUID id, Usuario usuario) {
        Lote lote = loteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote não encontrado!"));

        if (!lote.getEstoque().getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new RuntimeException("Você não tem acesso a esse lote!");
        }
        return lote;
    }
}