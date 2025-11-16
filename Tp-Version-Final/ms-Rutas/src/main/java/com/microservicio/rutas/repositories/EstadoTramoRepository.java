package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.EstadoTramo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoTramoRepository extends JpaRepository <EstadoTramo, Long> {
    Optional<EstadoTramo> findByNombre(String nombre);
}
