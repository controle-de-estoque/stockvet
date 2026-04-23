package ufrpe.stockvet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ufrpe.stockvet.DTO.*;
import ufrpe.stockvet.models.*;
import ufrpe.stockvet.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepositorio movimentacaoRepositorio;
    private final ItemMovimentacaoRepositorio itemMovimentacaoRepositorio;
    private final LoteRepositorio loteRepositorio;
    private final EstoqueRepositorio estoqueRepositorio;
    private final ProdutoRepositorio produtoRepositorio;
    private final ProcedimentoRepositorio procedimentoRepositorio;

    // RF19, RF12 - Entrada manual: cria novo lote e registra a movimentação
    @Transactional
    public RespostaMovimentacaoDTO registrarEntrada(EntradaDTO dto) {
        Usuario usuario = getUsuarioAutenticado();

        Estoque estoque = estoqueRepositorio.findByUsuario(usuario)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));

        Produto produto = produtoRepositorio.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Lote lote = new Lote();
        lote.setMarca(dto.getMarca());
        lote.setDataValidade(dto.getDataValidade());
        lote.setQuantidade(dto.getQuantidade());
        lote.setEstoque(estoque);
        lote.setProduto(produto);
        loteRepositorio.save(lote);

        Movimentacao mov = criarMovimentacao("ENTRADA", dto.getJustificativa(), null);

        ItemMovimentacao item = new ItemMovimentacao();
        item.setMovimentacao(mov);
        item.setLote(lote);
        item.setQtdMovimentada(dto.getQuantidade());
        itemMovimentacaoRepositorio.save(item);

        return toRespostaDTO(mov, List.of(item));
    }

    // RF19, RN02 - Saída manual com FEFO
    @Transactional
    public RespostaMovimentacaoDTO registrarSaida(SaidaDTO dto) {
        Usuario usuario = getUsuarioAutenticado();

        Procedimento procedimento = null;
        if (dto.getProcedimentoId() != null) {
            procedimento = procedimentoRepositorio.findById(dto.getProcedimentoId())
                    .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));
        }

        Movimentacao mov = criarMovimentacao("SAIDA", dto.getJustificativa(), procedimento);

        List<ItemMovimentacao> itens = deduzirERegistrar(
                mov, usuario, dto.getProdutoId(), dto.getQuantidade()
        );

        return toRespostaDTO(mov, itens);
    }


    public List<RespostaMovimentacaoDTO> listarTodas() {
        Usuario usuario = getUsuarioAutenticado();
        return loteRepositorio.findByEstoqueUsuario(usuario).stream()
                .flatMap(lote -> itemMovimentacaoRepositorio.findByLoteId(lote.getId()).stream())
                .map(ItemMovimentacao::getMovimentacao)
                .distinct()
                .map((Movimentacao mov) -> toRespostaDTO(mov, itemMovimentacaoRepositorio.findByMovimentacaoId(mov.getId())))
                .toList();
    }


    public List<RespostaMovimentacaoDTO> listarPorTipo(String tipo) {
        Usuario usuario = getUsuarioAutenticado();
        return loteRepositorio.findByEstoqueUsuario(usuario).stream()
                .flatMap(lote -> itemMovimentacaoRepositorio.findByLoteId(lote.getId()).stream())
                .map(ItemMovimentacao::getMovimentacao)
                .distinct()
                .filter((Movimentacao mov) -> mov.getTipo().equalsIgnoreCase(tipo))
                .map((Movimentacao mov) -> toRespostaDTO(mov, itemMovimentacaoRepositorio.findByMovimentacaoId(mov.getId())))
                .toList();
    }


    public List<RespostaMovimentacaoDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        Usuario usuario = getUsuarioAutenticado();
        return loteRepositorio.findByEstoqueUsuario(usuario).stream()
                .flatMap(lote -> itemMovimentacaoRepositorio.findByLoteId(lote.getId()).stream())
                .map(ItemMovimentacao::getMovimentacao)
                .distinct()
                .filter((Movimentacao mov) -> !mov.getData().isBefore(inicio) && !mov.getData().isAfter(fim))
                .map((Movimentacao mov) -> toRespostaDTO(mov, itemMovimentacaoRepositorio.findByMovimentacaoId(mov.getId())))
                .toList();
    }

    public RespostaMovimentacaoDTO buscarPorId(UUID id) {
        Movimentacao mov = movimentacaoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        return toRespostaDTO(mov, itemMovimentacaoRepositorio.findByMovimentacaoId(id));
    }


    private Usuario getUsuarioAutenticado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Movimentacao criarMovimentacao(String tipo, String justificativa, Procedimento procedimento) {
        Movimentacao mov = new Movimentacao();
        mov.setTipo(tipo);
        mov.setData(LocalDate.now());
        mov.setHora(LocalTime.now());
        mov.setJustificativa(justificativa);
        mov.setProcedimento(procedimento);
        return movimentacaoRepositorio.save(mov);
    }


    private List<ItemMovimentacao> deduzirERegistrar(Movimentacao mov, Usuario usuario, UUID produtoId, Integer qtdSolicitada) {
        List<Lote> lotesFefo = loteRepositorio
                .findByProdutoIdAndEstoqueUsuarioOrderByDataValidadeAsc(produtoId, usuario);

        int qtdRestante = qtdSolicitada;
        List<ItemMovimentacao> itensCriados = new ArrayList<>();

        for (Lote lote : lotesFefo) {
            if (qtdRestante <= 0) break;

            int qtdDeduzida = Math.min(lote.getQuantidade(), qtdRestante);
            lote.setQuantidade(lote.getQuantidade() - qtdDeduzida);
            loteRepositorio.save(lote);

            ItemMovimentacao item = new ItemMovimentacao();
            item.setMovimentacao(mov);
            item.setLote(lote);
            item.setQtdMovimentada(qtdDeduzida);
            itemMovimentacaoRepositorio.save(item);

            itensCriados.add(item);
            qtdRestante -= qtdDeduzida;
        }

        if (qtdRestante > 0) {
            throw new RuntimeException("Estoque insuficiente. Faltam " + qtdRestante + " unidades.");
        }

        return itensCriados;
    }

    private RespostaMovimentacaoDTO toRespostaDTO(Movimentacao mov, List<ItemMovimentacao> itens) {
        List<RespostaItemMovimentacaoDTO> itensDTO = itens.stream()
                .map(item -> new RespostaItemMovimentacaoDTO(
                        item.getId(),
                        item.getLote().getId(),
                        item.getLote().getMarca(),
                        item.getLote().getDataValidade(),
                        item.getQtdMovimentada()
                ))
                .toList();

        return new RespostaMovimentacaoDTO(
                mov.getId(),
                mov.getTipo(),
                mov.getData(),
                mov.getHora(),
                mov.getJustificativa(),
                mov.getProcedimento() != null ? mov.getProcedimento().getId() : null,
                itensDTO
        );
    }
}