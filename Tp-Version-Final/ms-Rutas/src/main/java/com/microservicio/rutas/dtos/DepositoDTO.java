package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositoDTO {
    private String nombre;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private BigDecimal costoEstadiaDiario;
    private Long idCiudad; // referencia a ciudad
}

