package com.microservicio.solicitudes.repositories;

import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.models.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // Buscar solicitud por ID de contenedor
    Optional<Solicitud> findByContenedor_IdContenedor(Long idContenedor);

    // Buscar todas las solicitudes de un contenedor (por si hay varias)
    List<Solicitud> findAllByContenedor_IdContenedor(Long idContenedor);
}
