package com.udea.innosistemas.repository;

import com.udea.innosistemas.model.entity.Proyecto;
import com.udea.innosistemas.model.entity.Tarea;
import com.udea.innosistemas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByProyecto(Proyecto proyecto);

    List<Tarea> findByAsignadaA(Usuario usuario);

    List<Tarea> findByCreadaPor(Usuario usuario);

    @Query("SELECT t FROM Tarea t WHERE t.proyecto.id = :proyectoId")
    List<Tarea> findByProyectoId(@Param("proyectoId") Integer proyectoId);

    @Query("SELECT t FROM Tarea t WHERE t.asignadaA.id = :usuarioId")
    List<Tarea> findByAsignadaAId(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT t FROM Tarea t WHERE t.proyecto.equipo.id = :equipoId")
    List<Tarea> findByEquipoId(@Param("equipoId") Integer equipoId);

    @Query("SELECT t FROM Tarea t WHERE t.fechaEntrega < :fecha AND t.estado != com.udea.innosistemas.model.enums.EstadoTarea.COMPLETADA")
    List<Tarea> findTareasVencidas(@Param("fecha") LocalDate fecha);
}