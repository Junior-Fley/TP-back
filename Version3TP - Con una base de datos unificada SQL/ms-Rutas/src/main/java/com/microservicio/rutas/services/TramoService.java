package com.microservicio.rutas.services;

import com.microservicio.rutas.clients.CamionesApiClient;
import com.microservicio.rutas.dtos.CostoEntregaDTO;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.repositories.TramoRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TramoService {

    private final TramoRepository repo;
    private final CamionesApiClient camionesApiClient;

    public List<Tramo> obtenerTodos() {
        return repo.findAll();
    }

    public Tramo crear(Tramo t) {
        return repo.save(t);
    }

    public Optional<Tramo> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /**
     * Asigna un camión a un tramo específico.
     * Valida que tanto el tramo como el camión existan antes de realizar la asignación.
     * @param idTramo ID del tramo
     * @param idCamion ID del camión a asignar
     * @return Tramo actualizado con el camión asignado
     * @throws RuntimeException si el tramo no existe o el camión no existe en el microservicio de Transporte
     */
    public Tramo asignarCamion(Long idTramo, Long idCamion) {
        // 1. Verificar que el tramo existe
        Tramo tramo = repo.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + idTramo));

        // 2. Verificar que el camión existe en el microservicio de Transporte
        if (!camionesApiClient.existeCamion(idCamion)) {
            throw new RuntimeException("Camión no encontrado con ID: " + idCamion + " en el microservicio de Transporte");
        }

        // 3. Asignar el camión al tramo
        tramo.setIdCamion(idCamion);
        return repo.save(tramo);
    }

    public CostoEntregaDTO calcularCostoEntrega(Long idRuta) {
        // 1️⃣ Buscar todos los tramos de la ruta
        Optional<Tramo> tramos = repo.findById(idRuta);

        if (tramos.isEmpty()) {
            throw new RuntimeException("No se encontraron tramos para la ruta " + idRuta);
        }

        // 2️⃣ Calcular distancia total
        double distanciaTotal = tramos.stream()
                .mapToDouble(Tramo::getDistanciaKm)
                .sum();

        // 3️⃣ Calcular costo por distancia
        double costoPorKm = 10.0; // ejemplo
        double costoDistancia = distanciaTotal * costoPorKm;

        // 4️⃣ Calcular costo por peso y volumen (ejemplo fijo, normalmente viene del microservicio de transporte)
        double peso = 1000; // kg
        double volumen = 5; // m3
        double costoPesoVolumen = (peso * 0.5) + (volumen * 100);

        // 5️⃣ Calcular estadía (horas en depósitos)
        double costoPorEstadia = tramos.stream()
                .mapToDouble(t -> {
                    if (t.getFechaHoraInicio() != null && t.getFechaHoraFin() != null) {
                        long horas = Duration.between(t.getFechaHoraInicio(), t.getFechaHoraFin()).toHours();
                        return horas * 50; // $50 por hora
                    }
                    return 0.0;
                }).sum();

        // 6️⃣ Sumar todo
        double total = costoDistancia + costoPesoVolumen + costoPorEstadia;

        return new CostoEntregaDTO(distanciaTotal, costoDistancia, costoPesoVolumen, costoPorEstadia, total);
    }

}
