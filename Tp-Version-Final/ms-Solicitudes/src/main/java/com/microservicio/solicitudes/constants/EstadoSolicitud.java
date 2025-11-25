package com.microservicio.solicitudes.constants;

/**
 * Constantes para los estados de la solicitud
 * Estados válidos y únicos permitidos en el sistema
 * 
 * Flujo de estados:
 * 1. disponible -> 2. pendiente_entrega -> 3. en_transito -> 4. entregado
 */
public class EstadoSolicitud {
    public static final String DISPONIBLE = "disponible";
    public static final String PENDIENTE_ENTREGA = "pendiente_entrega";
    public static final String EN_TRANSITO = "en_transito";
    public static final String ENTREGADO = "entregado";

    private EstadoSolicitud() {
        // Clase de constantes - no instanciable
    }

    /**
     * Valida si un estado es válido
     */
    public static boolean esValido(String estado) {
        return DISPONIBLE.equalsIgnoreCase(estado) ||
                PENDIENTE_ENTREGA.equalsIgnoreCase(estado) ||
                EN_TRANSITO.equalsIgnoreCase(estado) ||
                ENTREGADO.equalsIgnoreCase(estado);
    }
}
