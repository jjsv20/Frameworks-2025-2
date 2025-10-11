package com.rollerspeed.rollerspeed.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Model.PagosModel;
import com.rollerspeed.rollerspeed.Model.PagosModel.EstadoPago;

@Repository
public interface PagosRepository extends JpaRepository<PagosModel, Long>{
    List<PagosModel> findByAlumno(AlumnoModel alumno);

    List<PagosModel> findByAlumnoId(Long id);

    List<AlumnoModel> findByAlumnoAndEstado(AlumnoModel alumno, EstadoPago pendiente);
 long countByEstado(PagosModel.EstadoPago estado);
}
