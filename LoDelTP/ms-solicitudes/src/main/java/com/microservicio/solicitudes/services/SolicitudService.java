package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.repositories.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository repo;

    public SolicitudService(SolicitudRepository repo) {
        this.repo = repo;
    }

    public List<Solicitud> obtenerTodas() {
        return repo.findAll();
    }

    public Solicitud crear(Solicitud solicitud) {
        return repo.save(solicitud);
    }

    public Solicitud obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
