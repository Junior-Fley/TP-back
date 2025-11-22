package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para cálculo de costo de transporte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculoCostoDTO {

    private BigDecimal distanciaKm;
    private BigDecimal volumenM3;
    private BigDecimal pesoKg;
    private Integer diasEstadia;
    private BigDecimal consumoCombustibleLitrosPorKm;

    // Resultado del cálculo
    private BigDecimal costoKilometraje;
    private BigDecimal costoCombustible;
    private BigDecimal costoEstadia;
    private BigDecimal costoTotal;
}
