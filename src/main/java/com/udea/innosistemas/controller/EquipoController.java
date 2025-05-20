package com.udea.innosistemas.controller;

import com.udea.innosistemas.model.dto.CrearEquipoRequest;
import com.udea.innosistemas.model.dto.EquipoDto;
import com.udea.innosistemas.service.EquipoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
@Tag(name = "Equipos", description = "API para la gestión de equipos")
@SecurityRequirement(name = "bearerAuth")
public class EquipoController {

    private final EquipoService equipoService;

    @PostMapping
    public ResponseEntity<EquipoDto> crearEquipo(
            @Valid @RequestBody CrearEquipoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        EquipoDto equipo = equipoService.crearEquipo(request, userDetails.getUsername());
        return ResponseEntity.ok(equipo);
    }

    @GetMapping
    public ResponseEntity<List<EquipoDto>> listarEquiposDelUsuario(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<EquipoDto> equipos = equipoService.listarEquiposDelUsuario(userDetails.getUsername());
        return ResponseEntity.ok(equipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDto> obtenerEquipo(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        EquipoDto equipo = equipoService.obtenerEquipo(id, userDetails.getUsername());
        return ResponseEntity.ok(equipo);
    }
}