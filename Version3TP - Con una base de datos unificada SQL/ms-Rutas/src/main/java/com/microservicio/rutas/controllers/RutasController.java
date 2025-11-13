package com.microservicio.rutas.controllers;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.dtos.RutaTentativaDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.services.RutasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    // 🔹 Requerimiento Funcional #3: Consultar rutas tentativas con todos los tramos
    // sugeridos y el tiempo y costo estimados (Operador / Administrador)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tentativas")
    public ResponseEntity<List<RutaTentativaDTO>> obtenerRutasTentativas() {
        List<RutaTentativaDTO> rutasTentativas = service.obtenerRutasTentativas();
        return ResponseEntity.ok(rutasTentativas);
    }

    // 🔹 Obtener una ruta tentativa específica por ID con todos sus tramos
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/tentativa")
    public ResponseEntity<RutaTentativaDTO> obtenerRutaTentativaPorId(@PathVariable("id") Long id) {
        RutaTentativaDTO ruta = service.obtenerRutaTentativaPorId(id);
        if (ruta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ruta);
    }

    /**
     * 📦 Obtener todos los tramos de una ruta específica
     * GET /api/rutas/{idRuta}/tramos
     *
     * Necesario para calcular el costo final de una solicitud
     */
    @GetMapping("/{idRuta}/tramos")
    public ResponseEntity<?> obtenerTramosPorRuta(@PathVariable("idRuta") Long idRuta) {
        try {
            List<com.microservicio.rutas.models.Tramo> tramos = service.obtenerTramosPorRuta(idRuta);
            return ResponseEntity.ok(tramos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    /**
     * 🛠️ UTILIDAD: Corrige la relación bidireccional de tramos huérfanos
     * POST /api/rutas/corregir-tramos
     *
     * Este endpoint corrige los tramos que no tienen id_ruta asignado en la base de datos
     */
    @PostMapping("/corregir-tramos")
    public ResponseEntity<String> corregirRelacionTramos() {
        try {
            String resultado = service.corregirRelacionTramos();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * 🛠️ UTILIDAD: Asigna manualmente un tramo a una ruta
     * PUT /api/rutas/{idRuta}/asignar-tramo/{idTramo}
     */
    @PutMapping("/{idRuta}/asignar-tramo/{idTramo}")
    public ResponseEntity<String> asignarTramoARuta(
            @PathVariable("idRuta") Long idRuta,
            @PathVariable("idTramo") Long idTramo) {
        try {
            String resultado = service.asignarTramoARuta(idRuta, idTramo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * 🛠️ DEBUG: Lista todos los tramos y su estado de asignación a rutas
     * GET /api/rutas/debug/tramos
     */
    @GetMapping("/debug/tramos")
    public ResponseEntity<?> listarTodosLosTramosConEstado() {
        try {
            return ResponseEntity.ok(service.listarTodosLosTramosConEstado());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * 🛠️ UTILIDAD: Recalcula el campo cantidadTramos de todas las rutas
     * POST /api/rutas/recalcular-cantidad-tramos
     */
    @PostMapping("/recalcular-cantidad-tramos")
    public ResponseEntity<String> recalcularCantidadTramos() {
        try {
            String resultado = service.recalcularCantidadTramos();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}
