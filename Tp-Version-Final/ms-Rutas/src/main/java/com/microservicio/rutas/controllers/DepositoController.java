package com.microservicio.rutas.controllers;

import com.microservicio.rutas.dtos.DepositoDTO;
import com.microservicio.rutas.models.Deposito;
import com.microservicio.rutas.services.DepositoService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Listar depósitos", description = "Obtiene la lista completa de depósitos")
    @GetMapping
    public ResponseEntity<List<Deposito>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    // GET: obtener por ID
    @Operation(summary = "Obtener depósito por ID", description = "Obtiene los datos de un depósito a partir de su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<Deposito> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: crear nuevo depósito (recibe DTO sin id)
    @Operation(summary = "Crear depósito", description = "Crea un nuevo depósito con los datos proporcionados")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DepositoDTO dto) {
        try {
            Deposito nuevo = service.crearDeposito(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT: actualizar depósito existente (ID por path, body sin id)
    @Operation(summary = "Actualizar depósito", description = "Actualiza los datos de un depósito existente (ID por path)")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id, @RequestBody DepositoDTO dto) {
        try {
            Deposito actualizado = service.actualizarDeposito(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // DELETE: eliminar por ID
    @Operation(summary = "Eliminar depósito", description = "Elimina un depósito por su identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
