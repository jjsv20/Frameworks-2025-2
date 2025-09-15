package com.rollerspeed.rollerspeed.controllers;

import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Mostrar login
    @GetMapping("/login")
    public String login() {
        return "auth/login"; // template login.html
    }

    // Procesar login
    @PostMapping("/login")
    public String loginPost(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        Optional<UserModel> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("usuario", user);

                // Redirigir según rol
                switch (user.getRol()) {
                    case ADMIN -> { return "redirect:/dashboard/admin"; }
                    case INSTRUCTOR -> { return "redirect:/dashboard/instructor"; }
                    case STUDENT -> { return "redirect:/dashboard/estudiante"; }
                    case PUBLIC -> { return "redirect:/"; }
                }
            } else {
                model.addAttribute("error", "Contraseña incorrecta");
            }
        } else {
            model.addAttribute("error", "Usuario no encontrado");
        }

        return "auth/login";
    }

    // Mostrar registro
    @GetMapping("/register")
    public String register() {
        return "auth/register"; // template register.html
    }

    // Procesar registro
    @PostMapping("/register")
    public String registerPost(@RequestParam String nombre,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(defaultValue = "STUDENT") String rol,
                               Model model) {

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email ya registrado");
            return "register";
        }

        UserModel user = new UserModel();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setPassword(password);
        user.setRol(UserModel.Role.valueOf(rol.toUpperCase()));

        userRepository.save(user);

        return "redirect:/auth/login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
