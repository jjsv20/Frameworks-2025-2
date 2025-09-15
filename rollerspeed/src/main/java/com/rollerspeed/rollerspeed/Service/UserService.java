package com.rollerspeed.rollerspeed.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserModel> Login(String email, String password) {
        Optional<UserModel> usuario = userRepository.findByEmail(email);
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return usuario;
        }
        return Optional.empty();
    }

    // Registrar usuario
    public UserModel registrar(UserModel usuario) {
        return userRepository.save(usuario);
    }

}
