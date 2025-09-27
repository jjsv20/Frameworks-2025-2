package com.rollerspeed.rollerspeed.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Repository.AlumnoRepository;

@Service
public class AlumnoService {
    @Autowired
    private AlumnoRepository alumnoRepository;

    public AlumnoModel save(AlumnoModel alumno) {
        return alumnoRepository.save(alumno);
    }

    public List<AlumnoModel> getAll() {
        return alumnoRepository.findAll();
    }

    public AlumnoModel getById(Long id) {
        return alumnoRepository.findById(id).orElse(null);
    }
}
