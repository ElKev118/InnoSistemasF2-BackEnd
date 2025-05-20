package com.udea.innosistemas.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDto {
    private Integer id;
    private String nombre;
    private LocalDateTime fechaCreacion;
    private UsuarioDto creador;
    private Integer totalMiembros;
}