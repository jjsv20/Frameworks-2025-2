package com.rollerspeed.rollerspeed.controllers;

import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Model.ClaseModel;
import com.rollerspeed.rollerspeed.Model.PagosModel;
import com.rollerspeed.rollerspeed.Repository.AlumnoRepository;
import com.rollerspeed.rollerspeed.Repository.ClaseRepository;
import com.rollerspeed.rollerspeed.Repository.PagosRepository;
import com.rollerspeed.rollerspeed.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private PagosRepository pagosRepository;

    @Autowired
    private ClaseRepository claseRepository;

     @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        // Buscar el perfil del alumno
        Optional<AlumnoModel> alumnoOpt = alumnoRepository.findByUserId(usuario.getId());
        if (alumnoOpt.isPresent()) {
            model.addAttribute("alumno", alumnoOpt.get());
            model.addAttribute("usuario", usuario);
            return "alumnos/dashboard";
        } else {
            return "redirect:/aspirantes/register";
        }
    }

    @GetMapping("/editar")
    public String editarPerfil(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

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
    public String guardarPerfil(@ModelAttribute AlumnoModel alumnoEditado, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

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
    public String crearPerfil(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("alumno", new AlumnoModel());
        model.addAttribute("usuario", usuario);
        return "redirect:/aspirantes/register";
    }

    @PostMapping("/login")
    public String guardarNuevoPerfil(@ModelAttribute AlumnoModel alumno, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        alumno.setUser(usuario);
        alumno.setEstadoPago(AlumnoModel.EstadoPago.PENDIENTE);
        alumnoRepository.save(alumno);

        return "redirect:/alumnos/dashboard";
    }

    @GetMapping("/pagos")
    public String verPagosAlumno(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        AlumnoModel alumno = alumnoRepository.findByUserId(usuario.getId()).orElse(null);
        if (alumno == null) {
            return "redirect:/aspirantes/register";
        }

        List<PagosModel> pagos = pagosRepository.findByAlumnoId(alumno.getId());
        model.addAttribute("pagos", pagos);
        model.addAttribute("alumno", alumno);
        model.addAttribute("usuario", usuario);
        return "alumnos/pagos"; // → vista para el estudiante
    }
    
    @GetMapping("/clases")
    public String verClasesAsignadas(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

        if (usuario == null || !usuario.getRol().equals(UserModel.Role.STUDENT)) {
            return "redirect:/auth/login";
        }

        AlumnoModel alumno = alumnoRepository.findByUserId(usuario.getId()).orElse(null);
        if (alumno == null) {
            return "redirect:/aspirantes/register";
        }

        List<ClaseModel> clases = claseRepository.findByAlumnoId(alumno.getId());
        model.addAttribute("clases", clases);
        model.addAttribute("alumno", alumno);
        model.addAttribute("usuario", usuario);

        return "alumnos/clases";
    }
    
}