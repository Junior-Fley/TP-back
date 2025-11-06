package com.microservicio.solicitudes.clients;

import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RutasApiClient {

    private final RestClient rutasRestClient;

    /**
     * Obtiene el resumen completo de una ruta por su ID
     */
//    public RutaResumenDTO obtenerResumenPorId(Long idRuta) {
//        try {
//            log.info("🔍 Consultando ruta {} en ms-rutas", idRuta);
//
//            RutaResumenDTO ruta = rutasRestClient.get()
//                    .uri("/{id}/resumen", idRuta)
//                    .retrieve()
//                    .body(RutaResumenDTO.class);
//
//            if (ruta != null) {
//                log.info("✅ Ruta obtenida: {} tramos, {} km, ${}",
//                        ruta.getCantidadTramos(),
//                        ruta.getDistanciaTotalKm(),
//                        ruta.getCostoEstimado());
//            }
//
//            return ruta;
//
//        } catch (RestClientException e) {
//            log.error("❌ Error al consultar ruta {}: {}", idRuta, e.getMessage());
//            throw new RuntimeException("No se pudo obtener la ruta desde ms-rutas", e);
//        }
//    }

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
            return null; // Puede que la solicitud aún no tenga ruta asignada
        }
    }

    /**
     * Obtiene una ruta por ID desde ms-rutas
     */
    public RutaResumenDTO obtenerRutaRaw(Long idRuta) {
        try {
            log.info("🔍 Consultando ruta {} en ms-rutas", idRuta);

            RutaResumenDTO ruta = rutasRestClient.get()
                    .uri("/{id}/resumen", idRuta)
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
}
