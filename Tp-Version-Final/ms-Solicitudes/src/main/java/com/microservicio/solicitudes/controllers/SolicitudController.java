package com.microservicio.solicitudes.controllers;

import com.microservicio.solicitudes.clients.RutasApiClient;
// import com.microservicio.solicitudes.dtos.AsignarRutaDTO;
import com.microservicio.solicitudes.dtos.ContenedorPendienteDTO;
// import com.microservicio.solicitudes.dtos.CrearSolicitudDTO;
import com.microservicio.solicitudes.dtos.EstadoContenedorDTO;
import com.microservicio.solicitudes.dtos.GenerarRutasTentativasRequestDTO;
import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.dtos.RutasTentativasResponseDTO;
// import com.microservicio.solicitudes.dtos.SeleccionarRutaDTO;
import com.microservicio.solicitudes.dtos.SolicitudRequestDTO;
import com.microservicio.solicitudes.models.Solicitud;
import com.microservicio.solicitudes.services.SolicitudService;
import com.microservicio.solicitudes.services.ContenedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;
    private final RutasApiClient rutasApiClient;
    private final ContenedorService contenedorService;

    public SolicitudController(SolicitudService service, RutasApiClient rutasApiClient,
            ContenedorService contenedorService) {
        this.service = service;
        this.rutasApiClient = rutasApiClient;
        this.contenedorService = contenedorService;
    }

    // ==========================================
    // GET ENDPOINTS
    // ==========================================

    @Operation(summary = "Listar todas las solicitudes", description = "Obtiene una lista de todas las solicitudes registradas en el sistema.")
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Solicitud>> listar() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @Operation(summary = "Consultar contenedores pendientes", description = "Obtiene una lista de los contenedores que están pendientes de ser procesados.")
//    @PreAuthorize("hasRole('ADMIN')")
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

    @Operation(summary = "Consultar estado del contenedor", description = "Obtiene el estado actual de un contenedor específico por su ID.")
//    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
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

    @Operation(summary = "Obtener solicitud por ID", description = "Busca y retorna una solicitud específica basada en su ID.")
//    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtener(@PathVariable("id") Long id) {
        Solicitud s = service.obtenerPorId(id);
        return (s != null) ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener solicitud con ruta completa", description = "Obtiene los detalles de una solicitud junto con la información completa de su ruta asignada.")
//    @PreAuthorize("hasRole('ADMIN')")
    @Hidden
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

    @Operation(summary = "Obtener resumen de costos", description = "Calcula y devuelve el resumen de costos asociado a una solicitud específica.")
    @GetMapping("/{id}/resumen-costos")
    @Hidden
    public ResponseEntity<?> obtenerResumenCostos(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(service.obtenerResumenCostos(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    // ==========================================
    // POST ENDPOINTS
    // ==========================================

    @Operation(summary = "Crear solicitud completa", description = "Crea una nueva solicitud con todos los detalles necesarios.")
//    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitudCompleta(@RequestBody SolicitudRequestDTO dto) {
        try {
            Solicitud solicitud = service.crearSolicitudCompleta(dto);
            return ResponseEntity.status(201).body(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Finalizar solicitud", description = "Marca una solicitud como finalizada.")
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

    @Operation(summary = "Generar rutas tentativas", description = "Genera opciones de rutas tentativas para una solicitud basada en sus coordenadas.")
//    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @PostMapping("/rutasTentativas/{idSolicitud}")
    public ResponseEntity<?> generarRutasTentativas(
            @PathVariable("idSolicitud") Long idSolicitud) {
        try {
            System.out.println("=== Generando rutas tentativas para solicitud " + idSolicitud + " ===");

            // Obtener la solicitud para verificar que tiene coordenadas
            Solicitud solicitud = service.obtenerPorId(idSolicitud);
            if (solicitud == null) {
                return ResponseEntity.status(404).body("Solicitud no encontrada con ID: " + idSolicitud);
            }

            // Validar que la solicitud tiene coordenadas
            if (solicitud.getLatitudOrigen() == null || solicitud.getLongitudOrigen() == null ||
                    solicitud.getLatitudDestino() == null || solicitud.getLongitudDestino() == null) {
                return ResponseEntity.status(400)
                        .body("La solicitud no tiene coordenadas de origen y destino guardadas");
            }

            // Crear el request con las coordenadas de la solicitud
            GenerarRutasTentativasRequestDTO request = new GenerarRutasTentativasRequestDTO();
            request.setLatitudOrigen(solicitud.getLatitudOrigen());
            request.setLongitudOrigen(solicitud.getLongitudOrigen());
            request.setLatitudDestino(solicitud.getLatitudDestino());
            request.setLongitudDestino(solicitud.getLongitudDestino());

            RutasTentativasResponseDTO rutas = service.generarRutasTentativas(idSolicitud, request);
            System.out.println("=== Rutas tentativas generadas exitosamente ===");
            return ResponseEntity.ok(rutas);
        } catch (RuntimeException e) {
            System.err.println("=== ERROR: " + e.getMessage() + " ===");
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("=== ERROR interno: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @Operation(summary = "Seleccionar ruta tentativa", description = "Asigna una de las rutas tentativas generadas a la solicitud.")
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{idSolicitud}/seleccionRuta/{numeroRuta}")
    public ResponseEntity<?> seleccionRuta(
            @PathVariable("idSolicitud") Long idSolicitud,
            @PathVariable("numeroRuta") Integer numeroRuta) {
        try {
            System.out.println("=== Seleccionando ruta tentativa #" + numeroRuta +
                    " para solicitud " + idSolicitud + " ===");
            Solicitud solicitud = service.seleccionarRutaTentativa(idSolicitud, numeroRuta);
            System.out.println("=== Ruta seleccionada y asignada exitosamente ===");
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            System.err.println("=== ERROR: " + e.getMessage() + " ===");
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("=== ERROR interno: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    // POST /api/solicitudes - Crear solicitud básica (DESHABILITADO - causa
    // conflicto con crearSolicitudCompleta)
    // @PreAuthorize("hasRole('ADMIN')")
    // @PostMapping
    // public ResponseEntity<Solicitud> crear(@RequestBody Solicitud solicitud) {
    // return ResponseEntity.status(201).body(service.crear(solicitud));
    // }

    // POST /api/solicitudes/creacion - Crear solicitud simplificada
    // @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    // @PostMapping("/creacion")
    // public ResponseEntity<?> crearSolicitudSimple(@RequestBody CrearSolicitudDTO
    // dto) {
    // try {
    // Solicitud solicitud = service.crearSolicitudSimple(dto);
    // return ResponseEntity.status(201).body(solicitud);
    // } catch (RuntimeException e) {
    // return ResponseEntity.status(400).body("Error: " + e.getMessage());
    // } catch (Exception e) {
    // return ResponseEntity.status(500).body("Error interno del servidor: " +
    // e.getMessage());
    // }
    // }

    // ==========================================
    // PUT ENDPOINTS
    // ==========================================

    @Operation(summary = "Iniciar tránsito de contenedor", description = "Actualiza el estado del contenedor a 'en tránsito' e inicia el proceso de la solicitud.")
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

    @Operation(summary = "Finalizar solicitud automáticamente", description = "Finaliza la solicitud y marca el contenedor como entregado, calculando costos finales.")
    @PutMapping("/{idSolicitud}/finalizacion")
    @Hidden
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

    // PUT /api/solicitudes/{idSolicitud}/asignacion-ruta - Asignar ruta a solicitud
    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/{idSolicitud}/asignacion-ruta")
    // public ResponseEntity<?> asignarRuta(
    // @PathVariable("idSolicitud") Long idSolicitud,
    // @RequestBody AsignarRutaDTO asignarRutaDTO) {
    // try {
    // Solicitud solicitud = service.asignarRuta(idSolicitud, asignarRutaDTO);
    // return ResponseEntity.ok(solicitud);
    // } catch (RuntimeException e) {
    // return ResponseEntity.status(404).body("Error: " + e.getMessage());
    // } catch (Exception e) {
    // return ResponseEntity.status(500).body("Error interno del servidor: " +
    // e.getMessage());
    // }
    // }

    // ==========================================
    // DELETE ENDPOINTS
    // ==========================================

    @Operation(summary = "Eliminar solicitud", description = "Elimina una solicitud del sistema por su ID.")
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desasignar ruta", description = "Elimina la asignación de ruta de una solicitud.")
//    @PreAuthorize("hasRole('ADMIN')")
    @Hidden
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
