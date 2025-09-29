package com.rollerspeed.rollerspeed.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rollerspeed.rollerspeed.Model.InstructorModel;
import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.InstructorRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/instructores")
public class InstructorController {

    @Autowired
    private InstructorRepository instructorRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.INSTRUCTOR)) {
            return "redirect:/auth/login";
        }

        // Buscar el perfil de alumno
        Optional<InstructorModel> instructorOpt = instructorRepository.findByUserId(usuario.getId());
        if (instructorOpt.isPresent()) {
            model.addAttribute("instructor", instructorOpt.get());
            model.addAttribute("usuario", usuario);
            return "instructores/dashboard";
        } else {
            // Si no existe perfil de alumno, redirigir a crearlo
            return "redirect:/auth/login";
        }
    }

    // Editar perfil del instructor
    @GetMapping("/editar")
    public String editarPerfil(HttpSession session, Model model) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.INSTRUCTOR)) {
            return "redirect:/auth/login";
        }

        Optional<InstructorModel> instructorOpt = instructorRepository.findByUserId(usuario.getId());
        if (instructorOpt.isPresent()) {
            model.addAttribute("instructor", instructorOpt.get());
            model.addAttribute("usuario", usuario);
            return "instructores/editar";
        } else {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/editar")
    public String guardarPerfil(@ModelAttribute InstructorModel instructorEditado, HttpSession session) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.INSTRUCTOR)) {
            return "redirect:/auth/login";
        }

        Optional<InstructorModel> instructorOpt = instructorRepository.findByUserId(usuario.getId());
        if (instructorOpt.isPresent()) {
            InstructorModel instructor = instructorOpt.get();
            instructor.setNombre(instructorEditado.getNombre());
            instructor.setEspecialidad(instructorEditado.getEspecialidad());
            instructor.setCorreo(instructorEditado.getCorreo());
            instructor.setTelefono(instructorEditado.getTelefono());

            instructorRepository.save(instructor);
            return "redirect:/instructores/dashboard?updated=true";
        } else {
            return "redirect:/auth/login";
        }
    }

}
