package com.rollerspeed.rollerspeed.controllers;

import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        // Buscar el perfil de alumno
        Optional<AlumnoModel> alumnoOpt = alumnoRepository.findByUserId(usuario.getId());
        if (alumnoOpt.isPresent()) {
            model.addAttribute("alumno", alumnoOpt.get());
            model.addAttribute("usuario", usuario);
            return "alumnos/dashboard";
        } else {
            // Si no existe perfil de alumno, redirigir a crearlo
            return "redirect:/aspirantes/register";
        }
    }

    @GetMapping("/editar")
    public String editarPerfil(HttpSession session, Model model) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        Optional<AlumnoModel> alumnoOpt = alumnoRepository.findByUserId(usuario.getId());
        if (alumnoOpt.isPresent()) {
            model.addAttribute("alumno", alumnoOpt.get());
            model.addAttribute("usuario", usuario);
            return "alumnos/editar";
        } else {
            return "redirect:/aspirantes/register";
        }
    }

    @PostMapping("/editar")
    public String guardarPerfil(@ModelAttribute AlumnoModel alumnoEditado, HttpSession session) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        Optional<AlumnoModel> alumnoOpt = alumnoRepository.findByUserId(usuario.getId());
        if (alumnoOpt.isPresent()) {
            AlumnoModel alumno = alumnoOpt.get();
            
            // Actualizar solo los campos editables
            alumno.setNombre(alumnoEditado.getNombre());
            alumno.setTelefono(alumnoEditado.getTelefono());
            alumno.setGenero(alumnoEditado.getGenero());
            alumno.setMetodoPago(alumnoEditado.getMetodoPago());
            alumno.setNivel(alumnoEditado.getNivel());

            alumnoRepository.save(alumno);
            return "redirect:/alumnos/dashboard?updated=true";
        } else {
             return "redirect:/aspirantes/register";
        }
    }

    @GetMapping("/register")
    public String crearPerfil(HttpSession session, Model model) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("alumno", new AlumnoModel());
        model.addAttribute("usuario", usuario);
         return "redirect:/aspirantes/register";
    }

    @PostMapping("/login")
    public String guardarNuevoPerfil(@ModelAttribute AlumnoModel alumno, HttpSession session) {
        UserModel usuario = (UserModel) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        alumno.setUser(usuario);
        alumno.setEstadoPago(AlumnoModel.EstadoPago.PENDIENTE);
        alumnoRepository.save(alumno);


        return "redirect:/alumnos/dashboard";
    }
}