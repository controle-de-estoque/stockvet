package com.finconnect.auth_service.service;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.ProcedimentoRequest;
import com.finconnect.auth_service.entity.Procedimento;
import com.finconnect.auth_service.repository.ProcedimentoRepository;

@Service
public class ProcedimentoService {
    @Autowired
    private ProcedimentoRepository repository;

    public Procedimento salvar(ProcedimentoRequest request) {
        Procedimento p = new Procedimento();
        
        // Use os nomes exatos definidos no seu record
        p.setNome(request.nomeProcedimento()); 
        p.setEspecie(request.nomeEspecie());
        p.setGenero(request.genero());
        p.setEstoque(request.estoque());
        
        p.setAtivo(true);

        System.out.print("Tentando salvar: " + p.toString());

        return repository.save(p);
    }

    public void inativar(UUID id) {
        Procedimento p = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));
        p.setAtivo(false);
        repository.save(p);
    }

    public List<Procedimento> buscarPorEstoque(UUID estoqueId) {
        return repository.findByEstoque(estoqueId);
    }
}