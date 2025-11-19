package com.microservicio.rutas.controllers;


import com.microservicio.rutas.dtos.OSRMRequestDTO;
import com.microservicio.rutas.services.OSRMService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.microservicio.rutas.dtos.DistanciaRutaDTO;
@RestController
@RequestMapping("/api/osrm")
public class OSRMController {

    private final OSRMService osrmService;

    public OSRMController(OSRMService osrmService) {
        this.osrmService = osrmService;
    }

    /**
     * Calcula la distancia y tiempo entre dos coordenadas usando OSRM
     *
     * POST /api/osrm/calcular-distancia
     *
     * Body:
     * {
     *   "latitudOrigen": -34.603722,
     *   "longitudOrigen": -58.381592,
     *   "latitudDestino": -34.921230,
     *   "longitudDestino": -57.954540
     * }
     *
     * Respuesta:
     * {
     *   "distanciaMetros": 52341.5,
     *   "distanciaKm": 52.34,
     *   "tiempoSegundos": 3156.2,
     *   "tiempoMinutos": 52.6
     * }
     */
    @PostMapping("/cálculo-distancia")
    @Operation(summary = "Calcular distancia y tiempo entre dos coordenadas usando OSRM",
               description = "Calcula la distancia en metros y kilómetros, así como el tiempo en segundos y minutos entre dos puntos geográficos utilizando el servicio OSRM.")
    public ResponseEntity<DistanciaRutaDTO> calcularDistancia(@RequestBody OSRMRequestDTO request) {
        DistanciaRutaDTO resultado = osrmService.calcularDistanciaYTiempo(request);
        return ResponseEntity.ok(resultado);
    }
}

