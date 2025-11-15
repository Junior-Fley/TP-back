package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar la ubicación actual de un contenedor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {
    private String descripcion;        // "En tránsito hacia Córdoba", "En depósito de Buenos Aires"
    private String ciudad;             // Ciudad actual
    private String nombreDeposito;     // Si está en un depósito
    private Double latitud;            // Coordenadas actuales
    private Double longitud;
    private String estadoTramoActual;  // "iniciado", "finalizado"
    private Long idTramoActual;        // ID del tramo donde se encuentra
}

