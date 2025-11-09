package com.microservicio.solicitudes.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de ejemplo protegido para CLIENTES
 * Demuestra endpoints específicos para el rol CLIENTE
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudEjemploController {

    /**
     * Endpoint para que CLIENTES creen solicitudes
     * Requiere ROLE_CLIENTE
     */
    @PostMapping("/crear")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, String>> crearSolicitud(
            @RequestBody Map<String, Object> solicitud,
            Authentication authentication) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "Solicitud creada exitosamente");
        response.put("usuario", authentication.getName());
        response.put("rol", "CLIENTE");
        response.put("accion", "Crear solicitud de transporte");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para que CLIENTES consulten sus solicitudes
     * Requiere ROLE_CLIENTE
     */
    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> misSolicitudes(Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", authentication.getName());
        response.put("message", "Listado de solicitudes del cliente");
        response.put("solicitudes", "[]"); // Aquí iría la lógica real

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para que ADMINS vean todas las solicitudes
     * Requiere ROLE_ADMIN
     */
    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> todasLasSolicitudes(Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", authentication.getName());
        response.put("rol", "ADMIN");
        response.put("message", "Todas las solicitudes del sistema");
        response.put("solicitudes", "[]"); // Aquí iría la lógica real

        return ResponseEntity.ok(response);
    }
}

