package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para representar un tramo sugerido dentro de una ruta tentativa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoSugeridoDTO {
    private Long idTramo;
    private String origen;
    private String destino;
    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;
    private Double distanciaKm;
    private BigDecimal costoAproximado;
    private Integer tiempoEstimadoMinutos;
    private String tipoTramo;
    private String estado;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
}

