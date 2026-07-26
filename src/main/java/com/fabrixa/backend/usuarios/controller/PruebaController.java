package com.fabrixa.backend.usuarios.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PruebaController {

    @GetMapping("/api/prueba")
    public String prueba(Authentication auth) {
        return "Hola " + auth.getName() + ", tu rol es: " + auth.getAuthorities();
    }
}