package com.microservicio.solicitudes.clients;

import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.dtos.TramoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

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
     * Obtiene una ruta por ID desde ms-rutas
     */
    public RutaResumenDTO obtenerRutaRaw(Long idRuta) {
        try {
            log.info("🔍 Consultando ruta {} en ms-rutas", idRuta);

            RutaResumenDTO ruta = rutasRestClient.get()
                    .uri("/{id}", idRuta)
                    .retrieve()
                    .body(RutaResumenDTO.class);

            if (ruta != null) {
                log.info("✅ Ruta obtenida: ID={}, Tramos={}, Depositos={}, Costo={}",
                        ruta.getIdRuta(), ruta.getCantidadTramos(),
                        ruta.getCantidadDepositos(), ruta.getCostoAproximado());
            }

            return ruta;
        } catch (RestClientException e) {
            log.error("❌ Error al consultar ruta {}: {}", idRuta, e.getMessage());
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
