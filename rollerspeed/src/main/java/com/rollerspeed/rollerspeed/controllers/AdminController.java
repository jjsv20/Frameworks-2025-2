package com.rollerspeed.rollerspeed.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import com.rollerspeed.rollerspeed.Model.AsistenciaModel;
import com.rollerspeed.rollerspeed.Model.ClaseModel;
import com.rollerspeed.rollerspeed.Model.InstructorModel;
import com.rollerspeed.rollerspeed.Model.PagosModel;
import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.*;

import jakarta.transaction.Transactional;

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

    @Autowired
    private PagosRepository pagosRepository;

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

        if (usuario == null || !usuario.getRol().equals(UserModel.Role.ADMIN)) {
            return "redirect:/auth/login";
        }
        long totalAlumnos = alumnoRepository.count();
        long totalInstructores = instructorRepository.count();
        long totalClases = claseRepository.count();
        long totalPagosPendientes = pagosRepository.countByEstado(PagosModel.EstadoPago.PENDIENTE);
        
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("totalInstructores", totalInstructores);
        model.addAttribute("totalPagos", totalPagosPendientes);
        model.addAttribute("totalClases", totalClases);
        model.addAttribute("usuario", usuario);

        
        return "admin/dashboard";
    }



    // ====================== GESTIÓN DE ALUMNOS ======================
    @GetMapping("/gestionAlumnos")
    public String listarAlumnos(Model model) {
        model.addAttribute("alumnos", alumnoRepository.findAll());
        return "admin/gestionAlumnos/listarAlumnos";
    }

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
        userRepository.save(user);

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

        PagosModel pago = new PagosModel();
        pago.setAlumno(alumno);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodo(metodoPago);
        pago.setMonto(BigDecimal.valueOf(100000)); 
        pago.setEstado(PagosModel.EstadoPago.PENDIENTE);
        pago.setAlumno(alumno);
        pagosRepository.save(pago);

        return "redirect:/admin/gestionAlumnos?exito=alumno_creado";
    }

    // ====================== EDITAR ALUMNO ======================
    @GetMapping("/gestionAlumnos/editarAlumno/{id}")
    public String mostrarFormularioEditarAlumno(@PathVariable Long id, Model model) {
        AlumnoModel alumno = alumnoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
        model.addAttribute("alumno", alumno);
        model.addAttribute("niveles", List.of("INICIAL", "INTERMEDIO", "AVANZADO"));
        model.addAttribute("metodos", List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA"));

        return "admin/gestionAlumnos/editarAlumno";
    }


    @PostMapping("/gestionAlumnos/editarAlumno")
        public String editarAlumno(
        @RequestParam Long id,
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
            return "admin/gestionAlumnos/editarAlumno";
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

        boolean tienePagoPendiente = pagosRepository.findByAlumnoAndEstado(alumno, PagosModel.EstadoPago.PENDIENTE).size() > 0;

        if (!tienePagoPendiente) {
            PagosModel pago = new PagosModel();
            pago.setAlumno(alumno);
            pago.setFechaPago(LocalDateTime.now());
            pago.setMetodo(metodoPago);
            pago.setMonto(BigDecimal.valueOf(100000)); 
            pago.setEstado(PagosModel.EstadoPago.PENDIENTE);
            pagosRepository.save(pago);
        }
        return "redirect:/admin/gestionAlumnos?exito=alumno_editado";
    }

// ====================== ELIMINAR ALUMNO ======================
    @Transactional
    @GetMapping("/gestionAlumnos/eliminarAlumno/{id}")
    public String eliminarAlumno(@PathVariable Long id) {
        AlumnoModel alumno = alumnoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado con ID: " + id));
        UserModel user = alumno.getUser();

        List<PagosModel> pagosAlumno = pagosRepository.findByAlumno(alumno);
        if (!pagosAlumno.isEmpty()) {
            pagosRepository.deleteAll(pagosAlumno);
        }

    // Eliminar primero el usuario si existe
        if (user != null) {
            userRepository.delete(user);
        }
    

        alumnoRepository.delete(alumno);

        return "redirect:/admin/gestionAlumnos?exito=alumno_eliminado";
    }



    @GetMapping("/gestionPagos")
    public String listarPagos(Model model) {
        model.addAttribute("pagos", pagosRepository.findAll());
        return "admin/gestionPagos/listarPagos";
    }

    // Aprobar pago
    @GetMapping("/gestionPagos/aprobar/{id}")
    public String aprobarPago(@PathVariable Long id) {
        PagosModel pago = pagosRepository.findById(id).orElse(null);
        if (pago != null) {
            // Actualizar pago
            pago.setEstado(PagosModel.EstadoPago.APROBADO);
            pagosRepository.save(pago);
            pago.getAlumno().setEstadoPago(AlumnoModel.EstadoPago.APROBADO);
            alumnoRepository.save(pago.getAlumno());

        }
        return "redirect:/admin/gestionPagos?exito=pago_aprobado";
    }

    // Rechazar pago
    @GetMapping("/gestionPagos/rechazar/{id}")
    public String rechazarPago(@PathVariable Long id) {
        PagosModel pago = pagosRepository.findById(id).orElse(null);
        if (pago != null) {
            // Actualizar pago
            pago.setEstado(PagosModel.EstadoPago.RECHAZADO);
            pagosRepository.save(pago);
            pago.getAlumno().setEstadoPago(AlumnoModel.EstadoPago.RECHAZADO);
            alumnoRepository.save(pago.getAlumno());

        }
        return "redirect:/admin/gestionPagos?exito=pago_rechazado";
    }


// ====================== GESTIÓN DE INSTRUCTORES ======================


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

    @GetMapping("/gestionInstructores/editarInstructor/{id}")
    public String mostrarFormularioEditarInstructor(@PathVariable Long id, Model model) {
        InstructorModel instructor = instructorRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Instructor no encontrado"));
        model.addAttribute("instructor", instructor);
        model.addAttribute("especialidades", List.of("Patinaje Artistico", "Avanzado", "Recreativo"));

        return "admin/gestionInstructores/editarInstructor";
    }


    @PostMapping("/gestionInstructores/editarInstructor")
        public String editarInstructor(
        @RequestParam Long id,
        @RequestParam String nombre,
        @RequestParam String email,
        @RequestParam String telefono,
        @RequestParam String especialidad,
        Model model) {
            InstructorModel instructor = instructorRepository.findById(id)    
            .orElseThrow(() -> new IllegalArgumentException("Instructor no encontrado"));
            UserModel instor = instructor.getUser();
        // Validar email único (excepto el actual)
        if (!instor.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El correo ya está en uso");
            model.addAttribute("instructor", instructor);
            model.addAttribute("especialidades", List.of("Patinaje Artistico", "Avanzado", "Recreativo"));
            return "admin/gestionInstructores/editarInstructor";
        }
        // Actualizar UserModel
        instor.setNombre(nombre);
        instor.setEmail(email);
        userRepository.save(instor);
        // Actualizar AlumnoModel
        instructor.setNombre(nombre);
        instructor.setCorreo(email);
        instructor.setTelefono(telefono);
        instructor.setEspecialidad(especialidad);
        instructorRepository.save(instructor);
        return "redirect:/admin/gestionAlumnos?exito=instructor_editado";
    }

// ====================== ELIMINAR ALUMNO ======================
    @Transactional
    @GetMapping("/gestionInstructores/eliminarInstructor/{id}")
    public String eliminarInstructor(@PathVariable Long id) {
        InstructorModel instructor = instructorRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("instructor no encontrado con ID: " + id));
        UserModel instrctor = instructor.getUser();
    // Eliminar primero el usuario si existe
        if (instrctor != null) {
            userRepository.delete(instrctor);
        }
        instructorRepository.delete(instructor);
        return "redirect:/admin/gestionInstructores?exito=instructor_eliminado";
    }
    

 
    

    @GetMapping("/gestionClases")
    public String listarClases(Model model) {
        model.addAttribute("clases", claseRepository.findAll());
        model.addAttribute("instructores", instructorRepository.findAll());
        model.addAttribute("alumnos", alumnoRepository.findAll());
        return "admin/gestionClases/listarClases";
    }

    @GetMapping("/gestionClases/asignarInstructor/{claseId}/{instructorId}")
    public String asignarInstructor(@PathVariable Long claseId, @PathVariable Long instructorId) {
        ClaseModel clase = claseRepository.findById(claseId).orElse(null);
        InstructorModel instructor = instructorRepository.findById(instructorId).orElse(null);

        if(clase != null && instructor != null) {
            clase.setInstructor(instructor);
            claseRepository.save(clase);
        }

        return "redirect:/admin/gestionClases?exito=instructor_asignado";
    }

    @GetMapping("/gestionClases/agregarAlumno/{claseId}/{alumnoId}")
    public String agregarAlumno(@PathVariable Long claseId, @PathVariable Long alumnoId) {
        ClaseModel clase = claseRepository.findById(claseId).orElse(null);
        AlumnoModel alumno = alumnoRepository.findById(alumnoId).orElse(null);

        if (clase != null && alumno != null) {

        // Evita duplicados en la lista de alumnos
            if (!clase.getAlumnos().contains(alumno)) {
                clase.getAlumnos().add(alumno);
                claseRepository.save(clase);
            }

            boolean asistenciaExiste = asistenciaRepository.existsByAlumnoAndClase(alumno, clase);

            if (!asistenciaExiste) {
                AsistenciaModel asistencia = new AsistenciaModel();
                asistencia.setAlumno(alumno);
                asistencia.setClase(clase);
                asistencia.setFecha(LocalDate.now());
                asistencia.setEstado(AsistenciaModel.EstadoAsistencia.PENDIENTE);
                asistenciaRepository.save(asistencia);
            }
        }

        return "redirect:/admin/gestionClases?exito=alumno_agregado";
    }

    

    @GetMapping("/gestionClases/nuevaClase")
    public String mostrarFormularioNuevaClase(Model model) {
        model.addAttribute("clase", new ClaseModel());
        model.addAttribute("instructores", instructorRepository.findAll());
        return "admin/gestionClases/nuevaClase";
    }

    @PostMapping("/gestionClases/guardar")
    public String guardarNuevaClase(@ModelAttribute ClaseModel clase, @RequestParam Long instructorId) {
        InstructorModel instructor = instructorRepository.findById(instructorId).orElse(null);
        clase.setInstructor(instructor);
        claseRepository.save(clase);
        return "redirect:/admin/gestionClases?exito=clase_creada";
    }
}





