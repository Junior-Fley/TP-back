package com.microservicio.rutas.services;

import com.microservicio.rutas.models.Ciudad;
import com.microservicio.rutas.repositories.CiudadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CiudadService {

    private final CiudadRepository repository;

    // Listar todas las ciudades
    public List<Ciudad> obtenerTodas() {
        return repository.findAll();
    }

    // Buscar una ciudad por ID
    public Optional<Ciudad> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Crear una nueva ciudad
    public Ciudad crear(Ciudad ciudad) {
        return repository.save(ciudad);
    }

    // Actualizar ciudad existente
    public Ciudad actualizar(Long id, Ciudad ciudadActualizada) {
        return repository.findById(id)
                .map(ciudad -> {
                    ciudad.setNombre(ciudadActualizada.getNombre());
                    return repository.save(ciudad);
                })
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada con id " + id));
    }

    // Eliminar ciudad por ID
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
