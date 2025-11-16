package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para detallar el cálculo del costo real de un tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostoRealDTO {
    private Long idTramo;
    private BigDecimal distanciaKm;
    private BigDecimal costoBaseKmCamion;
    private BigDecimal costoKilometraje;
    private BigDecimal consumoCombustibleKm;
    private BigDecimal precioCombustible;
    private BigDecimal costoCombustible;
    private Integer diasEstadia;
    private BigDecimal costoEstadiaDiario;
    private BigDecimal costoEstadia;
    private BigDecimal cargoGestion;
    private BigDecimal costoTotal;
    private String detalleCalculo;
}
