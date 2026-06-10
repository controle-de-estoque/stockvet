package com.finconnect.auth_service.service;

import com.finconnect.auth_service.dto.ProcedimentoRequest;
import com.finconnect.auth_service.entity.Procedimento;
import com.finconnect.auth_service.entity.ItemProcedimento;
import com.finconnect.auth_service.repository.ProcedimentoRepository;
import com.finconnect.auth_service.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProcedimentoService {

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public Procedimento salvar(ProcedimentoRequest request) {
        Procedimento procedimento = new Procedimento();
        
        procedimento.setNome(request.nomeProcedimento());
        procedimento.setEspecie(request.nomeEspecie());
        procedimento.setGenero(request.genero());
        procedimento.setEstoque(request.estoque());
        procedimento.setAtivo(true);

        if (request.itens() != null) {
            List<ItemProcedimento> itens = request.itens().stream().map(itemDto -> {
                ItemProcedimento item = new ItemProcedimento();
                item.setProdutoId(itemDto.produtoId());
                item.setQuantidade(itemDto.quantidade());
                item.setTipo(itemDto.tipo());
                
                String nomeProduto = produtoRepository.findById(itemDto.produtoId())
                    .map(p -> p.getNome())
                    .orElse("Produto Desconhecido");
                item.setNome(nomeProduto);

                item.setProcedimento(procedimento);
                return item;
            }).collect(Collectors.toList());

            procedimento.setItens(itens);
        }

        return procedimentoRepository.save(procedimento);
    }

    public Procedimento findById(UUID id) {
        return procedimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado com o ID: " + id));
    }

    public List<Procedimento> buscarPorEstoque(UUID estoqueId) {
        return procedimentoRepository.findByEstoque(estoqueId);
    }

    @Transactional
    public void inativar(UUID id) {
        Procedimento procedimento = findById(id);
        procedimento.setAtivo(false);
        procedimentoRepository.save(procedimento);
    }
}