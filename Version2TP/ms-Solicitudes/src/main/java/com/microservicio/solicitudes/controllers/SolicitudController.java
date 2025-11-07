package com.microservicio.solicitudes.controllers;


import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.services.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;
    private final RutasApiClient rutasApiClient;


    public SolicitudController(SolicitudService service, RutasApiClient rutasApiClient) {
        this.service = service;
        this.rutasApiClient = rutasApiClient;
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

    // ✅ Consultar información de una ruta desde el microservicio de Rutas
    // Ejemplo: GET localhost:8090/api/solicitudes/consultar-ruta/1
    @GetMapping("/consultar-ruta/{idRuta}")
    public ResponseEntity<RutaResumenDTO> consultarRuta(@PathVariable Long idRuta) {
        RutaResumenDTO ruta = rutasApiClient.obtenerRutaRaw(idRuta);

        if (ruta == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ruta);
    }

}
