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
import com.finconnect.auth_service.entity.Lote;
import com.finconnect.auth_service.entity.Movimentacao;
import com.finconnect.auth_service.entity.MovimentacaoLote;
import com.finconnect.auth_service.entity.Produto;
import com.finconnect.auth_service.entity.TipoMovimentacao;
import com.finconnect.auth_service.entity.Users;
import com.finconnect.auth_service.exception_handler.exceptions.InsufficientProductsException;
import com.finconnect.auth_service.exception_handler.exceptions.ResourceNotFoundException;
import com.finconnect.auth_service.dto.CreateMovimentacaoEntradaRequest;
import com.finconnect.auth_service.dto.CreateMovimentacaoSaidaRequest;
import com.finconnect.auth_service.dto.MovimentacaoResponse;
import com.finconnect.auth_service.repository.CessionarioRepository;
import com.finconnect.auth_service.repository.EstoqueRepository;
import com.finconnect.auth_service.repository.MovimentacaoRepository;
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

    public void saveMovimentacao(CreateMovimentacaoEntradaRequest request) {
        logger.info("Tentando registrar movimentação");

        //cada movimentação de entrada gera o registro de um novo lote
        LoteResponse lote = this.loteService.saveLote(new CreateLoteRequest(request.loteId(), request.dataValidade(), request.quantidade(), request.estoque(), request.produto()));

    }

    @Transactional
    public void registrarEntrada(CreateMovimentacaoEntradaRequest request) {
        // Validação de existência das entidades relacionadas
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

        // 1. Salva o novo lote no sistema
        var novoLote = loteService.saveLote(new CreateLoteRequest(request.loteId(), request.dataValidade(), request.quantidade(), request.estoque(), request.produto()));

        // 2. Cria a movimentação única de entrada
        Movimentacao mov = new Movimentacao();
        mov.setTipo(TipoMovimentacao.ENTRADA);
        mov.setQuantidade(novoLote.quantidadeRecebida());
        mov.setProduto(produto);
        mov.setEstoque(estoque);
        mov.setCessionario(cessionario);
        mov.setMovimentadorPor(usuario);
        mov.setDataHoraMovimentacao(request.dataHoraMovimentacao());

        // 3. Vincula o lote à movimentação
        MovimentacaoLote item = new MovimentacaoLote();
        item.setMovimentacao(mov);
        item.setLote(this.loteService.findLoteByIdAndEstoque(request.loteId(), request.estoque()));
        item.setQuantidade(novoLote.quantidadeRecebida());
        
        mov.getItensLotes().add(item);

        repository.save(mov);
    }

    @Transactional
    public void registrarSaida(CreateMovimentacaoSaidaRequest request) {
        // Validação de existência das entidades relacionadas
        Produto produto = produtoRepository.findById(request.produto())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + request.produto()));
        
        Estoque estoque = estoqueRepository.findById(request.estoque())
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado ID: " + request.estoque()));
        
        Users usuario = usersRepository.findById(request.movimentadoPor())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado ID: " + request.movimentadoPor()));

        // 1. Busca os lotes disponíveis no estoque para o produto (Regra FEFO)
        List<Lote> lotesDisponiveis = loteService.findLotesDisponiveisFEFO(request.produto(), request.estoque());

        // 2. Verifica se a soma total de todos os lotes é suficiente
        int saldoTotalDisponivel = lotesDisponiveis.stream()
                .mapToInt(Lote::getQuantidadeAtual)
                .sum();

        if (request.quantidade() > saldoTotalDisponivel) {
            throw new InsufficientProductsException();
        }

        // 3. Cria a movimentação única de SAÍDA
        Movimentacao mov = new Movimentacao();
        mov.setTipo(TipoMovimentacao.SAIDA);
        mov.setQuantidade(request.quantidade());
        mov.setProduto(produto);
        mov.setEstoque(estoque);
        mov.setMovimentadorPor(usuario);
        mov.setDataHoraMovimentacao(request.dataHoraMovimentacao());
        mov.setItensLotes(new ArrayList<>());

        int quantidadePendente = request.quantidade();

        // 4. Algoritmo de abatimento nos lotes (Cascata FEFO)
        for (Lote lote : lotesDisponiveis) {
            if (quantidadePendente <= 0) break;

            int quantidadeARetirar = Math.min(lote.getQuantidadeAtual(), quantidadePendente);

            // Atualiza o saldo do lote
            loteService.atualizarQuantidadeAtual(lote.getIdentificador(), lote.getQuantidadeAtual() - quantidadeARetirar);

            // Cria o vínculo do lote com a movimentação
            MovimentacaoLote item = new MovimentacaoLote();
            item.setMovimentacao(mov);
            item.setLote(lote);
            item.setQuantidade(quantidadeARetirar);
            
            mov.getItensLotes().add(item);

            quantidadePendente -= quantidadeARetirar;
        }

        // 5. Salva a movimentação (Persiste cabeçalho e itens via Cascade)
        repository.save(mov);
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
}
