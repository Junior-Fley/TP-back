package com.microservicio.rutas.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microservicio.rutas.dtos.CostoRealDTO;
import com.microservicio.rutas.dtos.FinalizarTramoDTO;
import com.microservicio.rutas.dtos.IniciarTramoDTO;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.services.TramoService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microservicio.rutas.dtos.CostoEntregaDTO;

@RestController
@RequestMapping("/api/tramos")
@RequiredArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TramoController {

    private final TramoService service;

    // ==========================================
    // GET ENDPOINTS
    // ==========================================

    @Operation(summary = "Obtener todos los tramos", description = "Devuelve una lista de todos los tramos registrados en el sistema.")
    @GetMapping
    public ResponseEntity<List<Tramo>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Obtener tramo por ID", description = "Busca un tramo específico por su identificador único.")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener costo real del tramo", description = "Obtiene el detalle del cálculo del costo real de un tramo específico.")
    @GetMapping("/{id}/costo-real")
    public ResponseEntity<CostoRealDTO> obtenerCostoReal(@PathVariable("id") Long id) {
        Tramo tramo = service.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        CostoRealDTO costoReal = service.calcularCostoReal(tramo);
        return ResponseEntity.ok(costoReal);
    }

    @Operation(summary = "Calcular costo de entrega", description = "Calcula el costo total de una entrega basado en el ID de la ruta.")
    @GetMapping("/costo/{idRuta}")
    @Hidden
    public ResponseEntity<CostoEntregaDTO> calcularCostoEntrega(@PathVariable("idRuta") Long idRuta) {
        CostoEntregaDTO costo = service.calcularCostoEntrega(idRuta);
        return ResponseEntity.ok(costo);
    }

    // ==========================================
    // POST ENDPOINTS
    // ==========================================

    @Operation(summary = "Crear tramo", description = "Crea un nuevo tramo en el sistema.")
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Tramo> crear(@RequestBody Tramo tramo) {
        Tramo nuevo = service.crear(tramo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Iniciar tramo", description = "Registra el inicio de un tramo de traslado.")
    @PostMapping("/{id}/inicializacion")
    // @PreAuthorize("hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> iniciarTramo(
            @PathVariable("id") Long id,
            @RequestBody(required = false) IniciarTramoDTO dto) {

        Tramo tramoIniciado = service.iniciarTramo(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tramo iniciado exitosamente");
        response.put("tramo", tramoIniciado);
        response.put("fechaHoraInicio", tramoIniciado.getFechaHoraInicio());
        response.put("estado", tramoIniciado.getEstado().getNombre());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Finalizar tramo", description = "Registra la finalización de un tramo y calcula el costo real.")
    @PostMapping("/{id}/finalizacion")
    // @PreAuthorize("hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> finalizarTramo(
            @PathVariable("id") Long id,
            @RequestBody(required = false) FinalizarTramoDTO dto) {

        Tramo tramoFinalizado = service.finalizarTramo(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tramo finalizado exitosamente");
        response.put("tramo", tramoFinalizado);
        response.put("fechaHoraInicio", tramoFinalizado.getFechaHoraInicio());
        response.put("fechaHoraFin", tramoFinalizado.getFechaHoraFin());
        response.put("costoReal", tramoFinalizado.getCostoReal());
        response.put("estado", tramoFinalizado.getEstado().getNombre());

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // PUT ENDPOINTS
    // ==========================================

    @Operation(summary = "Actualizar tramo", description = "Actualiza los datos de un tramo existente.")
    @PutMapping("/{id}")
    public ResponseEntity<Tramo> actualizar(@PathVariable("id") Long id, @RequestBody Tramo tramo) {
        Tramo tramoActualizado = service.actualizar(id, tramo);
        return ResponseEntity.ok(tramoActualizado);
    }

    @Operation(summary = "Asignar camión", description = "Asigna un camión a un tramo de traslado.")
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{idTramo}/asignacion-camion/{idCamion}")
    public ResponseEntity<Tramo> asignarCamion(
            @PathVariable("idTramo") Long idTramo,
            @PathVariable("idCamion") Long idCamion) {
        Tramo tramoActualizado = service.asignarCamion(idTramo, idCamion);
        return ResponseEntity.ok(tramoActualizado);
    }

    // ==========================================
    // DELETE ENDPOINTS
    // ==========================================

    @Operation(summary = "Eliminar tramo", description = "Elimina un tramo del sistema por su ID.")
    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
