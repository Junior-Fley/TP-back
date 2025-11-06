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

    // 🔹 Obtener resumen de una ruta (para comunicación entre microservicios)
    @GetMapping("/{id}/resumen")
    public ResponseEntity<RutaResumenDTO> obtenerResumen(@PathVariable("id") Long id) {
        RutaResumenDTO resumen = service.obtenerResumen(id);
        if (resumen == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resumen);
    }

}
