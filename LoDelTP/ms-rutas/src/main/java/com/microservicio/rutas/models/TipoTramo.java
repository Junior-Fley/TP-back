package com.microservicio.rutas.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_tramo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoTramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_tramo")
    private Long idTipoTramo;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
}
