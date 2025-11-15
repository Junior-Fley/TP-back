package com.microservicio.rutas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rutas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long idRuta;

    @Column(name = "id_solicitud")
    private Long idSolicitud; // FK lógica hacia el microservicio de Solicitudes

    @Column(name = "cantidad_tramos")
    private Integer cantidadTramos;

    @Column(name = "cantidad_depositos")
    private Integer cantidadDepositos;

    @Column(name = "distancia_total_km")
    private Double distanciaTotal;

    @Column(name = "tiempo_estimado_min")
    private Double  tiempoEstimadoMin;

    @Column(name = "costo_total")
    private Double costoTotal;

    // Relación bidireccional con TRAMO
    // mappedBy = "ruta" indica que la FK está en la tabla tramo
    @JsonManagedReference
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tramo> tramos;
}
