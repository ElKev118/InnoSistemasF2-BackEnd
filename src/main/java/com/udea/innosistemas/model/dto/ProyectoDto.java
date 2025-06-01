package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.EstadoProyecto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private LocalDate fechaInicio;
    private LocalDate fechaFinPrevista;
    private LocalDate fechaCompletado;
    private EstadoProyecto estado;
    private Integer equipoId;
    private String equipoNombre;
    private UsuarioDto creador;
}