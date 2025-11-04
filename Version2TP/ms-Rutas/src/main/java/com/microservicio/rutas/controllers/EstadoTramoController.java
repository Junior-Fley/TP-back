package com.microservicio.rutas.controllers;

import com.microservicio.rutas.models.EstadoTramo;
import com.microservicio.rutas.services.EstadoTramoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados-tramo")
@RequiredArgsConstructor
public class EstadoTramoController {

    private final EstadoTramoService service;

    // Este endopint responde a GET /api/estados-tramo
    // Debería devolver un json algo así:
    // [
    //  { "idEstado": 1, "nombre": "Pendiente" },
    //  { "idEstado": 2, "nombre": "En curso" },
    //  { "idEstado": 3, "nombre": "Finalizado" }
    //]

    @GetMapping
    public ResponseEntity<List<EstadoTramo>> listarEstados() {
        return ResponseEntity.ok(service.obtenerTodos());
    }
}