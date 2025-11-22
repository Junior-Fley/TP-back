package com.microservicio.rutas.services;

import com.microservicio.rutas.clients.CamionesApiClient;
import com.microservicio.rutas.clients.SolicitudesApiClient;

import com.microservicio.rutas.dtos.CamionDTO;
import com.microservicio.rutas.dtos.ContenedorDTO;
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
    private final TarifaService tarifaService; // Changed from TarifasApiClient
    private final SolicitudesApiClient solicitudesApiClient;
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
     * ⭐ MEJORADO: Valida que el camión puede transportar el contenedor
     */
    public Tramo asignarCamion(Long idTramo, Long idCamion) {
        log.info("🚛 Asignando camión {} a tramo {}", idCamion, idTramo);

        // 1. Verificar que el tramo existe
        Tramo tramo = repo.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + idTramo));

        // 2. Verificar que el camión existe y obtener sus datos
        CamionDTO camion = camionesApiClient.obtenerCamion(idCamion);
        if (camion == null) {
            throw new RuntimeException(
                    "Camión no encontrado con ID: " + idCamion + " en el microservicio de Transporte");
        }
        log.info("✅ Camión encontrado: Patente={}, Capacidad Peso={}kg, Capacidad Volumen={}m³",
                camion.getPatente(), camion.getCapacidadPeso(), camion.getCapacidadVolumen());

        // 3. ⭐ NUEVO: Verificar que el camión está disponible
        if (!camion.isDisponibilidad()) {
            throw new RuntimeException("El camión " + camion.getPatente() +
                    " no está disponible. Debe estar libre para asignarse a un tramo.");
        }
        log.info("✅ Camión disponible para asignación");

        // 4. ⭐ NUEVO: Obtener información del contenedor de la solicitud asociada a la
        // ruta
        if (tramo.getRuta() != null && tramo.getRuta().getIdSolicitud() != null) {
            ContenedorDTO contenedor = solicitudesApiClient.obtenerContenedorPorSolicitud(
                    tramo.getRuta().getIdSolicitud());

            if (contenedor != null) {
                // 4.1 Validar capacidad de peso
                if (contenedor.getPeso() != null && contenedor.getPeso() > camion.getCapacidadPeso()) {
                    throw new RuntimeException(String.format(
                            "❌ El camión %s no puede transportar el contenedor. " +
                                    "Peso del contenedor: %.2f kg > Capacidad del camión: %.2f kg",
                            camion.getPatente(),
                            contenedor.getPeso(),
                            camion.getCapacidadPeso()));
                }
                log.info("✅ Validación de peso: Contenedor {}kg <= Camión {}kg",
                        contenedor.getPeso(), camion.getCapacidadPeso());

                // 4.2 Validar capacidad de volumen
                if (contenedor.getVolumen() != null && contenedor.getVolumen() > camion.getCapacidadVolumen()) {
                    throw new RuntimeException(String.format(
                            "❌ El camión %s no puede transportar el contenedor. " +
                                    "Volumen del contenedor: %.2f m³ > Capacidad del camión: %.2f m³",
                            camion.getPatente(),
                            contenedor.getVolumen(),
                            camion.getCapacidadVolumen()));
                }
                log.info("✅ Validación de volumen: Contenedor {}m³ <= Camión {}m³",
                        contenedor.getVolumen(), camion.getCapacidadVolumen());

                log.info("✅ El camión {} puede transportar el contenedor (Peso: {}kg, Volumen: {}m³)",
                        camion.getPatente(), contenedor.getPeso(), contenedor.getVolumen());
            } else {
                log.warn("⚠️ No se pudo obtener información del contenedor. Se omite validación de capacidad.");
            }
        } else {
            log.warn(
                    "⚠️ El tramo no tiene ruta o solicitud asociada. Se omite validación de capacidad del contenedor.");
        }

        // 5. Asignar el camión al tramo
        tramo.setIdCamion(idCamion);

        // 6. Actualizar estado a "asignado"
        EstadoTramo estadoAsignado = obtenerOCrearEstado("asignado");
        tramo.setEstado(estadoAsignado);

        Tramo tramoGuardado = repo.save(tramo);
        log.info("✅ Camión {} asignado exitosamente al tramo {}", idCamion, idTramo);

        return tramoGuardado;
    }

    /**
     * Registra el INICIO de un tramo de traslado (Transportista)
     * Si es el primer tramo de la ruta, notifica al microservicio de Solicitudes
     * para cambiar el estado del contenedor a "en tránsito"
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

        // ⭐ MODIFICADO: Tomar fecha/hora actual automáticamente
        LocalDateTime fechaInicio = LocalDateTime.now();
        tramo.setFechaHoraInicio(fechaInicio);

        // ⭐ NUEVO: Guardar observaciones si se proporcionan
        if (dto != null && dto.getObservaciones() != null && !dto.getObservaciones().trim().isEmpty()) {
            tramo.setObservaciones(dto.getObservaciones());
            log.info("📝 Observaciones registradas: {}", dto.getObservaciones());
        }

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

        // ⭐ NUEVO: Verificar si es el primer tramo de la ruta y notificar a Solicitudes
        if (tramo.getRuta() != null) {
            Long idRuta = tramo.getRuta().getIdRuta();
            List<Tramo> tramosRuta = repo.findByRutaIdRuta(idRuta);

            // Contar cuántos tramos ya están iniciados o finalizados
            long tramosEnCurso = tramosRuta.stream()
                    .filter(t -> t.getEstado() != null &&
                            ("iniciado".equalsIgnoreCase(t.getEstado().getNombre()) ||
                                    "finalizado".equalsIgnoreCase(t.getEstado().getNombre())))
                    .count();

            // Si este es el primer tramo que se inicia, notificar a Solicitudes
            if (tramosEnCurso == 0 && tramo.getRuta().getIdSolicitud() != null) {
                log.info(
                        "📦 Primer tramo de la ruta iniciado. Notificando cambio de estado del contenedor a 'en tránsito'");
                solicitudesApiClient.notificarInicioTransito(tramo.getRuta().getIdSolicitud());
            }
        }

        Tramo tramoGuardado = repo.save(tramo);
        log.info("✅ Tramo iniciado exitosamente a las {}", fechaInicio);

        return tramoGuardado;
    }

    /**
     * Registra la finalización de un tramo y calcula el costo real (Transportista)
     * Si es el último tramo de la ruta, notifica al microservicio de Solicitudes
     * para finalizar la solicitud y cambiar el contenedor a "entregado"
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

        // ⭐ MODIFICADO: Tomar fecha/hora actual automáticamente
        LocalDateTime fechaFin = LocalDateTime.now();
        tramo.setFechaHoraFin(fechaFin);

        // ⭐ NUEVO: Guardar observaciones si se proporcionan
        if (dto != null && dto.getObservaciones() != null && !dto.getObservaciones().trim().isEmpty()) {
            // Concatenar con observaciones previas si existen
            String observacionesActuales = tramo.getObservaciones();
            if (observacionesActuales != null && !observacionesActuales.trim().isEmpty()) {
                tramo.setObservaciones(observacionesActuales + " | Fin: " + dto.getObservaciones());
            } else {
                tramo.setObservaciones("Fin: " + dto.getObservaciones());
            }
            log.info("📝 Observaciones de finalización registradas: {}", dto.getObservaciones());
        }

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

        // ⭐ NUEVO: Verificar si este es el último tramo de la ruta y notificar a
        // Solicitudes
        if (tramo.getRuta() != null) {
            Long idRuta = tramo.getRuta().getIdRuta();
            List<Tramo> tramosRuta = repo.findByRutaIdRuta(idRuta);

            // Verificar si TODOS los tramos están finalizados
            boolean todosTramosFinalizados = tramosRuta.stream()
                    .allMatch(t -> t.getEstado() != null &&
                            "finalizado".equalsIgnoreCase(t.getEstado().getNombre()));

            // Si todos los tramos están finalizados, notificar a Solicitudes para finalizar
            // automáticamente
            if (todosTramosFinalizados && tramo.getRuta().getIdSolicitud() != null) {
                log.info(
                        "📦 Último tramo de la ruta finalizado. Notificando finalización de solicitud y cambio de contenedor a 'entregado'");
                solicitudesApiClient.notificarFinalizacionTodosTramos(tramo.getRuta().getIdSolicitud());
            }
        }

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
        BigDecimal precioCombustible = tarifaService.obtenerValorTarifa("COMBUSTIBLE");
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

        if (tramo.getDepositoDestino() != null && tramo.getFechaHoraInicio() != null
                && tramo.getFechaHoraFin() != null) {
            Deposito deposito = tramo.getDepositoDestino();

            // Calcular días de estadía (diferencia entre fechas)
            diasEstadia = (int) ChronoUnit.DAYS.between(
                    tramo.getFechaHoraInicio().toLocalDate(),
                    tramo.getFechaHoraFin().toLocalDate());

            if (diasEstadia < 1) {
                diasEstadia = 1; // Mínimo 1 día
            }

            BigDecimal costoEstadiaDiario = deposito.getCostoEstadiaDiario() != null
                    ? deposito.getCostoEstadiaDiario()
                    : tarifaService.obtenerValorTarifa("ESTADIA_DEPOSITO");

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
        BigDecimal cargoGestion = tarifaService.obtenerValorTarifa("CARGO_GESTION_TRAMO");
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

        // 4️⃣ Calcular costo por peso y volumen (ejemplo fijo, normalmente viene del
        // microservicio de transporte)
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
