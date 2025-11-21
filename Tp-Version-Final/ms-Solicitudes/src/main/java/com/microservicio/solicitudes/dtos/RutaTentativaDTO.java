package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO que representa una ruta tentativa completa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaTentativaDTO {
    private String tipo; // "DIRECTA", "CON_1_DEPOSITO", "CON_2_DEPOSITOS"
    private String descripcion; // Descripción legible
    private Integer cantidadTramos;

    // Lista de tramos que componen esta ruta
    private List<TramoTentativoDTO> tramos;

    // Totales calculados
    private Double distanciaTotalKm;
    private Double duracionTotalMinutos;
    private BigDecimal costoTotalAproximado;

    // Información adicional
    private List<String> depositosIntermedios; // Nombres de depósitos
}

