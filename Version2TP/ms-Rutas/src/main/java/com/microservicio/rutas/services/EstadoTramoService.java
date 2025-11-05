package com.microservicio.rutas.services;

import com.microservicio.rutas.models.EstadoTramo;
import com.microservicio.rutas.repositories.EstadoTramoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoTramoService {

    private final EstadoTramoRepository repository;

    public List<EstadoTramo> obtenerTodos() {
        return repository.findAll();
    }
}
