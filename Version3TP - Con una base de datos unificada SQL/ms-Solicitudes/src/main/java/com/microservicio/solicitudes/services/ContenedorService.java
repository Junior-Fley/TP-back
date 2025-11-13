package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.repositories.ContenedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenedorService {

    private final ContenedorRepository repo;

    public List<Contenedor> listar() {
        return repo.findAll();
    }

    public Contenedor obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Contenedor crear(Contenedor contenedor) {
        return repo.save(contenedor);
    }

    public Contenedor actualizar(Long id, Contenedor actualizado) {
        Contenedor existente = repo.findById(id).orElseThrow(() ->
                new RuntimeException("Contenedor no encontrado con ID " + id));
        existente.setPeso(actualizado.getPeso());
        existente.setVolumen(actualizado.getVolumen());
        existente.setEstado(actualizado.getEstado());
        return repo.save(existente);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
