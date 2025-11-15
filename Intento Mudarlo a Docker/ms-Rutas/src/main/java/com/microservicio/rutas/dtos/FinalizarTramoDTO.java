package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para registrar la finalización de un tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarTramoDTO {
    private Long idTramo;
    private LocalDateTime fechaHoraFin;
    private String observaciones;
}

