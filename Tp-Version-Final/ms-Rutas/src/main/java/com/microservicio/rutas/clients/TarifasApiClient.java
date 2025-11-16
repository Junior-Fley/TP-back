package com.microservicio.rutas.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Cliente para consumir el microservicio de Tarifas
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TarifasApiClient {

    private final RestTemplate restTemplate;

    @Value("${tarifas.service.url:http://localhost:8092}")
    private String tarifasServiceUrl;

    /**
     * Obtiene el valor de una tarifa por su tipo
     */
    public BigDecimal obtenerValorTarifa(String tipo) {
        try {
            String url = tarifasServiceUrl + "/api/tarifas/tipo/" + tipo;
            log.info("Consultando tarifa: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("valor")) {
                Object valor = response.get("valor");
                if (valor instanceof Number) {
                    return BigDecimal.valueOf(((Number) valor).doubleValue());
                }
            }

            log.warn("No se pudo obtener tarifa de tipo: {}", tipo);
            return BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Error al consultar tarifa {}: {}", tipo, e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}

