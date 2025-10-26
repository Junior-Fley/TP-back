package Content.services;

import Content.models.Solicitud;
import Content.repositories.SolicitudRepository;

import java.util.List;

public class SolicitudService {
    SolicitudRepository solicitudRepository;

    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }
}
