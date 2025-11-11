package com.microservicio.rutas.services;

import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.repositories.TramoRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor

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

    /**
     * Obtener tramos por ruta
     */
    public List<Tramo> obtenerTramosPorRuta(Long idRuta) {
}
        List<Tramo> tramos = repo.findByDepositoDestinoIdDeposito(idDeposito);


