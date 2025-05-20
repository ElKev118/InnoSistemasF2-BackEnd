package com.udea.innosistemas.controller;

import com.udea.innosistemas.model.dto.LoginRequest;
import com.udea.innosistemas.model.dto.LoginResponse;
import com.udea.innosistemas.model.dto.RegistroRequest;
import com.udea.innosistemas.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para la autenticación de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registrarUsuario(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.autenticarUsuario(request));
    }
}