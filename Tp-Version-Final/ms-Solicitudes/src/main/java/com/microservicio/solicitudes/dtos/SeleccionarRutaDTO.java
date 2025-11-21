package com.microservicio.solicitudes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para seleccionar una ruta tentativa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para seleccionar una ruta tentativa")
public class SeleccionarRutaDTO {

    @Schema(description = "Número de la ruta a seleccionar (1, 2 o 3)", example = "1", required = true)
    private Integer numeroRuta;
}
