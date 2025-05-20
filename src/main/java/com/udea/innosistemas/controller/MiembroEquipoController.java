package com.udea.innosistemas.controller;

import com.udea.innosistemas.model.dto.AgregarMiembroRequest;
import com.udea.innosistemas.model.dto.MiembroEquipoDto;
import com.udea.innosistemas.service.MiembroEquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos/{equipoId}/miembros")
@RequiredArgsConstructor
public class MiembroEquipoController {

    private final MiembroEquipoService miembroEquipoService;

    @PostMapping
    public ResponseEntity<MiembroEquipoDto> agregarMiembro(
            @PathVariable Integer equipoId,
            @Valid @RequestBody AgregarMiembroRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        MiembroEquipoDto miembro = miembroEquipoService.agregarMiembro(
                equipoId, request, userDetails.getUsername());

        return ResponseEntity.ok(miembro);
    }

    @DeleteMapping("/{miembroId}")
    public ResponseEntity<?> eliminarMiembro(
            @PathVariable Integer equipoId,
            @PathVariable Integer miembroId,
            @AuthenticationPrincipal UserDetails userDetails) {

        miembroEquipoService.eliminarMiembro(equipoId, miembroId, userDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MiembroEquipoDto>> listarMiembrosDeEquipo(
            @PathVariable Integer equipoId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<MiembroEquipoDto> miembros = miembroEquipoService.listarMiembrosDeEquipo(
                equipoId, userDetails.getUsername());

        return ResponseEntity.ok(miembros);
    }
}