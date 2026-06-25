package com.ufu.finance.service.auth;

import com.ufu.finance.dto.AuthResponseDTO;
import com.ufu.finance.dto.LoginDTO;
import com.ufu.finance.entity.Usuario;
import com.ufu.finance.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        authService = new AuthService();
        setPrivateField(authService, "usuarioService", usuarioService);
        setPrivateField(authService, "jwtService", jwtService);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void loginReturnsAuthResponseForValidCredentials() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Joao");
        usuario.setEmail("joao@test.com");
        String rawPassword = "secret123";
        String hashed = new BCryptPasswordEncoder(12).encode(rawPassword);
        usuario.setSenha(hashed);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("joao@test.com");
        loginDTO.setSenha(rawPassword);

        when(usuarioService.buscarPorEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioService.getPasswordEncoder()).thenReturn(new BCryptPasswordEncoder(12));
        when(jwtService.generateToken(2L, "Joao")).thenReturn("token-abc");

        AuthResponseDTO response = authService.login(loginDTO);

        assertEquals("token-abc", response.getToken());
        assertEquals(2L, response.getUserId());
        assertEquals("Joao", response.getNome());
        verify(jwtService).generateToken(2L, "Joao");
    }

    @Test
    void loginThrowsWhenEmailNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("naoexiste@test.com");
        loginDTO.setSenha("senha");

        when(usuarioService.buscarPorEmail("naoexiste@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginDTO));

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() {
        Usuario usuario = new Usuario();
        usuario.setId(3L);
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@test.com");
        usuario.setSenha(new BCryptPasswordEncoder(12).encode("senha-correta"));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("pedro@test.com");
        loginDTO.setSenha("senhaerrada");

        when(usuarioService.buscarPorEmail("pedro@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioService.getPasswordEncoder()).thenReturn(new BCryptPasswordEncoder(12));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginDTO));

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }

    @Test
    void loginUsesGenericErrorMessageForInvalidPassword() {
        Usuario usuario = new Usuario();
        usuario.setId(4L);
        usuario.setNome("Mariana");
        usuario.setEmail("maria@test.com");
        usuario.setSenha(new BCryptPasswordEncoder(12).encode("senha123"));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("maria@test.com");
        loginDTO.setSenha("senhaIncorreta");

        when(usuarioService.buscarPorEmail("maria@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioService.getPasswordEncoder()).thenReturn(new BCryptPasswordEncoder(12));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginDTO));

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }

    @Test
    void loginVerifiesJwtGenerationIsCalledWithUserInfo() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNome("Luca");
        usuario.setEmail("luca@test.com");
        usuario.setSenha(new BCryptPasswordEncoder(12).encode("senha789"));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("luca@test.com");
        loginDTO.setSenha("senha789");

        when(usuarioService.buscarPorEmail("luca@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioService.getPasswordEncoder()).thenReturn(new BCryptPasswordEncoder(12));
        when(jwtService.generateToken(7L, "Luca")).thenReturn("jwt");

        authService.login(loginDTO);

        verify(jwtService, times(1)).generateToken(7L, "Luca");
    }
}
