package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para mostrar rutas existentes en la BD con sus tramos
 * (Diferente de RutaTentativaDTO que es para rutas generadas por OSRM)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaConTramosDTO {
    private Long idRuta;
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private Double distanciaTotal;
    private Double tiempoEstimadoMin;
    private BigDecimal costoTotal;
    private List<TramoSugeridoDTO> tramos;
}

