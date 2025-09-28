package com.rollerspeed.rollerspeed.Model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_clases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String dia;
    private LocalDateTime hora;
    private Integer duracionMinutos;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private InstructorModel instructor;

    @ManyToMany
    @JoinTable(
        name = "tbl_clase_alumno",
        joinColumns = @JoinColumn(name = "clase_id"),
        inverseJoinColumns = @JoinColumn(name = "alumno_id")
    )
    private List<AlumnoModel> alumnos;

    
}
