package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.constants.EstadoSolicitud;
import com.microservicio.solicitudes.models.Estado;
import com.microservicio.solicitudes.repositories.EstadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadoService {

    private final EstadoRepository repo;

    public List<Estado> listar() {
        return repo.findAll();
    }

    public Estado obtenerPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + id));
    }

    public Estado crear(Estado estado) {
        return repo.save(estado);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public Estado obtenerPorNombre(String nombre) {
        return repo.findByNombre(nombre).orElse(null);
    }

    /**
     * ⭐ REFACTORIZADO: Obtiene un estado existente por ID
     * Los estados son fijos en la BD:
     * - ID 1 = "disponible"
     * - ID 2 = "pendiente_entrega"
     * - ID 3 = "en_transito"
     * - ID 4 = "entregado"
     *
     * NO se crean nuevos estados dinámicamente.
     */
    public Estado obtenerEstadoDisponible() {
        return obtenerPorId(1L); // Estado fijo: disponible
    }

    public Estado obtenerEstadoPendienteEntrega() {
        return obtenerPorId(2L); // Estado fijo: pendiente_entrega
    }

    public Estado obtenerEstadoEnTransito() {
        return obtenerPorId(3L); // Estado fijo: en_transito
    }

    public Estado obtenerEstadoEntregado() {
        return obtenerPorId(4L); // Estado fijo: entregado
    }

    /**
     * @deprecated Use obtenerEstadoDisponible(), obtenerEstadoPendienteEntrega(),
     *             obtenerEstadoEnTransito() o obtenerEstadoEntregado()
     *             Este método se mantiene solo para compatibilidad temporal
     */
    @Deprecated
    public Estado obtenerOCrearPorNombre(String nombre) {
        log.warn("⚠️ Método obsoleto obtenerOCrearPorNombre('{}') - Use los métodos específicos por ID", nombre);

        // Mapear nombres a IDs fijos
        if (EstadoSolicitud.DISPONIBLE.equalsIgnoreCase(nombre)) {
            return obtenerEstadoDisponible();
        } else if (EstadoSolicitud.PENDIENTE_ENTREGA.equalsIgnoreCase(nombre)) {
            return obtenerEstadoPendienteEntrega();
        } else if (EstadoSolicitud.EN_TRANSITO.equalsIgnoreCase(nombre)) {
            return obtenerEstadoEnTransito();
        } else if (EstadoSolicitud.ENTREGADO.equalsIgnoreCase(nombre)) {
            return obtenerEstadoEntregado();
        }

        throw new IllegalArgumentException(
                "Estado inválido: '" + nombre + "'. " +
                        "Use: disponible, pendiente_entrega, en_transito, o entregado");
    }
}
