package com.ufu.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponseDTO {
    
    private String token;
    private String tipo = "Bearer";
    private Long userId;
    private String nome;
    
    public AuthResponseDTO(String token, Long userId, String nome) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
    }
}
