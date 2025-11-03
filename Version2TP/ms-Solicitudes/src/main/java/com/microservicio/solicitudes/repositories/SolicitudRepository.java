package com.microservicio.solicitudes.repositories;

import com.microservicio.solicitudes.models.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // 🔹 Consultas automáticas (Spring las genera a partir del nombre del método)
    List<Solicitud> findByIdCliente(Long idCliente);

    List<Solicitud> findByEstadoSolicitud(String estadoSolicitud);
}
