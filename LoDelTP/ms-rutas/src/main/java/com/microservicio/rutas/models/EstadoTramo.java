package com.microservicio.rutas.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estado_tramo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoTramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(name = "name", nullable = false, length = 50)
    private String nombre;
}
