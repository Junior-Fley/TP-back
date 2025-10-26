package com.microservicio.rutas.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "deposito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deposito")
    private Long idDeposito;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "latitud")
    private double latitud;

    @Column(name = "longitud")
    private double longitud;

    @Column(name = "costo_estadia_diario", precision = 10, scale = 2)
    private BigDecimal costoEstadiaDiario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudad") // FK que referencia a ciudad(id_ciudad)
    private Ciudad ciudad;
}
