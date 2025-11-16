package com.microservicio.rutas.repositories;

import com.microservicio.rutas.models.Rutas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RutasRepository extends JpaRepository<Rutas, Long> {

    @Query("SELECT r FROM Rutas r LEFT JOIN FETCH r.tramos WHERE r.idRuta = :idRuta")
    Optional<Rutas> findByIdWithTramos(@Param("idRuta") Long idRuta);

}
