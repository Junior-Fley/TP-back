package com.microservicio.rutas.services;

import com.microservicio.rutas.clients.CamionesApiClient;
import com.microservicio.rutas.clients.TarifasApiClient;
import com.microservicio.rutas.dtos.CamionDTO;
import com.microservicio.rutas.dtos.CostoEntregaDTO;
import com.microservicio.rutas.dtos.CostoRealDTO;
import com.microservicio.rutas.dtos.FinalizarTramoDTO;
import com.microservicio.rutas.dtos.IniciarTramoDTO;
import com.microservicio.rutas.models.Deposito;
import com.microservicio.rutas.models.EstadoTramo;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.repositories.EstadoTramoRepository;
import com.microservicio.rutas.repositories.TramoRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TramoService {

    private final TramoRepository repo;
    private final CamionesApiClient camionesApiClient;
    private final TarifasApiClient tarifasApiClient;
    private final EstadoTramoRepository estadoTramoRepository;

    public List<Tramo> obtenerTodos() {
        return repo.findAll();
    }

    public Tramo crear(Tramo t) {
        return repo.save(t);
    }

    /**
     * Actualiza cualquier dato de un tramo
     */
    @Transactional
    public Tramo actualizar(Long id, Tramo tramoActualizado) {
        Tramo tramoExistente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + id));

        // Actualizar coordenadas (solo si no son 0.0 que es el valor por defecto)
        if (tramoActualizado.getLatitudOrigen() != 0.0) {
            tramoExistente.setLatitudOrigen(tramoActualizado.getLatitudOrigen());
        }
        if (tramoActualizado.getLongitudOrigen() != 0.0) {
            tramoExistente.setLongitudOrigen(tramoActualizado.getLongitudOrigen());
        }
        if (tramoActualizado.getLatitudDestino() != 0.0) {
            tramoExistente.setLatitudDestino(tramoActualizado.getLatitudDestino());
        }
        if (tramoActualizado.getLongitudDestino() != 0.0) {
            tramoExistente.setLongitudDestino(tramoActualizado.getLongitudDestino());
        }
        if (tramoActualizado.getDistanciaKm() != null && tramoActualizado.getDistanciaKm() != 0.0) {
            tramoExistente.setDistanciaKm(tramoActualizado.getDistanciaKm());
        }
        if (tramoActualizado.getCostoAproximado() != null) {
            tramoExistente.setCostoAproximado(tramoActualizado.getCostoAproximado());
        }
        if (tramoActualizado.getCostoReal() != null) {
            tramoExistente.setCostoReal(tramoActualizado.getCostoReal());
        }
        if (tramoActualizado.getFechaHoraInicio() != null) {
            tramoExistente.setFechaHoraInicio(tramoActualizado.getFechaHoraInicio());
        }
        if (tramoActualizado.getFechaHoraFin() != null) {
            tramoExistente.setFechaHoraFin(tramoActualizado.getFechaHoraFin());
        }
        if (tramoActualizado.getIdCamion() != null) {
            tramoExistente.setIdCamion(tramoActualizado.getIdCamion());
        }
        if (tramoActualizado.getEstado() != null && tramoActualizado.getEstado().getNombre() != null) {
            // ⚠️ FIX: Buscar o crear el estado por NOMBRE, no por ID
            String nombreEstado = tramoActualizado.getEstado().getNombre();
            EstadoTramo estadoGestionado = obtenerOCrearEstado(nombreEstado);
            tramoExistente.setEstado(estadoGestionado);
            log.info("✅ Estado actualizado a: {}", nombreEstado);
        }
        if (tramoActualizado.getTipoTramo() != null) {
            tramoExistente.setTipoTramo(tramoActualizado.getTipoTramo());
        }
        if (tramoActualizado.getDepositoOrigen() != null) {
            tramoExistente.setDepositoOrigen(tramoActualizado.getDepositoOrigen());
        }
        if (tramoActualizado.getDepositoDestino() != null) {
            tramoExistente.setDepositoDestino(tramoActualizado.getDepositoDestino());
        }
        if (tramoActualizado.getRuta() != null) {
            tramoExistente.setRuta(tramoActualizado.getRuta());
        }

        return repo.save(tramoExistente);
    }

    public Optional<Tramo> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /**
     * Asigna un camión a un tramo específico.
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

        // 4. Actualizar estado a "asignado"
        EstadoTramo estadoAsignado = obtenerOCrearEstado("asignado");
        tramo.setEstado(estadoAsignado);

        return repo.save(tramo);
    }

    /**
     * Registra el inicio de un tramo (Transportista)
     */
    @Transactional
    public Tramo iniciarTramo(Long idTramo, IniciarTramoDTO dto) {
        log.info("🚚 Iniciando tramo ID: {}", idTramo);

        Tramo tramo = repo.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + idTramo));

        // Validar que el tramo tiene camión asignado
        if (tramo.getIdCamion() == null) {
            throw new RuntimeException("El tramo no tiene un camión asignado");
        }

        // Validar que el tramo está en estado "asignado"
        if (tramo.getEstado() == null || !"asignado".equalsIgnoreCase(tramo.getEstado().getNombre())) {
            throw new RuntimeException("El tramo debe estar en estado 'asignado' para poder iniciarse");
        }

        // Registrar fecha de inicio
        LocalDateTime fechaInicio = dto.getFechaHoraInicio() != null
            ? dto.getFechaHoraInicio()
            : LocalDateTime.now();
        tramo.setFechaHoraInicio(fechaInicio);

        // Actualizar estado a "iniciado"
        EstadoTramo estadoIniciado = obtenerOCrearEstado("iniciado");
        tramo.setEstado(estadoIniciado);

        // Marcar camión como NO disponible
        try {
            camionesApiClient.actualizarDisponibilidad(tramo.getIdCamion(), false);
            log.info("✅ Camión ID {} marcado como NO disponible", tramo.getIdCamion());
        } catch (Exception e) {
            log.warn("⚠️ No se pudo actualizar disponibilidad del camión: {}", e.getMessage());
        }

        Tramo tramoGuardado = repo.save(tramo);
        log.info("✅ Tramo iniciado exitosamente a las {}", fechaInicio);

        return tramoGuardado;
    }

    /**
     * Registra la finalización de un tramo y calcula el costo real (Transportista)
     */
    @Transactional
    public Tramo finalizarTramo(Long idTramo, FinalizarTramoDTO dto) {
        log.info("🏁 Finalizando tramo ID: {}", idTramo);

        Tramo tramo = repo.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + idTramo));

        // Validar que el tramo está iniciado
        if (tramo.getEstado() == null || !"iniciado".equalsIgnoreCase(tramo.getEstado().getNombre())) {
            throw new RuntimeException("El tramo debe estar en estado 'iniciado' para poder finalizarse");
        }

        if (tramo.getFechaHoraInicio() == null) {
            throw new RuntimeException("El tramo no tiene fecha de inicio registrada");
        }

        // Registrar fecha de finalización
        LocalDateTime fechaFin = dto.getFechaHoraFin() != null
            ? dto.getFechaHoraFin()
            : LocalDateTime.now();
        tramo.setFechaHoraFin(fechaFin);

        // ⭐ CALCULAR COSTO REAL ⭐
        CostoRealDTO costoReal = calcularCostoReal(tramo);
        tramo.setCostoReal(costoReal.getCostoTotal());

        // Actualizar estado a "finalizado"
        EstadoTramo estadoFinalizado = obtenerOCrearEstado("finalizado");
        tramo.setEstado(estadoFinalizado);

        // Liberar camión (disponible = true)
        if (tramo.getIdCamion() != null) {
            try {
                camionesApiClient.actualizarDisponibilidad(tramo.getIdCamion(), true);
                log.info("✅ Camión ID {} liberado (disponible)", tramo.getIdCamion());
            } catch (Exception e) {
                log.warn("⚠️ No se pudo liberar el camión: {}", e.getMessage());
            }
        }

        Tramo tramoGuardado = repo.save(tramo);
        log.info("✅ Tramo finalizado. Costo real: ${}", costoReal.getCostoTotal());
        log.info("📊 Detalle: {}", costoReal.getDetalleCalculo());

        return tramoGuardado;
    }

    /**
     * Calcula el costo real de un tramo finalizado
     */
    public CostoRealDTO calcularCostoReal(Tramo tramo) {
        log.info("💰 Calculando costo real del tramo ID: {}", tramo.getIdTramo());

        CostoRealDTO dto = new CostoRealDTO();
        dto.setIdTramo(tramo.getIdTramo());
        dto.setDistanciaKm(BigDecimal.valueOf(tramo.getDistanciaKm() != null ? tramo.getDistanciaKm() : 0));

        StringBuilder detalle = new StringBuilder();
        BigDecimal costoTotal = BigDecimal.ZERO;

        // 1. Obtener información del camión
        BigDecimal costoBaseKm = BigDecimal.ZERO;
        BigDecimal consumoCombustibleKm = BigDecimal.ZERO;

        if (tramo.getIdCamion() != null) {
            try {
                CamionDTO camion = camionesApiClient.obtenerCamion(tramo.getIdCamion());
                costoBaseKm = BigDecimal.valueOf(camion.getCostoBaseKm());
                consumoCombustibleKm = BigDecimal.valueOf(camion.getConsumoCombustibleKm());
                dto.setCostoBaseKmCamion(costoBaseKm);
                dto.setConsumoCombustibleKm(consumoCombustibleKm);
            } catch (Exception e) {
                log.warn("⚠️ No se pudo obtener datos del camión, usando valores por defecto");
            }
        }

        // 2. Costo por kilometraje (costo base del camión × distancia)
        BigDecimal costoKilometraje = costoBaseKm
                .multiply(dto.getDistanciaKm())
                .setScale(2, RoundingMode.HALF_UP);
        dto.setCostoKilometraje(costoKilometraje);
        costoTotal = costoTotal.add(costoKilometraje);
        detalle.append(String.format("Kilometraje: $%.2f/km × %.2f km = $%.2f | ",
            costoBaseKm, dto.getDistanciaKm(), costoKilometraje));

        // 3. Costo de combustible (consumo × distancia × precio litro)
        BigDecimal precioCombustible = tarifasApiClient.obtenerValorTarifa("COMBUSTIBLE");
        dto.setPrecioCombustible(precioCombustible);

        BigDecimal litrosConsumidos = consumoCombustibleKm.multiply(dto.getDistanciaKm());
        BigDecimal costoCombustible = litrosConsumidos
                .multiply(precioCombustible)
                .setScale(2, RoundingMode.HALF_UP);
        dto.setCostoCombustible(costoCombustible);
        costoTotal = costoTotal.add(costoCombustible);
        detalle.append(String.format("Combustible: %.2f L/km × %.2f km × $%.2f/L = $%.2f | ",
            consumoCombustibleKm, dto.getDistanciaKm(), precioCombustible, costoCombustible));

        // 4. Costo de estadía en depósito (si aplica)
        BigDecimal costoEstadia = BigDecimal.ZERO;
        int diasEstadia = 0;

        if (tramo.getDepositoDestino() != null && tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
            Deposito deposito = tramo.getDepositoDestino();

            // Calcular días de estadía (diferencia entre fechas)
            diasEstadia = (int) ChronoUnit.DAYS.between(
                tramo.getFechaHoraInicio().toLocalDate(),
                tramo.getFechaHoraFin().toLocalDate()
            );

            if (diasEstadia < 1) {
                diasEstadia = 1; // Mínimo 1 día
            }

            BigDecimal costoEstadiaDiario = deposito.getCostoEstadiaDiario() != null
                ? deposito.getCostoEstadiaDiario()
                : tarifasApiClient.obtenerValorTarifa("ESTADIA_DEPOSITO");

            costoEstadia = costoEstadiaDiario
                    .multiply(BigDecimal.valueOf(diasEstadia))
                    .setScale(2, RoundingMode.HALF_UP);

            dto.setDiasEstadia(diasEstadia);
            dto.setCostoEstadiaDiario(costoEstadiaDiario);
            dto.setCostoEstadia(costoEstadia);
            costoTotal = costoTotal.add(costoEstadia);

            detalle.append(String.format("Estadía en %s: %d días × $%.2f/día = $%.2f | ",
                deposito.getNombre(), diasEstadia, costoEstadiaDiario, costoEstadia));
        }

        // 5. Cargo de gestión por tramo
        BigDecimal cargoGestion = tarifasApiClient.obtenerValorTarifa("CARGO_GESTION_TRAMO");
        dto.setCargoGestion(cargoGestion);
        costoTotal = costoTotal.add(cargoGestion);
        detalle.append(String.format("Gestión: $%.2f", cargoGestion));

        // Total
        dto.setCostoTotal(costoTotal);
        dto.setDetalleCalculo(detalle.toString());

        log.info("💰 Costo real calculado: ${}", costoTotal);
        return dto;
    }

    /**
     * Calcula el costo total de entrega para una ruta (Requerimiento funcional 8)
     */
    public CostoEntregaDTO calcularCostoEntrega(Long idRuta) {
        // 1️⃣ Buscar todos los tramos de la ruta
        List<Tramo> tramos = repo.findByRutaIdRuta(idRuta);

        if (tramos.isEmpty()) {
            throw new RuntimeException("No se encontraron tramos para la ruta " + idRuta);
        }

        // 2️⃣ Calcular distancia total
        double distanciaTotal = tramos.stream()
                .mapToDouble(t -> t.getDistanciaKm() != null ? t.getDistanciaKm() : 0.0)
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

    /**
     * Obtiene o crea un estado de tramo por nombre
     */
    private EstadoTramo obtenerOCrearEstado(String nombre) {
        return estadoTramoRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    EstadoTramo nuevoEstado = new EstadoTramo();
                    nuevoEstado.setNombre(nombre);
                    return estadoTramoRepository.save(nuevoEstado);
                });
    }
}
