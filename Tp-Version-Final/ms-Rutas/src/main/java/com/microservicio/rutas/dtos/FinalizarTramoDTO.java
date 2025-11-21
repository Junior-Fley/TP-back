package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar la finalización de un tramo
 * Solo requiere observaciones (opcional)
 * La fecha/hora de fin se toma automáticamente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarTramoDTO {
    private String observaciones; // Opcional
}
