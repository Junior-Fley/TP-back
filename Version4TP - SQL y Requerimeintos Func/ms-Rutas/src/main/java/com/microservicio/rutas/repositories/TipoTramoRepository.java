package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.TipoTramo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoTramoRepository extends JpaRepository <TipoTramo, Long> {
}

