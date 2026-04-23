package ufrpe.stockvet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ufrpe.stockvet.DTO.PermissaoDTO;
import ufrpe.stockvet.models.Permissao;
import ufrpe.stockvet.repository.PermissaoRepositorio;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final PermissaoRepositorio permissaoRepositorio;

    public Permissao criar(PermissaoDTO dto) {
        if (permissaoRepositorio.existsByNome(dto.nome())) {
            throw new RuntimeException("Permissão '" + dto.nome() + "' já existe.");
        }
        Permissao permissao = new Permissao();
        permissao.setNome(dto.nome());
        return permissaoRepositorio.save(permissao);
    }

    public List<Permissao> listarTodas() {
        return permissaoRepositorio.findAll();
    }

    public Permissao buscarPorId(UUID id) {
        return permissaoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada"));
    }

    public Permissao atualizar(UUID id, PermissaoDTO dto) {
        Permissao permissao = buscarPorId(id);
        permissao.setNome(dto.nome());
        return permissaoRepositorio.save(permissao);
    }

    public void deletar(UUID id) {
        if (!permissaoRepositorio.existsById(id)) {
            throw new RuntimeException("Permissão não encontrada");
        }
        permissaoRepositorio.deleteById(id);
    }
}