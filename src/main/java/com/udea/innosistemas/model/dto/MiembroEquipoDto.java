package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.RolEquipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiembroEquipoDto {
    private Integer id;
    private Integer equipoId;
    private String equipoNombre;
    private UsuarioDto usuario;
    private RolEquipo rol;
    private LocalDateTime fechaIncorporacion;
}