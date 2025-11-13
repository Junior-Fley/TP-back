package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para consultar rutas tentativas con todos los tramos sugeridos
 * y el tiempo y costo estimados (Requerimiento Funcional #3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaTentativaDTO {
    private Long idRuta;
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private Double distanciaTotalKm;
    private Double tiempoEstimadoMinutos;
    private BigDecimal costoTotalEstimado;
    private List<TramoSugeridoDTO> tramosSugeridos;
}

