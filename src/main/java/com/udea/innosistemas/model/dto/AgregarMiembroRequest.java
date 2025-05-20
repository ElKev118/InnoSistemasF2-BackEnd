package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.RolEquipo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgregarMiembroRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    @Pattern(regexp = ".*@udea\\.edu\\.co$", message = "El email debe ser del dominio @udea.edu.co")
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private RolEquipo rol;
}