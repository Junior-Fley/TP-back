package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta que contiene las 3 rutas tentativas generadas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutasTentativasResponseDTO {
    private RutaTentativaDTO rutaDirecta;
    private RutaTentativaDTO rutaCon1Deposito;
    private RutaTentativaDTO rutaCon2Depositos;

    // Información del origen y destino solicitados
    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;
}

