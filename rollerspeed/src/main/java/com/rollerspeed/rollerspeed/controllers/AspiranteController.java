package com.rollerspeed.rollerspeed.controllers;

import com.rollerspeed.rollerspeed.Model.AlumnoModel;
import com.rollerspeed.rollerspeed.Model.PagosModel;
import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.AlumnoRepository;
import com.rollerspeed.rollerspeed.Repository.PagosRepository;
import com.rollerspeed.rollerspeed.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/aspirantes")
public class AspiranteController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PagosRepository pagosRepository;

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("niveles", List.of("INICIAL", "INTERMEDIO", "AVANZADO"));
        model.addAttribute("metodos", List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA"));
        return "aspirantes/register";
    }

    @PostMapping("/register")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento, // formato yyyy-MM-dd 
            @RequestParam String genero,
            @RequestParam String documento,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String nivel,
            @RequestParam String metodoPago,
            Model model
    ) {
        // 1) Validar email único
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado");
            model.addAttribute("niveles", List.of("INICIAL", "INTERMEDIO", "AVANZADO"));
            model.addAttribute("metodos", List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA"));
            return "aspirantes/register";
        }

        // 2) Crear UserModel con rol STUDENT
        UserModel user = new UserModel();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(UserModel.Role.STUDENT);
        user = userRepository.save(user);

        // 3) Crear AspiranteModel y asociar al user creado
        AlumnoModel alumno = new AlumnoModel();
        alumno.setNombre(nombre);
        alumno.setEmail(email);
        alumno.setTelefono(telefono);
        alumno.setGenero(genero);
        alumno.setFechaNacimiento(fechaNacimiento.toString());
        alumno.setMetodoPago(metodoPago);
        alumno.setNivel(nivel);
        alumno.setEstadoPago(AlumnoModel.EstadoPago.PENDIENTE);
        alumno.setUser(user);
        alumnoRepository.save(alumno);
        alumno = alumnoRepository.save(alumno);

        PagosModel pago = new PagosModel();
        pago.setAlumno(alumno);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodo(metodoPago);
        pago.setMonto(BigDecimal.valueOf(100000)); 
        pago.setEstado(PagosModel.EstadoPago.PENDIENTE);
        pago.setAlumno(alumno);
        pagosRepository.save(pago);

        // 4) Redirigir al login con mensaje (puede usar RedirectAttributes para flash)
        return "redirect:/auth/login?registered=true";
    }
}
