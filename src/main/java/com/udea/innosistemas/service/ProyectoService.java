package com.udea.innosistemas.service;

import com.udea.innosistemas.exception.ResourceNotFoundException;
import com.udea.innosistemas.model.dto.CrearProyectoRequest;
import com.udea.innosistemas.model.dto.ProyectoDto;
import com.udea.innosistemas.model.dto.UsuarioDto;
import com.udea.innosistemas.model.entity.Equipo;
import com.udea.innosistemas.model.entity.Proyecto;
import com.udea.innosistemas.model.entity.Usuario;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MiembroEquipoRepository miembroEquipoRepository;

    @Transactional
    public ProyectoDto crearProyecto(CrearProyectoRequest request, String emailCreador){

        // Obtener el equipo
        Equipo equipo = equipoRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", request.getEquipoId()));

        // Obtener usuario que creó el proyecto
        Usuario creador = usuarioRepository.findByEmail(emailCreador)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado con email:" + emailCreador));

        // Verificar si un usuario hace parte del equipo
        if(!miembroEquipoRepository.existsByEquipoAndUsuario(equipo, creador)){
            throw new RuntimeException("El usuario no es miembro del equipo");
        }

        //Verificar que no exista otro proyecto con el mismo nombre
        if(proyectoRepository.existsByNombreAndEquipo(request.getNombre(), equipo)){
            throw new RuntimeException("Ya existe un proyecto con el nombre: " + request.getNombre());
        }

        //Crear el proyecto
        Proyecto proyecto = Proyecto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaCreacion(LocalDateTime.now())
                .equipo(equipo)
                .creador(creador)
                .build();

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        return mapearDto(proyectoGuardado);
    }

    @Transactional(readOnly = true)
    public List<ProyectoDto> listarProyectosPorEquipo(Integer equipoId, String emailUsuario){

        // Obtener el equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", equipoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado con email:" + emailUsuario));

        // Verificar si un usuario hace parte del equipo
        if(!miembroEquipoRepository.existsByEquipoAndUsuario(equipo, usuario)){
            throw new RuntimeException("El usuario no es miembro del equipo");
        }

        // Obtener los proyectos del equipo
        List<Proyecto> proyectos = proyectoRepository.findByEquipo(equipo);

        return  proyectos.stream()
                .map(this::mapearDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProyectoDto obtenerProyectoPorId(Integer proyectoId, String emailUsuario){
        // Obtener el proyecto
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(()-> new ResourceNotFoundException("Proyecto", "id", proyectoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado con email:" + emailUsuario));

        // Verificar si un usuario hace parte del equipo al que pertenece el proyecto
        if(!miembroEquipoRepository.existsByEquipoAndUsuario(proyecto.getEquipo(), usuario)){
            throw new RuntimeException("El usuario no es miembro del equipo");
        }

        return mapearDto(proyecto);
    }

    @Transactional
    public ProyectoDto actualizarProyecto(Integer proyectoId, CrearProyectoRequest request, String emailUsuario ){
        // Obtener el Proyecto
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(()-> new ResourceNotFoundException("Proyecto", "id", proyectoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado con email:" + emailUsuario));

        // Verificar si el usuario es creador del proyecto o miembro del equipo
        if(!proyecto.getCreador().getId().equals(usuario.getId()) &&
                !miembroEquipoRepository.existsByEquipoAndUsuario(proyecto.getEquipo(), usuario)){
            throw new RuntimeException("No tienes permisos suficientes para actualizar este proyecto");
        }

        // Verificar que no se intente cambiar el equipo
        if(!proyecto.getEquipo().getId().equals(request.getEquipoId())){
            throw new RuntimeException("No puedes cambiar el equipo del proyecto");
        }

        // Verificar que el nombre no se repita
        if(!proyecto.getNombre().equals(request.getNombre()) &&
                proyectoRepository.existsByNombreAndEquipo(request.getNombre(), proyecto.getEquipo())){
            throw new RuntimeException("Ya existe un proyecto con el nombre: " + request.getNombre());
        }

        //actualizar el proyecto
        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());

        Proyecto proyectoActualizado = proyectoRepository.save(proyecto);

        return mapearDto(proyectoActualizado);

    }

    @Transactional
    public void eliminarProyecto(Integer proyectoId, String emailUsuario){
        // Obtener el proyecto
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(()-> new ResourceNotFoundException("Proyecto", "id", proyectoId));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado con email:" + emailUsuario));

        // Verificar si el usuario es creador del proyecto
        if(!proyecto.getCreador().getId().equals(usuario.getId())){
            throw new RuntimeException("No tienes permisos suficientes para eliminar este proyecto");
        }

        // Eliminar el proyecto
        proyectoRepository.delete(proyecto);
    }

    private ProyectoDto mapearDto(Proyecto proyecto) {
        return ProyectoDto.builder()
                .id(proyecto.getId())
                .nombre(proyecto.getNombre())
                .descripcion(proyecto.getDescripcion())
                .fechaCreacion(proyecto.getFechaCreacion())
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
