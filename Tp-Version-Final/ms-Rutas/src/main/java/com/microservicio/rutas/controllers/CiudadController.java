package com.microservicio.rutas.controllers;

import com.microservicio.rutas.dtos.CiudadDTO;
import com.microservicio.rutas.models.Ciudad;
import com.microservicio.rutas.services.CiudadService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService service;

    // GET: listar todas las ciudades
    @Operation(summary = "Listar ciudades", description = "Obtiene la lista completa de ciudades")
    @GetMapping
    public ResponseEntity<List<Ciudad>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // GET: obtener ciudad por ID
    @Operation(summary = "Obtener ciudad por ID", description = "Obtiene los datos de una ciudad a partir de su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: crear nueva ciudad (recibe DTO sin id)
    @Operation(summary = "Crear ciudad", description = "Crea una nueva ciudad con los datos proporcionados")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CiudadDTO dto) {
        try {
            Ciudad nueva = service.crearCiudad(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT: actualizar ciudad existente
    @Operation(summary = "Actualizar ciudad", description = "Actualiza los datos de una ciudad existente (ID por path)")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id, @RequestBody CiudadDTO dto) {
        try {
            Ciudad actualizada = service.actualizarCiudad(id, dto);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // DELETE: eliminar ciudad por ID
    @Operation(summary = "Eliminar ciudad", description = "Elimina una ciudad por su identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
