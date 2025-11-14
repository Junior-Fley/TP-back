package com.microservicio.solicitudes.clients;

import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.dtos.TramoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RutasApiClient {

    private final RestClient rutasRestClient;

    /**
     * Obtiene una ruta por el ID de la solicitud
     */
    public RutaResumenDTO obtenerRutaPorSolicitud(Long idSolicitud) {
        try {
            log.info("🔍 Consultando ruta de solicitud {} en ms-rutas", idSolicitud);

            RutaResumenDTO ruta = rutasRestClient.get()
                    .uri("/solicitud/{idSolicitud}/resumen", idSolicitud)
                    .retrieve()
                    .body(RutaResumenDTO.class);

            return ruta;

        } catch (RestClientException e) {
            log.error("❌ Error al consultar ruta de solicitud {}: {}", idSolicitud, e.getMessage());
            return null;
        }
    }

    /**
     * ⭐ MEJORADO: Obtiene una ruta por ID desde ms-rutas
     * Mapea la respuesta completa y extrae correctamente todos los datos
     */
    public RutaResumenDTO obtenerRutaRaw(Long idRuta) {
        try {
            log.info("🔍 Consultando ruta {} en ms-rutas", idRuta);

            // Obtener la respuesta como Map para manejar la estructura completa
            Map<String, Object> rutaMap = rutasRestClient.get()
                    .uri("/{id}", idRuta)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (rutaMap == null) {
                log.warn("⚠️ Ruta {} no encontrada", idRuta);
                return null;
            }

            // Extraer y convertir los datos
            RutaResumenDTO ruta = new RutaResumenDTO();
            ruta.setIdRuta(((Number) rutaMap.get("idRuta")).longValue());

            if (rutaMap.get("cantidadTramos") != null) {
                ruta.setCantidadTramos(((Number) rutaMap.get("cantidadTramos")).intValue());
            }

            if (rutaMap.get("cantidadDepositos") != null) {
                ruta.setCantidadDepositos(((Number) rutaMap.get("cantidadDepositos")).intValue());
            }

            // ⭐ IMPORTANTE: Usar costoTotal en lugar de costoAproximado
            if (rutaMap.get("costoTotal") != null) {
                ruta.setCostoAproximado(new BigDecimal(rutaMap.get("costoTotal").toString()));
            }

            if (rutaMap.get("tiempoEstimadoMin") != null) {
                ruta.setTiempoEstimadoMin(((Number) rutaMap.get("tiempoEstimadoMin")).doubleValue());
            }

            if (rutaMap.get("distanciaTotal") != null) {
                ruta.setDistanciaTotalKm(((Number) rutaMap.get("distanciaTotal")).doubleValue());
            }

            log.info("✅ Ruta obtenida: ID={}, Tramos={}, Depositos={}, Costo={}, Tiempo={} min, Distancia={} km",
                    ruta.getIdRuta(),
                    ruta.getCantidadTramos(),
                    ruta.getCantidadDepositos(),
                    ruta.getCostoAproximado(),
                    ruta.getTiempoEstimadoMin(),
                    ruta.getDistanciaTotalKm());

            return ruta;
        } catch (RestClientException e) {
            log.error("❌ Error al consultar ruta {}: {}", idRuta, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("❌ Error al procesar datos de ruta {}: {}", idRuta, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene todas las rutas tentativas con su resumen completo.
     * Incluye todos los tramos sugeridos, tiempo y costo estimados.
     */
    public List<RutaResumenDTO> obtenerRutasTentativas() {
        try {
            log.info("🔍 Consultando rutas tentativas en ms-rutas");

            List<RutaResumenDTO> rutas = rutasRestClient.get()
                    .uri("/tentativas/resumen")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RutaResumenDTO>>() {});

            if (rutas != null) {
                log.info("✅ {} rutas tentativas obtenidas", rutas.size());
            }

            return rutas;

        } catch (RestClientException e) {
            log.error("❌ Error al consultar rutas tentativas: {}", e.getMessage());
            throw new RuntimeException("No se pudo obtener las rutas tentativas desde ms-rutas", e);
        }
    }

    /**
     * Obtiene todos los tramos de una ruta específica para calcular costos finales
     */
    public List<TramoDTO> obtenerTramosPorRuta(Long idRuta) {
        try {
            log.info("🔍 Consultando tramos de ruta {} en ms-rutas", idRuta);

            List<TramoDTO> tramos = rutasRestClient.get()
                    .uri("/{idRuta}/tramos", idRuta)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TramoDTO>>() {});

            if (tramos != null) {
                log.info("✅ {} tramos obtenidos para ruta {}", tramos.size(), idRuta);
            }

            return tramos;

        } catch (RestClientException e) {
            log.error("❌ Error al consultar tramos de ruta {}: {}", idRuta, e.getMessage());
            return null;
        }
    }

}
