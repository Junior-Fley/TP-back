package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoContenedorDTO {
    // Información del contenedor
    private Long idContenedor;
    private Double peso;
    private Double volumen;

    // Información de la solicitud
    private Long numeroSolicitud;
    private String estadoActual;

    // Información del cliente
    private Long idCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String dniCliente;

    // Información de costos y tiempos
    private BigDecimal costoEstimado;
    private Integer tiempoEstimado;
    private BigDecimal costoFinal;
    private Integer tiempoReal;

    // Información de la ruta (si existe)
    private Long idRuta;
}

