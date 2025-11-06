package com.ms.transportes.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransportista;

    @Column(nullable = false)
    private String nombre;

    private String telefono;
    private String mail;
    private String direccion;

    public void setIdTransportista(Long id) {
        this.idTransportista = id;
    }
}
