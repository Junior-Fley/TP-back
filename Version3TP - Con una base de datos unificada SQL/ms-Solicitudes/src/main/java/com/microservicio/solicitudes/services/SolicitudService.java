package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.constants.EstadoContenedor;
import com.microservicio.solicitudes.constants.EstadoSolicitud;
import com.microservicio.solicitudes.dtos.AsignarRutaDTO;
import com.microservicio.solicitudes.dtos.ContenedorPendienteDTO;
import com.microservicio.solicitudes.dtos.CrearSolicitudDTO;
import com.microservicio.solicitudes.dtos.EstadoContenedorDTO;
import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.dtos.SolicitudRequestDTO;
import com.microservicio.solicitudes.dtos.TramoDTO;
import com.microservicio.solicitudes.dtos.UbicacionDTO;
import com.microservicio.solicitudes.models.Cliente;
import com.microservicio.solicitudes.models.Estado;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.repositories.SolicitudRepository;
import com.microservicio.solicitudes.repositories.ContenedorRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SolicitudService {

    private final SolicitudRepository repo;
    private final ContenedorRepository contRepo;
    private final RutasApiClient rutasApiClient;
    private final ClienteService clienteService;
    private final EstadoService estadoService;
    private final ContenedorService contenedorService;

    public SolicitudService(SolicitudRepository repo,
                            ContenedorRepository contRepo,
                            RutasApiClient rutasApiClient,
                            ClienteService clienteService,
                            EstadoService estadoService,
                            ContenedorService contenedorService) {
        this.repo = repo;
        this.contRepo = contRepo;
        this.rutasApiClient = rutasApiClient;
        this.clienteService = clienteService;
        this.estadoService = estadoService;
        this.contenedorService = contenedorService;
    }

    public List<Solicitud> obtenerTodas() {
        return repo.findAll();
    }

    public Solicitud crear(Solicitud solicitud) {
        return repo.save(solicitud);
    }

    public Solicitud obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
    /**
     * Crea una solicitud completa incluyendo:
     * 1. Creación del contenedor con identificación única y estado "disponible"
     * 2. Registro del cliente si no existe previamente (busca por DNI)
     * 3. Asignación del estado inicial (por defecto "borrador")
     */
    @Transactional
    public Solicitud crearSolicitudCompleta(SolicitudRequestDTO requestDTO) {
        try {
            System.out.println(">>> PASO 1: Creando contenedor...");
            // 1. Crear el contenedor con estado inicial "disponible"
            Contenedor contenedor = new Contenedor();
            contenedor.setPeso(requestDTO.getPesoContenedor());
            contenedor.setVolumen(requestDTO.getVolumenContenedor());
            contenedor.setEstado(EstadoContenedor.DISPONIBLE); // Estado inicial automático
            contenedor = contRepo.save(contenedor);
            System.out.println(">>> Contenedor creado con ID: " + contenedor.getIdContenedor() + " - Estado: " + contenedor.getEstado());

            System.out.println(">>> PASO 2: Buscando/creando cliente...");
            // 2. Obtener o crear el cliente si no existe (buscar por DNI)
            Cliente cliente;
            Cliente clienteExistente = clienteService.obtenerPorDni(requestDTO.getDniCliente());

            if (clienteExistente != null) {
                System.out.println(">>> Cliente existente encontrado con ID: " + clienteExistente.getIdCliente());
                cliente = clienteExistente;
            } else {
                System.out.println(">>> Cliente no existe, creando nuevo...");
                // El cliente no existe, crear uno nuevo
                cliente = new Cliente();
                cliente.setNombre(requestDTO.getNombreCliente());
                cliente.setApellido(requestDTO.getApellidoCliente());
                cliente.setDni(requestDTO.getDniCliente());
                cliente.setTelefono(requestDTO.getTelefonoCliente());
                cliente.setMail(requestDTO.getMailCliente());
                cliente.setDireccion(requestDTO.getDireccionCliente());
                cliente = clienteService.crear(cliente);
                System.out.println(">>> Cliente creado con ID: " + cliente.getIdCliente());
            }

            System.out.println(">>> PASO 3: Obteniendo estado inicial...");
            // 3. ⭐ REFACTORIZADO: Estado inicial siempre es "disponible" (ID=1)
            Estado estado = estadoService.obtenerEstadoDisponible();
            System.out.println(">>> Estado obtenido: " + estado.getNombre() + " (ID: " + estado.getIdEstado() + ")");

            System.out.println(">>> PASO 4: Creando solicitud...");
            // 4. Crear la solicitud - Hibernate manejará los IDs automáticamente
            Solicitud solicitud = new Solicitud();
            solicitud.setContenedor(contenedor);
            solicitud.setCliente(cliente);
            solicitud.setEstadoSolicitud(estado);

            System.out.println(">>> PASO 5: Guardando solicitud en BD...");
            Solicitud solicitudGuardada = repo.save(solicitud);
            System.out.println(">>> Solicitud guardada exitosamente con ID: " + solicitudGuardada.getNumeroSolicitud());

            return solicitudGuardada;
        } catch (Exception e) {
            System.err.println(">>> ERROR EN EL SERVICIO: " + e.getClass().getName());
            System.err.println(">>> MENSAJE: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * ⭐ NUEVO MÉTODO SIMPLIFICADO
     * Crea una solicitud solo con ID de cliente e ID de contenedor
     * Estado inicial: "disponible" (ID=1)
     */
    @Transactional
    public Solicitud crearSolicitudSimple(CrearSolicitudDTO dto) {
        log.info("📝 Creando solicitud simple - Cliente ID: {}, Contenedor ID: {}",
                dto.getIdCliente(), dto.getIdContenedor());

        // 1. Validar que el cliente existe
        Cliente cliente = clienteService.obtenerPorId(dto.getIdCliente());
        if (cliente == null) {
            log.error("❌ Cliente no encontrado con ID: {}", dto.getIdCliente());
            throw new RuntimeException("Cliente no encontrado con ID: " + dto.getIdCliente());
        }
        log.info("✅ Cliente encontrado: {} {} (ID: {})",
                cliente.getNombre(), cliente.getApellido(), cliente.getIdCliente());

        // 2. Validar que el contenedor existe
        Contenedor contenedor = contRepo.findById(dto.getIdContenedor())
                .orElseThrow(() -> {
                    log.error("❌ Contenedor no encontrado con ID: {}", dto.getIdContenedor());
                    return new RuntimeException("Contenedor no encontrado con ID: " + dto.getIdContenedor());
                });
        log.info("✅ Contenedor encontrado: ID: {}, Estado: {}",
                contenedor.getIdContenedor(), contenedor.getEstado());

        // 3. ⭐ REFACTORIZADO: Estado inicial siempre es "disponible" (ID=1)
        Estado estadoDisponible = estadoService.obtenerEstadoDisponible();
        log.info("✅ Estado obtenido: {} (ID: {})",
                estadoDisponible.getNombre(), estadoDisponible.getIdEstado());

        // 4. Crear la solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.setCliente(cliente);
        solicitud.setContenedor(contenedor);
        solicitud.setEstadoSolicitud(estadoDisponible);

        log.info("💾 Guardando solicitud en base de datos...");
        Solicitud solicitudGuardada = repo.save(solicitud);
        log.info("✅ Solicitud creada exitosamente con ID: {} - Estado: disponible (ID=1)",
                solicitudGuardada.getNumeroSolicitud());

        return solicitudGuardada;
    }
    /**
     * Consultar el estado del transporte de un contenedor (Cliente)
     * Permite al cliente verificar el estado actual de su contenedor
     *
     * @param idContenedor ID del contenedor a consultar
     * @return DTO con información completa del estado del contenedor
     */
    public EstadoContenedorDTO obtenerEstadoContenedor(Long idContenedor) {
        // Buscar la solicitud asociada al contenedor
        Solicitud solicitud = repo.findByContenedor_IdContenedor(idContenedor);

        if (solicitud != null && solicitud.getEstadoSolicitud() != null) {
            return new EstadoContenedorDTO(
                idContenedor,
                solicitud.getEstadoSolicitud().getNombre(),
                solicitud.getNumeroSolicitud()
            );
        }

        return null;
    }
    /**
     * 🔹 Requerimiento Funcional #4:
     * Asignar una ruta con todos sus tramos a la solicitud (Operador/Administrador)
     * Al asignar la ruta, cambia el estado del contenedor a "pendiente de entrega"
     *
     * @param idSolicitud ID de la solicitud
     * @param asignarRutaDTO DTO con información de la ruta a asignar (solo requiere idRuta)
     * @return Solicitud actualizada with la ruta asignada
     */
    @Transactional
    public Solicitud asignarRuta(Long idSolicitud, AsignarRutaDTO asignarRutaDTO) {
        // 1. Validar que la solicitud existe
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + idSolicitud));

        // 2. Validar que la ruta existe en el microservicio de rutas y obtener todos sus datos
        RutaResumenDTO rutaResumen = rutasApiClient.obtenerRutaRaw(asignarRutaDTO.getIdRuta());
        if (rutaResumen == null) {
            throw new RuntimeException("Ruta no encontrada con ID: " + asignarRutaDTO.getIdRuta());
        }

        log.info("📦 Asignando ruta {} a solicitud {}", asignarRutaDTO.getIdRuta(), idSolicitud);
        log.info("   ├─ Tramos: {}", rutaResumen.getCantidadTramos());
        log.info("   ├─ Depósitos: {}", rutaResumen.getCantidadDepositos());
        log.info("   ├─ Distancia: {} km", rutaResumen.getDistanciaTotalKm());
        log.info("   ├─ Tiempo: {} min", rutaResumen.getTiempoEstimadoMin());
        log.info("   └─ Costo: ${}", rutaResumen.getCostoAproximado());

        // 3. Asignar la ruta a la solicitud
        solicitud.setIdRuta(asignarRutaDTO.getIdRuta());

        // 4. ⭐ MEJORADO: Calcular automáticamente costos y tiempos desde la ruta
        // Si se proporcionan valores en el DTO, esos tienen prioridad
        if (asignarRutaDTO.getCostoEstimado() != null) {
            solicitud.setCostoEstimado(asignarRutaDTO.getCostoEstimado());
            log.info("💰 Costo personalizado asignado: ${}", asignarRutaDTO.getCostoEstimado());
        } else if (rutaResumen.getCostoAproximado() != null) {
            // Usar el costo calculado de la ruta
            solicitud.setCostoEstimado(rutaResumen.getCostoAproximado());
            log.info("💰 Costo automático desde ruta: ${}", rutaResumen.getCostoAproximado());
        }

        if (asignarRutaDTO.getTiempoEstimado() != null) {
            solicitud.setTiempoEstimado(asignarRutaDTO.getTiempoEstimado());
            log.info("⏱️ Tiempo personalizado asignado: {} min", asignarRutaDTO.getTiempoEstimado());
        } else if (rutaResumen.getTiempoEstimadoMin() != null) {
            // Usar el tiempo calculado de la ruta (convertir de Double a Integer)
            solicitud.setTiempoEstimado(rutaResumen.getTiempoEstimadoMin().intValue());
            log.info("⏱️ Tiempo automático desde ruta: {} min", rutaResumen.getTiempoEstimadoMin().intValue());
        }

        // 5. ⭐ REFACTORIZADO: Cambiar estado a "en proceso" (ID=2) al asignar ruta
        Estado estadoEnProceso = estadoService.obtenerEstadoEnProceso();
        solicitud.setEstadoSolicitud(estadoEnProceso);
        log.info("🔄 Solicitud {} cambiada a estado: en proceso (ID=2)", idSolicitud);

        // 6. ⭐ NUEVO: Cambiar estado del contenedor a "pendiente de entrega"
        if (solicitud.getContenedor() != null) {
            contenedorService.cambiarEstadoPendienteEntrega(solicitud.getContenedor().getIdContenedor());
            log.info("📦 Contenedor {} cambiado a estado: {}",
                    solicitud.getContenedor().getIdContenedor(),
                    EstadoContenedor.PENDIENTE_ENTREGA);
        }

        // 7. Guardar la solicitud actualizada
        Solicitud solicitudGuardada = repo.save(solicitud);

        log.info("✅ Ruta {} asignada exitosamente a solicitud {}", asignarRutaDTO.getIdRuta(), idSolicitud);

        return solicitudGuardada;
    }

    /**
     * Desasignar una ruta de una solicitud
     * Al desasignar, devuelve el contenedor a estado "disponible"
     *
     * @param idSolicitud ID de la solicitud
     * @return Solicitud actualizada sin ruta asignada
     */
    @Transactional
    public Solicitud desasignarRuta(Long idSolicitud) {
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + idSolicitud));

        solicitud.setIdRuta(null);
        solicitud.setCostoEstimado(null);
        solicitud.setTiempoEstimado(null);

        // ⭐ REFACTORIZADO: Volver el estado a "disponible" (ID=1)
        Estado estadoDisponible = estadoService.obtenerEstadoDisponible();
        solicitud.setEstadoSolicitud(estadoDisponible);
        log.info("🔄 Solicitud {} devuelta a estado: disponible (ID=1)", idSolicitud);

        // ⭐ NUEVO: Volver el contenedor a estado "disponible"
        if (solicitud.getContenedor() != null) {
            contenedorService.cambiarEstadoDisponible(solicitud.getContenedor().getIdContenedor());
            log.info("📦 Contenedor {} devuelto a estado: {}",
                    solicitud.getContenedor().getIdContenedor(),
                    EstadoContenedor.DISPONIBLE);
        }

        return repo.save(solicitud);
    }

    /**
     * 🔹 Requerimiento Funcional #5:
     * Consultar todos los contenedores pendientes de entrega y su ubicación/estado
     * (Operador/Administrador)
     *
     * Retorna solo los contenedores con estado "Pendiente de entrega"
     *
     * @return Lista de contenedores pendientes con su información completa
     */
    public List<ContenedorPendienteDTO> obtenerContenedoresPendientes() {
        log.info("📦 Consultando contenedores pendientes de entrega");

        // Obtener todas las solicitudes
        List<Solicitud> solicitudes = repo.findAll();

        // Filtrar solo contenedores con estado "Pendiente de entrega" o "En tránsito"
        return solicitudes.stream()
                // Filtrar solo los contenedores pendientes o en tránsito
                .filter(s -> s.getContenedor() != null &&
                            s.getContenedor().getEstado() != null &&
                            ("Pendiente de entrega".equalsIgnoreCase(s.getContenedor().getEstado()) ||
                             "En tránsito".equalsIgnoreCase(s.getContenedor().getEstado())))
                // Mapear a DTO con información de ubicación
                .map(s -> {
                    ContenedorPendienteDTO dto = new ContenedorPendienteDTO();

                    // Información básica
                    dto.setIdContenedor(s.getContenedor().getIdContenedor());
                    dto.setNumeroSolicitud(s.getNumeroSolicitud());
                    dto.setEstadoContenedor(s.getContenedor().getEstado());
                    dto.setEstadoSolicitud(s.getEstadoSolicitud() != null ? s.getEstadoSolicitud().getNombre() : "Sin estado");
                    dto.setIdRuta(s.getIdRuta());
                    dto.setNombreCliente(s.getCliente() != null ? s.getCliente().getNombre() + " " + s.getCliente().getApellido() : "Sin cliente");
                    dto.setDniCliente(s.getCliente() != null ? s.getCliente().getDni() : null);
                    dto.setPeso(s.getContenedor().getPeso());
                    dto.setVolumen(s.getContenedor().getVolumen());
                    dto.setCostoEstimado(s.getCostoEstimado());
                    dto.setTiempoEstimado(s.getTiempoEstimado());

                    // ⭐ NUEVO: Obtener información de ubicación desde los tramos
                    if (s.getIdRuta() != null) {
                        try {
                            List<TramoDTO> tramos = rutasApiClient.obtenerTramosPorRuta(s.getIdRuta());
                            if (tramos != null && !tramos.isEmpty()) {
                                // Calcular progreso
                                dto.setCantidadTramos(tramos.size());
                                long tramosFinalizados = tramos.stream()
                                        .filter(t -> "finalizado".equalsIgnoreCase(t.getEstado()))
                                        .count();
                                dto.setTramosCompletados((int) tramosFinalizados);
                                dto.setPorcentajeAvance((tramosFinalizados * 100.0) / tramos.size());

                                // Determinar ubicación actual
                                UbicacionDTO ubicacion = determinarUbicacionActual(tramos);
                                dto.setUbicacionActual(ubicacion);

                                log.debug("📍 Contenedor {} - Ubicación: {}",
                                        dto.getIdContenedor(),
                                        ubicacion != null ? ubicacion.getDescripcion() : "Desconocida");
                            }
                        } catch (Exception e) {
                            log.warn("⚠️ No se pudo obtener ubicación del contenedor {}: {}",
                                    dto.getIdContenedor(), e.getMessage());
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * ⭐ NUEVO: Determina la ubicación actual del contenedor basándose en el estado de los tramos
     */
    private UbicacionDTO determinarUbicacionActual(List<TramoDTO> tramos) {
        if (tramos == null || tramos.isEmpty()) {
            return null;
        }

        // Buscar el tramo actualmente en curso (iniciado pero no finalizado)
        TramoDTO tramoEnCurso = tramos.stream()
                .filter(t -> "iniciado".equalsIgnoreCase(t.getEstado()))
                .findFirst()
                .orElse(null);

        if (tramoEnCurso != null) {
            // El contenedor está en tránsito
            UbicacionDTO ubicacion = new UbicacionDTO();
            ubicacion.setDescripcion("En tránsito");
            ubicacion.setEstadoTramoActual("iniciado");
            ubicacion.setIdTramoActual(tramoEnCurso.getIdTramo());
            return ubicacion;
        }

        // Buscar el último tramo finalizado
        TramoDTO ultimoFinalizado = tramos.stream()
                .filter(t -> "finalizado".equalsIgnoreCase(t.getEstado()))
                .reduce((first, second) -> second) // Obtener el último
                .orElse(null);

        if (ultimoFinalizado != null) {
            // El contenedor está en el depósito destino del último tramo finalizado
            UbicacionDTO ubicacion = new UbicacionDTO();
            ubicacion.setDescripcion("En depósito (esperando siguiente tramo)");
            ubicacion.setEstadoTramoActual("esperando");
            ubicacion.setIdTramoActual(ultimoFinalizado.getIdTramo());
            return ubicacion;
        }

        // Ningún tramo iniciado aún - está en el origen
        UbicacionDTO ubicacion = new UbicacionDTO();
        ubicacion.setDescripcion("En origen (esperando inicio de transporte)");
        ubicacion.setEstadoTramoActual("pendiente");
        ubicacion.setIdTramoActual(tramos.get(0).getIdTramo());
        return ubicacion;
    }
    /**
     * 🏁 Finaliza una solicitud y calcula el costo final real
     * Cambia el estado de la solicitud a "completada" y el contenedor a "entregado"
     *
     * Requerimiento: "Al finalizar registrar el cálculo de tiempo real y el cálculo de costo real en la solicitud."
     *
     * Suma todos los costos reales de los tramos de la ruta asociada.
     * Actualiza costoFinal y tiempoReal en la solicitud.
     * Cambia el estado a "completada".
     *
     * @param idSolicitud ID de la solicitud a finalizar
     * @return Solicitud finalizada with costos reales
     */
    @Transactional
    public Solicitud finalizarSolicitud(Long idSolicitud) {
        log.info("🏁 Finalizando solicitud ID: {}", idSolicitud);

        // 1. Obtener la solicitud
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + idSolicitud));

        // 2. Validar que tiene ruta asignada
        if (solicitud.getIdRuta() == null) {
            throw new RuntimeException("La solicitud no tiene una ruta asignada");
        }

        // 3. Obtener todos los tramos de la ruta
        List<TramoDTO> tramos = rutasApiClient.obtenerTramosPorRuta(solicitud.getIdRuta());

        if (tramos == null || tramos.isEmpty()) {
            throw new RuntimeException("No se encontraron tramos para la ruta ID: " + solicitud.getIdRuta());
        }

        // 4. Validar que todos los tramos estén finalizados
        boolean todosFinalizados = tramos.stream()
                .allMatch(t -> "finalizado".equalsIgnoreCase(t.getEstado()));

        if (!todosFinalizados) {
            throw new RuntimeException("No todos los tramos de la ruta están finalizados. " +
                    "Finalice todos los tramos antes de finalizar la solicitud.");
        }

        // 5. Calcular el costo final sumando todos los costos reales de los tramos
        BigDecimal costoFinal = tramos.stream()
                .map(TramoDTO::getCostoReal)
                .filter(costo -> costo != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        solicitud.setCostoFinal(costoFinal);

        // 6. Calcular el tiempo real (en horas)
        LocalDateTime primerInicio = tramos.stream()
                .map(TramoDTO::getFechaHoraInicio)
                .filter(fecha -> fecha != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime ultimoFin = tramos.stream()
                .map(TramoDTO::getFechaHoraFin)
                .filter(fecha -> fecha != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (primerInicio != null && ultimoFin != null) {
            long horasReales = ChronoUnit.HOURS.between(primerInicio, ultimoFin);
            solicitud.setTiempoReal((int) horasReales);
        }

        // 7. ⭐ REFACTORIZADO: Actualizar estado a "completada" (ID=3)
        Estado estadoCompletada = estadoService.obtenerEstadoCompletada();
        solicitud.setEstadoSolicitud(estadoCompletada);
        log.info("🔄 Solicitud {} cambiada a estado: completada (ID=3)", idSolicitud);

        // 8. ⭐ NUEVO: Cambiar estado del contenedor a "entregado"
        if (solicitud.getContenedor() != null) {
            contenedorService.cambiarEstadoEntregado(solicitud.getContenedor().getIdContenedor());
            log.info("📦 Contenedor {} cambiado a estado: {}",
                    solicitud.getContenedor().getIdContenedor(),
                    EstadoContenedor.ENTREGADO);
        }

        // 9. Guardar la solicitud actualizada
        Solicitud solicitudFinalizada = repo.save(solicitud);

        log.info("✅ Solicitud completada. Costo final: ${}, Tiempo real: {} horas",
                costoFinal, solicitud.getTiempoReal());
        log.info("📊 Diferencia con estimado: ${} (Estimado: ${}, Real: ${})",
                solicitud.getCostoEstimado() != null
                    ? costoFinal.subtract(solicitud.getCostoEstimado())
                    : "N/A",
                solicitud.getCostoEstimado(),
                costoFinal);

        return solicitudFinalizada;
    }

    /**
     * Obtiene un resumen comparativo de costos estimados vs reales de una solicitud
     */
    public Map<String, Object> obtenerResumenCostos(Long idSolicitud) {
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + idSolicitud));

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("idSolicitud", idSolicitud);
        resumen.put("costoEstimado", solicitud.getCostoEstimado());
        resumen.put("costoFinal", solicitud.getCostoFinal());
        resumen.put("tiempoEstimado", solicitud.getTiempoEstimado());
        resumen.put("tiempoReal", solicitud.getTiempoReal());
        resumen.put("estado", solicitud.getEstadoSolicitud() != null
                ? solicitud.getEstadoSolicitud().getNombre()
                : "Sin estado");

        if (solicitud.getCostoEstimado() != null && solicitud.getCostoFinal() != null) {
            BigDecimal diferencia = solicitud.getCostoFinal().subtract(solicitud.getCostoEstimado());
            resumen.put("diferenciaCosto", diferencia);

            if (solicitud.getCostoEstimado().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal porcentaje = diferencia
                        .divide(solicitud.getCostoEstimado(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                resumen.put("porcentajeDiferencia", porcentaje);
            }
        }

        if (solicitud.getTiempoEstimado() != null && solicitud.getTiempoReal() != null) {
            int diferenciaTiempo = solicitud.getTiempoReal() - solicitud.getTiempoEstimado();
            resumen.put("diferenciaTiempo", diferenciaTiempo);
        }

        return resumen;
    }



    /**
     * ⭐ REFACTORIZADO: Cambia el estado de la solicitud a "en proceso" (ID=2)
     * Se llama cuando se inicia el primer tramo de la ruta
     */
    @Transactional
    public void cambiarEstadoEnProceso(Long idSolicitud) {
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + idSolicitud));

        Estado estadoEnProceso = estadoService.obtenerEstadoEnProceso();
        solicitud.setEstadoSolicitud(estadoEnProceso);
        repo.save(solicitud);

        log.info("✅ Solicitud {} cambiada a estado: en proceso (ID=2)", idSolicitud);
    }
}
