package com.finconnect.auth_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.dto.CreateLoteRequest;
import com.finconnect.auth_service.dto.LoteResponse;
import com.finconnect.auth_service.entity.Cessionario;
import com.finconnect.auth_service.entity.Estoque;
import com.finconnect.auth_service.entity.ItemProcedimento;
import com.finconnect.auth_service.entity.Lote;
import com.finconnect.auth_service.entity.Movimentacao;
import com.finconnect.auth_service.entity.MovimentacaoLote;
import com.finconnect.auth_service.entity.Procedimento;
import com.finconnect.auth_service.entity.Produto;
import com.finconnect.auth_service.entity.TipoMovimentacao;
import com.finconnect.auth_service.entity.Users;
import com.finconnect.auth_service.exception_handler.exceptions.InsufficientProductsException;
import com.finconnect.auth_service.exception_handler.exceptions.ResourceNotFoundException;
import com.finconnect.auth_service.dto.CreateMovimentacaoEntradaRequest;
import com.finconnect.auth_service.dto.CreateMovimentacaoProcedimentoRequest;
import com.finconnect.auth_service.dto.CreateMovimentacaoSaidaRequest;
import com.finconnect.auth_service.dto.MovimentacaoResponse;
import com.finconnect.auth_service.repository.CessionarioRepository;
import com.finconnect.auth_service.repository.EstoqueRepository;
import com.finconnect.auth_service.repository.MovimentacaoRepository;
import com.finconnect.auth_service.repository.ProcedimentoRepository;
import com.finconnect.auth_service.repository.ProdutoRepository;
import com.finconnect.auth_service.repository.UsersRepository;
import jakarta.transaction.Transactional;

@Service
public class MovimentacaoService {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoService.class);

    @Autowired
    private MovimentacaoRepository repository;

    @Autowired
    private LoteService loteService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CessionarioRepository cessionarioRepository;

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    public void saveMovimentacao(CreateMovimentacaoEntradaRequest request) {
        logger.info("Tentando registrar movimentação");
        LoteResponse lote = this.loteService.saveLote(new CreateLoteRequest(request.loteId(), request.dataValidade(), request.quantidade(), request.estoque(), request.produto()));
    }

    @Transactional
    public void registrarEntrada(List<CreateMovimentacaoEntradaRequest> requestList) {
        for(CreateMovimentacaoEntradaRequest request: requestList){
            Produto produto = produtoRepository.findById(request.produto())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + request.produto()));
            
            Estoque estoque = estoqueRepository.findById(request.estoque())
                    .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado ID: " + request.estoque()));
            
            Users usuario = usersRepository.findById(request.movimentadoPor())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado ID: " + request.movimentadoPor()));

            Cessionario cessionario = null;
            if (request.cessionario() != null) {
                cessionario = cessionarioRepository.findById(request.cessionario())
                        .orElseThrow(() -> new ResourceNotFoundException("Cessionário não encontrado ID: " + request.cessionario()));
            }

            var novoLote = loteService.saveLote(new CreateLoteRequest(request.loteId(), request.dataValidade(), request.quantidade(), request.estoque(), request.produto()));

            Movimentacao mov = new Movimentacao();
            mov.setTipo(TipoMovimentacao.ENTRADA);
            mov.setQuantidade(novoLote.quantidadeRecebida());
            mov.setProduto(produto);
            mov.setEstoque(estoque);
            mov.setCessionario(cessionario);
            mov.setMovimentadorPor(usuario);
            mov.setDataHoraMovimentacao(request.dataHoraMovimentacao());

            MovimentacaoLote item = new MovimentacaoLote();
            item.setMovimentacao(mov);
            item.setLote(this.loteService.findLoteByIdAndEstoque(request.loteId(), request.estoque()));
            item.setQuantidade(novoLote.quantidadeRecebida());
            
            mov.getItensLotes().add(item);

            repository.save(mov);
        }
    }

    public List<MovimentacaoResponse> findAllByEstoque(UUID estoqueId) {
        logger.info("Buscando movimentacoes do estoque: {}", estoqueId);
        return this.repository.findAllByEstoqueIdOrderByDataHoraMovimentacaoDesc(estoqueId).stream()
                .map(this::toResponse)
                .toList();
    }

    private MovimentacaoResponse toResponse(Movimentacao mov) {
        String nomeUsuario = mov.getMovimentadorPor().getFirstName() + " " + mov.getMovimentadorPor().getLastName();
        String nomeCessionario = mov.getCessionario() != null ? mov.getCessionario().getNome() : null;

        return new MovimentacaoResponse(
                mov.getId(),
                mov.getTipo().name(),
                nomeUsuario,
                nomeCessionario,
                mov.getDataHoraMovimentacao()
        );
    }

    @Transactional
    public void registrarSaidaPorProcedimento(CreateMovimentacaoProcedimentoRequest request) {
        Procedimento procedimento = procedimentoRepository.findById(request.procedimentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento não encontrado ID: " + request.procedimentoId()));

        List<CreateMovimentacaoSaidaRequest> saidasCalculadas = new ArrayList<>();

        for (ItemProcedimento item : procedimento.getItens()) {
            double quantidadeFinal = item.getQuantidade();
            
            if ("variavel".equalsIgnoreCase(item.getTipo())) {
                quantidadeFinal = item.getQuantidade() * request.pesoAnimal();
            }

            int quantidadeInteira = (int) Math.ceil(quantidadeFinal);

            CreateMovimentacaoSaidaRequest saidaDto = new CreateMovimentacaoSaidaRequest(
                item.getProdutoId(),
                request.estoque(),
                request.movimentadoPor(),
                quantidadeInteira,
                request.dataHoraMovimentacao(),
                "SAIDA",
                "",
                ""
            );
            saidasCalculadas.add(saidaDto);
        }

        registrarSaida(saidasCalculadas);
    }

    @Transactional
    public void registrarSaida(List<CreateMovimentacaoSaidaRequest> requestList) {
        for(CreateMovimentacaoSaidaRequest request: requestList) {
            Produto produto = produtoRepository.findById(request.produto())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + request.produto()));
            
            Estoque estoque = estoqueRepository.findById(request.estoque())
                    .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado ID: " + request.estoque()));
            
            Users usuario = usersRepository.findById(request.movimentadoPor())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado ID: " + request.movimentadoPor()));

            List<Lote> lotesDisponiveis = loteService.findLotesDisponiveisFEFO(request.produto(), request.estoque());

            int saldoTotalDisponivel = lotesDisponiveis.stream()
                    .mapToInt(Lote::getQuantidadeAtual)
                    .sum();

            if (request.quantidade() > saldoTotalDisponivel) {
                throw new InsufficientProductsException();
            }

            Movimentacao mov = new Movimentacao();
            mov.setTipo(TipoMovimentacao.SAIDA);
            mov.setQuantidade(request.quantidade());
            mov.setProduto(produto);
            mov.setEstoque(estoque);
            mov.setMovimentadorPor(usuario);
            mov.setDataHoraMovimentacao(request.dataHoraMovimentacao());
            mov.setItensLotes(new ArrayList<>());

            int quantidadePendente = request.quantidade();

            for (Lote lote : lotesDisponiveis) {
                if (quantidadePendente <= 0) break;

                int quantidadeARetirar = Math.min(lote.getQuantidadeAtual(), quantidadePendente);

                loteService.atualizarQuantidadeAtual(lote.getIdentificador(), lote.getQuantidadeAtual() - quantidadeARetirar);

                MovimentacaoLote item = new MovimentacaoLote();
                item.setMovimentacao(mov);
                item.setLote(lote);
                item.setQuantidade(quantidadeARetirar);
                
                mov.getItensLotes().add(item);

                quantidadePendente -= quantidadeARetirar;
            }

            repository.save(mov);
        }
    }
}