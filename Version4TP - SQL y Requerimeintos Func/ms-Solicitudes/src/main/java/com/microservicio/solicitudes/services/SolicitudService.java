package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.dtos.*;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.models.Cliente;
import com.microservicio.solicitudes.models.Estado;
import com.microservicio.solicitudes.repositories.SolicitudRepository;
import com.microservicio.solicitudes.repositories.ContenedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
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

    public Solicitud obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /**
     * Consulta el estado del transporte de un contenedor por su ID
     * Retorna información completa sobre la solicitud, cliente, estado y costos
     */
    public EstadoContenedorDTO consultarEstadoContenedor(Long idContenedor) {
        System.out.println(">>> Consultando estado del contenedor ID: " + idContenedor);

        Solicitud solicitud = repo.findByContenedor_IdContenedor(idContenedor)
            .orElseThrow(() -> new RuntimeException("No se encontró ninguna solicitud para el contenedor con ID: " + idContenedor));

        EstadoContenedorDTO dto = new EstadoContenedorDTO();

        // Información del contenedor
        if (solicitud.getContenedor() != null) {
            dto.setIdContenedor(solicitud.getContenedor().getIdContenedor());
            dto.setPeso(solicitud.getContenedor().getPeso());
            dto.setVolumen(solicitud.getContenedor().getVolumen());
        }

        // Información de la solicitud
        dto.setNumeroSolicitud(solicitud.getNumeroSolicitud());
        dto.setEstadoActual(solicitud.getEstadoSolicitud() != null ?
            solicitud.getEstadoSolicitud().getNombre() : "Sin estado");

        // Información del cliente
        if (solicitud.getCliente() != null) {
            dto.setIdCliente(solicitud.getCliente().getIdCliente());
            dto.setNombreCliente(solicitud.getCliente().getNombre());
            dto.setApellidoCliente(solicitud.getCliente().getApellido());
            dto.setDniCliente(solicitud.getCliente().getDni());
        }

        // Información de costos y tiempos
        dto.setCostoEstimado(solicitud.getCostoEstimado());
        dto.setTiempoEstimado(solicitud.getTiempoEstimado());
        dto.setCostoFinal(solicitud.getCostoFinal());
        dto.setTiempoReal(solicitud.getTiempoReal());
        dto.setIdRuta(solicitud.getIdRuta());

        System.out.println(">>> Estado del contenedor: " + dto.getEstadoActual());

        return dto;
    }

    /**
     * RF4: Asignar una ruta con todos sus tramos a la solicitud
     */
    @Transactional
    public Solicitud asignarRutaASolicitud(Long numeroSolicitud, Long idRuta) {
        Solicitud solicitud = repo.findById(numeroSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setIdRuta(idRuta);

        // Actualizar estado a "En tránsito" o "Asignada"
        Estado estadoAsignada = estadoService.obtenerOCrearPorNombre("Asignada");
        solicitud.setEstadoSolicitud(estadoAsignada);

        return repo.save(solicitud);
    }

    /**
     * RF5: Consultar todos los contenedores pendientes de entrega con filtros
     */
    public List<ContenedorPendienteDTO> consultarContenedoresPendientes(String estadoFiltro) {
        List<Solicitud> solicitudes;

        if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
            // Filtrar por estado específico (ejemplo: "En tránsito", "En depósito", etc.)
            solicitudes = repo.findAll().stream()
                    .filter(s -> s.getEstadoSolicitud() != null &&
                                s.getEstadoSolicitud().getNombre().equalsIgnoreCase(estadoFiltro))
                    .collect(Collectors.toList());
        } else {
            // Obtener todas las solicitudes que no estén entregadas
            solicitudes = repo.findAll().stream()
                    .filter(s -> s.getEstadoSolicitud() != null &&
                                !s.getEstadoSolicitud().getNombre().equalsIgnoreCase("Entregado"))
                    .collect(Collectors.toList());
        }

        return solicitudes.stream()
                .map(this::convertirAContenedorPendienteDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF8: Calcular el costo total de la entrega
     */
    public CalculoCostoDTO calcularCostoTotal(Long numeroSolicitud) {
        Solicitud solicitud = repo.findById(numeroSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        CalculoCostoDTO dto = new CalculoCostoDTO();
        dto.setNumeroSolicitud(numeroSolicitud);

        // Obtener datos del contenedor
        if (solicitud.getContenedor() != null) {
            dto.setPesoContenedor(solicitud.getContenedor().getPeso());
            dto.setVolumenContenedor(solicitud.getContenedor().getVolumen());
        }

        // Aquí se debería llamar al microservicio de rutas para obtener:
        // 1. Distancia total del recorrido
        // 2. Días de estadía en depósitos (diferencia entre fechas de entrada/salida)
        // Por ahora, usamos valores de ejemplo o los que ya estén calculados

        dto.setDistanciaTotal(0.0); // Calcular desde microservicio de rutas
        dto.setDiasEstadiaTotal(0); // Calcular desde tramos

        // Cálculo del costo
        BigDecimal costoRecorrido = BigDecimal.ZERO; // Distancia * tarifa
        BigDecimal costoEstadia = BigDecimal.ZERO; // Días * tarifa depósito
        BigDecimal costoTotal = costoRecorrido.add(costoEstadia);

        dto.setCostoRecorrido(costoRecorrido);
        dto.setCostoEstadia(costoEstadia);
        dto.setCostoTotal(costoTotal);

        return dto;
    }

    /**
     * RF9: Finalizar solicitud registrando tiempo real y costo real
     */
    @Transactional
    public Solicitud finalizarSolicitud(FinalizarSolicitudDTO dto) {
        Solicitud solicitud = repo.findById(dto.getNumeroSolicitud())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setCostoFinal(dto.getCostoFinal());
        solicitud.setTiempoReal(dto.getTiempoReal());

        // Actualizar estado a "Entregado"
        Estado estadoEntregado = estadoService.obtenerOCrearPorNombre("Entregado");
        solicitud.setEstadoSolicitud(estadoEntregado);

        return repo.save(solicitud);
    }

    // Método auxiliar para convertir Solicitud a ContenedorPendienteDTO
    private ContenedorPendienteDTO convertirAContenedorPendienteDTO(Solicitud solicitud) {
        ContenedorPendienteDTO dto = new ContenedorPendienteDTO();

        if (solicitud.getContenedor() != null) {
            dto.setIdContenedor(solicitud.getContenedor().getIdContenedor());
            dto.setPeso(solicitud.getContenedor().getPeso());
            dto.setVolumen(solicitud.getContenedor().getVolumen());
        }

        dto.setNumeroSolicitud(solicitud.getNumeroSolicitud());
        dto.setEstadoActual(solicitud.getEstadoSolicitud() != null ?
                solicitud.getEstadoSolicitud().getNombre() : "Sin estado");

        if (solicitud.getCliente() != null) {
            dto.setNombreCliente(solicitud.getCliente().getNombre() + " " +
                    solicitud.getCliente().getApellido());
            dto.setTelefonoCliente(solicitud.getCliente().getTelefono());
        }

        // Ubicación actual podría obtenerse del microservicio de rutas
        dto.setUbicacionActual("Por determinar");

        return dto;
    }

}

