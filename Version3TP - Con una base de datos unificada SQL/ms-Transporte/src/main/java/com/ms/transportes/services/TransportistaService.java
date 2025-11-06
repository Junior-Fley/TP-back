package com.ms.transportes.services;

import com.ms.transportes.models.Transportista;
import com.ms.transportes.repositories.TransportistaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransportistaService {

    private final TransportistaRepository repo;

    public TransportistaService(TransportistaRepository repo) {
        this.repo = repo;
    }

    public List<Transportista> obtenerTodos() {
        return repo.findAll();
    }

    public Transportista obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Transportista guardar(Transportista transportista) {
        return repo.save(transportista);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
