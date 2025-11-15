package com.microservicio.solicitudes.repositories;

import com.microservicio.solicitudes.models.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
    // Buscar contenedores por estado
    List<Contenedor> findByEstado(String estado);

    // Buscar contenedores que NO estén entregados
    List<Contenedor> findByEstadoNot(String estado);
}

