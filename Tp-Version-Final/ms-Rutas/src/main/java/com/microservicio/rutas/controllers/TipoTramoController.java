package com.microservicio.rutas.controllers;

import com.microservicio.rutas.models.TipoTramo;
import com.microservicio.rutas.services.TipoTramoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-tramo")
@RequiredArgsConstructor
public class TipoTramoController {

    private final TipoTramoService service;

    @GetMapping
    public ResponseEntity<List<TipoTramo>> listarTiposDeTramo() {
        return ResponseEntity.ok(service.obtenerTodos());
    }
}
