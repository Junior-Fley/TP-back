package com.microservicio.solicitudes.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONTENEDOR")
public class Contenedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contenedor")
    private Long idContenedor;
    private Double peso;
    private Double volumen;
    private String estado; // Ejemplo: "Pendiente de entrega", "En tránsito", "Entregado", etc."
}
