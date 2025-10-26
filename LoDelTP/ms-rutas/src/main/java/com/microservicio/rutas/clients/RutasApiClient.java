package com.microservicio.rutas.clients;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class RutasApiClient {

    private final RestClient rutasRestClient;

    public RutaResumenDTO obtenerResumen(Long idRuta) {
        try {
            return rutasRestClient.get()
                    .uri("/resumen/{id}", idRuta)
                    .retrieve()
                    .body(RutaResumenDTO.class);
        } catch (RestClientResponseException ex) {
            System.err.println("❌ Error al consultar ruta: " + ex.getStatusCode());
            return null;
        }
    }
}
