package com.microservicio.solicitudes.repositories;

import com.microservicio.solicitudes.models.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {}
