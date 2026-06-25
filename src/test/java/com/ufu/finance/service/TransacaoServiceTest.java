package com.ufu.finance.service;

import com.ufu.finance.dto.TransacaoRequestDTO;
import com.ufu.finance.dto.TransacaoResponseDTO;
import com.ufu.finance.dto.ResumoMensalDTO;
import com.ufu.finance.entity.Categoria;
import com.ufu.finance.entity.Transacao;
import com.ufu.finance.entity.Usuario;
import com.ufu.finance.enums.TipoTransacao;
import com.ufu.finance.repository.CategoriaRepository;
import com.ufu.finance.repository.TransacaoRepository;
import com.ufu.finance.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private OrcamentoService orcamentoService;

    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() throws Exception {
        transacaoService = new TransacaoService();
        setPrivateField(transacaoService, "transacaoRepository", transacaoRepository);
        setPrivateField(transacaoService, "usuarioRepository", usuarioRepository);
        setPrivateField(transacaoService, "categoriaRepository", categoriaRepository);
        setPrivateField(transacaoService, "orcamentoService", orcamentoService);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void criarSetsAlertaOrcamentoFalseForRevenue() {
        TransacaoRequestDTO dto = new TransacaoRequestDTO();
        dto.setTipo(TipoTransacao.R);
        dto.setValor(new BigDecimal("120.00"));
        dto.setIdCategoria(3);
        dto.setDescricao("Salario");

        Usuario usuario = new Usuario();
        usuario.setId(7L);
        Categoria categoria = new Categoria();
        categoria.setId(3);
        categoria.setNome("Salario");

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(3)).thenReturn(Optional.of(categoria));
        when(transacaoRepository.save(any())).thenAnswer(invocation -> {
            Transacao saved = invocation.getArgument(0);
            saved.setId(42L);
            saved.setDataHoraTransacao(LocalDateTime.now());
            return saved;
        });

        TransacaoResponseDTO response = transacaoService.criar(dto, 7L);

        assertEquals(42L, response.getIdTransacao());
        assertEquals(7L, response.getIdUsuario());
        assertEquals(3, response.getIdCategoria());
        assertEquals("Salario", response.getDescricao());
        assertEquals("R", response.getTipo());
        assertFalse(response.getAlertaOrcamento());
        verify(orcamentoService, never()).shouldAlert(anyLong(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void criarSetsAlertaOrcamentoTrueForExpense() {
        TransacaoRequestDTO dto = new TransacaoRequestDTO();
        dto.setTipo(TipoTransacao.D);
        dto.setValor(new BigDecimal("45.00"));
        dto.setIdCategoria(5);
        dto.setDescricao("Mercado");

        Usuario usuario = new Usuario();
        usuario.setId(8L);
        Categoria categoria = new Categoria();
        categoria.setId(5);
        categoria.setNome("Alimentacao");

        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(5)).thenReturn(Optional.of(categoria));
        when(transacaoRepository.save(any())).thenAnswer(invocation -> {
            Transacao saved = invocation.getArgument(0);
            saved.setId(45L);
            saved.setDataHoraTransacao(LocalDateTime.now());
            return saved;
        });
        when(orcamentoService.shouldAlert(eq(8L), eq(5), anyInt(), anyInt())).thenReturn(true);

        TransacaoResponseDTO response = transacaoService.criar(dto, 8L);

        assertTrue(response.getAlertaOrcamento());
        verify(orcamentoService, times(1)).shouldAlert(eq(8L), eq(5), anyInt(), anyInt());
    }

    @Test
    void deletarThrowsWhenUserIsNotOwner() {
        Transacao transacao = new Transacao();
        transacao.setId(10L);
        Usuario owner = new Usuario();
        owner.setId(22L);
        transacao.setUsuario(owner);

        when(transacaoRepository.findById(10L)).thenReturn(Optional.of(transacao));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transacaoService.deletar(10L, 55L));

        assertEquals("Acesso negado: você não pode deletar esta transação", exception.getMessage());
    }

    @Test
    void listarPorPeriodoThrowsWhenInicioIsAfterFim() {
        LocalDate inicio = LocalDate.of(2025, 3, 10);
        LocalDate fim = LocalDate.of(2025, 3, 1);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transacaoService.listarPorPeriodo(1L, inicio, fim));

        assertEquals("Data de início não pode ser posterior à data de fim", exception.getMessage());
        verify(transacaoRepository, never()).findByUsuarioIdAndDataHoraTransacaoBetweenOrderByDataHoraTransacaoDesc(anyLong(), any(), any());
    }

    @Test
    void resumoMensalBuildsSummaryWithCategoryBreakdown() {
        when(transacaoRepository.sumByTipoAndMesAno(3L, TipoTransacao.R, 2, 2024)).thenReturn(new BigDecimal("500.00"));
        when(transacaoRepository.sumByTipoAndMesAno(3L, TipoTransacao.D, 2, 2024)).thenReturn(new BigDecimal("250.00"));
        when(transacaoRepository.sumPorCategoriaNoMes(3L, TipoTransacao.D, 2, 2024))
                .thenReturn(List.<Object[]>of(new Object[]{"Mercado", new BigDecimal("200.00")}));
        when(transacaoRepository.sumPorCategoriaNoMes(3L, TipoTransacao.R, 2, 2024))
                .thenReturn(List.<Object[]>of(new Object[]{"Salario", new BigDecimal("500.00")}));

        ResumoMensalDTO resumo = transacaoService.resumoMensal(3L, 2, 2024);

        assertEquals(2, resumo.getMes());
        assertEquals(2024, resumo.getAno());
        assertEquals(new BigDecimal("500.00"), resumo.getTotalReceitas());
        assertEquals(new BigDecimal("250.00"), resumo.getTotalDespesas());
        assertEquals(new BigDecimal("250.00"), resumo.getSaldo());
        assertEquals(1, resumo.getDespesasPorCategoria().size());
        assertEquals(1, resumo.getReceitasPorCategoria().size());
        assertEquals("Mercado", resumo.getDespesasPorCategoria().get(0).getCategoria());
    }
}
