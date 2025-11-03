package com.microservicio.rutas.controllers;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.services.RutasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

//    @GetMapping("/{id}")
//    public ResponseEntity<Rutas> obtenerPorSolicitud(@PathVariable("id") Long solicitudId) {
//        Rutas ruta = service.obtenerPorSolicitud(solicitudId);
//        if (ruta == null) return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(ruta);
//    }

//    // 🔹 Endpoint específico para el cliente externo (ej: ms-cliente)
//    @GetMapping("/resumen/{id}")
//    public ResponseEntity<RutaResumenDTO> obtenerResumen(@PathVariable Long id) {
//        Rutas ruta = service.obtenerPorId(id);
//        if (ruta == null) return ResponseEntity.notFound().build();
//
//        BigDecimal costoAprox = service.calcularCostoAproximado(ruta);
//
//        RutaResumenDTO dto = new RutaResumenDTO(
//                ruta.getIdRuta(),
//                ruta.getCantidadTramos(),
//                ruta.getCantidadDepositos(),
//                costoAprox
//        );
//
//        return ResponseEntity.ok(dto);
//    }
}
