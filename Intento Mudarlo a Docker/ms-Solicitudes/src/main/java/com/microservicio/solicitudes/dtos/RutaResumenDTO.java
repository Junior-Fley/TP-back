package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaResumenDTO {
    private Long idRuta;
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private BigDecimal costoAproximado;
    private Double tiempoEstimadoMin; // ⭐ NUEVO: Tiempo estimado en minutos
    private Double distanciaTotalKm; // ⭐ NUEVO: Distancia total en kilómetros
}
