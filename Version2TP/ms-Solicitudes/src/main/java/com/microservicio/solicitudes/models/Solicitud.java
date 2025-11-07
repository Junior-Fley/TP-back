package com.microservicio.solicitudes.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_solicitud")
    private Long numeroSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contenedor")
    @JsonIgnore
    private Contenedor contenedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "costo_estimado")
    private BigDecimal costoEstimado;

    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;

    @Column(name = "costo_final")
    private BigDecimal costoFinal;

    @Column(name = "tiempo_real")
    private Integer tiempoReal;

    @Column(name = "id_tarifa")
    private Integer idTarifa;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estado")
    @JsonIgnore
    private Estado estadoSolicitud;

    @Column(name = "id_ruta")
    private Long idRuta;
}
