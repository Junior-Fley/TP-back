package com.microservicio.solicitudes.controllers;

import com.microservicio.solicitudes.dtos.ContenedorCreateDTO;
import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.services.ContenedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenedores")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService service;

    @GetMapping
    public List<Contenedor> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> obtenerPorId(@PathVariable("id") Long id) {
        Contenedor contenedor = service.obtenerPorId(id);
        if (contenedor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contenedor);
    }

    /**
     * Crea un contenedor nuevo
     * NO acepta ID ni estado - se asignan automáticamente
     */
    @PostMapping
    public ResponseEntity<Contenedor> crear(@RequestBody ContenedorCreateDTO dto) {
        try {
            Contenedor contenedor = service.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(contenedor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza peso y volumen de un contenedor
     * El estado NO se puede modificar directamente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Contenedor> actualizar(@PathVariable("id") Long id, @RequestBody ContenedorCreateDTO dto) {
        try {
            Contenedor contenedor = service.actualizar(id, dto);
            return ResponseEntity.ok(contenedor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
