package com.microservicio.rutas.clients;

import com.microservicio.rutas.dtos.ContenedorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Cliente para comunicación con el microservicio de Solicitudes
 */
@Component
@Slf4j
public class SolicitudesApiClient {

    private final RestTemplate restTemplate;
    private final String solicitudesBaseUrl;

    public SolicitudesApiClient(RestTemplate restTemplate,
                               @Value("${api.solicitudes.base-url:http://localhost:8090}") String solicitudesBaseUrl) {
        this.restTemplate = restTemplate;
        this.solicitudesBaseUrl = solicitudesBaseUrl;
    }

    /**
     * Notifica al microservicio de Solicitudes que debe cambiar el estado del contenedor a "en tránsito"
     * cuando se inicia el primer tramo de una ruta
     */
    public void notificarInicioTransito(Long idSolicitud) {
        try {
            String url = solicitudesBaseUrl + "/api/solicitudes/" + idSolicitud + "/contenedor/iniciar-transito";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                String.class
            );

            log.info("✅ Notificación de inicio de tránsito enviada a Solicitud {}: {}",
                    idSolicitud, response.getBody());
        } catch (Exception e) {
            log.warn("⚠️ No se pudo notificar inicio de tránsito para Solicitud {}: {}",
                    idSolicitud, e.getMessage());
        }
    }

    /**
     * Notifica al microservicio de Solicitudes que todos los tramos están finalizados
     * para que finalice automáticamente la solicitud y cambie el contenedor a "entregado"
     */
    public void notificarFinalizacionTodosTramos(Long idSolicitud) {
        try {
            String url = solicitudesBaseUrl + "/api/solicitudes/" + idSolicitud + "/finalizar";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );

            log.info("✅ Solicitud {} finalizada automáticamente. Contenedor marcado como 'entregado'",
                    idSolicitud);
        } catch (Exception e) {
            log.warn("⚠️ No se pudo finalizar automáticamente la Solicitud {}: {}",
                    idSolicitud, e.getMessage());
        }
    }

    /**
     * ⭐ NUEVO: Actualiza el costo acumulado de la solicitud cada vez que se finaliza un tramo
     * Esto permite ver el costo progresivo mientras se van finalizando los tramos
     */
    public void actualizarCostoAcumulado(Long idSolicitud, BigDecimal costoTramo) {
        try {
            String url = solicitudesBaseUrl + "/api/solicitudes/" + idSolicitud + "/costo-acumulado";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<BigDecimal> request = new HttpEntity<>(costoTramo, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                String.class
            );

            log.info("✅ Costo acumulado actualizado para Solicitud {}: {}",
                    idSolicitud, response.getBody());
        } catch (Exception e) {
            log.warn("⚠️ No se pudo actualizar el costo acumulado para Solicitud {}: {}",
                    idSolicitud, e.getMessage());
        }
    }

    /**
     * ⭐ NUEVO: Obtiene el contenedor asociado a una solicitud
     * Necesario para validar que el camión puede transportar el contenedor
     */
    public ContenedorDTO obtenerContenedorPorSolicitud(Long idSolicitud) {
        try {
            String url = solicitudesBaseUrl + "/api/solicitudes/" + idSolicitud;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
            );

            if (response.getBody() != null && response.getBody().get("contenedor") != null) {
                Map<String, Object> contenedorMap = (Map<String, Object>) response.getBody().get("contenedor");

                ContenedorDTO contenedor = new ContenedorDTO();
                contenedor.setIdContenedor(((Number) contenedorMap.get("idContenedor")).longValue());
                contenedor.setPeso((Double) contenedorMap.get("peso"));
                contenedor.setVolumen((Double) contenedorMap.get("volumen"));
                contenedor.setEstado((String) contenedorMap.get("estado"));

                log.info("✅ Contenedor obtenido: ID={}, Peso={}kg, Volumen={}m³",
                        contenedor.getIdContenedor(), contenedor.getPeso(), contenedor.getVolumen());

                return contenedor;
            }

            log.warn("⚠️ No se encontró contenedor para la Solicitud {}", idSolicitud);
            return null;

        } catch (Exception e) {
            log.warn("⚠️ No se pudo obtener contenedor para Solicitud {}: {}",
                    idSolicitud, e.getMessage());
            return null;
        }
    }
}
