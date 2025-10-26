package com.microservicio.rutas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "ruta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rutas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long idRuta;

    // Campo que podría representar una relación con una solicitud externa (por ejemplo, ms-solicitudes)
    @Column(nullable = true)
    private Long solicitud;

    @Column(name = "cantidad_tramos")
    private Integer cantidadTramos;

    @Column(name = "cantidad_depositos")
    private Integer cantidadDepositos;

    // Relación bidireccional con TRAMO
    // mappedBy = "ruta" indica que la FK está en la tabla tramo
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tramo> tramos;
}
