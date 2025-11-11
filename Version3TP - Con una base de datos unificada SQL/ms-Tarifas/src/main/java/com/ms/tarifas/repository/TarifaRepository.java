package com.ms.tarifas.repository;

import com.ms.tarifas.entity.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Tarifa
 */
@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    /**
     * Busca una tarifa por su tipo
     */
    Optional<Tarifa> findByTipo(String tipo);

    /**
     * Busca todas las tarifas activas
     */
    List<Tarifa> findByActivoTrue();

    /**
     * Verifica si existe una tarifa por tipo
     */
    boolean existsByTipo(String tipo);
}

