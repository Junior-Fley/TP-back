package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.dtos.AsignarRutaDTO;
import com.microservicio.solicitudes.dtos.ContenedorPendienteDTO;
import com.microservicio.solicitudes.dtos.EstadoContenedorDTO;
import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.dtos.SolicitudRequestDTO;
import com.microservicio.solicitudes.dtos.TramoDTO;
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

    public SolicitudService(SolicitudRepository repo,
                            ContenedorRepository contRepo,
                            RutasApiClient rutasApiClient,
                            ClienteService clienteService,
                            EstadoService estadoService) {
        this.repo = repo;
        this.contRepo = contRepo;
        this.rutasApiClient = rutasApiClient;
        this.clienteService = clienteService;
        this.estadoService = estadoService;
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
     * 1. Creación del contenedor con identificación única
     * 2. Registro del cliente si no existe previamente (busca por DNI)
     * 3. Asignación del estado inicial (por defecto "borrador")
     */
    @Transactional
    public Solicitud crearSolicitudCompleta(SolicitudRequestDTO requestDTO) {
        try {
            System.out.println(">>> PASO 1: Creando contenedor...");
            // 1. Crear el contenedor con su identificación única
            Contenedor contenedor = new Contenedor();
            contenedor.setPeso(requestDTO.getPesoContenedor());
            contenedor.setVolumen(requestDTO.getVolumenContenedor());
            contenedor = contRepo.save(contenedor);
            System.out.println(">>> Contenedor creado con ID: " + contenedor.getIdContenedor());

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

            System.out.println(">>> PASO 3: Obteniendo/creando estado...");
            // 3. Determinar el estado inicial (por defecto "borrador")
            String nombreEstado = requestDTO.getEstadoInicial() != null
                    ? requestDTO.getEstadoInicial()
                    : "borrador";
            Estado estado = estadoService.obtenerOCrearPorNombre(nombreEstado);
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
     *
     * @param idSolicitud ID de la solicitud
     * @param asignarRutaDTO DTO con información de la ruta a asignar
     * @return Solicitud actualizada with la ruta asignada
     */
    @Transactional
    public Solicitud asignarRuta(Long idSolicitud, AsignarRutaDTO asignarRutaDTO) {
        // 1. Validar que la solicitud existe
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + idSolicitud));

        // 2. Validar que la ruta existe en el microservicio de rutas
        RutaResumenDTO rutaResumen = rutasApiClient.obtenerRutaRaw(asignarRutaDTO.getIdRuta());
        if (rutaResumen == null) {
            throw new RuntimeException("Ruta no encontrada con ID: " + asignarRutaDTO.getIdRuta());
        }

        // 3. Asignar la ruta a la solicitud
        solicitud.setIdRuta(asignarRutaDTO.getIdRuta());

        // 4. Actualizar costos y tiempos estimados
        if (asignarRutaDTO.getCostoEstimado() != null) {
            solicitud.setCostoEstimado(asignarRutaDTO.getCostoEstimado());
        } else if (rutaResumen.getCostoAproximado() != null) {
            // Si no se proporciona costo, usar el de la ruta
            solicitud.setCostoEstimado(rutaResumen.getCostoAproximado());
        }

        if (asignarRutaDTO.getTiempoEstimado() != null) {
            solicitud.setTiempoEstimado(asignarRutaDTO.getTiempoEstimado());
        }

        // 5. Actualizar el estado a "programada" si está en borrador
        if (solicitud.getEstadoSolicitud() != null &&
            "borrador".equalsIgnoreCase(solicitud.getEstadoSolicitud().getNombre())) {
            Estado estadoProgramada = estadoService.obtenerOCrearPorNombre("programada");
            solicitud.setEstadoSolicitud(estadoProgramada);
        }

        // 6. Guardar la solicitud actualizada
        return repo.save(solicitud);
    }

    /**
     * Desasignar una ruta de una solicitud
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

        // Volver el estado a borrador si estaba programada
        if (solicitud.getEstadoSolicitud() != null &&
            "programada".equalsIgnoreCase(solicitud.getEstadoSolicitud().getNombre())) {
            Estado estadoBorrador = estadoService.obtenerOCrearPorNombre("borrador");
            solicitud.setEstadoSolicitud(estadoBorrador);
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

        // Obtener todas las solicitudes
        List<Solicitud> solicitudes = repo.findAll();

        // Filtrar solo contenedores con estado "Pendiente de entrega"
        return solicitudes.stream()
                // Filtrar solo los contenedores con estado "Pendiente de entrega"
                .filter(s -> s.getContenedor() != null &&
                            s.getContenedor().getEstado() != null &&
                            "Pendiente de entrega".equalsIgnoreCase(s.getContenedor().getEstado()))
                // Mapear a DTO
                .map(s -> new ContenedorPendienteDTO(
                        s.getContenedor().getIdContenedor(),
                        s.getNumeroSolicitud(),
                        s.getContenedor().getEstado(),
                        s.getEstadoSolicitud() != null ? s.getEstadoSolicitud().getNombre() : "Sin estado",
                        s.getIdRuta(),
                        s.getCliente() != null ? s.getCliente().getNombre() + " " + s.getCliente().getApellido() : "Sin cliente",
                        s.getCliente() != null ? s.getCliente().getDni() : null,
                        s.getContenedor().getPeso(),
                        s.getContenedor().getVolumen(),
                        s.getCostoEstimado(),
                        s.getTiempoEstimado()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 🏁 Finaliza una solicitud y calcula el costo final real
     *
     * Requerimiento: "Al finalizar registrar el cálculo de tiempo real y el cálculo de costo real en la solicitud."
     *
     * Suma todos los costos reales de los tramos de la ruta asociada.
     * Actualiza costoFinal y tiempoReal en la solicitud.
     * Cambia el estado a "entregada".
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

        // 7. Actualizar estado a "entregada"
        Estado estadoEntregada = estadoService.obtenerOCrearPorNombre("entregada");
        solicitud.setEstadoSolicitud(estadoEntregada);

        // 8. Guardar la solicitud actualizada
        Solicitud solicitudFinalizada = repo.save(solicitud);

        log.info("✅ Solicitud finalizada. Costo final: ${}, Tiempo real: {} horas",
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
}
