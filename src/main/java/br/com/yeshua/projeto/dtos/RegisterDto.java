package br.com.yeshua.projeto.dtos;


import br.com.yeshua.projeto.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record RegisterDto(@NotNull String email,@NotNull String password, @NotNull UserRole role ) {
    
}
