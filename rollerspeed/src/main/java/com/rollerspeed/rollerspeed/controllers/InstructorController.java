package com.rollerspeed.rollerspeed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rollerspeed.rollerspeed.Model.AsistenciaModel;
import com.rollerspeed.rollerspeed.Model.ClaseModel;
import com.rollerspeed.rollerspeed.Model.InstructorModel;
import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.AsistenciaRepository;
import com.rollerspeed.rollerspeed.Repository.ClaseRepository;
import com.rollerspeed.rollerspeed.Repository.InstructorRepository;
import com.rollerspeed.rollerspeed.Repository.UserRepository;



@Controller
@RequestMapping("/instructores")
public class InstructorController {

    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

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
            return "redirect:/auth/login";
        }
    }

    // Editar perfil del instructor
    @GetMapping("/editar")
    public String editarPerfil(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

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
    public String guardarPerfil(@ModelAttribute InstructorModel instructorEditado, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

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

    @GetMapping("/clases")
    public String verClasesAsignadas(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        UserModel usuario = userRepository.findByEmail(email).orElse(null);

        if (usuario == null || !usuario.getRol().equals(UserModel.Role.INSTRUCTOR)) {
            return "redirect:/auth/login";
        }

        InstructorModel instructor = instructorRepository.findByUserId(usuario.getId()).orElse(null);
        if (instructor == null) {
            return "redirect:/aspirantes/register";
        }

        List<ClaseModel> clases = claseRepository.findByInstructorId(instructor.getId());
        model.addAttribute("clases", clases);
        model.addAttribute("instructor", instructor);
        model.addAttribute("usuario", usuario);

        return "instructores/clases";
    }

    @GetMapping("/asistencia")
    public String listarAlumnos(Model model) {
        model.addAttribute("asistencias", asistenciaRepository.findAll());
        return "instructores/asistencia";
    }

    // Aprobar asistencia
    @GetMapping("/asistencia/presente/{id}")
    public String aprobarAsistencia(@PathVariable Long id) {
        AsistenciaModel asistencia = asistenciaRepository.findById(id).orElse(null);
        if (asistencia != null) {
            // Actualizar asistencia
            asistencia.setEstado(AsistenciaModel.EstadoAsistencia.PRESENTE);
            asistenciaRepository.save(asistencia);
        }
        return "redirect:/instructores/asistencia?exito=exito_presente";
    }

    // Rechazar asistencia
    @GetMapping("/asistencia/ausente/{id}")
    public String rechazarAsistencia(@PathVariable Long id) {
        AsistenciaModel asistencia = asistenciaRepository.findById(id).orElse(null);
        if (asistencia != null) {
            // Actualizar asistencia
            asistencia.setEstado(AsistenciaModel.EstadoAsistencia.AUSENTE);
            asistenciaRepository.save(asistencia);
        }
        return "redirect:/instructores/asistencia?exito=exito_ausente";
    }
}
