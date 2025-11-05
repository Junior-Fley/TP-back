package com.microservicio.solicitudes.services;


import com.microservicio.solicitudes.models.Estado;
import com.microservicio.solicitudes.repositories.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoService {

    private final EstadoRepository repo;

    public List<Estado> listar() {
        return repo.findAll();
    }

    public Estado obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Estado crear(Estado estado) {
        return repo.save(estado);
    }

//    public Estado actualizar(Long id, Estado actualizado) {
//        Estado existente = repo.findById(id).orElseThrow(() ->
//                new RuntimeException("Estado no encontrado con ID " + id));
//        existente.setNombre(actualizado.getNombre());
//        return repo.save(existente);
//    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
