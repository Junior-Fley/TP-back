package com.microservicio.solicitudes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación de contenedores
 * NO incluye ID ni estado (se asignan automáticamente)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorCreateDTO {
    private Double peso;
    private Double volumen;
}

