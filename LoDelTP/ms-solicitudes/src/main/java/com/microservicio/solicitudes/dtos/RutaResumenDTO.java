package com.microservicio.solicitudes.dtos;


import lombok.Data;

@Data
public class RutaResumenDTO {
    private Long id;
    private String descripcion;
    private Double distancia;
    private Integer cantidadTramos;
}
