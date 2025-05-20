package com.udea.innosistemas.repository;

import com.udea.innosistemas.model.entity.Equipo;
import com.udea.innosistemas.model.entity.MiembroEquipo;
import com.udea.innosistemas.model.entity.Usuario;
import com.udea.innosistemas.model.enums.RolEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MiembroEquipoRepository extends JpaRepository<MiembroEquipo, Integer> {

    List<MiembroEquipo> findByEquipo(Equipo equipo);

    List<MiembroEquipo> findByUsuario(Usuario usuario);

    Optional<MiembroEquipo> findByEquipoAndUsuario(Equipo equipo, Usuario usuario);

    boolean existsByEquipoAndUsuario(Equipo equipo, Usuario usuario);

    @Query("SELECT COUNT(me) FROM MiembroEquipo me WHERE me.equipo = :equipo")
    Integer countByEquipo(Equipo equipo);

    @Query("SELECT COUNT(me) > 0 FROM MiembroEquipo me WHERE me.equipo = :equipo AND me.usuario = :usuario AND me.rol = :rol")
    boolean existsByEquipoAndUsuarioAndRol(Equipo equipo, Usuario usuario, RolEquipo rol);
}