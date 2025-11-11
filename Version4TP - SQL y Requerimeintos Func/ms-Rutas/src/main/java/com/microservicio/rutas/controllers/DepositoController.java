package com.microservicio.rutas.controllers;

import com.microservicio.rutas.models.Deposito;
import com.microservicio.rutas.services.DepositoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depositos")
@RequiredArgsConstructor
public class DepositoController {

    private final DepositoService service;

    // GET: listar todos
    @GetMapping
    public ResponseEntity<List<Deposito>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    // GET: obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Deposito> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: crear nuevo depósito
    @PostMapping
    public ResponseEntity<Deposito> crear(@RequestBody Deposito deposito) {
        Deposito nuevo = service.crear(deposito);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // PUT: actualizar depósito existente
    @PutMapping("/{id}")
    public ResponseEntity<Deposito> actualizar(@PathVariable Long id, @RequestBody Deposito deposito) {
        try {
            Deposito actualizado = service.actualizar(id, deposito);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: eliminar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
