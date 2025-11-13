package com.microservicio.solicitudes.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para representar un Tramo desde el microservicio de Rutas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TramoDTO {
    private Long idTramo;
    private BigDecimal costoAproximado;
    private BigDecimal costoReal;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Long idCamion;
    private EstadoTramoDTO estado;
    private Double distanciaKm;
    
    /**
     * Obtiene el nombre del estado como String
     */
    public String getEstado() {
        return estado != null ? estado.getNombre() : null;
    }
    
    /**
     * DTO interno para el estado del tramo
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EstadoTramoDTO {
        private Long idEstado;
        private String nombre;
    }
}
