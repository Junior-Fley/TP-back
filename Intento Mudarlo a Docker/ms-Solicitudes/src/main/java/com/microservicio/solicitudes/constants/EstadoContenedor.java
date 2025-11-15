package com.microservicio.solicitudes.constants;

/**
 * Constantes para los estados del contenedor
 */
public class EstadoContenedor {
    public static final String DISPONIBLE = "disponible";
    public static final String PENDIENTE_ENTREGA = "pendiente de entrega";
    public static final String EN_TRANSITO = "en tránsito";
    public static final String ENTREGADO = "entregado";

    private EstadoContenedor() {
        // Clase de constantes - no instanciable
    }
}
