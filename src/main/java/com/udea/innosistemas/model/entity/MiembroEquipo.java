package com.udea.innosistemas.model.entity;

import com.udea.innosistemas.model.enums.RolEquipo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "miembro_equipo", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"equipo_id", "usuario_id"})
})
public class MiembroEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolEquipo rol;

    @Column(name = "fecha_incorporacion", nullable = false, updatable = false)
    private LocalDateTime fechaIncorporacion;

    @PrePersist
    protected void onCreate() {
        fechaIncorporacion = LocalDateTime.now();
    }
}