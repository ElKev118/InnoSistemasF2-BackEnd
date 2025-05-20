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
public class ProyectoDto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private Integer equipoId;
    private String equipoNombre;
    private UsuarioDto creador;
}
