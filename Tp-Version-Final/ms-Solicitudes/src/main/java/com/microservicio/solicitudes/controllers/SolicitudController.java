package com.microservicio.solicitudes.controllers;


import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.dtos.AsignarRutaDTO;
import com.microservicio.solicitudes.dtos.ContenedorPendienteDTO;
import com.microservicio.solicitudes.dtos.CrearSolicitudDTO;
import com.microservicio.solicitudes.dtos.EstadoContenedorDTO;
import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.dtos.SolicitudRequestDTO;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.services.SolicitudService;
import com.microservicio.solicitudes.services.ContenedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;
    private final RutasApiClient rutasApiClient;
    private final ContenedorService contenedorService;


    public SolicitudController(SolicitudService service, RutasApiClient rutasApiClient, ContenedorService contenedorService) {
        this.service = service;
        this.rutasApiClient = rutasApiClient;
        this.contenedorService = contenedorService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Solicitud>> listar() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    /**
     * ⭐ NUEVO ENDPOINT SIMPLIFICADO
     * Crear solicitud solo con ID de cliente e ID de contenedor
     * POST /api/solicitudes/crear
     * Body: { "idCliente": 1, "idContenedor": 2 }
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @PostMapping("/creacion")
    public ResponseEntity<?> crearSolicitudSimple(@RequestBody CrearSolicitudDTO dto) {
        try {
            Solicitud solicitud = service.crearSolicitudSimple(dto);
            return ResponseEntity.status(201).body(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtener(@PathVariable("id") Long id) {
        Solicitud s = service.obtenerPorId(id);
        return (s != null) ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Solicitud> crear(@RequestBody Solicitud solicitud) {
        return ResponseEntity.status(201).body(service.crear(solicitud));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //"Obtener solicitud con su ruta completa desde ms-rutas"
    // localhost:8090/api/solicitudes/1/rutas
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{idSolicitud}/rutas")
    public ResponseEntity<RutaResumenDTO> obtenerConRuta(@PathVariable Long idSolicitud) {
        Solicitud solicitud = service.obtenerPorId(idSolicitud);

        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        if (solicitud.getIdRuta() == null) {
            return ResponseEntity.badRequest().build(); // La solicitud no tiene ruta asignada
        }

        RutaResumenDTO resultado = rutasApiClient.obtenerRutaRaw(solicitud.getIdRuta());

        if (resultado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resultado);
    }

    /**
     * Registrar una nueva solicitud de transporte de contenedor (Cliente)
     * POST /api/solicitudes/completa
     *
     * La solicitud incluye:
     * 1. La creación del contenedor con su identificación única
     * 2. El registro del cliente si no existe previamente
     * 3. Las solicitudes registran un estado: borrador, programada, en tránsito, entregada
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/completa")
    public ResponseEntity<Solicitud> crearSolicitudCompleta(@RequestBody SolicitudRequestDTO dto) {
        try {
            Solicitud solicitud = service.crearSolicitudCompleta(dto);
            return ResponseEntity.status(201).body(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para consultar el estado del transporte de un contenedor.
     * Permite al cliente verificar el estado actual de su contenedor.
     *
     * GET /api/solicitudes/contenedor/{idContenedor}/estado
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/contenedor/{idContenedor}/estado")
    public ResponseEntity<?> obtenerEstadoContenedor(@PathVariable("idContenedor") Long idContenedor) {
        try {
            System.out.println("=== Consultando estado del contenedor " + idContenedor + " ===");
            EstadoContenedorDTO estado = service.obtenerEstadoContenedor(idContenedor);

            if (estado == null) {
                System.err.println("=== ERROR: Contenedor no encontrado ===");
                return ResponseEntity.status(404).body("Contenedor no encontrado o sin solicitud asociada");
            }

            System.out.println("=== Estado encontrado: " + estado.getEstadoActual() + " ===");
            return ResponseEntity.ok(estado);

        } catch (RuntimeException e) {
            System.err.println("=== ERROR RuntimeException: " + e.getMessage() + " ===");
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("=== ERROR Exception: " + e.getMessage() + " ===");
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     *  Requerimiento Funcional #44444444444444(Cuatro):
     * Asignar una ruta con todos sus tramos a la solicitud (Operador/Administrador)
     * PUT /api/solicitudes/{idSolicitud}/asignar-ruta
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{idSolicitud}/asignacion-ruta")
    public ResponseEntity<?> asignarRuta(
            @PathVariable("idSolicitud") Long idSolicitud,
            @RequestBody AsignarRutaDTO asignarRutaDTO) {
        try {
            Solicitud solicitud = service.asignarRuta(idSolicitud, asignarRutaDTO);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Desasignar una ruta de una solicitud
     * DELETE /api/solicitudes/{idSolicitud}/desasignar-ruta
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{idSolicitud}/designacion-ruta")
    public ResponseEntity<?> desasignarRuta(@PathVariable("idSolicitud") Long idSolicitud) {
        try {
            Solicitud solicitud = service.desasignarRuta(idSolicitud);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * 🔹 Requerimiento Funcional #5:
     * Consultar todos los contenedores pendientes de entrega y su ubicación/estado con filtros
     * (Operador/Administrador)
     *
     * GET /api/solicitudes/contenedores-pendientes
     *
     * Parámetros opcionales:
     * - estado: Filtrar por estado específico (ej: "borrador", "programada", "en tránsito")
     * - idRuta: Filtrar por ruta asignada
     * - clienteDni: Filtrar por DNI del cliente
     *
     * Ejemplos de uso:
     * - GET /api/solicitudes/contenedores-pendientes (todos los pendientes)
     * - GET /api/solicitudes/contenedores-pendientes?estado=programada
     * - GET /api/solicitudes/contenedores-pendientes?idRuta=5
     * - GET /api/solicitudes/contenedores-pendientes?clienteDni=12345678
     * - GET /api/solicitudes/contenedores-pendientes?estado=programada&idRuta=5
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/contenedores-pendientes")
    public ResponseEntity<List<ContenedorPendienteDTO>> obtenerContenedoresPendientes() {
        try {
            System.out.println("=== Consultando contenedores pendientes ===");

            List<ContenedorPendienteDTO> contenedores = service.obtenerContenedoresPendientes();

            System.out.println("=== Se encontraron " + contenedores.size() + " contenedores pendientes ===");
            return ResponseEntity.ok(contenedores);

        } catch (Exception e) {
            System.err.println("=== ERROR al consultar contenedores pendientes: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 🏁 Finaliza una solicitud y calcula el costo final real
     * POST /api/solicitudes/{id}/finalizar
     *
     * Requerimiento: "Al finalizar registrar el cálculo de tiempo real y el cálculo de costo real en la solicitud."
     *
     * Suma todos los costos reales de los tramos de la ruta asociada.
     * Valida que todos los tramos estén finalizados.
     * Actualiza costoFinal y tiempoReal en la solicitud.
     * Cambia el estado a "entregada".
     */
    @PostMapping("/{id}/finalizacion")
    public ResponseEntity<?> finalizarSolicitud(@PathVariable("id") Long id) {
        try {
            Solicitud solicitudFinalizada = service.finalizarSolicitud(id);
            return ResponseEntity.ok(solicitudFinalizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * 📊 Obtiene un resumen comparativo de costos estimados vs reales
     * GET /api/solicitudes/{id}/resumen-costos
     *
     * Devuelve:
     * - Costo estimado vs costo final
     * - Tiempo estimado vs tiempo real
     * - Diferencias y porcentajes
     */
    @GetMapping("/{id}/resumen-costos")
    public ResponseEntity<?> obtenerResumenCostos(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(service.obtenerResumenCostos(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para que el microservicio de Rutas notifique el inicio del primer tramo
     * Cambia el estado de la solicitud a "en proceso" y el contenedor a "en tránsito"
     */
    @PutMapping("/{idSolicitud}/contenedor/inicializacion-transito")
    public ResponseEntity<?> iniciarTransitoContenedor(@PathVariable Long idSolicitud) {
        try {
            Solicitud solicitud = service.obtenerPorId(idSolicitud);
            if (solicitud == null) {
                return ResponseEntity.notFound().build();
            }

            if (solicitud.getContenedor() == null) {
                return ResponseEntity.badRequest().body("La solicitud no tiene un contenedor asignado");
            }

            // Cambiar estado del contenedor a "en tránsito"
            contenedorService.cambiarEstadoEnTransito(solicitud.getContenedor().getIdContenedor());

            // Cambiar estado de la solicitud a "en proceso"
            service.cambiarEstadoEnProceso(idSolicitud);

            java.util.HashMap<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Solicitud en proceso y contenedor en tránsito");
            response.put("idSolicitud", idSolicitud);
            response.put("idContenedor", solicitud.getContenedor().getIdContenedor());
            response.put("estadoSolicitud", "en proceso");
            response.put("estadoContenedor", "en tránsito");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ⭐ NUEVO: Endpoint para que el microservicio de Rutas notifique la finalización del último tramo
     * Cambia el estado de la solicitud a "completada" y el contenedor a "entregado"
     */
    @PutMapping("/{idSolicitud}/finalizacion")
    public ResponseEntity<?> finalizarSolicitudAutomatica(@PathVariable Long idSolicitud) {
        try {
            Solicitud solicitud = service.finalizarSolicitud(idSolicitud);

            java.util.HashMap<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Solicitud completada y contenedor entregado");
            response.put("idSolicitud", idSolicitud);
            response.put("estadoSolicitud", "completada");
            response.put("costoFinal", solicitud.getCostoFinal());
            response.put("tiempoReal", solicitud.getTiempoReal());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

}
