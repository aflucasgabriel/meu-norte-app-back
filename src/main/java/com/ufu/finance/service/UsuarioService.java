package com.ufu.finance.service;

import com.ufu.finance.dto.UserDTO;
import com.ufu.finance.dto.UserResponseDTO;
import com.ufu.finance.entity.Usuario;
import com.ufu.finance.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // BCrypt com força 12 (padrão é 10; 12 é mais seguro sem impacto perceptível)
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    /**
     * Retorna o encoder para que outros serviços possam validar senhas
     */
    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    /**
     * Cadastra um novo usuário com senha criptografada
     */
    public UserResponseDTO cadastrarUsuario(UserDTO userDTO) {
        if (usuarioRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(userDTO.getNome().trim());
        usuario.setEmail(userDTO.getEmail().toLowerCase().trim());
        usuario.setSenha(passwordEncoder.encode(userDTO.getSenha())); // 🔒 hash BCrypt

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return new UserResponseDTO(savedUsuario);
    }

    /**
     * Busca um usuário por ID
     */
    public UserResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UserResponseDTO(usuario);
    }

    /**
     * Lista todos os usuários
     */
    public List<UserResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca usuário por email (usado no login)
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email.toLowerCase().trim());
    }
}