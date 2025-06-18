package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.EstadoTarea;
import com.udea.innosistemas.model.enums.PrioridadTarea;
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
public class TareaDto {
    private Integer id;
    private String titulo;
    private String descripcion;
    private Integer proyectoId;
    private String proyectoNombre;
    private UsuarioDto creadaPor;
    private UsuarioDto asignadaA;
    private EstadoTarea estado;
    private PrioridadTarea prioridad;
    private LocalDateTime fechaCreacion;
    private LocalDate fechaEntrega;
    private LocalDate fechaCompletado;
}