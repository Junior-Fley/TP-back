package com.microservicio.solicitudes.dtos;

import lombok.Data;

@Data
public class SolicitudRequestDTO {
    // Datos del contenedor
    private Double pesoContenedor;
    private Double volumenContenedor;

    // Datos del cliente (si no existe se crea)
    private String nombreCliente;
    private String apellidoCliente;
    private String dniCliente;
    private String telefonoCliente;
    private String mailCliente;
    private String direccionCliente;

    // Estado inicial (por defecto: "borrador")
    private String estadoInicial; // borrador, programada, en tránsito, entregada
}

