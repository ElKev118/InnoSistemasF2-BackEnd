package com.udea.innosistemas.service;

import com.udea.innosistemas.model.dto.LoginRequest;
import com.udea.innosistemas.model.dto.LoginResponse;
import com.udea.innosistemas.model.dto.RegistroRequest;
import com.udea.innosistemas.model.dto.UsuarioDto;
import com.udea.innosistemas.model.entity.Usuario;
import com.udea.innosistemas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;

    public LoginResponse registrarUsuario(RegistroRequest request) {
        // Verificar si el correo ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        // Crear nuevo usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .fechaCreacion(LocalDateTime.now())
                .build();

        usuarioRepository.save(usuario);

        // Generar token JWT
        String jwt = jwtService.generateToken(usuario);

        // Mapear a DTO
        UsuarioDto usuarioDto = UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();

        // Devolver respuesta
        return LoginResponse.builder()
                .token(jwt)
                .usuario(usuarioDto)
                .build();
    }

    public LoginResponse autenticarUsuario(LoginRequest request) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Obtener usuario autenticado
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Actualizar último acceso
        usuarioService.actualizarUltimoAcceso(usuario.getEmail());

        // Generar token JWT
        String jwt = jwtService.generateToken(usuario);

        // Obtener DTO de usuario
        UsuarioDto usuarioDto = usuarioService.buscarPorEmail(usuario.getEmail());

        // Devolver respuesta
        return LoginResponse.builder()
                .token(jwt)
                .usuario(usuarioDto)
                .build();
    }
}