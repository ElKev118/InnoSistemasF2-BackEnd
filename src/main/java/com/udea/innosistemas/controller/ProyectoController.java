package com.udea.innosistemas.controller;

import com.udea.innosistemas.model.dto.CrearProyectoRequest;
import com.udea.innosistemas.model.dto.ProyectoDto;
import com.udea.innosistemas.service.ProyectoService;
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
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@Tag(name = "Proyectos", description = "API para la gestión de proyectos")
@SecurityRequirement(name = "bearerAuth")
public class ProyectoController {

    private final ProyectoService proyectoService;

    @PostMapping
    @Operation(summary = "Crear un nuevo proyecto", description = "Crea un nuevo proyecto asociado a un equipo. Solo usuarios con rol LIDER en el equipo pueden crear proyectos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Proyecto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear proyectos en este equipo")
    })
    public ResponseEntity<ProyectoDto> crearProyecto(
            @Valid @RequestBody CrearProyectoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProyectoDto proyecto = proyectoService.crearProyecto(request, userDetails.getUsername());
        return new ResponseEntity<>(proyecto, HttpStatus.CREATED);
    }

    @GetMapping("/equipo/{equipoId}")
    @Operation(summary = "Listar proyectos de un equipo", description = "Obtiene todos los proyectos de un equipo específico")
    public ResponseEntity<List<ProyectoDto>> listarProyectosPorEquipo(
            @PathVariable Integer equipoId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ProyectoDto> proyectos = proyectoService.listarProyectosPorEquipo(equipoId, userDetails.getUsername());
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un proyecto por ID", description = "Obtiene los detalles de un proyecto específico")
    public ResponseEntity<ProyectoDto> obtenerProyectoPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProyectoDto proyecto = proyectoService.obtenerProyectoPorId(id, userDetails.getUsername());
        return ResponseEntity.ok(proyecto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un proyecto", description = "Actualiza la información de un proyecto. Solo usuarios con rol LIDER en el equipo pueden modificar proyectos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proyecto actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado para modificar este proyecto")
    })
    public ResponseEntity<ProyectoDto> actualizarProyecto(
            @PathVariable Integer id,
            @Valid @RequestBody CrearProyectoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProyectoDto proyecto = proyectoService.actualizarProyecto(id, request, userDetails.getUsername());
        return ResponseEntity.ok(proyecto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un proyecto", description = "Elimina un proyecto. Solo usuarios con rol LIDER en el equipo pueden eliminar proyectos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Proyecto eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar este proyecto")
    })
    public ResponseEntity<Void> eliminarProyecto(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        proyectoService.eliminarProyecto(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}