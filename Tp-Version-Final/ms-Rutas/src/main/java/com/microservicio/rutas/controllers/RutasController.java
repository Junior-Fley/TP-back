package com.microservicio.rutas.controllers;


import com.microservicio.rutas.dtos.GenerarRutasTentativasRequestDTO;
import com.microservicio.rutas.dtos.RutasTentativasResponseDTO;
import com.microservicio.rutas.dtos.RutaConTramosDTO;
import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.services.RutasService;
import com.microservicio.rutas.services.RutasTentativasService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
@Slf4j
public class RutasController {

    private final RutasService service;
    private final RutasTentativasService rutasTentativasService; // servicio para generar tentativas

    @Operation(summary = "Obtener todas las rutas", description = "Obtiene una lista de todas las rutas disponibles.")
    @GetMapping
    public ResponseEntity<List<Rutas>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @Operation(summary = "Obtener una ruta por ID", description = "Obtiene los detalles de una ruta específica utilizando su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Rutas> obtenerPorId(@PathVariable("id") Long id) {
        Rutas ruta = service.obtenerPorId(id);
        if (ruta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ruta);
    }

    @Operation(summary = "Obtener tramos por ruta", description = "Obtiene todos los tramos asociados a una ruta específica utilizando su ID.")
    @GetMapping("/{id}/tramos")
    public ResponseEntity<?> obtenerTramosPorRuta(@PathVariable("id") Long idRuta) {
        try {
            List<com.microservicio.rutas.models.Tramo> tramos = service.obtenerTramosPorRuta(idRuta);
            return ResponseEntity.ok(tramos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Crear ruta desde tentativa", description = "Crea una nueva ruta basada en una ruta tentativa proporcionada.")
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

    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')") // Comentado por ahora (sin autenticación)
    @Operation(summary = "Generar rutas tentativas", description = "Genera hasta 3 rutas tentativas entre un punto de origen y un punto de destino dados.")
    @PostMapping("/tentativas")
    public ResponseEntity<?> generarRutasTentativas(@RequestBody GenerarRutasTentativasRequestDTO request) {
        try {
            log.info("📦 Solicitud de rutas tentativas recibida: Origen ({}, {}), Destino ({}, {})",
                    request.getLatitudOrigen(), request.getLongitudOrigen(),
                    request.getLatitudDestino(), request.getLongitudDestino());

            // Validar datos de entrada
            if (request.getLatitudOrigen() == null || request.getLongitudOrigen() == null ||
                request.getLatitudDestino() == null || request.getLongitudDestino() == null) {
                return ResponseEntity.badRequest()
                        .body("Error: Todas las coordenadas son obligatorias (latitudOrigen, longitudOrigen, latitudDestino, longitudDestino)");
            }

            // Validar rangos de coordenadas
            if (!coordenadasValidas(request)) {
                return ResponseEntity.badRequest()
                        .body("Error: Coordenadas inválidas. Latitud debe estar entre -90 y 90, longitud entre -180 y 180");
            }

            // Generar las 3 rutas tentativas
            RutasTentativasResponseDTO response = rutasTentativasService.generarRutasTentativas(request);

            log.info("✅ Rutas tentativas generadas exitosamente");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("❌ Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());

        } catch (Exception e) {
            log.error("❌ Error al generar rutas tentativas: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error interno al generar rutas tentativas: " + e.getMessage());
        }
    }

    private boolean coordenadasValidas(GenerarRutasTentativasRequestDTO request) {
        return request.getLatitudOrigen() >= -90 && request.getLatitudOrigen() <= 90 &&
               request.getLatitudDestino() >= -90 && request.getLatitudDestino() <= 90 &&
               request.getLongitudOrigen() >= -180 && request.getLongitudOrigen() <= 180 &&
               request.getLongitudDestino() >= -180 && request.getLongitudDestino() <= 180;
    }

    @Operation(summary = "Eliminar una ruta", description = "Elimina una ruta específica utilizando su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //    // POST /api/rutas/correcion-tramos - Corregir relación de tramos
//    @PostMapping("/correcion-tramos")
//    public ResponseEntity<String> corregirRelacionTramos() {
//        try {
//            String resultado = service.corregirRelacionTramos();
//            return ResponseEntity.ok(resultado);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

    //    // GET /api/rutas/{id}/resumen - Obtener resumen de una ruta
//    @GetMapping("/{id}/resumen")
//    public ResponseEntity<RutaResumenDTO> obtenerResumen(@PathVariable("id") Long id) {
//        RutaResumenDTO resumen = service.obtenerResumen(id);
//        if (resumen == null) return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(resumen);
//    }

    // GET /api/rutas/{id}/tentativa - Obtener ruta tentativa específica por ID
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/{id}/tentativa")
//    public ResponseEntity<RutaConTramosDTO> obtenerRutaTentativaPorId(@PathVariable("id") Long id) {
//        RutaConTramosDTO ruta = service.obtenerRutaTentativaPorId(id);
//        if (ruta == null) return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(ruta);
//    }

    // POST /api/rutas - Crear una nueva ruta
//    @PostMapping
//    public ResponseEntity<Rutas> crear(@RequestBody Rutas ruta) {
//        Rutas nueva = service.crear(ruta);
//        return ResponseEntity.status(201).body(nueva);
//    }

    // GET /api/rutas/tentativas - Consultar rutas tentativas
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/tentativas")
//    public ResponseEntity<List<RutaConTramosDTO>> obtenerRutasTentativas() {
//        List<RutaConTramosDTO> rutasTentativas = service.obtenerRutasTentativas();
//        return ResponseEntity.ok(rutasTentativas);
//    }
}
