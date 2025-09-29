package com.rollerspeed.rollerspeed.controllers;

import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        Optional<UserModel> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Siempre guardar UserModel en sesión
                session.setAttribute("usuario", user);

                // Redirigir según rol
                return switch (user.getRol()) {
                    case ADMIN -> "redirect:/admin/dashboard";
                    case INSTRUCTOR -> "redirect:/instructores/dashboard";
                    case STUDENT -> "redirect:/alumnos/dashboard";
                    case PUBLIC -> "redirect:/";
                };
            } else {
                model.addAttribute("error", "Contraseña incorrecta");
            }
        } else {
            model.addAttribute("error", "Usuario no encontrado");
        }

        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login?logout=true";
    }
}