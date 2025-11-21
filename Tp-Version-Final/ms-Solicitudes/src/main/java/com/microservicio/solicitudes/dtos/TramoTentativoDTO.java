package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO que representa un tramo individual dentro de una ruta tentativa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoTentativoDTO {
    private Integer orden; // 1, 2, 3...
    private String tipoTramo; // "directo", "con_deposito", etc.

    // Coordenadas de origen
    private Double latitudOrigen;
    private Double longitudOrigen;
    private String nombreOrigen; // "Punto de origen" o nombre del depósito
    private Long idDepositoOrigen; // null si es punto inicial

    // Coordenadas de destino
    private Double latitudDestino;
    private Double longitudDestino;
    private String nombreDestino; // "Punto de destino" o nombre del depósito
    private Long idDepositoDestino; // null si es punto final

    // Datos calculados por OSRM
    private Double distanciaKm;
    private Double duracionMinutos;

    // Costos estimados
    private BigDecimal costoAproximado;
    private BigDecimal costoEstadiaDiario; // Si hay depósito destino
}
