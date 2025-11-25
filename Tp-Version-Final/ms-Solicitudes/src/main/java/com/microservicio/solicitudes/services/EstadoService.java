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
     * - ID 2 = "en proceso"
     * - ID 3 = "completada"
     *
     * NO se crean nuevos estados dinámicamente.
     */
    public Estado obtenerEstadoDisponible() {
        return obtenerPorId(1L); // Estado fijo: disponible
    }

    public Estado obtenerEstadoEnProceso() {
        return obtenerPorId(3L); // Estado fijo: en proceso
    }

    public Estado obtenerEstadoCompletada() {
        return obtenerPorId(4L); // Estado fijo: completada
    }

    /**
     * @deprecated Use obtenerEstadoDisponible(), obtenerEstadoEnProceso() o
     *             obtenerEstadoCompletada()
     *             Este método se mantiene solo para compatibilidad temporal
     */
    @Deprecated
    public Estado obtenerOCrearPorNombre(String nombre) {
        log.warn("⚠️ Método obsoleto obtenerOCrearPorNombre('{}') - Use los métodos específicos por ID", nombre);

        // Mapear nombres a IDs fijos
        if (EstadoSolicitud.DISPONIBLE.equalsIgnoreCase(nombre)) {
            return obtenerEstadoDisponible();
        } else if (EstadoSolicitud.EN_PROCESO.equalsIgnoreCase(nombre)) {
            return obtenerEstadoEnProceso();
        } else if (EstadoSolicitud.COMPLETADA.equalsIgnoreCase(nombre)) {
            return obtenerEstadoCompletada();
        }

        throw new IllegalArgumentException(
                "Estado inválido: '" + nombre + "'. " +
                        "Use: disponible, en proceso, o completada");
    }
}
