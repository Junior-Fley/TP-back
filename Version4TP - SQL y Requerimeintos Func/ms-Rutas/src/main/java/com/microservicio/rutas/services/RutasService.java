package com.microservicio.rutas.services;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.dtos.TramoDTO;
import com.microservicio.rutas.models.Deposito;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.repositories.RutasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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

        BigDecimal costoAproximado = BigDecimal.ZERO;
        String tiempoEstimado = "0 horas";
        List<TramoDTO> tramosDTO = null;

        if (ruta.getTramos() != null && !ruta.getTramos().isEmpty()) {
            costoAproximado = ruta.getTramos().stream()
                    .map(t -> t.getCostoAproximado() != null ? t.getCostoAproximado() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            tiempoEstimado = calcularTiempoEstimado(ruta.getTramos());
            tramosDTO = convertirTramosADTO(ruta.getTramos());
        }

        return new RutaResumenDTO(
                ruta.getIdRuta(),
                ruta.getCantidadTramos(),
                ruta.getCantidadDepositos(),
                costoAproximado,
                tiempoEstimado,
                tramosDTO
        );
    }

    // 🔹 Obtener todas las rutas tentativas con su resumen completo
    public List<RutaResumenDTO> obtenerRutasTentativas() {
        List<Rutas> todasLasRutas = repo.findAll();

        return todasLasRutas.stream()
                .map(ruta -> {
                    BigDecimal costoAproximado = BigDecimal.ZERO;
                    String tiempoEstimado = "0 horas";
                    List<TramoDTO> tramosDTO = null;

                    if (ruta.getTramos() != null && !ruta.getTramos().isEmpty()) {
                        costoAproximado = ruta.getTramos().stream()
                                .map(t -> t.getCostoAproximado() != null ? t.getCostoAproximado() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        tiempoEstimado = calcularTiempoEstimado(ruta.getTramos());
                        tramosDTO = convertirTramosADTO(ruta.getTramos());
                    }

                    return new RutaResumenDTO(
                            ruta.getIdRuta(),
                            ruta.getCantidadTramos(),
                            ruta.getCantidadDepositos(),
                            costoAproximado,
                            tiempoEstimado,
                            tramosDTO
                    );
                })
                .collect(Collectors.toList());
    }

    private String calcularTiempoEstimado(List<Tramo> tramos) {
        long totalHoras = 0;

        for (Tramo tramo : tramos) {
            if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
                Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
                totalHoras += duracion.toHours();
            } else {
                if (tramo.getCostoAproximado() != null) {
                    totalHoras += 2;
                }
            }
        }

        if (totalHoras == 0) {
            totalHoras = tramos.size() * 2L;
        }

        return totalHoras + " horas";
    }

    private List<TramoDTO> convertirTramosADTO(List<Tramo> tramos) {
        return tramos.stream()
                .map(tramo -> {
                    String origen = obtenerNombreUbicacion(tramo.getDepositoOrigen());
                    String destino = obtenerNombreUbicacion(tramo.getDepositoDestino());
                    String tipoTramo = tramo.getTipoTramo() != null ? tramo.getTipoTramo().getNombre() : "No especificado";

                    BigDecimal distancia = calcularDistancia(
                            tramo.getLatitudOrigen(),
                            tramo.getLongitudOrigen(),
                            tramo.getLatitudDestino(),
                            tramo.getLongitudDestino()
                    );

                    String tiempoTramo;
                    if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
                        Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
                        tiempoTramo = duracion.toHours() + " horas";
                    } else {
                        long horas = distancia.divide(BigDecimal.valueOf(80), 0, RoundingMode.UP).longValue();
                        tiempoTramo = horas + " horas";
                    }

                    return new TramoDTO(
                            tramo.getIdTramo(),
                            origen,
                            destino,
                            tipoTramo,
                            distancia,
                            tramo.getCostoAproximado(),
                            tiempoTramo
                    );
                })
                .collect(Collectors.toList());
    }

    private String obtenerNombreUbicacion(Deposito deposito) {
        if (deposito == null) {
            return "Ubicación no especificada";
        }

        if (deposito.getCiudad() != null) {
            return deposito.getCiudad().getNombre();
        }

        return "Depósito #" + deposito.getIdDeposito();
    }

    private BigDecimal calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final double RADIO_TIERRA_KM = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = RADIO_TIERRA_KM * c;

        return BigDecimal.valueOf(distancia).setScale(2, RoundingMode.HALF_UP);
    }
}
