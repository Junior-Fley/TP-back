package com.microservicio.rutas.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microservicio.rutas.dtos.CostoEntregaDTO;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.services.TramoService;
import lombok.RequiredArgsConstructor;
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

    // ✅ Obtener todos los tramos
    @GetMapping
    public ResponseEntity<List<Tramo>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    // ✅ Obtener tramo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Crear nuevo tramo
    @PostMapping
    public ResponseEntity<Tramo> crear(@RequestBody Tramo tramo) {
        Tramo nuevo = service.crear(tramo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // ✅ Eliminar tramo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Asignar camión a un tramo
    @PutMapping("/{idTramo}/asignar-camion/{idCamion}")
    public ResponseEntity<Tramo> asignarCamion(
            @PathVariable("idTramo") Long idTramo,
            @PathVariable("idCamion") Long idCamion) {
        Tramo tramoActualizado = service.asignarCamion(idTramo, idCamion);
        return ResponseEntity.ok(tramoActualizado);
    }

    // ✅ Calcular costo total de una entrega (requerimiento funcional 8)
    @GetMapping("/costo/{idRuta}")
    public ResponseEntity<CostoEntregaDTO> calcularCostoEntrega(@PathVariable Long idRuta) {
        CostoEntregaDTO costo = service.calcularCostoEntrega(idRuta);
        return ResponseEntity.ok(costo);
    }
}
