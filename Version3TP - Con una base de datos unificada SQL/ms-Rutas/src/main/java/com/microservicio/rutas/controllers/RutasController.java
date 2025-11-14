package com.microservicio.rutas.controllers;


import com.microservicio.rutas.dtos.RutaConTramosDTO;
import com.microservicio.rutas.dtos.RutaResumenDTO;
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
    public ResponseEntity<List<RutaConTramosDTO>> obtenerRutasTentativas() {
        List<RutaConTramosDTO> rutasTentativas = service.obtenerRutasTentativas();
        return ResponseEntity.ok(rutasTentativas);
    }

    // 🔹 Obtener una ruta tentativa específica por ID con todos sus tramos
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/tentativa")
    public ResponseEntity<RutaConTramosDTO> obtenerRutaTentativaPorId(@PathVariable("id") Long id) {
        RutaConTramosDTO ruta = service.obtenerRutaTentativaPorId(id);
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
     * ⭐ NUEVO: Crea una ruta definitiva a partir de una ruta tentativa seleccionada
     *
     * POST /api/rutas/crear-desde-tentativa
     *
     * Body ejemplo:
     * {
     *   "idSolicitud": 1,
     *   "tipoRuta": "CON_1_DEPOSITO",
     *   "latitudOrigen": -31.4201,
     *   "longitudOrigen": -64.1888,
     *   "latitudDestino": -34.6037,
     *   "longitudDestino": -58.3816,
     *   "tramos": [ ... ] // Array de tramos tal como viene de /api/rutas/tentativas
     * }
     */
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')") // ⚠️ COMENTADO - Sin autenticación
    @PostMapping("/crear-desde-tentativa")
    public ResponseEntity<?> crearRutaDesdeTentativa(
            @RequestBody com.microservicio.rutas.dtos.CrearRutaDesdeTeantativaDTO dto) {
        try {
            Rutas rutaCreada = service.crearRutaDesdeTentativa(dto);
            return ResponseEntity.status(201).body(rutaCreada);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al crear ruta: " + e.getMessage());
        }
    }
}
