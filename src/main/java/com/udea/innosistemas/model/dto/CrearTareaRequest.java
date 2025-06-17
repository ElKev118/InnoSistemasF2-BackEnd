package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.EstadoTarea;
import com.udea.innosistemas.model.enums.PrioridadTarea;
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
public class CrearTareaRequest {

    @NotBlank(message = "El título de la tarea es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @NotNull(message = "El ID del proyecto es obligatorio")
    private Integer proyectoId;

    private Integer asignadaAId;

    @NotNull(message = "El estado de la tarea es obligatorio")
    private EstadoTarea estado;

    @NotNull(message = "La prioridad de la tarea es obligatoria")
    private PrioridadTarea prioridad;

    @FutureOrPresent(message = "La fecha de entrega debe ser presente o futura")
    private LocalDate fechaEntrega;
}