package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para registrar el inicio de un tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IniciarTramoDTO {
    private Long idTramo;
    private LocalDateTime fechaHoraInicio;
    private String observaciones;
}

