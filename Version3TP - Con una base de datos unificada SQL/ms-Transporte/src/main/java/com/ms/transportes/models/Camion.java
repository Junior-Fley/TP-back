package com.ms.transportes.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCamion;

    @Column(nullable = false, unique = true)
    private String patente; // Ej: ABC123

    private String telefono;
    private double capacidadPeso;       // Peso máximo que puede cargar
    private double capacidadVolumen;    // Volumen máximo que puede cargar
    private boolean disponibilidad;     // true = libre, false = ocupado
    private double costoBaseKm;         // Costo base por kilómetro
    private double consumoCombustibleKm; // Litros por km

    // Relación con Transportista (N camiones → 1 transportista)
    @ManyToOne
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;
}
