package com.rollerspeed.rollerspeed.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rollerspeed.rollerspeed.Model.ClaseModel;
import com.rollerspeed.rollerspeed.Model.InstructorModel;

@Repository
public interface ClaseRepository extends JpaRepository<ClaseModel, Long>{    
    long count();
    @Query("SELECT c FROM ClaseModel c JOIN c.alumnos a WHERE a.id = :alumnoId")
    List<ClaseModel> findByAlumnoId(Long alumnoId);

    List<ClaseModel> findByInstructorId(Long instructorId);
    Object findByInstructor(InstructorModel instructor);
}
