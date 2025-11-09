package com.ms.transportes.controlles;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de ejemplo para TRANSPORTISTAS
 * Demuestra endpoints específicos para gestionar tramos
 */
@RestController
@RequestMapping("/api/tramos")
public class TramoEjemploController {

    /**
     * Endpoint para que TRANSPORTISTAS vean sus tramos asignados
     * Requiere ROLE_TRANSPORTISTA
     */
    @GetMapping("/mis-tramos")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<Map<String, Object>> misTramos(Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", authentication.getName());
        response.put("rol", "TRANSPORTISTA");
        response.put("message", "Tramos asignados al transportista");
        response.put("tramos", "[]"); // Aquí iría la lógica real

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para que TRANSPORTISTAS registren el inicio de un tramo
     * Requiere ROLE_TRANSPORTISTA
     */
    @PostMapping("/{tramoId}/iniciar")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<Map<String, Object>> iniciarTramo(
            @PathVariable Long tramoId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        response.put("tramoId", tramoId);
        response.put("usuario", authentication.getName());
        response.put("accion", "Inicio de tramo");
        response.put("fechaInicio", LocalDateTime.now());
        response.put("message", "Tramo iniciado exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para que TRANSPORTISTAS registren el fin de un tramo
     * Requiere ROLE_TRANSPORTISTA
     */
    @PostMapping("/{tramoId}/finalizar")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<Map<String, Object>> finalizarTramo(
            @PathVariable Long tramoId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        response.put("tramoId", tramoId);
        response.put("usuario", authentication.getName());
        response.put("accion", "Finalización de tramo");
        response.put("fechaFin", LocalDateTime.now());
        response.put("message", "Tramo finalizado exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para que ADMINS gestionen todos los tramos
     * Requiere ROLE_ADMIN
     */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> todosLosTramos(Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", authentication.getName());
        response.put("rol", "ADMIN");
        response.put("message", "Todos los tramos del sistema");
        response.put("tramos", "[]"); // Aquí iría la lógica real

        return ResponseEntity.ok(response);
    }
}

