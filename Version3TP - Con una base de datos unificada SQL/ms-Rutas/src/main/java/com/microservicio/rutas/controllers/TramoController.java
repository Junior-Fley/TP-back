package com.microservicio.rutas.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.services.TramoService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tramo> crear(@RequestBody Tramo tramo) {
        Tramo nuevo = service.crear(tramo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Asigna un camión a un tramo de traslado de contenedor.
     * PUT /api/tramos/{idTramo}/asignar-camion/{idCamion}
     */
    @PutMapping("/{idTramo}/asignar-camion/{idCamion}")
    public ResponseEntity<Tramo> asignarCamion(
            @PathVariable("idTramo") Long idTramo,
            @PathVariable("idCamion") Long idCamion) {
        Tramo tramoActualizado = service.asignarCamion(idTramo, idCamion);
        return ResponseEntity.ok(tramoActualizado);
    }
}
