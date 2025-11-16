package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar la generación de rutas tentativas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarRutasTentativasRequestDTO {
    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;
}

