package com.ufu.finance.service;

import com.ufu.finance.dto.OrcamentoRequestDTO;
import com.ufu.finance.dto.OrcamentoResponseDTO;
import com.ufu.finance.entity.Categoria;
import com.ufu.finance.entity.Orcamento;
import com.ufu.finance.entity.Usuario;
import com.ufu.finance.repository.CategoriaRepository;
import com.ufu.finance.repository.OrcamentoRepository;
import com.ufu.finance.repository.TransacaoRepository;
import com.ufu.finance.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    private OrcamentoService orcamentoService;

    @BeforeEach
    void setUp() throws Exception {
        orcamentoService = new OrcamentoService();
        setPrivateField(orcamentoService, "orcamentoRepository", orcamentoRepository);
        setPrivateField(orcamentoService, "usuarioRepository", usuarioRepository);
        setPrivateField(orcamentoService, "categoriaRepository", categoriaRepository);
        setPrivateField(orcamentoService, "transacaoRepository", transacaoRepository);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }


    @Test
    void criarThrowsConflictWhenBudgetAlreadyExists() {
        OrcamentoRequestDTO dto = new OrcamentoRequestDTO(1, 1, 2024, new BigDecimal("500.00"));

        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(new Usuario()));
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(new Categoria()));
        when(orcamentoRepository.existsByUsuarioIdAndCategoriaIdAndMesAndAno(20L, 1, 1, 2024)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orcamentoService.criar(dto, 20L));

        assertEquals("409 CONFLICT \"Já existe orçamento para esta categoria no mês/ano informado\"", exception.getMessage());
    }

    @Test
    void shouldAlertReturnsTrueWhenConsumoMeetsThreshold() {
        Orcamento orcamento = new Orcamento();
        orcamento.setId(9L);
        Usuario usuario = new Usuario();
        usuario.setId(22L);
        orcamento.setUsuario(usuario);
        Categoria categoria = new Categoria();
        categoria.setId(7);
        categoria.setNome("Transporte");
        orcamento.setCategoria(categoria);
        orcamento.setMes(3);
        orcamento.setAno(2025);
        orcamento.setLimite(new BigDecimal("100.00"));

        when(orcamentoRepository.findByUsuarioIdAndCategoriaIdAndMesAndAno(22L, 7, 3, 2025))
                .thenReturn(Optional.of(orcamento));
        when(transacaoRepository.sumByCategoriaTipoMesAno(22L, 7, com.ufu.finance.enums.TipoTransacao.D, 3, 2025))
                .thenReturn(new BigDecimal("80.00"));

        assertTrue(orcamentoService.shouldAlert(22L, 7, 3, 2025));
    }

    @Test
    void shouldAlertReturnsFalseWhenBudgetDoesNotExist() {
        when(orcamentoRepository.findByUsuarioIdAndCategoriaIdAndMesAndAno(30L, 5, 4, 2025))
                .thenReturn(Optional.empty());

        assertFalse(orcamentoService.shouldAlert(30L, 5, 4, 2025));
    }

    @Test
    void buscarPorIdThrowsForbiddenWhenBudgetBelongsToAnotherUser() {
        Orcamento orcamento = new Orcamento();
        orcamento.setId(15L);
        Usuario usuario = new Usuario();
        usuario.setId(99L);
        orcamento.setUsuario(usuario);
        orcamento.setCategoria(new Categoria());
        orcamento.setMes(6);
        orcamento.setAno(2025);
        orcamento.setLimite(new BigDecimal("200.00"));
        orcamento.setDataCriacao(LocalDateTime.now());
        orcamento.setDataAtualizacao(LocalDateTime.now());

        when(orcamentoRepository.findById(15L)).thenReturn(Optional.of(orcamento));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orcamentoService.buscarPorId(15L, 1L));

        assertEquals("403 FORBIDDEN \"Acesso negado: este orçamento pertence a outro usuário\"", exception.getMessage());
    }
}
