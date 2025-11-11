package com.microservicio.solicitudes.controllers;

import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.services.ContenedorService;
import lombok.RequiredArgsConstructor;
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
    public Contenedor obtenerPorId(@PathVariable("id") Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Contenedor crear(@RequestBody Contenedor contenedor) {
        return service.crear(contenedor);
    }

    @PutMapping("/{id}")
    public Contenedor actualizar(@PathVariable("id") Long id, @RequestBody Contenedor actualizado) {
        return service.actualizar(id, actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
    }
}
