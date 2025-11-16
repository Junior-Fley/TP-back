package com.microservicio.rutas.services;

import com.microservicio.rutas.models.TipoTramo;
import com.microservicio.rutas.repositories.TipoTramoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoTramoService {

    private final TipoTramoRepository repository;

    public List<TipoTramo> obtenerTodos() {
        return repository.findAll();
    }
}
