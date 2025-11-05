package com.microservicio.solicitudes.clients;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class RutasApiClient {

    private final RestClient rutasRestClient;

    public RutasApiClient(RestClient rutasClient) {
        this.rutasRestClient = rutasClient;
    }

    // 🔹 Método que consume la API de Rutas y devuelve el JSON como String
    public String obtenerRutaRaw(Long idRuta) {
        try {
            return rutasRestClient.get()
                    .uri("/{id}", idRuta)
                    .retrieve()
                    .body(String.class); // ← el JSON crudo
        } catch (RestClientResponseException e) {
            System.err.println("Error al consultar rutas: " + e.getMessage());
            return null;
        }
    }
}
