package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.dtos.EstadoContenedorDTO;
import com.microservicio.solicitudes.dtos.SolicitudRequestDTO;
import com.microservicio.solicitudes.models.Cliente;
import com.microservicio.solicitudes.models.Estado;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.models.Contenedor;
import com.microservicio.solicitudes.repositories.SolicitudRepository;
import com.microservicio.solicitudes.repositories.ContenedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
