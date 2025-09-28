package com.rollerspeed.rollerspeed.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UserController {

    // ---------------- HOME ----------------
    @GetMapping({"", "home"}) // soporta "/" y "/home"
    public String home() {
        return "index"; // index.html en templates
    }

    // ---------------- CORPORATIVO ----------------

    @GetMapping("corporativo/nosotros")
    public String mision() {
        return "corporativo/nosotros"; // mision.html en templates/corporativo
    }

    @GetMapping("corporativo/servicios")
    public String servicios() {
        return "corporativo/servicios"; // servicios.html en templates/corporativo
    }

    @GetMapping("corporativo/eventos")
    public String eventos() {
        return "corporativo/eventos"; // eventos.html en templates/corporativo
    }

    @GetMapping("corporativo/galeria")
    public String galeria() {
        return "corporativo/galeria"; // eventos.html en templates/corporativo
    }

    @GetMapping("corporativo/blog")
    public String blog() {
        return "corporativo/blog"; // eventos.html en templates/corporativo
    }

}
