package com.rollerspeed.rollerspeed.controllers;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Repository.AlumnoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoRestController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Operation(summary = "Listar todos los alumnos")
    @GetMapping
    public List<AlumnoModel> listarAlumnos() {
        return alumnoRepository.findAll();
    }

    @Operation(summary = "Obtener un alumno por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alumno encontrado"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    @GetMapping("/{id}")
    public AlumnoModel obtenerAlumno(@PathVariable Long id) {
        return alumnoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + id));
    }

    @Operation(summary = "Crear un nuevo alumno")
    @PostMapping
    public AlumnoModel crearAlumno(@RequestBody AlumnoModel alumno) {
        alumno.setEstadoPago(AlumnoModel.EstadoPago.PENDIENTE); // default
        return alumnoRepository.save(alumno);
    }

    @Operation(summary = "Actualizar alumno existente")
    @PutMapping("/{id}")
    public AlumnoModel actualizarAlumno(@PathVariable Long id, @RequestBody AlumnoModel datos) {
        AlumnoModel alumno = alumnoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + id));

        alumno.setNombre(datos.getNombre());
        alumno.setTelefono(datos.getTelefono());
        alumno.setGenero(datos.getGenero());
        alumno.setNivel(datos.getNivel());
        alumno.setMetodoPago(datos.getMetodoPago());
        alumno.setEstadoPago(datos.getEstadoPago());

        return alumnoRepository.save(alumno);
    }

    @Operation(summary = "Eliminar un alumno")
    @DeleteMapping("/{id}")
    public void eliminarAlumno(@PathVariable Long id) {
        alumnoRepository.deleteById(id);
    }
}
