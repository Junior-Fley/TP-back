package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.EstadoTramo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoTramoRepository extends JpaRepository <EstadoTramo, Long> {
}
