package com.rollerspeed.rollerspeed.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Model.InstructorModel;
import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.AlumnoRepository;
import com.rollerspeed.rollerspeed.Repository.InstructorRepository;
import com.rollerspeed.rollerspeed.Repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
         long totalAlumnos = alumnoRepository.count();
        long totalInstructores = instructorRepository.count();
        
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("totalInstructores", totalInstructores);

        return "admin/dashboard"; // el HTML que me mostraste
    }


    @GetMapping("/gestionAlumnos")
    public String listarAlumnos(Model model) {
        model.addAttribute("alumnos", alumnoRepository.findAll());
        return "admin/gestionAlumnos/listarAlumnos";
    }


    //alumnos por admin
    @GetMapping("/gestionAlumnos/nuevoalumno")
    public String mostrarFormularioAlumno(Model model) {
        model.addAttribute("niveles", List.of("INICIAL", "INTERMEDIO", "AVANZADO"));
        model.addAttribute("metodos", List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA"));
        return "admin/gestionAlumnos/nuevoalumno";
    }

    @PostMapping("/gestionAlumnos/nuevoalumno")
    public String guardarAlumno(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fechaNacimiento, // como String "yyyy-MM-dd"
            @RequestParam String genero,
            @RequestParam String telefono,
            @RequestParam String nivel,
            @RequestParam String metodoPago,
            Model model) {

        // Validar email único en usuarios y alumnos
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El correo ya está en uso");
            model.addAttribute("niveles", List.of("INICIAL", "INTERMEDIO", "AVANZADO"));
            model.addAttribute("metodos", List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA"));
            return "admin/gestionAlumnos/nuevoalumno";
        }

        // Crear usuario
        UserModel user = new UserModel();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(UserModel.Role.STUDENT);
        user = userRepository.save(user);

        // Crear alumno
        AlumnoModel alumno = new AlumnoModel();
        alumno.setNombre(nombre);
        alumno.setEmail(email);
        alumno.setTelefono(telefono);
        alumno.setGenero(genero);
        alumno.setFechaNacimiento(fechaNacimiento); // ya es String
        alumno.setMetodoPago(metodoPago);
        alumno.setNivel(nivel);
        alumno.setEstadoPago(AlumnoModel.EstadoPago.PENDIENTE);
        alumno.setUser(user);
        alumnoRepository.save(alumno);

        return "redirect:/admin/gestionAlumnos?exito=alumno_creado";
    }

//**********************************************************FALTA REVISAR**************************************************** */
    @GetMapping("/gestionAlumnos/editar/{id}")
    public String mostrarFormularioEditarAlumno(@PathVariable Long id, Model model) {
        AlumnoModel alumno = alumnoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
        model.addAttribute("alumno", alumno);
        model.addAttribute("niveles", List.of("INICIAL", "INTERMEDIO", "AVANZADO"));
        model.addAttribute("metodos", List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA"));
        return "admin/gestionAlumnos/editarAlumnos";
    }

    @PostMapping("/gestionAlumnos/editar/{id}")
    public String editarAlumno(
        @PathVariable Long id,
        @RequestParam String nombre,
        @RequestParam String email,
        @RequestParam String fechaNacimiento,
        @RequestParam String genero,
        @RequestParam String telefono,
        @RequestParam String nivel,
        @RequestParam String metodoPago,
        Model model) {

        AlumnoModel alumno = alumnoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
        UserModel user = alumno.getUser();

    // Validar email único (excepto el actual)
        if (!user.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El correo ya está en uso");
            model.addAttribute("alumno", alumno);
            model.addAttribute("niveles", List.of("INICIAL", "INTERMEDIO", "AVANZADO"));
            model.addAttribute("metodos", List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA"));
            return "admin/gestionAlumnos/editarAlumnos";
        }

    // Actualizar UserModel
        user.setNombre(nombre);
        user.setEmail(email);
        userRepository.save(user);

    // Actualizar AlumnoModel
        alumno.setNombre(nombre);
        alumno.setEmail(email);
        alumno.setTelefono(telefono);
        alumno.setGenero(genero);
        alumno.setFechaNacimiento(fechaNacimiento);
        alumno.setMetodoPago(metodoPago);
        alumno.setNivel(nivel);
        alumnoRepository.save(alumno);

        return "redirect:/admin/gestionAlumnos?exito=alumno_editado";
}


    


    //FALTA POR IMPLEMENTAR____________

    @GetMapping("/gestionInstructores")
    public String listarInstructores(Model model) {
        model.addAttribute("instructores", instructorRepository.findAll());
        return "admin/gestionInstructores/listarInstructores";
    }

    @GetMapping("/gestionInstructores/nuevoInstructor")
    public String mostrarFormularioInstructor(Model model) {
        model.addAttribute("especialidades", List.of("Patinaje Artistico", "Avanzado", "Recreativo"));
        return "admin/gestionInstructores/nuevoInstructor";
    }

    @PostMapping("/gestionInstructores/nuevoInstructor")
    public String guardarInstructor(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String especialidad,
            @RequestParam(required = false) String telefono,
            Model model) {

        // Validar correo único
        if (userRepository.findByEmail(correo).isPresent() || 
            instructorRepository.findByCorreo(correo).isPresent()) {
            model.addAttribute("especialidades", List.of("Patinaje Artistico", "Avanzado", "Recreativo"));
            model.addAttribute("error", "El correo ya está registrado");
            return "admin/gestionInstructores/nuevoInstructor";
        }

        // Crear usuario
        UserModel user = new UserModel();
        user.setNombre(nombre);
        user.setEmail(correo);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(UserModel.Role.INSTRUCTOR);
        userRepository.save(user);

        // Crear instructor
        InstructorModel instructor = new InstructorModel();
        instructor.setNombre(nombre);
        instructor.setCorreo(correo);
        instructor.setEspecialidad(especialidad);
        instructor.setTelefono(telefono);
        instructor.setUser(user);
        instructorRepository.save(instructor);

        return "redirect:/admin/gestionInstructores?exito=instructor_creado";
    }

    
}