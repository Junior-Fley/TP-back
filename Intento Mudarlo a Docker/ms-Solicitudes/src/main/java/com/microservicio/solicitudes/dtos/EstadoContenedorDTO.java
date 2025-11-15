package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del estado del contenedor
 * Proporciona información detallada sobre el estado actual del transporte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoContenedorDTO {

    private Long idContenedor;
    private String estadoActual;
    private String descripcionEstado;
    private Long numeroSolicitud;

    /**
     * Constructor para estado encontrado
     */
    public EstadoContenedorDTO(Long idContenedor, String estadoActual, Long numeroSolicitud) {
        this.idContenedor = idContenedor;
        this.estadoActual = estadoActual;
        this.numeroSolicitud = numeroSolicitud;
        this.descripcionEstado = obtenerDescripcion(estadoActual);
    }

    /**
     * Proporciona una descripción amigable del estado
     */
    private String obtenerDescripcion(String estado) {
        if (estado == null) return "Estado desconocido";

        String estadoLower = estado.toLowerCase();

        if (estadoLower.equals("borrador")) {
            return "La solicitud ha sido creada y está pendiente de confirmación";
        } else if (estadoLower.equals("programada")) {
            return "El transporte ha sido programado y está listo para comenzar";
        } else if (estadoLower.equals("en tránsito") || estadoLower.equals("en transito")) {
            return "El contenedor está siendo transportado";
        } else if (estadoLower.equals("entregada") || estadoLower.equals("entregado")) {
            return "El contenedor ha sido entregado en su destino";
        } else {
            return "Estado: " + estado;
        }
    }
}
