package com.rollerspeed.rollerspeed.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Model.UserModel;

@Repository
public interface AlumnoRepository extends JpaRepository<AlumnoModel, Long>{
    //Optional<AlumnoModel> findByEmail(String email);
    Optional<AlumnoModel> findByUserId(Long userId);
    long count();
    Optional<UserModel> findByEmail(String email);
    Optional<AlumnoModel> findByUser(UserModel user);
}
