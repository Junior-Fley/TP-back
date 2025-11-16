package com.microservicio.solicitudes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para crear una solicitud completa con contenedor y cliente")
public class SolicitudRequestDTO {

    @Schema(description = "Peso del contenedor en kilogramos", example = "15000.0", required = true)
    private Double pesoContenedor;

    @Schema(description = "Volumen del contenedor en metros cúbicos", example = "30.0", required = true)
    private Double volumenContenedor;

    @Schema(description = "Nombre del cliente", example = "Juan", required = true)
    private String nombreCliente;

    @Schema(description = "Apellido del cliente", example = "Pérez", required = true)
    private String apellidoCliente;

    @Schema(description = "DNI del cliente (si existe, se reutiliza)", example = "12345678", required = true)
    private String dniCliente;

    @Schema(description = "Teléfono de contacto del cliente", example = "3514567890", required = true)
    private String telefonoCliente;

    @Schema(description = "Email del cliente", example = "juan.perez@email.com", required = true)
    private String mailCliente;

    @Schema(description = "Dirección completa del cliente", example = "Av. Colón 1234, Córdoba", required = true)
    private String direccionCliente;

    @Deprecated
    @Schema(description = "⚠️ DEPRECADO - Este campo se ignora. El estado siempre se asigna automáticamente como 'disponible'",
            deprecated = true,
            hidden = true)
    private String estadoInicial;
}
