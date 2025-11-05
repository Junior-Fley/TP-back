package com.microservicio.rutas.controllers;

import com.microservicio.rutas.models.Ciudad;
import com.microservicio.rutas.services.CiudadService;
import lombok.RequiredArgsConstructor;
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
    @GetMapping
    public ResponseEntity<List<Ciudad>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // GET: obtener ciudad por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: crear nueva ciudad
    @PostMapping
    public ResponseEntity<Ciudad> crear(@RequestBody Ciudad ciudad) {
        Ciudad nueva = service.crear(ciudad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // PUT: actualizar ciudad existente
    @PutMapping("/{id}")
    public ResponseEntity<Ciudad> actualizar(@PathVariable Long id, @RequestBody Ciudad ciudad) {
        try {
            Ciudad actualizada = service.actualizar(id, ciudad);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: eliminar ciudad por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
