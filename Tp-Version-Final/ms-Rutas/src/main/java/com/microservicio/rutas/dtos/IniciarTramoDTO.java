package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar el inicio de un tramo
 * Solo requiere observaciones (opcional)
 * La fecha/hora de inicio se toma automáticamente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IniciarTramoDTO {
    private String observaciones; // Opcional
}
