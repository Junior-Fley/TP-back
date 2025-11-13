package com.microservicio.rutas.services;

import com.microservicio.rutas.clients.GoogleMapsClient;
import com.microservicio.rutas.models.Tramo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculoRutaService {

    private final GoogleMapsClient googleMapsClient;

    /**
     * Calcula la distancia total de una lista de tramos usando Google Maps
     */
    public BigDecimal calcularDistanciaTotal(List<Tramo> tramos) {
        BigDecimal distanciaTotal = BigDecimal.ZERO;

        for (Tramo tramo : tramos) {
            Double distancia = googleMapsClient.calcularDistancia(
                    tramo.getLatitudOrigen(),
                    tramo.getLongitudOrigen(),
                    tramo.getLatitudDestino(),
                    tramo.getLongitudDestino()
            );

            if (distancia != null) {
                distanciaTotal = distanciaTotal.add(BigDecimal.valueOf(distancia));
            } else {
                // Si falla Google Maps, usar cálculo de distancia euclidiana como fallback
                double distanciaEuclidiana = calcularDistanciaEuclidiana(
                        tramo.getLatitudOrigen(), tramo.getLongitudOrigen(),
                        tramo.getLatitudDestino(), tramo.getLongitudDestino()
                );
                distanciaTotal = distanciaTotal.add(BigDecimal.valueOf(distanciaEuclidiana));
                log.warn("⚠️ Usando distancia euclidiana como fallback: {} km", distanciaEuclidiana);
            }
        }

        return distanciaTotal;
    }

    /**
     * Cálculo de distancia euclidiana (fallback si falla Google Maps)
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

        return R * c;
    }
}