package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositoRepository extends JpaRepository<Deposito, Long> {
}

