package com.microservicio.rutas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RutaResumenDTO {
    private Long idRuta;
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private BigDecimal costoAproximado;
}
