package com.udea.innosistemas.controller;

import com.udea.innosistemas.model.dto.ActualizarEstadoTareaRequest;
import com.udea.innosistemas.model.dto.CrearTareaRequest;
import com.udea.innosistemas.model.dto.TareaDto;
import com.udea.innosistemas.service.TareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "API para la gestión de tareas")
@SecurityRequirement(name = "bearerAuth")
public class TareaController {

    private final TareaService tareaService;

    @PostMapping
    @Operation(summary = "Crear una nueva tarea", description = "Crea una nueva tarea asociada a un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear tareas en este proyecto")
    })
    public ResponseEntity<TareaDto> crearTarea(
            @Valid @RequestBody CrearTareaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TareaDto tarea = tareaService.crearTarea(request, userDetails.getUsername());
        return new ResponseEntity<>(tarea, HttpStatus.CREATED);
    }

    @GetMapping("/proyecto/{proyectoId}")
    @Operation(summary = "Listar tareas de un proyecto", description = "Obtiene todas las tareas de un proyecto específico")
    public ResponseEntity<List<TareaDto>> listarTareasPorProyecto(
            @PathVariable Integer proyectoId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TareaDto> tareas = tareaService.listarTareasPorProyecto(proyectoId, userDetails.getUsername());
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/asignadas")
    @Operation(summary = "Listar tareas asignadas", description = "Obtiene todas las tareas asignadas al usuario autenticado")
    public ResponseEntity<List<TareaDto>> listarTareasAsignadas(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TareaDto> tareas = tareaService.listarTareasAsignadas(userDetails.getUsername());
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/creadas")
    @Operation(summary = "Listar tareas creadas", description = "Obtiene todas las tareas creadas por el usuario autenticado")
    public ResponseEntity<List<TareaDto>> listarTareasCreadas(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TareaDto> tareas = tareaService.listarTareasCreadas(userDetails.getUsername());
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una tarea por ID", description = "Obtiene los detalles de una tarea específica")
    public ResponseEntity<TareaDto> obtenerTareaPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        TareaDto tarea = tareaService.obtenerTareaPorId(id, userDetails.getUsername());
        return ResponseEntity.ok(tarea);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una tarea", description = "Actualiza la información de una tarea existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado para modificar esta tarea")
    })
    public ResponseEntity<TareaDto> actualizarTarea(
            @PathVariable Integer id,
            @Valid @RequestBody CrearTareaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TareaDto tarea = tareaService.actualizarTarea(id, request, userDetails.getUsername());
        return ResponseEntity.ok(tarea);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de una tarea", description = "Actualiza únicamente el estado de una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de tarea actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido o transición no permitida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para modificar el estado de esta tarea")
    })
    public ResponseEntity<TareaDto> actualizarEstadoTarea(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstadoTareaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TareaDto tarea = tareaService.actualizarEstadoTarea(id, request, userDetails.getUsername());
        return ResponseEntity.ok(tarea);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea", description = "Elimina una tarea existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea eliminada correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar esta tarea")
    })
    public ResponseEntity<Void> eliminarTarea(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        tareaService.eliminarTarea(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}