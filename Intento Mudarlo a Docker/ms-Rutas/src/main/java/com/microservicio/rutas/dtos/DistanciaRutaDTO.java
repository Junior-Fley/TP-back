package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanciaRutaDTO {
    private Double distanciaMetros;
    private Double distanciaKm;
    private Double tiempoSegundos;
    private Double tiempoMinutos;
}

