package com.ms.tarifas.controller;

import com.ms.tarifas.dto.CalculoCostoDTO;
import com.ms.tarifas.dto.TarifaDTO;
import com.ms.tarifas.entity.Tarifa;
import com.ms.tarifas.service.TarifaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de Tarifas
 */
@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tarifas", description = "Gestión de tarifas del sistema de transporte")
public class TarifaController {

    private final TarifaService tarifaService;

    @GetMapping
    @Operation(summary = "Obtener todas las tarifas")
    public ResponseEntity<List<Tarifa>> obtenerTodas() {
        log.info("GET /api/tarifas - Obteniendo todas las tarifas");
        return ResponseEntity.ok(tarifaService.obtenerTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Obtener tarifas activas")
    public ResponseEntity<List<Tarifa>> obtenerActivas() {
        log.info("GET /api/tarifas/activas");
        return ResponseEntity.ok(tarifaService.obtenerActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tarifa por ID")
    public ResponseEntity<Tarifa> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifaService.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener tarifa por tipo")
    public ResponseEntity<Tarifa> obtenerPorTipo(@PathVariable String tipo) {
        log.info("GET /api/tarifas/tipo/{}", tipo);
        return ResponseEntity.ok(tarifaService.obtenerPorTipo(tipo));
    }

    @PostMapping
    @Operation(summary = "Crear nueva tarifa")
    public ResponseEntity<Tarifa> crear(@Valid @RequestBody TarifaDTO dto) {
        log.info("POST /api/tarifas - Creando tarifa: {}", dto.getTipo());
        Tarifa tarifa = tarifaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifa);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tarifa existente")
    public ResponseEntity<Tarifa> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody TarifaDTO dto) {
        log.info("PUT /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (desactivar) tarifa")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/tarifas/{}", id);
        tarifaService.eliminar(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tarifa desactivada exitosamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calcular-costo")
    @Operation(summary = "Calcular costo total de transporte")
    public ResponseEntity<CalculoCostoDTO> calcularCosto(@Valid @RequestBody CalculoCostoDTO calculo) {
        log.info("POST /api/tarifas/calcular-costo");
        return ResponseEntity.ok(tarifaService.calcularCostoTransporte(calculo));
    }

    @PostMapping("/inicializar")
    @Operation(summary = "Inicializar tarifas por defecto")
    public ResponseEntity<Map<String, String>> inicializarDefecto() {
        log.info("POST /api/tarifas/inicializar");
        tarifaService.inicializarTarifasDefecto();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tarifas por defecto inicializadas");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check del microservicio")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ms-tarifas");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}

