package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir datos básicos de un camión desde el microservicio de Transporte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionDTO {
    private Long idCamion;
    private String patente;
    private boolean disponibilidad;
    private double capacidadPeso;
    private double capacidadVolumen;
}

