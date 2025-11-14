package com.microservicio.rutas.controllers;

import com.microservicio.rutas.dtos.GenerarRutasTentativasRequestDTO;
import com.microservicio.rutas.dtos.RutasTentativasResponseDTO;
import com.microservicio.rutas.services.RutasTentativasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🎯 Controlador para generar rutas tentativas usando OSRM
 *
 * Endpoint principal:
 * POST /api/rutas/tentativas
 *
 * Genera 3 opciones de ruta:
 * 1. Ruta directa (1 tramo)
 * 2. Ruta con 1 depósito intermedio (2 tramos)
 * 3. Ruta con 2 depósitos intermedios (3 tramos)
 */
@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
@Slf4j
public class RutasTentativasController {

    private final RutasTentativasService rutasTentativasService;

    /**
     * 🚀 Genera 3 rutas tentativas entre un origen y destino
     *
     * POST /api/rutas/tentativas
     *
     * Body ejemplo:
     * {
     *   "latitudOrigen": -31.4201,
     *   "longitudOrigen": -64.1888,
     *   "latitudDestino": -34.6037,
     *   "longitudDestino": -58.3816
     * }
     *
     * Respuesta:
     * {
     *   "rutaDirecta": { ... },
     *   "rutaCon1Deposito": { ... },
     *   "rutaCon2Depositos": { ... },
     *   "latitudOrigen": -31.4201,
     *   "longitudOrigen": -64.1888,
     *   "latitudDestino": -34.6037,
     *   "longitudDestino": -58.3816
     * }
     */
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')") // ⚠️ COMENTADO - Sin autenticación
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

    /**
     * Valida que las coordenadas estén en rangos válidos
     */
    private boolean coordenadasValidas(GenerarRutasTentativasRequestDTO request) {
        return request.getLatitudOrigen() >= -90 && request.getLatitudOrigen() <= 90 &&
               request.getLatitudDestino() >= -90 && request.getLatitudDestino() <= 90 &&
               request.getLongitudOrigen() >= -180 && request.getLongitudOrigen() <= 180 &&
               request.getLongitudDestino() >= -180 && request.getLongitudDestino() <= 180;
    }
}
