package com.ms.transportes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un camión sin transportista asignado
 * El teléfono se genera automáticamente (351 + 7 dígitos random)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearCamionDTO {
    private String patente;           // Obligatorio - Ej: "ABC123"
    private double capacidadPeso;     // Obligatorio - Peso máximo en kg
    private double capacidadVolumen;  // Obligatorio - Volumen máximo en m³
    private double costoBaseKm;       // Obligatorio - Costo base por km
    private double consumoCombustibleKm; // Obligatorio - Consumo en litros/km
}

