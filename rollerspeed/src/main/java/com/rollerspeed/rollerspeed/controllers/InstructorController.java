package com.rollerspeed.rollerspeed.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/instructor")
public class InstructorController {
    @GetMapping
    public String dashboard() {
        return "dashboard/instructor"; // template dashboard/admin.html
    }
}
