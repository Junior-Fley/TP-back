package com.ms.transportes.repositories;


import com.ms.transportes.models.Camion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CamionRepository extends JpaRepository<Camion, Long> {
}
