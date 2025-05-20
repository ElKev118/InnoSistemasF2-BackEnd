package com.udea.innosistemas.service;

import com.udea.innosistemas.exception.ResourceNotFoundException;
import com.udea.innosistemas.model.dto.CrearEquipoRequest;
import com.udea.innosistemas.model.dto.EquipoDto;
import com.udea.innosistemas.model.dto.UsuarioDto;
import com.udea.innosistemas.model.entity.Equipo;
import com.udea.innosistemas.model.entity.MiembroEquipo;
import com.udea.innosistemas.model.entity.Usuario;
import com.udea.innosistemas.model.enums.RolEquipo;
import com.udea.innosistemas.repository.EquipoRepository;
import com.udea.innosistemas.repository.MiembroEquipoRepository;
import com.udea.innosistemas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MiembroEquipoRepository miembroEquipoRepository;

    @Transactional
    public EquipoDto crearEquipo(CrearEquipoRequest request, String emailCreador) {
        // Verificar si ya existe un equipo con el mismo nombre
        if (equipoRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe un equipo con el nombre: " + request.getNombre());
        }

        // Buscar el usuario creador
        Usuario creador = usuarioRepository.findByEmail(emailCreador)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailCreador));

        // Crear equipo
        Equipo equipo = Equipo.builder()
                .nombre(request.getNombre())
                .fechaCreacion(LocalDateTime.now())
                .creador(creador)
                .build();

        Equipo equipoGuardado = equipoRepository.save(equipo);

        // Agregar al creador como líder del equipo
        MiembroEquipo miembroEquipo = MiembroEquipo.builder()
                .equipo(equipoGuardado)
                .usuario(creador)
                .rol(RolEquipo.LIDER)
                .fechaIncorporacion(LocalDateTime.now())
                .build();

        miembroEquipoRepository.save(miembroEquipo);

        return mapearADto(equipoGuardado, 1);
    }

    @Transactional(readOnly = true)
    public List<EquipoDto> listarEquiposDelUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Obtener equipos donde el usuario es miembro
        List<Equipo> equipos = equipoRepository.findByMiembro(usuario);

        return equipos.stream()
                .map(equipo -> {
                    Integer totalMiembros = miembroEquipoRepository.countByEquipo(equipo);
                    return mapearADto(equipo, totalMiembros);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipoDto obtenerEquipo(Integer equipoId, String emailUsuario) {
        // Verificar si el equipo existe
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", equipoId));

        // Verificar si el usuario pertenece al equipo
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        if (!miembroEquipoRepository.existsByEquipoAndUsuario(equipo, usuario)) {
            throw new RuntimeException("No tienes acceso a este equipo");
        }

        Integer totalMiembros = miembroEquipoRepository.countByEquipo(equipo);
        return mapearADto(equipo, totalMiembros);
    }

    private EquipoDto mapearADto(Equipo equipo, Integer totalMiembros) {
        return EquipoDto.builder()
                .id(equipo.getId())
                .nombre(equipo.getNombre())
                .fechaCreacion(equipo.getFechaCreacion())
                .creador(UsuarioDto.builder()
                        .id(equipo.getCreador().getId())
                        .nombre(equipo.getCreador().getNombre())
                        .email(equipo.getCreador().getEmail())
                        .rol(equipo.getCreador().getRol())
                        .build())
                .totalMiembros(totalMiembros)
                .build();
    }
}