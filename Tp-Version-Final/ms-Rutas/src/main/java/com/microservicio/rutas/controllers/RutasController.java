package com.microservicio.rutas.controllers;


import com.microservicio.rutas.dtos.RutaConTramosDTO;
import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.services.RutasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutasController {

    private final RutasService service;

    // GET /api/rutas - Obtener todas las rutas
    @GetMapping
    public ResponseEntity<List<Rutas>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // GET /api/rutas/tentativas - Consultar rutas tentativas
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tentativas")
    public ResponseEntity<List<RutaConTramosDTO>> obtenerRutasTentativas() {
        List<RutaConTramosDTO> rutasTentativas = service.obtenerRutasTentativas();
        return ResponseEntity.ok(rutasTentativas);
    }

    // GET /api/rutas/{id} - Obtener una ruta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Rutas> obtenerPorId(@PathVariable("id") Long id) {
        Rutas ruta = service.obtenerPorId(id);
        if (ruta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ruta);
    }

    // GET /api/rutas/{id}/resumen - Obtener resumen de una ruta
    @GetMapping("/{id}/resumen")
    public ResponseEntity<RutaResumenDTO> obtenerResumen(@PathVariable("id") Long id) {
        RutaResumenDTO resumen = service.obtenerResumen(id);
        if (resumen == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resumen);
    }

    // GET /api/rutas/{id}/tentativa - Obtener ruta tentativa específica por ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/tentativa")
    public ResponseEntity<RutaConTramosDTO> obtenerRutaTentativaPorId(@PathVariable("id") Long id) {
        RutaConTramosDTO ruta = service.obtenerRutaTentativaPorId(id);
        if (ruta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ruta);
    }

    // GET /api/rutas/{idRuta}/tramos - Obtener todos los tramos de una ruta
    @GetMapping("/{idRuta}/tramos")
    public ResponseEntity<?> obtenerTramosPorRuta(@PathVariable("idRuta") Long idRuta) {
        try {
            List<com.microservicio.rutas.models.Tramo> tramos = service.obtenerTramosPorRuta(idRuta);
            return ResponseEntity.ok(tramos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    // POST /api/rutas - Crear una nueva ruta
    @PostMapping
    public ResponseEntity<Rutas> crear(@RequestBody Rutas ruta) {
        Rutas nueva = service.crear(ruta);
        return ResponseEntity.status(201).body(nueva);
    }

    // POST /api/rutas/creacion-desde-tentativa - Crear ruta desde tentativa
    @PostMapping("/creacion-desde-tentativa")
    public ResponseEntity<?> crearRutaDesdeTentativa(
            @RequestBody com.microservicio.rutas.dtos.CrearRutaDesdeTeantativaDTO dto) {
        try {
            Rutas rutaCreada = service.crearRutaDesdeTentativa(dto);
            return ResponseEntity.status(201).body(rutaCreada);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al crear ruta: " + e.getMessage());
        }
    }

    // POST /api/rutas/correcion-tramos - Corregir relación de tramos
    @PostMapping("/correcion-tramos")
    public ResponseEntity<String> corregirRelacionTramos() {
        try {
            String resultado = service.corregirRelacionTramos();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // DELETE /api/rutas/{id} - Eliminar una ruta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
