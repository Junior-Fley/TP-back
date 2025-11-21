package com.microservicio.solicitudes.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.microservicio.solicitudes.dtos.RutasTentativasResponseDTO;
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
    @JoinColumn(name = "id_contenedor")
    private Contenedor contenedor;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
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

    @ManyToOne
    @JoinColumn(name = "id_estado", referencedColumnName = "id_estado")
    private Estado estadoSolicitud;

    @Column(name = "id_ruta")
    private Long idRuta;

    // Coordenadas de origen
    @Column(name = "latitud_origen")
    private Double latitudOrigen;

    @Column(name = "longitud_origen")
    private Double longitudOrigen;

    // Coordenadas de destino
    @Column(name = "latitud_destino")
    private Double latitudDestino;

    @Column(name = "longitud_destino")
    private Double longitudDestino;

    // Lista temporal de rutas tentativas (no se persiste en BD)
    @Transient
    private RutasTentativasResponseDTO rutasTentativas;
}
