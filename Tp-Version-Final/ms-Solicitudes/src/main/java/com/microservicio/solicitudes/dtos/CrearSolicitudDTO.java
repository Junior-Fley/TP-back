package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado para crear una solicitud
 * Solo requiere el ID del cliente y el ID del contenedor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearSolicitudDTO {
    private Long idCliente;
    private Long idContenedor;
}

