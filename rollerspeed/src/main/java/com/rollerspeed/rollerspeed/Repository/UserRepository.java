package com.rollerspeed.rollerspeed.Repository;

import org.springframework.stereotype.Repository;
import com.rollerspeed.rollerspeed.Model.UserModel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);    
}
