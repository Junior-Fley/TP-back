package com.microservicio.solicitudes.clients;

import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RutasApiClient {

    private final RestClient rutasRestClient;

    public RutasApiClient(RestClient rutasRestClient) {
        this.rutasRestClient = rutasRestClient;
    }

    public RutaResumenDTO obtenerResumen(Long idRuta) {
        return rutasRestClient.get()
                .uri("http://localhost:8090/api/solicitudes/ruta/{id}", idRuta)
                .retrieve()
                .body(RutaResumenDTO.class);
    }
}
