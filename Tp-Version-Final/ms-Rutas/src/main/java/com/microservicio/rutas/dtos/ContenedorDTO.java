package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir datos de un contenedor desde el microservicio de Solicitudes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorDTO {
    private Long idContenedor;
    private Double peso;
    private Double volumen;
    private String estado;
}

