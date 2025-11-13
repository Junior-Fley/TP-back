package com.ms.transportes.services;


import com.ms.transportes.models.Camion;
import com.ms.transportes.repositories.CamionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional  // ✅ AGREGADO: Manejar transacciones a nivel de clase
public class CamionService {

    private final CamionRepository repo;

    public CamionService(CamionRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)  // ✅ AGREGADO: Optimizar lecturas
    public List<Camion> obtenerTodos() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)  // ✅ AGREGADO: Optimizar lecturas
    public Camion obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Camion guardar(Camion camion) {
        return repo.save(camion);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /**
     * Actualiza la disponibilidad de un camión
     */
    public Camion actualizarDisponibilidad(Long id, boolean disponible) {
        Camion camion = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado con ID: " + id));

        camion.setDisponibilidad(disponible);
        return repo.save(camion);
    }
}
