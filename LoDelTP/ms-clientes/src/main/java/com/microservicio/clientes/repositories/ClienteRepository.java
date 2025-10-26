package com.microservicio.clientes.repositories;

import com.microservicio.clientes.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // 🔹 Consultas automáticas (Spring las genera a partir del nombre del método)
    List<Cliente> findByDni(String dni);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}

