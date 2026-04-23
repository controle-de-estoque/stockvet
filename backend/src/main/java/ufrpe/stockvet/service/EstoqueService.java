package ufrpe.stockvet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufrpe.stockvet.models.Estoque;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.repository.EstoqueRepositorio;

import java.util.List;
import java.util.UUID;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    public Estoque criarEstoque(Estoque estoque, Usuario usuario) {
        if (!usuario.getFuncao().getName().equals("ADMIN")) {
            throw new RuntimeException("apenas admins podem criar estoque");
        }
        estoque.setUsuario(usuario);
        return estoqueRepositorio.save(estoque);
    }

    public Estoque buscarEstoque(UUID id, Usuario usuario) {
        Estoque estoque = estoqueRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("estoque não encontrado"));


        if (!estoque.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new RuntimeException("você não tem acesso a esse estoque");
        }
        return estoque;
    }


    public List<Estoque> listarEstoques(Usuario usuario) {
        return estoqueRepositorio.  findByUsuario(usuario);
    }

    public Estoque editarEstoque(UUID id, Estoque estoqueAtualizado, Usuario usuario) {
        Estoque estoque = buscarEstoque(id, usuario);
        estoque.setNome(estoqueAtualizado.getNome());
        return estoqueRepositorio.save(estoque);
    }

    public void deletarEstoque(UUID id, Usuario usuario) {
        Estoque estoque = buscarEstoque(id, usuario);
        estoqueRepositorio.delete(estoque);
    }
}