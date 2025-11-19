package com.microservicio.solicitudes.controllers;

import com.microservicio.solicitudes.dtos.ContenedorCreateDTO;
import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.services.ContenedorService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Lista todos los contenedores", description = "Obtiene una lista de todos los contenedores disponibles en el sistema.")
    @GetMapping
    public List<Contenedor> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtiene un contenedor por ID", description = "Obtiene un contenedor por su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> obtenerPorId(@PathVariable("id") Long id) {
        Contenedor contenedor = service.obtenerPorId(id);
        if (contenedor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contenedor);
    }
    @Operation(summary = "Crea un nuevo contenedor", description = "Crea un nuevo contenedor con el peso y volumen especificados.")
    @PostMapping
    public ResponseEntity<Contenedor> crear(@RequestBody ContenedorCreateDTO dto) {
        try {
            Contenedor contenedor = service.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(contenedor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @Operation(summary = "Actualiza un contenedor existente", description = "Actualiza el peso y volumen de un contenedor existente identificado por su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Contenedor> actualizar(@PathVariable("id") Long id, @RequestBody ContenedorCreateDTO dto) {
        try {
            Contenedor contenedor = service.actualizar(id, dto);
            return ResponseEntity.ok(contenedor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Elimina un contenedor", description = "Elimina un contenedor existente identificado por su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
