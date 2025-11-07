package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.repositories.SolicitudRepository;
import com.microservicio.solicitudes.repositories.ContenedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository repo;
    private final ContenedorRepository contRepo;
    private final RutasApiClient rutasApiClient;

    public SolicitudService(SolicitudRepository repo, ContenedorRepository contRepo, RutasApiClient rutasApiClient) {
        this.repo = repo;
        this.contRepo = contRepo;
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

//    // ✅ Asigna un contenedor si no está usado por otra solicitud activa
//    public Solicitud asignarContenedor(Long idSolicitud, Long idContenedor) {
//        Solicitud solicitud = repo.findById(idSolicitud)
//                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
//        Contenedor contenedor = contRepo.findById(idContenedor)
//                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));
//
//        // ⚠️ Regla: no se puede usar un contenedor que ya esté en otra solicitud activa
//        boolean ocupado = repo.existsByContenedorAndEstadoSolicitud(contenedor, "activa");
//        if (ocupado) {
//            throw new RuntimeException("El contenedor ya está asignado a otra solicitud activa");
//        }
//
//        solicitud.setContenedor(contenedor);
//        solicitud.setEstadoSolicitud("activa");
//        return repo.save(solicitud);
//    }
//
//    // 🔄 Libera el contenedor (la solicitud deja de usarlo)
//    public Solicitud liberarContenedor(Long idSolicitud) {
//        Solicitud solicitud = repo.findById(idSolicitud)
//                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
//
//        solicitud.setContenedor(null);
//        solicitud.setEstadoSolicitud("finalizada");
//        return repo.save(solicitud);
//    }
}
