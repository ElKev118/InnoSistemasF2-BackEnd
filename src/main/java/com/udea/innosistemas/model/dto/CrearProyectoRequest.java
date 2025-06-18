package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.EstadoProyecto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearProyectoRequest {

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @NotNull(message = "El ID del equipo es obligatorio")
    private Integer equipoId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de finalización prevista es obligatoria")
    @FutureOrPresent(message = "La fecha de finalización debe ser presente o futura")
    private LocalDate fechaFinPrevista;

    @NotNull(message = "El estado del proyecto es obligatorio")
    private EstadoProyecto estado;
}