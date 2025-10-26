package com.microservicio.clientes.controllers;

import com.microservicio.clientes.models.Cliente;
import com.microservicio.clientes.services.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable("id") Long id) {
        Cliente c = service.obtenerPorId(id);
        return (c != null) ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        return ResponseEntity.status(201).body(service.crear(cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable("id") Long id, @RequestBody Cliente cliente) {
        Cliente existente = service.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        cliente.setIdCliente(id);
        Cliente actualizado = service.crear(cliente);
        return ResponseEntity.ok(actualizado);
    }
}

