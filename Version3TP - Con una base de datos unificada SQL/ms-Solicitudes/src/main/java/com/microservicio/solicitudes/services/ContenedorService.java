package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.constants.EstadoContenedor;
import com.microservicio.solicitudes.dtos.ContenedorCreateDTO;
import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.repositories.ContenedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenedorService {

    private final ContenedorRepository repo;

    public List<Contenedor> listar() {
        return repo.findAll();
    }

    public Contenedor obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Crea un contenedor nuevo con estado inicial "disponible"
     * NO acepta ID ni estado por parámetro
     */
    public Contenedor crear(ContenedorCreateDTO dto) {
        Contenedor contenedor = new Contenedor();
        contenedor.setPeso(dto.getPeso());
        contenedor.setVolumen(dto.getVolumen());
        contenedor.setEstado(EstadoContenedor.DISPONIBLE); // Estado inicial automático
        return repo.save(contenedor);
    }

    /**
     * Actualiza solo peso y volumen de un contenedor
     * El estado NO se puede modificar directamente, se gestiona automáticamente
     */
    public Contenedor actualizar(Long id, ContenedorCreateDTO dto) {
        Contenedor existente = repo.findById(id).orElseThrow(() ->
                new RuntimeException("Contenedor no encontrado con ID " + id));
        existente.setPeso(dto.getPeso());
        existente.setVolumen(dto.getVolumen());
        // NO se permite cambiar el estado manualmente
        return repo.save(existente);
    }

    /**
     * Cambia el estado del contenedor a "pendiente de entrega"
     * Se llama cuando se asigna a una solicitud
     */
    public void cambiarEstadoPendienteEntrega(Long idContenedor) {
        Contenedor contenedor = repo.findById(idContenedor)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID " + idContenedor));
        contenedor.setEstado(EstadoContenedor.PENDIENTE_ENTREGA);
        repo.save(contenedor);
    }

    /**
     * Cambia el estado del contenedor a "en tránsito"
     * Se llama cuando arranca el primer tramo
     */
    public void cambiarEstadoEnTransito(Long idContenedor) {
        Contenedor contenedor = repo.findById(idContenedor)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID " + idContenedor));
        contenedor.setEstado(EstadoContenedor.EN_TRANSITO);
        repo.save(contenedor);
    }

    /**
     * Cambia el estado del contenedor a "entregado"
     * Se llama cuando termina el último tramo
     */
    public void cambiarEstadoEntregado(Long idContenedor) {
        Contenedor contenedor = repo.findById(idContenedor)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID " + idContenedor));
        contenedor.setEstado(EstadoContenedor.ENTREGADO);
        repo.save(contenedor);
    }

    /**
     * Cambia el estado del contenedor de vuelta a "disponible"
     * Se llama cuando se desasigna de una solicitud
     */
    public void cambiarEstadoDisponible(Long idContenedor) {
        Contenedor contenedor = repo.findById(idContenedor)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID " + idContenedor));
        contenedor.setEstado(EstadoContenedor.DISPONIBLE);
        repo.save(contenedor);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
