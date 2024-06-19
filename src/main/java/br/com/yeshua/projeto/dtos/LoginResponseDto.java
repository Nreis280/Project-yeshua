package br.com.yeshua.projeto.dtos;

import br.com.yeshua.projeto.enums.UserRole;

public record LoginResponseDto(String token, String email, UserRole role) {
}