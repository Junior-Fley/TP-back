package com.microservicio.rutas.controllers;

import com.microservicio.rutas.dtos.CalculoCostoDTO;
import com.microservicio.rutas.dtos.TarifaDTO;
import com.microservicio.rutas.models.Tarifa;
import com.microservicio.rutas.services.TarifaService;
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
    @Operation(summary = "Obtener todas las tarifas", description = "Recupera una lista completa de todas las tarifas registradas en el sistema, incluyendo activas e inactivas.")
    public ResponseEntity<List<Tarifa>> obtenerTodas() {
        log.info("GET /api/tarifas - Obteniendo todas las tarifas");
        return ResponseEntity.ok(tarifaService.obtenerTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Obtener tarifas activas", description = "Recupera únicamente las tarifas que se encuentran actualmente activas y vigentes.")
    public ResponseEntity<List<Tarifa>> obtenerActivas() {
        log.info("GET /api/tarifas/activas");
        return ResponseEntity.ok(tarifaService.obtenerActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tarifa por ID", description = "Busca y retorna una tarifa específica basada en su identificador único.")
    public ResponseEntity<Tarifa> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifaService.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener tarifa por tipo", description = "Busca una tarifa específica por su código de tipo (ej. COSTO_KM_BASE, COMBUSTIBLE).")
    public ResponseEntity<Tarifa> obtenerPorTipo(@PathVariable String tipo) {
        log.info("GET /api/tarifas/tipo/{}", tipo);
        return ResponseEntity.ok(tarifaService.obtenerPorTipo(tipo));
    }

    @PostMapping
    @Operation(summary = "Crear nueva tarifa", description = "Registra una nueva tarifa en el sistema. Valida que el tipo no exista previamente.")
    public ResponseEntity<Tarifa> crear(@Valid @RequestBody TarifaDTO dto) {
        log.info("POST /api/tarifas - Creando tarifa: {}", dto.getTipo());
        Tarifa tarifa = tarifaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifa);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tarifa existente", description = "Actualiza los valores de una tarifa existente identificada por su ID.")
    public ResponseEntity<Tarifa> actualizar(@PathVariable Long id,
            @Valid @RequestBody TarifaDTO dto) {
        log.info("PUT /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (desactivar) tarifa", description = "Realiza un borrado lógico de la tarifa, marcándola como inactiva.")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/tarifas/{}", id);
        tarifaService.eliminar(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tarifa desactivada exitosamente");
        return ResponseEntity.ok(response);
    }
}
