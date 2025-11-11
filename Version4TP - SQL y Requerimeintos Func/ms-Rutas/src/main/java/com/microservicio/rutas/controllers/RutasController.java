package com.microservicio.rutas.controllers;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.services.RutasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutasController {

    private final RutasService service;

    // 🔹 Obtener todas las rutas
    @GetMapping
    public ResponseEntity<List<Rutas>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // 🔹 Obtener una ruta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Rutas> obtenerPorId(@PathVariable("id") Long id) {
        Rutas ruta = service.obtenerPorId(id);
        if (ruta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ruta);
    }

    // 🔹 Crear una nueva ruta
    @PostMapping
    public ResponseEntity<Rutas> crear(@RequestBody Rutas ruta) {
        Rutas nueva = service.crear(ruta);
        return ResponseEntity.status(201).body(nueva);
    }

    // 🔹 Eliminar una ruta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 🔹 Obtener todas las rutas tentativas con tramos, tiempo y costo estimados
     * Endpoint para operadores/administradores
     * GET /api/rutas/tentativas/resumen
     */
    @GetMapping("/tentativas/resumen")
    public ResponseEntity<List<RutaResumenDTO>> obtenerRutasTentativas() {
        try {
            System.out.println("=== Obteniendo rutas tentativas desde ms-rutas ===");
            List<RutaResumenDTO> rutas = service.obtenerRutasTentativas();
            System.out.println("=== " + (rutas != null ? rutas.size() : 0) + " rutas procesadas ===");
            return ResponseEntity.ok(rutas);
        } catch (Exception e) {
            System.err.println("=== ERROR al obtener rutas tentativas ===");
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
