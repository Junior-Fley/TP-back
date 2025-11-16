package com.microservicio.solicitudes.constants;

/**
 * Constantes para los estados de la solicitud
 * Estados válidos y únicos permitidos en el sistema
 */
public class EstadoSolicitud {
    public static final String DISPONIBLE = "disponible";
    public static final String EN_PROCESO = "en proceso";
    public static final String COMPLETADA = "completada";

    private EstadoSolicitud() {
        // Clase de constantes - no instanciable
    }

    /**
     * Valida si un estado es válido
     */
    public static boolean esValido(String estado) {
        return DISPONIBLE.equalsIgnoreCase(estado) ||
               EN_PROCESO.equalsIgnoreCase(estado) ||
               COMPLETADA.equalsIgnoreCase(estado);
    }
}

