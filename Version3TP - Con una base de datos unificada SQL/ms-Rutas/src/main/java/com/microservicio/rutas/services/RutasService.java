package com.microservicio.rutas.services;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.repositories.RutasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutasService {

    private final RutasRepository repo;

    // 🔹 Obtener todas las rutas
    public List<Rutas> obtenerTodas() {
        return repo.findAll();
    }

    // 🔹 Obtener una ruta por su ID
    public Rutas obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    // 🔹 Crear una nueva ruta
    public Rutas crear(Rutas ruta) {
        return repo.save(ruta);
    }

    // 🔹 Eliminar una ruta
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // 🔹 Obtener resumen de una ruta (para comunicación entre microservicios)
    public RutaResumenDTO obtenerResumen(Long id) {
        Rutas ruta = repo.findById(id).orElse(null);
        if (ruta == null) {
            return null;
        }

        // Calcular el costo aproximado de todos los tramos
        BigDecimal costoAproximado = BigDecimal.ZERO;
        if (ruta.getTramos() != null && !ruta.getTramos().isEmpty()) {
            costoAproximado = ruta.getTramos().stream()
                    .map(t -> t.getCostoAproximado() != null ? t.getCostoAproximado() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return new RutaResumenDTO(
                ruta.getIdRuta(),
                ruta.getCantidadTramos(),
                ruta.getCantidadDepositos(),
                costoAproximado
        );
    }

//    // 🔹 Calcular el costo total aproximado de todos los tramos de una ruta
//    public BigDecimal calcularCostoAproximado(Rutas ruta) {
//        if (ruta.getTramos() == null || ruta.getTramos().isEmpty()) {
//            return BigDecimal.ZERO;
//        }
//
//        return ruta.getTramos().stream()
//                .map(t -> t.getCostoAproximado() != null ? t.getCostoAproximado() : BigDecimal.ZERO)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
}
