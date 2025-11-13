package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para asignar una ruta a una solicitud
 * (Requerimiento Funcional #4)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarRutaDTO {
    private Long idRuta;
    private BigDecimal costoEstimado;
    private Integer tiempoEstimado;
}

