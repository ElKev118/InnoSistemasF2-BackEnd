package com.udea.innosistemas.controller;


import com.udea.innosistemas.model.dto.CrearProyectoRequest;
import com.udea.innosistemas.model.dto.ProyectoDto;
import com.udea.innosistemas.service.ProyectoService;
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
@Tag(name = "Proyectos", description = "API para la gestión de proyectos")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProyectoController {
    private final ProyectoService proyectoService;

    @PostMapping
    public ResponseEntity<ProyectoDto> crearProyecto(
            @Valid @RequestBody CrearProyectoRequest request,
            @AuthenticationPrincipal UserDetails userDetails){
        ProyectoDto proyecto = proyectoService.crearProyecto( request, userDetails.getUsername() );
        return new ResponseEntity<>(proyecto, HttpStatus.CREATED);
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<ProyectoDto>> listarProyectosPorEquipo(
            @PathVariable Integer equipoId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        List<ProyectoDto> proyectos = proyectoService.listarProyectosPorEquipo(equipoId, userDetails.getUsername());
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoDto> obtenerProyectoPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        ProyectoDto proyecto = proyectoService.obtenerProyectoPorId(id, userDetails.getUsername());
        return ResponseEntity.ok(proyecto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoDto> actualizarProyecto(
            @PathVariable Integer id,
            @Valid @RequestBody CrearProyectoRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        ProyectoDto proyecto = proyectoService.actualizarProyecto(id, request, userDetails.getUsername());
        return ResponseEntity.ok(proyecto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProyecto(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        proyectoService.eliminarProyecto(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
