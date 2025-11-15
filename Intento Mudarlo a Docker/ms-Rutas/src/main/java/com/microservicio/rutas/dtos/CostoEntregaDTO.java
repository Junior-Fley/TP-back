package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar el costo de entrega de una ruta completa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostoEntregaDTO {
    private double distanciaTotalKm;
    private double costoPorDistancia;
    private double costoPorPesoYVolumen;
    private double costoPorEstadia;
    private double costoTotal;
}
