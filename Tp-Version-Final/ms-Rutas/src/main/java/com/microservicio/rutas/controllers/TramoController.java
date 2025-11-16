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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microservicio.rutas.dtos.CostoEntregaDTO;

@RestController
@RequestMapping("/api/tramos")
@RequiredArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TramoController {

    private final TramoService service;

    @GetMapping
    public ResponseEntity<List<Tramo>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Tramo> crear(@RequestBody Tramo tramo) {
        Tramo nuevo = service.crear(tramo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /**
     * Actualiza cualquier dato de un tramo
     * PUT /api/tramos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tramo> actualizar(@PathVariable("id") Long id, @RequestBody Tramo tramo) {
        Tramo tramoActualizado = service.actualizar(id, tramo);
        return ResponseEntity.ok(tramoActualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Asigna un camión a un tramo de traslado de contenedor.
     * PUT /api/tramos/{idTramo}/asignar-camion/{idCamion}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{idTramo}/asignacion-camion/{idCamion}")
    public ResponseEntity<Tramo> asignarCamion(
            @PathVariable("idTramo") Long idTramo,
            @PathVariable("idCamion") Long idCamion) {
        Tramo tramoActualizado = service.asignarCamion(idTramo, idCamion);
        return ResponseEntity.ok(tramoActualizado);
    }

    /**
     * 🚚 Registra el INICIO de un tramo de traslado (Transportista)
     * POST /api/tramos/{id}/iniciar
     *
     * Requerimiento Funcional: "Determinar el inicio o fin de un tramo de traslado. (Transportista)"
     */
    @PostMapping("/{id}/inicializacion")
//    @PreAuthorize("hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> iniciarTramo(
            @PathVariable("id") Long id,
            @RequestBody(required = false) IniciarTramoDTO dto) {

        if (dto == null) {
            dto = new IniciarTramoDTO();
        }
        dto.setIdTramo(id);

        Tramo tramoIniciado = service.iniciarTramo(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tramo iniciado exitosamente");
        response.put("tramo", tramoIniciado);
        response.put("fechaHoraInicio", tramoIniciado.getFechaHoraInicio());
        response.put("estado", tramoIniciado.getEstado().getNombre());

        return ResponseEntity.ok(response);
    }

    /**
     * 🏁 Registra la FINALIZACIÓN de un tramo y calcula el costo real (Transportista)
     * POST /api/tramos/{id}/finalizar
     *
     * Requerimiento Funcional: "Determinar el inicio o fin de un tramo de traslado. (Transportista)"
     * Calcula el costo real según: kilometraje + combustible + estadía + gestión
     */
    @PostMapping("/{id}/finalizacion")
//    @PreAuthorize("hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> finalizarTramo(
            @PathVariable("id") Long id,
            @RequestBody(required = false) FinalizarTramoDTO dto) {

        if (dto == null) {
            dto = new FinalizarTramoDTO();
        }
        dto.setIdTramo(id);

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

    /**
     * 💰 Obtiene el detalle del cálculo del costo real de un tramo
     * GET /api/tramos/{id}/costo-real
     */
    @GetMapping("/{id}/costo-real")
    public ResponseEntity<CostoRealDTO> obtenerCostoReal(@PathVariable("id") Long id) {
        Tramo tramo = service.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        CostoRealDTO costoReal = service.calcularCostoReal(tramo);
        return ResponseEntity.ok(costoReal);
    }

    // ✅ Calcular costo total de una entrega (requerimiento funcional 8)
    @GetMapping("/costo/{idRuta}")
    public ResponseEntity<CostoEntregaDTO> calcularCostoEntrega(@PathVariable("idRuta") Long idRuta) {
        CostoEntregaDTO costo = service.calcularCostoEntrega(idRuta);
        return ResponseEntity.ok(costo);
    }
}
