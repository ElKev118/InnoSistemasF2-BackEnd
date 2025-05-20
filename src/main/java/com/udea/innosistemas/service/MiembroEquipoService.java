package com.udea.innosistemas.service;

import com.udea.innosistemas.exception.ResourceNotFoundException;
import com.udea.innosistemas.model.dto.AgregarMiembroRequest;
import com.udea.innosistemas.model.dto.MiembroEquipoDto;
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
public class MiembroEquipoService {

    private final MiembroEquipoRepository miembroEquipoRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public MiembroEquipoDto agregarMiembro(Integer equipoId, AgregarMiembroRequest request, String emailLider) {
        // Verificar si el equipo existe
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", equipoId));

        // Verificar si el usuario que realiza la acción es líder del equipo
        Usuario lider = usuarioRepository.findByEmail(emailLider)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailLider));

        boolean esLider = miembroEquipoRepository.existsByEquipoAndUsuarioAndRol(equipo, lider, RolEquipo.LIDER);
        if (!esLider) {
            throw new RuntimeException("Solo el líder puede agregar miembros al equipo");
        }

        // Buscar el usuario a agregar
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario a agregar no encontrado con email: " + request.getEmail()));

        // Verificar que no sea el mismo usuario líder
        if (usuario.getId().equals(lider.getId())) {
            throw new RuntimeException("No puedes agregarte a ti mismo, ya eres miembro del equipo");
        }

        // Verificar si el usuario ya es miembro del equipo
        if (miembroEquipoRepository.existsByEquipoAndUsuario(equipo, usuario)) {
            throw new RuntimeException("El usuario ya es miembro de este equipo");
        }

        // Crear la relación miembro-equipo
        MiembroEquipo miembroEquipo = MiembroEquipo.builder()
                .equipo(equipo)
                .usuario(usuario)
                .rol(request.getRol())
                .fechaIncorporacion(LocalDateTime.now())
                .build();

        MiembroEquipo miembroGuardado = miembroEquipoRepository.save(miembroEquipo);
        return mapearADto(miembroGuardado);
    }

    @Transactional
    public void eliminarMiembro(Integer equipoId, Integer miembroId, String emailLider) {
        // Verificar si el equipo existe
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", equipoId));

        // Verificar si el usuario que realiza la acción es líder del equipo
        Usuario lider = usuarioRepository.findByEmail(emailLider)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailLider));

        boolean esLider = miembroEquipoRepository.existsByEquipoAndUsuarioAndRol(equipo, lider, RolEquipo.LIDER);
        if (!esLider) {
            throw new RuntimeException("Solo el líder puede eliminar miembros del equipo");
        }

        // Verificar que el miembro exista en el equipo
        MiembroEquipo miembro = miembroEquipoRepository.findById(miembroId)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro", "id", miembroId));

        // Verificar que el miembro pertenezca al equipo
        if (!miembro.getEquipo().getId().equals(equipoId)) {
            throw new RuntimeException("El miembro no pertenece a este equipo");
        }

        // Verificar que no se esté intentando eliminar al líder
        if (miembro.getRol() == RolEquipo.LIDER && miembro.getUsuario().getId().equals(lider.getId())) {
            throw new RuntimeException("No puedes eliminarte a ti mismo como líder del equipo");
        }

        miembroEquipoRepository.delete(miembro);
    }

    @Transactional(readOnly = true)
    public List<MiembroEquipoDto> listarMiembrosDeEquipo(Integer equipoId, String emailUsuario) {
        // Verificar si el equipo existe
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", equipoId));

        // Verificar si el usuario pertenece al equipo
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        if (!miembroEquipoRepository.existsByEquipoAndUsuario(equipo, usuario)) {
            throw new RuntimeException("No tienes acceso a este equipo");
        }

        // Listar todos los miembros del equipo
        List<MiembroEquipo> miembros = miembroEquipoRepository.findByEquipo(equipo);

        return miembros.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private MiembroEquipoDto mapearADto(MiembroEquipo miembroEquipo) {
        return MiembroEquipoDto.builder()
                .id(miembroEquipo.getId())
                .equipoId(miembroEquipo.getEquipo().getId())
                .equipoNombre(miembroEquipo.getEquipo().getNombre())
                .usuario(UsuarioDto.builder()
                        .id(miembroEquipo.getUsuario().getId())
                        .nombre(miembroEquipo.getUsuario().getNombre())
                        .email(miembroEquipo.getUsuario().getEmail())
                        .rol(miembroEquipo.getUsuario().getRol())
                        .build())
                .rol(miembroEquipo.getRol())
                .fechaIncorporacion(miembroEquipo.getFechaIncorporacion())
                .build();
    }
}