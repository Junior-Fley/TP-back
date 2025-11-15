package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear una ruta definitiva a partir de una ruta tentativa seleccionada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearRutaDesdeTeantativaDTO {
    private Long idSolicitud; // ID de la solicitud a asociar
    private String tipoRuta; // "DIRECTA", "CON_1_DEPOSITO", "CON_2_DEPOSITOS"

    // Coordenadas de origen y destino
    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;

    // Lista de tramos (tal como vienen de la respuesta tentativa)
    private List<TramoTentativoDTO> tramos;
}

