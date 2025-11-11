package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoDTO {
    private Long idTramo;
    private String origen;
    private String destino;
    private String tipoTramo;
    private BigDecimal distanciaKm;
    private BigDecimal costoEstimado;
    private String tiempoEstimado;
}
