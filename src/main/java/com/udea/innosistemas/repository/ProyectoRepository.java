package com.udea.innosistemas.repository;

import com.udea.innosistemas.model.entity.Equipo;
import com.udea.innosistemas.model.entity.Proyecto;
import com.udea.innosistemas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    List<Proyecto> findByEquipo(Equipo equipo);

    List<Proyecto> findByCreador(Usuario creador);

    boolean existsByNombreAndEquipo(String nombre, Equipo equipo);

    @Query("SELECT p FROM Proyecto p WHERE p.equipo.id = :equipoId")
    List<Proyecto> findByEquipoId(Integer equipoId);
}