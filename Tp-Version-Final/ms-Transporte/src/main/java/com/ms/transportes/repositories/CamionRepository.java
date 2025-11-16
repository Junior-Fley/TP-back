package com.ms.transportes.repositories;


import com.ms.transportes.models.Camion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CamionRepository extends JpaRepository<Camion, Long> {

    /**
     * Busca un camión por su patente
     */
    Optional<Camion> findByPatente(String patente);
}
