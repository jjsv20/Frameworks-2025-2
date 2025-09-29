package com.rollerspeed.rollerspeed.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rollerspeed.rollerspeed.Model.InstructorModel;

@Repository
public interface InstructorRepository extends JpaRepository<InstructorModel, Long> {
    Optional<InstructorModel> findByUserId(Long userId);

    Optional<InstructorModel> findByCorreo(String correo);

    long count();

}