package com.rollerspeed.rollerspeed.Model;

import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_asistencia")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoAsistencia estado = EstadoAsistencia.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "alumno_id", nullable = false)
    private AlumnoModel alumno;

    @ManyToOne
    @JoinColumn(name = "clase_id", nullable = false)
    private ClaseModel clase;

    public enum EstadoAsistencia{
        PENDIENTE,
        PRESENTE,
        AUSENTE
    }

}
