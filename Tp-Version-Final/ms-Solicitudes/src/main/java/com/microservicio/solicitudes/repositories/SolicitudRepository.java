package com.microservicio.solicitudes.repositories;

import com.microservicio.solicitudes.models.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    /**
     * Busca una solicitud por el ID del contenedor asociado
     * Permite consultar el estado del transporte de un contenedor específico
     */
    Solicitud findByContenedor_IdContenedor(Long idContenedor);

}
