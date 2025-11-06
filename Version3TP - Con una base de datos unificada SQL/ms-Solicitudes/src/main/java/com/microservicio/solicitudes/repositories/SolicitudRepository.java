package com.microservicio.solicitudes.repositories;

import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.models.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // 🔹 Consultas automáticas (Spring las genera a partir del nombre del método)
    // Antes: List<Solicitud> findByIdCliente(Long idCliente);
    // Ahora: navegar la propiedad 'cliente.idCliente'
//    List<Solicitud> findByClienteIdCliente(Long idCliente);
//
//    List<Solicitud> findByEstadoSolicitud(String estadoSolicitud);
//
//    boolean existsByContenedorAndEstadoSolicitud(Contenedor contenedor, String estadoSolicitud);

}
