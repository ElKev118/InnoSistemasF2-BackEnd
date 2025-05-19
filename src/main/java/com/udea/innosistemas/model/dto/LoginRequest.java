package com.udea.innosistemas.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    @Pattern(regexp = ".*@udea\\.edu\\.co$", message = "El email debe terminar en @udea.edu.co")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}