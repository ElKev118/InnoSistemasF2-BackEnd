package com.udea.innosistemas.model.dto;

import com.udea.innosistemas.model.enums.EstadoTarea;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoTareaRequest {

    @NotNull(message = "El estado de la tarea es obligatorio")
    private EstadoTarea estado;
}