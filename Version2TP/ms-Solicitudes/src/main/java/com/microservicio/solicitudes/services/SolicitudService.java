package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.repositories.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudService {


    private final SolicitudRepository repo;
    private final RutasApiClient rutasApiClient;

    public SolicitudService(SolicitudRepository repo, RutasApiClient rutasApiClient) {
        this.repo = repo;
        this.rutasApiClient = rutasApiClient;
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

    public void procesarSolicitud(Long idRuta) {
        String json = rutasApiClient.obtenerRutaRaw(idRuta);
        System.out.println("Respuesta de la API de rutas: " + json);
    }
}
