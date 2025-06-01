package com.udea.innosistemas.service;

import com.udea.innosistemas.exception.ResourceNotFoundException;
import com.udea.innosistemas.exception.ValidationException;
import com.udea.innosistemas.model.dto.CrearProyectoRequest;
import com.udea.innosistemas.model.dto.ProyectoDto;
import com.udea.innosistemas.model.dto.UsuarioDto;
import com.udea.innosistemas.model.entity.Equipo;
import com.udea.innosistemas.model.entity.MiembroEquipo;
import com.udea.innosistemas.model.entity.Proyecto;
import com.udea.innosistemas.model.entity.Usuario;
import com.udea.innosistemas.model.enums.EstadoProyecto;
import com.udea.innosistemas.model.enums.RolEquipo;
import com.udea.innosistemas.repository.EquipoRepository;
import com.udea.innosistemas.repository.MiembroEquipoRepository;
import com.udea.innosistemas.repository.ProyectoRepository;
import com.udea.innosistemas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MiembroEquipoRepository miembroEquipoRepository;

    // Método auxiliar para verificar si el usuario es LIDER en el equipo
    private boolean esLiderDelEquipo(Usuario usuario, Equipo equipo) {
        Optional<MiembroEquipo> miembro = miembroEquipoRepository.findByEquipoAndUsuario(equipo, usuario);
        return miembro.isPresent() && miembro.get().getRol() == RolEquipo.LIDER;
    }

    @Transactional
    public ProyectoDto crearProyecto(CrearProyectoRequest request, String emailCreador) {
        // Obtener el equipo
        Equipo equipo = equipoRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", request.getEquipoId()));

        // Obtener el usuario creador
        Usuario creador = usuarioRepository.findByEmail(emailCreador)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailCreador));

        // Verificar que el usuario es miembro del equipo
        if (!miembroEquipoRepository.existsByEquipoAndUsuario(equipo, creador)) {
            throw new ValidationException("El usuario no es miembro del equipo");
        }

        // Verificar que el usuario es LIDER del equipo
        if (!esLiderDelEquipo(creador, equipo)) {
            throw new ValidationException("Solo los líderes del equipo pueden crear proyectos");
        }

        // Verificar que el equipo tenga al menos un miembro
        List<MiembroEquipo> miembros = miembroEquipoRepository.findByEquipo(equipo);
        if (miembros.isEmpty()) {
            throw new ValidationException("El equipo debe tener al menos un miembro para crear un proyecto");
        }

        // Verificar que no exista otro proyecto con el mismo nombre en el equipo
        if (proyectoRepository.existsByNombreAndEquipo(request.getNombre(), equipo)) {
            throw new ValidationException("Ya existe un proyecto con el nombre: " + request.getNombre() + " en este equipo");
        }

        // Validar fechas
        if (request.getFechaInicio().isAfter(request.getFechaFinPrevista())) {
            throw new ValidationException("La fecha de inicio debe ser anterior a la fecha de finalización prevista");
        }

        // Crear el proyecto
        Proyecto proyecto = Proyecto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaCreacion(LocalDateTime.now())
                .fechaInicio(request.getFechaInicio())
                .fechaFinPrevista(request.getFechaFinPrevista())
                .estado(request.getEstado())
                .equipo(equipo)
                .creador(creador)
                .build();

        // Si el estado es COMPLETADO, establecer la fecha de completado
        if (request.getEstado() == EstadoProyecto.COMPLETADO) {
            proyecto.setFechaCompletado(java.time.LocalDate.now());
        }

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        return mapearADto(proyectoGuardado);
    }

    @Transactional
    public ProyectoDto actualizarProyecto(Integer proyectoId, CrearProyectoRequest request, String emailUsuario) {
        // Obtener el proyecto
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", proyectoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es LIDER del equipo al que pertenece el proyecto
        if (!esLiderDelEquipo(usuario, proyecto.getEquipo())) {
            throw new ValidationException("Solo los líderes del equipo pueden modificar proyectos");
        }

        // Verificar que el equipo no haya cambiado
        if (!proyecto.getEquipo().getId().equals(request.getEquipoId())) {
            throw new ValidationException("No se puede cambiar el equipo del proyecto");
        }

        // Verificar que el nombre no exista ya en otro proyecto del equipo (excepto si es el mismo proyecto)
        if (!proyecto.getNombre().equals(request.getNombre()) &&
                proyectoRepository.existsByNombreAndEquipo(request.getNombre(), proyecto.getEquipo())) {
            throw new ValidationException("Ya existe un proyecto con el nombre: " + request.getNombre() + " en este equipo");
        }

        // Validar fechas
        if (request.getFechaInicio().isAfter(request.getFechaFinPrevista())) {
            throw new ValidationException("La fecha de inicio debe ser anterior a la fecha de finalización prevista");
        }

        // Actualizar el proyecto
        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setFechaInicio(request.getFechaInicio());
        proyecto.setFechaFinPrevista(request.getFechaFinPrevista());
        proyecto.setEstado(request.getEstado());

        // Si el estado cambia a COMPLETADO, establecer la fecha de completado
        if (request.getEstado() == EstadoProyecto.COMPLETADO && proyecto.getFechaCompletado() == null) {
            proyecto.setFechaCompletado(java.time.LocalDate.now());
        } else if (request.getEstado() != EstadoProyecto.COMPLETADO) {
            proyecto.setFechaCompletado(null); // Si cambia a otro estado, eliminar fecha de completado
        }

        Proyecto proyectoActualizado = proyectoRepository.save(proyecto);

        return mapearADto(proyectoActualizado);
    }

    @Transactional
    public void eliminarProyecto(Integer proyectoId, String emailUsuario) {
        // Obtener el proyecto
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", proyectoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es LIDER del equipo al que pertenece el proyecto
        if (!esLiderDelEquipo(usuario, proyecto.getEquipo())) {
            throw new ValidationException("Solo los líderes del equipo pueden eliminar proyectos");
        }

        // Eliminar el proyecto
        proyectoRepository.delete(proyecto);
    }

    // Los demás métodos permanecen igual...
    @Transactional(readOnly = true)
    public List<ProyectoDto> listarProyectosPorEquipo(Integer equipoId, String emailUsuario) {
        // Obtener el equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", equipoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es miembro del equipo
        if (!miembroEquipoRepository.existsByEquipoAndUsuario(equipo, usuario)) {
            throw new ValidationException("El usuario no es miembro del equipo");
        }

        // Obtener los proyectos del equipo
        List<Proyecto> proyectos = proyectoRepository.findByEquipo(equipo);

        return proyectos.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProyectoDto obtenerProyectoPorId(Integer proyectoId, String emailUsuario) {
        // Obtener el proyecto
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", proyectoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es miembro del equipo al que pertenece el proyecto
        if (!miembroEquipoRepository.existsByEquipoAndUsuario(proyecto.getEquipo(), usuario)) {
            throw new ValidationException("El usuario no tiene acceso a este proyecto");
        }

        return mapearADto(proyecto);
    }

    private ProyectoDto mapearADto(Proyecto proyecto) {
        return ProyectoDto.builder()
                .id(proyecto.getId())
                .nombre(proyecto.getNombre())
                .descripcion(proyecto.getDescripcion())
                .fechaCreacion(proyecto.getFechaCreacion())
                .fechaInicio(proyecto.getFechaInicio())
                .fechaFinPrevista(proyecto.getFechaFinPrevista())
                .fechaCompletado(proyecto.getFechaCompletado())
                .estado(proyecto.getEstado())
                .equipoId(proyecto.getEquipo().getId())
                .equipoNombre(proyecto.getEquipo().getNombre())
                .creador(UsuarioDto.builder()
                        .id(proyecto.getCreador().getId())
                        .nombre(proyecto.getCreador().getNombre())
                        .email(proyecto.getCreador().getEmail())
                        .rol(proyecto.getCreador().getRol())
                        .build())
                .build();
    }
}