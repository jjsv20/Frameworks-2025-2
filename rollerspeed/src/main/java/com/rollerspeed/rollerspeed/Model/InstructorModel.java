package com.rollerspeed.rollerspeed.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_instructores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String especialidad;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(length = 15)
    private String telefono;

    // Relación One-to-One con UserModel
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;
}
