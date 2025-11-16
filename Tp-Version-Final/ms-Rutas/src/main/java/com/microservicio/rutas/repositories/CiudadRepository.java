package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
}
