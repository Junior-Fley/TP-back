package com.ms.transportes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para asignar un transportista a un camión
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarTransportistaDTO {
    private Long idTransportista; // ID del transportista a asignar
}
