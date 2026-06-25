package com.ufu.finance.service;

import com.ufu.finance.dto.UserDTO;
import com.ufu.finance.dto.UserResponseDTO;
import com.ufu.finance.entity.Usuario;
import com.ufu.finance.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() throws Exception {
        usuarioService = new UsuarioService();
        setPrivateField(usuarioService, "usuarioRepository", usuarioRepository);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void cadastrarUsuarioThrowsWhenEmailAlreadyExists() {
        UserDTO dto = new UserDTO();
        dto.setNome("Teste");
        dto.setEmail("email@teste.com");
        dto.setSenha("senha123");

        when(usuarioRepository.existsByEmail("email@teste.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrarUsuario(dto));

        assertEquals("Email já está em uso", exception.getMessage());
    }

    @Test
    void buscarPorIdReturnsUserResponseWhenFound() {
        Usuario usuario = new Usuario();
        usuario.setId(5L);
        usuario.setNome("Carlos");
        usuario.setEmail("carlos@test.com");
        usuario.setSenha("hash");
        usuario.setDataInclusao(LocalDateTime.now());

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));

        UserResponseDTO response = usuarioService.buscarPorId(5L);

        assertEquals(5L, response.getId());
        assertEquals("Carlos", response.getNome());
        assertEquals("carlos@test.com", response.getEmail());
        assertNotNull(response.getDataInclusao());
    }

    @Test
    void buscarPorIdThrowsWhenUserNotFound() {
        when(usuarioRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.buscarPorId(42L));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void buscarPorEmailNormalizesInputBeforeQuery() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana");
        usuario.setEmail("ana@test.com");
        usuario.setSenha("hash");

        when(usuarioRepository.findByEmail("meu@email.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.buscarPorEmail(" Meu@Email.com ");

        assertTrue(result.isPresent());
        assertEquals(usuario, result.get());
        verify(usuarioRepository).findByEmail("meu@email.com");
    }
}
