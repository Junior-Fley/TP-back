package com.microservicio.rutas.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.microservicio.rutas.config.LocalDateTimeEpochAttributeConverter;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tramo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long idTramo;

    @Column(name = "latitud_origen")
    private double latitudOrigen;
    @Column(name = "longitud_origen")
    private double longitudOrigen;
    @Column(name = "latitud_destino")
    private double latitudDestino;
    @Column(name = "longitud_destino")
    private double longitudDestino;

    @Column(name = "costo_aproximado", precision = 10, scale = 2)
    private BigDecimal costoAproximado;
    @Column(name = "costo_real", precision = 10, scale = 2)
    private BigDecimal costoReal;

    @Column(name = "fecha_hora_inicio")
    @Convert(converter = LocalDateTimeEpochAttributeConverter.class)
    private LocalDateTime fechaHoraInicio;
    @Column(name = "fecha_hora_fin")
    @Convert(converter = LocalDateTimeEpochAttributeConverter.class)
    private LocalDateTime fechaHoraFin;

    @Column(name = "id_camion")
    private Long idCamion; // FK lógica, sin relación directa aún
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_tramo")
    private TipoTramo tipoTramo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    private EstadoTramo estado;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta")
    private Rutas ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_deposito_origen")
    private Deposito depositoOrigen;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_deposito_destino")
    private Deposito depositoDestino;


    @Column(name = "distancia_km")
    private Double distanciaKm;

}
