package com.ms.transportes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCamionDTO {
    private String patente;
    private String telefono;
    private Double capacidadPeso;
    private Double capacidadVolumen;
    private Double costoBaseKm;
    private Double consumoCombustibleKm;
    private Boolean disponibilidad;
    private Long idTransportista; // Puede ser null si no tiene transportista asignado
}

