package com.microservicio.solicitudes.controllers;


import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.services.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> listar() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtener(@PathVariable("id") Long id) {
        Solicitud s = service.obtenerPorId(id);
        return (s != null) ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Solicitud> crear(@RequestBody Solicitud solicitud) {
        return ResponseEntity.status(201).body(service.crear(solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }



//    @PutMapping("/{id}")
//    public ResponseEntity<Solicitud> actualizar(@PathVariable("id") Long id, @RequestBody Solicitud solicitud) {
//        Solicitud existente = service.obtenerPorId(id);
//        if (existente == null) {
//            return ResponseEntity.notFound().build();
//        }
//        solicitud.setNumeroSolicitud(id);
//        Solicitud actualizado = service.crear(solicitud);
//        return ResponseEntity.ok(actualizado);
//    }
}
