package com.ms.transportes.services;


import com.ms.transportes.models.Camion;
import com.ms.transportes.repositories.CamionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CamionService {

    private final CamionRepository repo;

    public CamionService(CamionRepository repo) {
        this.repo = repo;
    }

    public List<Camion> obtenerTodos() {
        return repo.findAll();
    }

    public Camion obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Camion guardar(Camion camion) {
        return repo.save(camion);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
