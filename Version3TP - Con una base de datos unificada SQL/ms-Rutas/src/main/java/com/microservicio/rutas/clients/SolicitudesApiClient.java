package com.microservicio.rutas.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
}
