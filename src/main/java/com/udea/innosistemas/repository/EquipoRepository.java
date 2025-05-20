package com.udea.innosistemas.repository;

import com.udea.innosistemas.model.entity.Equipo;
import com.udea.innosistemas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Integer> {

    @Query("SELECT e FROM Equipo e WHERE e.creador = :usuario")
    List<Equipo> findByCreador(Usuario usuario);

    @Query("SELECT e FROM Equipo e JOIN MiembroEquipo me ON e.id = me.equipo.id WHERE me.usuario = :usuario")
    List<Equipo> findByMiembro(Usuario usuario);

    boolean existsByNombre(String nombre);
}