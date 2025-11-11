package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.Rutas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutasRepository extends JpaRepository<Rutas, Long> {

}
