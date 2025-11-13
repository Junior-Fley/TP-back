package com.microservicio.rutas.services;

import com.microservicio.rutas.clients.CamionesApiClient;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.repositories.TramoRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TramoService {

    private final TramoRepository repo;
    private final CamionesApiClient camionesApiClient;

    public List<Tramo> obtenerTodos() {
        return repo.findAll();
    }

    public Tramo crear(Tramo t) {
        return repo.save(t);
    }

    public Optional<Tramo> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /**
     * Asigna un camión a un tramo específico.
     * Valida que tanto el tramo como el camión existan antes de realizar la asignación.
     * @param idTramo ID del tramo
     * @param idCamion ID del camión a asignar
     * @return Tramo actualizado con el camión asignado
     * @throws RuntimeException si el tramo no existe o el camión no existe en el microservicio de Transporte
     */
    public Tramo asignarCamion(Long idTramo, Long idCamion) {
        // 1. Verificar que el tramo existe
        Tramo tramo = repo.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + idTramo));

        // 2. Verificar que el camión existe en el microservicio de Transporte
        if (!camionesApiClient.existeCamion(idCamion)) {
            throw new RuntimeException("Camión no encontrado con ID: " + idCamion + " en el microservicio de Transporte");
        }

        // 3. Asignar el camión al tramo
        tramo.setIdCamion(idCamion);
        return repo.save(tramo);
    }
}
