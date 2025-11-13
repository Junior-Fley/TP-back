package com.microservicio.rutas.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Component
public class GoogleMapsClient {

    private final RestClient restClient;
    private final String apiKey;
    private final boolean googleMapsEnabled;

    public GoogleMapsClient(
            @Value("${google.maps.api.base-url}") String baseUrl,
            @Value("${google.maps.api.key}") String apiKey,
            @Value("${google.maps.enabled:true}") boolean googleMapsEnabled
    ) {
        this.apiKey = apiKey;
        this.googleMapsEnabled = googleMapsEnabled;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        if (!googleMapsEnabled) {
            log.warn("⚠️ Google Maps deshabilitado. Se usará cálculo euclidiano para todas las distancias.");
        }
    }

    /**
     * Calcula la distancia entre dos puntos usando Google Directions API
     *
     * @param origenLat Latitud del origen
     * @param origenLng Longitud del origen
     * @param destinoLat Latitud del destino
     * @param destinoLng Longitud del destino
     * @return Distancia en kilómetros
     */
    public Double calcularDistancia(
            double origenLat, double origenLng,
            double destinoLat, double destinoLng
    ) {
        // Si Google Maps está deshabilitado, usar directamente el cálculo euclidiano
        if (!googleMapsEnabled) {
            log.debug("📐 Calculando distancia euclidiana (Google Maps deshabilitado)");
            return calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
        }

        try {
            log.info("🗺️ Consultando distancia en Google Maps");
            log.info("Origen: {},{} → Destino: {},{}",
                    origenLat, origenLng, destinoLat, destinoLng);

            String origen = origenLat + "," + origenLng;
            String destino = destinoLat + "," + destinoLng;

            Map response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/directions/json")
                            .queryParam("origin", origen)
                            .queryParam("destination", destino)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                log.error("❌ Respuesta nula de Google Maps");
                log.warn("⚠️ Usando cálculo de distancia euclidiana como fallback");
                return calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
            }

            String status = (String) response.get("status");
            if (!"OK".equals(status)) {
                log.error("❌ Error en Google Maps API: {}", status);

                // Mostrar más información del error
                if (response.containsKey("error_message")) {
                    log.error("❌ Mensaje de error: {}", response.get("error_message"));
                }

                // Mensajes específicos según el tipo de error
                switch (status) {
                    case "REQUEST_DENIED":
                        log.error("❌ API Key inválida o sin permisos. Verifique la configuración.");
                        log.error("❌ Asegúrese de que la API key tenga habilitado 'Directions API'");
                        break;
                    case "OVER_QUERY_LIMIT":
                        log.error("❌ Límite de consultas excedido");
                        break;
                    case "ZERO_RESULTS":
                        log.warn("⚠️ No se encontró ruta entre los puntos");
                        break;
                }

                log.warn("⚠️ Usando cálculo de distancia euclidiana como fallback");
                return calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
            }

            // Extraer distancia de la respuesta
            Map routes = ((java.util.List<Map>)
                    response.get("routes")).get(0);
            Map legs = ((java.util.List<Map>)
                    routes.get("legs")).get(0);
            Map distance = (Map) legs.get("distance");

            // Distancia en metros, convertir a kilómetros
            int distanciaMetros = (Integer) distance.get("value");
            double distanciaKm = distanciaMetros / 1000.0;

            log.info("✅ Distancia calculada: {} km", distanciaKm);
            return distanciaKm;

        } catch (RestClientException e) {
            log.error("❌ Error al consultar Google Maps: {}", e.getMessage());
            log.warn("⚠️ Usando cálculo de distancia euclidiana como fallback");
            return calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
        } catch (Exception e) {
            log.error("❌ Error inesperado: {}", e.getMessage(), e);
            log.warn("⚠️ Usando cálculo de distancia euclidiana como fallback");
            return calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
        }
    }

    /**
     * Cálculo de distancia euclidiana usando la fórmula de Haversine
     * Se usa como fallback cuando Google Maps no está disponible
     */
    private double calcularDistanciaEuclidiana(
            double lat1, double lon1, double lat2, double lon2
    ) {
        final int R = 6371; // Radio de la Tierra en km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distancia = R * c;

        log.info("📐 Distancia euclidiana calculada: {} km", distancia);
        return distancia;
    }

    /**
     * Calcula el tiempo estimado de viaje
     *
     * @return Duración en minutos
     */
    public Integer calcularTiempoEstimado(
            double origenLat, double origenLng,
            double destinoLat, double destinoLng
    ) {
        // Si Google Maps está deshabilitado, usar directamente la estimación
        if (!googleMapsEnabled) {
            Double distancia = calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
            int tiempoMinutos = (int) (distancia / 60.0 * 60); // 60 km/h promedio
            log.info("⏱️ Tiempo estimado: {} minutos (basado en 60 km/h)", tiempoMinutos);
            return tiempoMinutos;
        }

        try {
            String origen = origenLat + "," + origenLng;
            String destino = destinoLat + "," + destinoLng;

            Map response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/directions/json")
                            .queryParam("origin", origen)
                            .queryParam("destination", destino)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                Map routes = ((java.util.List<Map>)
                        response.get("routes")).get(0);
                Map legs = ((java.util.List<Map>)
                        routes.get("legs")).get(0);
                Map duration = (Map) legs.get("duration");

                // Duración en segundos, convertir a minutos
                int duracionSegundos = (Integer) duration.get("value");
                return duracionSegundos / 60;
            } else {
                log.warn("⚠️ No se pudo obtener tiempo desde Google Maps, usando estimación");
                // Estimación: 60 km/h promedio
                Double distancia = calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
                return (int) (distancia / 60.0 * 60); // minutos
            }

        } catch (Exception e) {
            log.error("❌ Error al calcular tiempo: {}", e.getMessage());
            // Estimación basada en distancia euclidiana
            Double distancia = calcularDistanciaEuclidiana(origenLat, origenLng, destinoLat, destinoLng);
            return distancia != null ? (int) (distancia / 60.0 * 60) : null;
        }
    }
}