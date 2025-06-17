package com.udea.innosistemas.service;

import com.udea.innosistemas.exception.ResourceNotFoundException;
import com.udea.innosistemas.exception.ValidationException;
import com.udea.innosistemas.model.dto.ActualizarEstadoTareaRequest;
import com.udea.innosistemas.model.dto.CrearTareaRequest;
import com.udea.innosistemas.model.dto.TareaDto;
import com.udea.innosistemas.model.dto.UsuarioDto;
import com.udea.innosistemas.model.entity.Proyecto;
import com.udea.innosistemas.model.entity.Tarea;
import com.udea.innosistemas.model.entity.Usuario;
import com.udea.innosistemas.model.enums.EstadoTarea;
import com.udea.innosistemas.repository.MiembroEquipoRepository;
import com.udea.innosistemas.repository.ProyectoRepository;
import com.udea.innosistemas.repository.TareaRepository;
import com.udea.innosistemas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MiembroEquipoRepository miembroEquipoRepository;

    @Transactional
    public TareaDto crearTarea(CrearTareaRequest request, String emailCreador) {
        // Obtener el proyecto
        Proyecto proyecto = proyectoRepository.findById(request.getProyectoId())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", request.getProyectoId()));

        // Obtener el usuario creador
        Usuario creador = usuarioRepository.findByEmail(emailCreador)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailCreador));

        // Verificar que el usuario es miembro del equipo
        if (!miembroEquipoRepository.existsByEquipoAndUsuario(proyecto.getEquipo(), creador)) {
            throw new ValidationException("El creador debe ser miembro del equipo al que pertenece el proyecto");
        }

        // Si se especificó un usuario asignado, verificar que existe y es miembro del equipo
        Usuario asignado = null;
        if (request.getAsignadaAId() != null) {
            asignado = usuarioRepository.findById(request.getAsignadaAId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario asignado", "id", request.getAsignadaAId()));

            if (!miembroEquipoRepository.existsByEquipoAndUsuario(proyecto.getEquipo(), asignado)) {
                throw new ValidationException("El usuario asignado debe ser miembro del equipo al que pertenece el proyecto");
            }
        }

        // Validar que la fecha de entrega no sea posterior a la fecha de fin del proyecto
        if (request.getFechaEntrega() != null &&
                request.getFechaEntrega().isAfter(proyecto.getFechaFinPrevista())) {
            throw new ValidationException("La fecha de entrega no puede ser posterior a la fecha de finalización prevista del proyecto");
        }

        // Crear la tarea
        Tarea tarea = Tarea.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .proyecto(proyecto)
                .creadaPor(creador)
                .asignadaA(asignado)
                .estado(request.getEstado())
                .prioridad(request.getPrioridad())
                .fechaEntrega(request.getFechaEntrega())
                .build();

        // Si el estado es COMPLETADA, establecer la fecha de completado
        if (request.getEstado() == EstadoTarea.COMPLETADA) {
            tarea.setFechaCompletado(LocalDate.now());
        }

        Tarea tareaGuardada = tareaRepository.save(tarea);

        return mapearADto(tareaGuardada);
    }

    @Transactional(readOnly = true)
    public List<TareaDto> listarTareasPorProyecto(Integer proyectoId, String emailUsuario) {
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

        // Obtener las tareas del proyecto
        List<Tarea> tareas = tareaRepository.findByProyecto(proyecto);

        return tareas.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TareaDto> listarTareasAsignadas(String emailUsuario) {
        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Obtener las tareas asignadas al usuario
        List<Tarea> tareas = tareaRepository.findByAsignadaA(usuario);

        return tareas.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TareaDto> listarTareasCreadas(String emailUsuario) {
        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Obtener las tareas creadas por el usuario
        List<Tarea> tareas = tareaRepository.findByCreadaPor(usuario);

        return tareas.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TareaDto obtenerTareaPorId(Integer tareaId, String emailUsuario) {
        // Obtener la tarea
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", tareaId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es miembro del equipo al que pertenece el proyecto de la tarea
        if (!miembroEquipoRepository.existsByEquipoAndUsuario(tarea.getProyecto().getEquipo(), usuario)) {
            throw new ValidationException("El usuario no tiene acceso a esta tarea");
        }

        return mapearADto(tarea);
    }

    @Transactional
    public TareaDto actualizarTarea(Integer tareaId, CrearTareaRequest request, String emailUsuario) {
        // Obtener la tarea
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", tareaId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es el creador de la tarea o es el líder del equipo
        boolean esCreador = tarea.getCreadaPor().getId().equals(usuario.getId());
        boolean esLider = miembroEquipoRepository.findByEquipoAndUsuario(tarea.getProyecto().getEquipo(), usuario)
                .map(miembro -> "LIDER".equals(miembro.getRol().name()))
                .orElse(false);

        if (!esCreador && !esLider) {
            throw new ValidationException("No tienes permisos para actualizar esta tarea");
        }

        // Verificar que el proyecto no haya cambiado
        if (!tarea.getProyecto().getId().equals(request.getProyectoId())) {
            throw new ValidationException("No se puede cambiar el proyecto de la tarea");
        }

        // Si se especificó un usuario asignado, verificar que existe y es miembro del equipo
        Usuario asignado = null;
        if (request.getAsignadaAId() != null) {
            asignado = usuarioRepository.findById(request.getAsignadaAId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario asignado", "id", request.getAsignadaAId()));

            if (!miembroEquipoRepository.existsByEquipoAndUsuario(tarea.getProyecto().getEquipo(), asignado)) {
                throw new ValidationException("El usuario asignado debe ser miembro del equipo al que pertenece el proyecto");
            }
        }

        // Validar que la fecha de entrega no sea posterior a la fecha de fin del proyecto
        if (request.getFechaEntrega() != null &&
                request.getFechaEntrega().isAfter(tarea.getProyecto().getFechaFinPrevista())) {
            throw new ValidationException("La fecha de entrega no puede ser posterior a la fecha de finalización prevista del proyecto");
        }

        // Actualizar la tarea
        tarea.setTitulo(request.getTitulo());
        tarea.setDescripcion(request.getDescripcion());
        tarea.setAsignadaA(asignado);
        tarea.setPrioridad(request.getPrioridad());
        tarea.setFechaEntrega(request.getFechaEntrega());

        // Actualizar el estado y la fecha de completado si es necesario
        if (request.getEstado() == EstadoTarea.COMPLETADA && tarea.getEstado() != EstadoTarea.COMPLETADA) {
            tarea.setFechaCompletado(LocalDate.now());
        } else if (request.getEstado() != EstadoTarea.COMPLETADA) {
            tarea.setFechaCompletado(null);
        }

        tarea.setEstado(request.getEstado());

        Tarea tareaActualizada = tareaRepository.save(tarea);

        return mapearADto(tareaActualizada);
    }

    @Transactional
    public TareaDto actualizarEstadoTarea(Integer tareaId, ActualizarEstadoTareaRequest request, String emailUsuario) {
        // Obtener la tarea
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", tareaId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es el asignado a la tarea, el creador o el líder del equipo
        boolean esAsignado = tarea.getAsignadaA() != null && tarea.getAsignadaA().getId().equals(usuario.getId());
        boolean esCreador = tarea.getCreadaPor().getId().equals(usuario.getId());
        boolean esLider = miembroEquipoRepository.findByEquipoAndUsuario(tarea.getProyecto().getEquipo(), usuario)
                .map(miembro -> "LIDER".equals(miembro.getRol().name()))
                .orElse(false);

        if (!esAsignado && !esCreador && !esLider) {
            throw new ValidationException("No tienes permisos para actualizar el estado de esta tarea");
        }

        // Validar la transición de estado
        validarTransicionEstado(tarea.getEstado(), request.getEstado());

        // Actualizar el estado
        tarea.setEstado(request.getEstado());

        // Si el estado es COMPLETADA, establecer la fecha de completado
        if (request.getEstado() == EstadoTarea.COMPLETADA) {
            tarea.setFechaCompletado(LocalDate.now());
        } else {
            tarea.setFechaCompletado(null);
        }

        Tarea tareaActualizada = tareaRepository.save(tarea);

        return mapearADto(tareaActualizada);
    }

    @Transactional
    public void eliminarTarea(Integer tareaId, String emailUsuario) {
        // Obtener la tarea
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", tareaId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        // Verificar que el usuario es el creador de la tarea o es el líder del equipo
        boolean esCreador = tarea.getCreadaPor().getId().equals(usuario.getId());
        boolean esLider = miembroEquipoRepository.findByEquipoAndUsuario(tarea.getProyecto().getEquipo(), usuario)
                .map(miembro -> "LIDER".equals(miembro.getRol().name()))
                .orElse(false);

        if (!esCreador && !esLider) {
            throw new ValidationException("No tienes permisos para eliminar esta tarea");
        }

        // Eliminar la tarea
        tareaRepository.delete(tarea);
    }

    // Validar la transición de estados
    private void validarTransicionEstado(EstadoTarea estadoActual, EstadoTarea nuevoEstado) {
        // Implementar reglas de transición de estados si son necesarias
        // Por ejemplo, no permitir pasar de PENDIENTE a COMPLETADA directamente
        if (estadoActual == EstadoTarea.PENDIENTE && nuevoEstado == EstadoTarea.COMPLETADA) {
            throw new ValidationException("No se puede cambiar directamente de PENDIENTE a COMPLETADA");
        }

        // Otro ejemplo: no permitir retroceder de REVISION a EN_PROGRESO
        if (estadoActual == EstadoTarea.REVISION && nuevoEstado == EstadoTarea.EN_PROGRESO) {
            throw new ValidationException("No se puede retroceder de REVISION a EN_PROGRESO");
        }
    }

    private TareaDto mapearADto(Tarea tarea) {
        return TareaDto.builder()
                .id(tarea.getId())
                .titulo(tarea.getTitulo())
                .descripcion(tarea.getDescripcion())
                .proyectoId(tarea.getProyecto().getId())
                .proyectoNombre(tarea.getProyecto().getNombre())
                .creadaPor(UsuarioDto.builder()
                        .id(tarea.getCreadaPor().getId())
                        .nombre(tarea.getCreadaPor().getNombre())
                        .email(tarea.getCreadaPor().getEmail())
                        .rol(tarea.getCreadaPor().getRol())
                        .build())
                .asignadaA(tarea.getAsignadaA() != null ?
                        UsuarioDto.builder()
                                .id(tarea.getAsignadaA().getId())
                                .nombre(tarea.getAsignadaA().getNombre())
                                .email(tarea.getAsignadaA().getEmail())
                                .rol(tarea.getAsignadaA().getRol())
                                .build() : null)
                .estado(tarea.getEstado())
                .prioridad(tarea.getPrioridad())
                .fechaCreacion(tarea.getFechaCreacion())
                .fechaEntrega(tarea.getFechaEntrega())
                .fechaCompletado(tarea.getFechaCompletado())
                .build();
    }
}