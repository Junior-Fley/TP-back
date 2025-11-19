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

    // GET /api/solicitudes - Listar todas las solicitudes
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Solicitud>> listar() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // GET /api/solicitudes/contenedores-pendientes - Consultar contenedores pendientes
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

    // GET /api/solicitudes/contenedor/{idContenedor}/estado - Consultar estado del contenedor
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

    // GET /api/solicitudes/{id} - Obtener solicitud por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtener(@PathVariable("id") Long id) {
        Solicitud s = service.obtenerPorId(id);
        return (s != null) ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    // GET /api/solicitudes/{idSolicitud}/rutas - Obtener solicitud con su ruta completa
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

    // GET /api/solicitudes/{id}/resumen-costos - Obtener resumen de costos
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

    // POST /api/solicitudes - Crear solicitud básica
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Solicitud> crear(@RequestBody Solicitud solicitud) {
        return ResponseEntity.status(201).body(service.crear(solicitud));
    }

    // POST /api/solicitudes/creacion - Crear solicitud simplificada
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

    // POST /api/solicitudes/completa - Crear solicitud completa
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

    // POST /api/solicitudes/{id}/finalizacion - Finalizar solicitud
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

    // PUT /api/solicitudes/{idSolicitud}/asignacion-ruta - Asignar ruta a solicitud
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

    // PUT /api/solicitudes/{idSolicitud}/contenedor/inicializacion-transito - Iniciar tránsito
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
            contenedorService.cambiarEstadoEnTransito(solicitud.getContenedor().getIdContenedor());
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

    // PUT /api/solicitudes/{idSolicitud}/finalizacion - Finalizar solicitud automáticamente
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

    // DELETE /api/solicitudes/{id} - Eliminar solicitud
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/solicitudes/{idSolicitud}/designacion-ruta - Desasignar ruta
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
}
