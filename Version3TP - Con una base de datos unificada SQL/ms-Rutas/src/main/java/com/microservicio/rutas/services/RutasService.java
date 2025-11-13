package com.microservicio.rutas.services;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.dtos.RutaTentativaDTO;
import com.microservicio.rutas.dtos.TramoSugeridoDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.repositories.RutasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 🔹 Requerimiento Funcional #3:
     * Consultar rutas tentativas con todos los tramos sugeridos y el tiempo y costo estimados
     * (Operador / Administrador)
     */
    public List<RutaTentativaDTO> obtenerRutasTentativas() {
        List<Rutas> rutas = repo.findAll();
        return rutas.stream()
                .map(this::convertirARutaTentativa)
                .collect(Collectors.toList());
    }

    /**
     * Obtener una ruta tentativa específica por ID
     */
    public RutaTentativaDTO obtenerRutaTentativaPorId(Long id) {
        Rutas ruta = repo.findById(id).orElse(null);
        if (ruta == null) {
            return null;
        }
        return convertirARutaTentativa(ruta);
    }

    /**
     * Convierte una entidad Rutas a RutaTentativaDTO con todos sus tramos
     */
    private RutaTentativaDTO convertirARutaTentativa(Rutas ruta) {
        // Calcular totales
        BigDecimal costoTotal = BigDecimal.ZERO;
        Double tiempoTotal = 0.0;

        List<TramoSugeridoDTO> tramosSugeridos = null;

        if (ruta.getTramos() != null && !ruta.getTramos().isEmpty()) {
            tramosSugeridos = ruta.getTramos().stream()
                    .map(this::convertirATramoSugerido)
                    .collect(Collectors.toList());

            // Calcular costo total estimado
            costoTotal = ruta.getTramos().stream()
                    .map(t -> t.getCostoAproximado() != null ? t.getCostoAproximado() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular tiempo total (si existe lógica de tiempo por tramo)
            // Por ahora usamos el tiempoEstimadoMin de la ruta si está disponible
        }

        return new RutaTentativaDTO(
                ruta.getIdRuta(),
                ruta.getCantidadTramos(),
                ruta.getCantidadDepositos(),
                ruta.getDistanciaTotal(),
                ruta.getTiempoEstimadoMin() != null ? ruta.getTiempoEstimadoMin() : tiempoTotal,
                ruta.getCostoTotal() != null ? BigDecimal.valueOf(ruta.getCostoTotal()) : costoTotal,
                tramosSugeridos
        );
    }

    /**
     * Convierte un Tramo a TramoSugeridoDTO
     */
    private TramoSugeridoDTO convertirATramoSugerido(Tramo tramo) {
        String origen = obtenerNombreDeposito(tramo.getDepositoOrigen());
        String destino = obtenerNombreDeposito(tramo.getDepositoDestino());

        // Calcular tiempo estimado en minutos si hay fechas
        Integer tiempoMinutos = null;
        if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
            long minutos = java.time.Duration.between(
                    tramo.getFechaHoraInicio(),
                    tramo.getFechaHoraFin()
            ).toMinutes();
            tiempoMinutos = (int) minutos;
        }

        return new TramoSugeridoDTO(
                tramo.getIdTramo(),
                origen,
                destino,
                tramo.getLatitudOrigen(),
                tramo.getLongitudOrigen(),
                tramo.getLatitudDestino(),
                tramo.getLongitudDestino(),
                tramo.getDistanciaKm(),
                tramo.getCostoAproximado(),
                tiempoMinutos,
                tramo.getTipoTramo() != null ? tramo.getTipoTramo().getNombre() : null,
                tramo.getEstado() != null ? tramo.getEstado().getNombre() : null,
                tramo.getFechaHoraInicio(),
                tramo.getFechaHoraFin()
        );
    }

    /**
     * Helper para obtener el nombre del depósito (si existe)
     */
    private String obtenerNombreDeposito(com.microservicio.rutas.models.Deposito deposito) {
        if (deposito == null) {
            return "Sin depósito asignado";
        }
        return deposito.getNombre() != null ? deposito.getNombre() : "Depósito ID: " + deposito.getIdDeposito();
    }

}
