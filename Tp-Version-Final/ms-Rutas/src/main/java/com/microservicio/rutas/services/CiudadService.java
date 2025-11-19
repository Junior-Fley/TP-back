package com.microservicio.rutas.services;

import com.microservicio.rutas.dtos.CiudadDTO;
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

    // Crear una nueva ciudad (desde DTO)
    public Ciudad crearCiudad(CiudadDTO dto) {
        // Validaciones básicas
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la ciudad es obligatorio");
        }
        Ciudad ciudad = new Ciudad();
        ciudad.setNombre(dto.getNombre().trim());
        return repository.save(ciudad);
    }

    // Actualizar ciudad existente (desde DTO)
    public Ciudad actualizarCiudad(Long id, CiudadDTO dto) {
        return repository.findById(id)
                .map(ciudad -> {
                    if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
                        ciudad.setNombre(dto.getNombre().trim());
                    }
                    return repository.save(ciudad);
                })
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada con id " + id));
    }

    // Crear una nueva ciudad (compatibilidad con el uso anterior)
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
