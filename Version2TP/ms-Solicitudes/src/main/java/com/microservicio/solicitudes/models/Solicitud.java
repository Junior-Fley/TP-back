package com.microservicio.solicitudes.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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

    @ManyToOne
    @JoinColumn(name = "id_contenedor", insertable = false, updatable = false)
    private Contenedor contenedor;

    @ManyToOne
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estado", referencedColumnName = "idEstado")
    private Estado estadoSolicitud;

    @Column(name = "id_ruta")
    private Long idRuta;
}
