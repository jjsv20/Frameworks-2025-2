package com.rollerspeed.rollerspeed.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/admin")
public class AdminController {
    @GetMapping
    public String dashboard() {
        return "dashboard/admin"; // template dashboard/admin.html
    }
}
