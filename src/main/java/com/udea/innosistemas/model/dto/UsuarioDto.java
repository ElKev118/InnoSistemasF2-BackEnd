package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.RolSistema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private Integer id;
    private String nombre;
    private String email;
    private RolSistema rol;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
}