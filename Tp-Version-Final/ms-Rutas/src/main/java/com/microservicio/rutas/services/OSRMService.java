package com.microservicio.rutas.services;

import com.microservicio.rutas.dtos.OSRMRequestDTO;
import com.microservicio.rutas.dtos.OSRMResponseDTO;
import com.microservicio.rutas.dtos.DistanciaRutaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Service
public class OSRMService {

    private static final Logger logger = LoggerFactory.getLogger(OSRMService.class);
    private static final String OSRM_BASE_URL = "http://router.project-osrm.org/route/v1/driving/";

    private final RestTemplate restTemplate;

    public OSRMService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Calcula la distancia y tiempo entre dos puntos usando OSRM.
     * IMPORTANTE: OSRM usa el formato longitud,latitud (no latitud,longitud)
     *
     * @param request DTO con las coordenadas de origen y destino
     * @return DTO con distancia en metros/km y tiempo en segundos/minutos
     */
    public DistanciaRutaDTO calcularDistanciaYTiempo(OSRMRequestDTO request) {
        // Validar coordenadas
        validarCoordenadas(request);

        // Construir URL: lon,lat;lon,lat (OSRM usa longitud primero)
        String url = construirUrlOSRM(
            request.getLongitudOrigen(),
            request.getLatitudOrigen(),
            request.getLongitudDestino(),
            request.getLatitudDestino()
        );

        logger.info("🗺️ Consultando OSRM: {}", url);

        try {
            // Hacer request a OSRM
            OSRMResponseDTO response = restTemplate.getForObject(url, OSRMResponseDTO.class);

            if (response == null || response.getRoutes() == null || response.getRoutes().isEmpty()) {
                throw new RuntimeException("No se pudo calcular la ruta entre los puntos indicados");
            }

            // Extraer primera ruta
            OSRMResponseDTO.Route route = response.getRoutes().get(0);

            // Convertir a DTO de respuesta
            DistanciaRutaDTO resultado = new DistanciaRutaDTO();
            resultado.setDistanciaMetros(route.getDistance());
            resultado.setDistanciaKm(route.getDistance() / 1000.0);
            resultado.setTiempoSegundos(route.getDuration());
            resultado.setTiempoMinutos(route.getDuration() / 60.0);

            logger.info("✅ Distancia calculada: {} km, Tiempo: {} min",
                String.format("%.2f", resultado.getDistanciaKm()),
                String.format("%.2f", resultado.getTiempoMinutos())
            );

            return resultado;

        } catch (Exception e) {
            logger.error("❌ Error al consultar OSRM: {}", e.getMessage());
            throw new RuntimeException("Error al calcular la ruta: " + e.getMessage(), e);
        }
    }

    /**
     * Construye la URL para OSRM con el formato correcto
     * Formato: {lonOrigen},{latOrigen};{lonDestino},{latDestino}?overview=false
     */
    private String construirUrlOSRM(Double lonOrigen, Double latOrigen,
                                     Double lonDestino, Double latDestino) {
        return String.format(Locale.US, "%s%f,%f;%f,%f?overview=false",
                OSRM_BASE_URL,
            lonOrigen, latOrigen,
            lonDestino, latDestino
        );
    }

    /**
     * Valida que las coordenadas sean válidas
     */
    private void validarCoordenadas(OSRMRequestDTO request) {
        if (request.getLatitudOrigen() == null || request.getLongitudOrigen() == null ||
            request.getLatitudDestino() == null || request.getLongitudDestino() == null) {
            throw new IllegalArgumentException("Todas las coordenadas son obligatorias");
        }

        // Validar rangos de latitud (-90 a 90) y longitud (-180 a 180)
        if (request.getLatitudOrigen() < -90 || request.getLatitudOrigen() > 90 ||
            request.getLatitudDestino() < -90 || request.getLatitudDestino() > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90");
        }

        if (request.getLongitudOrigen() < -180 || request.getLongitudOrigen() > 180 ||
            request.getLongitudDestino() < -180 || request.getLongitudDestino() > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180");
        }
    }
}
