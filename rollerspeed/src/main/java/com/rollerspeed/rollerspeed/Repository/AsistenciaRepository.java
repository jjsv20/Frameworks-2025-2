package com.rollerspeed.rollerspeed.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Model.AsistenciaModel;
import com.rollerspeed.rollerspeed.Model.ClaseModel;

@Repository
public interface AsistenciaRepository extends JpaRepository<AsistenciaModel,Long>{
     List<AsistenciaModel> findByClase(ClaseModel clase);
     boolean existsByAlumnoAndClase(AlumnoModel alumno, ClaseModel clase);

}
