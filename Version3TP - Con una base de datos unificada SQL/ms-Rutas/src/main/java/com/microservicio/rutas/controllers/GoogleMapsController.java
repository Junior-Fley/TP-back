package com.microservicio.rutas.controllers;

import com.microservicio.rutas.clients.GoogleMapsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/google-maps")
@RequiredArgsConstructor
public class GoogleMapsController {

    private final GoogleMapsClient googleMapsClient;

    /**
     * Endpoint de prueba para calcular distancia
     * GET /api/google-maps/distancia?origenLat=-31.4167&origenLng=-64.1833&destinoLat=-32.9442&destinoLng=-60.6505
     */
    @GetMapping("/distancia")
    public ResponseEntity<Map<String, Object>> calcularDistancia(
            @RequestParam("lat1") double origenLat,
            @RequestParam("long1") double origenLng,
            @RequestParam("lat2") double destinoLat,
            @RequestParam("long2") double destinoLng
    ) {
        Double distancia = googleMapsClient.calcularDistancia(
                origenLat, origenLng, destinoLat, destinoLng
        );

        Integer tiempo = googleMapsClient.calcularTiempoEstimado(
                origenLat, origenLng, destinoLat, destinoLng
        );

        Map<String, Object> response = new HashMap<>();
        response.put("origen", Map.of("lat", origenLat, "lng", origenLng));
        response.put("destino", Map.of("lat", destinoLat, "lng", destinoLng));
        response.put("distanciaKm", distancia);
        response.put("tiempoMinutos", tiempo);
        response.put("tiempoHoras", tiempo != null ? tiempo / 60.0 : null);

        return ResponseEntity.ok(response);
    }
}