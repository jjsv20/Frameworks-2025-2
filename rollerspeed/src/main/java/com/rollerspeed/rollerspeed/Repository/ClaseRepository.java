package com.rollerspeed.rollerspeed.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rollerspeed.rollerspeed.Model.ClaseModel;

public interface ClaseRepository extends JpaRepository<ClaseModel, Long>{

    List<ClaseModel> findByInstructorId(Long id);
    
}
