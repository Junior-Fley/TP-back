package com.ms.transportes.repositories;


import com.ms.transportes.models.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
}
