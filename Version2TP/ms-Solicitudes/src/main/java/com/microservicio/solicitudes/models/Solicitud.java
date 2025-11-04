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

    @Column(name = "id_contenedor")
    private Long idContenedor;

    @Column(name = "id_cliente")
    private Integer idCliente;

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

    @Column(name = "estado_solicitud")
    private String estadoSolicitud;
}
