package com.microservicio.rutas.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.services.TramoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "Obtener todos los tramos")
    public ResponseEntity<List<Tramo>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un tramo por ID")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
    }

    @GetMapping("/ruta/{idRuta}")
    @Operation(summary = "Obtener tramos de una ruta específica")
    public ResponseEntity<List<Tramo>> obtenerTramosPorRuta(@PathVariable("idRuta") Long idRuta) {
        return ResponseEntity.ok(service.obtenerTramosPorRuta(idRuta));
    }
    }

    @Operation(summary = "RF6: Asignar un camión a un tramo",
               description = "Asigna un camión disponible a un tramo específico del traslado")
    public ResponseEntity<Tramo> asignarCamion(@RequestBody AsignarCamionTramoDTO dto) {
        Tramo actualizado = service.asignarCamionATramo(dto);
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/iniciar-viaje")
    @Operation(summary = "RF7: Registrar inicio de viaje de un tramo",
               description = "Registra el inicio de un tramo con fecha/hora actual")
    public ResponseEntity<Tramo> iniciarViaje(@RequestBody IniciarFinalizarViajeDTO dto) {
