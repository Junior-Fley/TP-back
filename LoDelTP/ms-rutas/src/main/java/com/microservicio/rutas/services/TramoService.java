package com.microservicio.rutas.services;

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
}
