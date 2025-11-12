package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorPendienteDTO {
    private Long idContenedor;
    private Long numeroSolicitud;
    private String estadoContenedor;  // Estado del contenedor: "Pendiente de entrega", "En tránsito", "Entregado"
    private String estadoSolicitud;   // Estado de la solicitud: "borrador", "programada", etc.
    private Long idRuta;
    private String nombreCliente;
    private String dniCliente;
    private Double peso;
    private Double volumen;
    private BigDecimal costoEstimado;
    private Integer tiempoEstimado;
}
