package com.microservicio.solicitudes.repositories;

import com.microservicio.solicitudes.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByMail(String mail);

    boolean existsByDni(String dni);

    boolean existsByMail(String mail);
}

