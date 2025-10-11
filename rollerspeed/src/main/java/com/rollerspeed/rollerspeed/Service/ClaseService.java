package com.rollerspeed.rollerspeed.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Model.ClaseModel;
import com.rollerspeed.rollerspeed.Model.InstructorModel;
import com.rollerspeed.rollerspeed.Repository.AlumnoRepository;
import com.rollerspeed.rollerspeed.Repository.ClaseRepository;
import com.rollerspeed.rollerspeed.Repository.InstructorRepository;

@Service
public class ClaseService {
     @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<ClaseModel> listarClases() {
        return claseRepository.findAll();
    }

    public ClaseModel guardarClase(ClaseModel clase) {
        return claseRepository.save(clase);
    }

    public void asignarInstructor(Long claseId, Long instructorId) {
        ClaseModel clase = claseRepository.findById(claseId).orElseThrow();
        InstructorModel instructor = instructorRepository.findById(instructorId).orElseThrow();
        clase.setInstructor(instructor);
        claseRepository.save(clase);
    }

    public void agregarAlumno(Long claseId, Long alumnoId) {
        ClaseModel clase = claseRepository.findById(claseId).orElseThrow();
        AlumnoModel alumno = alumnoRepository.findById(alumnoId).orElseThrow();
        clase.getAlumnos().add(alumno);
        claseRepository.save(clase);
    }
}
