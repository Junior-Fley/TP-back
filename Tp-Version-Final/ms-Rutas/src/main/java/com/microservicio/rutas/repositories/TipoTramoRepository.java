package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.TipoTramo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoTramoRepository extends JpaRepository <TipoTramo, Long> {
    Optional<TipoTramo> findByNombre(String nombre);
}
