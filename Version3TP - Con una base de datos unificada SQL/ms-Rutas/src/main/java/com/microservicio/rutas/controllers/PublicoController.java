package com.microservicio.rutas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de ejemplo con endpoints públicos y protegidos
 * Demuestra la configuración de seguridad con Keycloak
 */
@RestController
@RequestMapping("/publico")
public class PublicoController {

    /**
     * Endpoint público - no requiere autenticación
     * Accesible sin token JWT
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ms-rutas");
        response.put("message", "Endpoint público - accesible sin autenticación");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público con información del sistema
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> response = new HashMap<>();
        response.put("service", "ms-rutas");
        response.put("version", "1.0.0");
        response.put("description", "Microservicio de gestión de rutas y tramos");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint protegido - requiere autenticación (cualquier rol)
     * Muestra información del usuario autenticado
     */
    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("authenticated", authentication.isAuthenticated());

        return ResponseEntity.ok(response);
    }
}

