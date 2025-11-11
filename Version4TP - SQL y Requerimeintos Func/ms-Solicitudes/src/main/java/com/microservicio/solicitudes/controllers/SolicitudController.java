package com.microservicio.solicitudes.controllers;


import com.microservicio.solicitudes.clients.RutasApiClient;
import com.microservicio.solicitudes.dtos.EstadoContenedorDTO;
import com.microservicio.solicitudes.dtos.RutaResumenDTO;
import com.microservicio.solicitudes.dtos.SolicitudRequestDTO;
import com.microservicio.solicitudes.models.Solicitud;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/solicitudes")
@Tag(name = "Solicitudes", description = "Gestión completa de solicitudes de traslado")
public class SolicitudController {

    private final SolicitudService service;
    private final RutasApiClient rutasApiClient;


    public SolicitudController(SolicitudService service, RutasApiClient rutasApiClient) {
        this.service = service;
        this.rutasApiClient = rutasApiClient;
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> listar() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @Operation(summary = "Obtener una solicitud por ID")
    public ResponseEntity<Solicitud> obtener(@PathVariable("id") Long id) {
        Solicitud s = service.obtenerPorId(id);
        return (s != null) ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Solicitud> crear(@RequestBody Solicitud solicitud) {
        return ResponseEntity.status(201).body(service.crear(solicitud));
    }

    @PostMapping("/completa")
    /**
     * Endpoint para crear una solicitud completa de transporte de contenedor.
     * Cumple con los requisitos:
     * 1. Crea el contenedor con su identificación única
     * 2. Registra el cliente si no existe previamente (busca por DNI)
     * 3. Registra el estado (borrador, programada, en tránsito, entregada)
            System.out.println("=== Iniciando creación de solicitud completa ===");
            System.out.println("DNI Cliente: " + requestDTO.getDniCliente());
            System.out.println("Peso Contenedor: " + requestDTO.getPesoContenedor());
            System.err.println("=== ERROR al crear solicitud ===");
            e.printStackTrace();
            System.out.println("Estado Inicial: " + requestDTO.getEstadoInicial());

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
        return ResponseEntity.ok(service.asignarRutaASolicitud(numeroSolicitud, idRuta));
    }

    @GetMapping("/contenedores-pendientes")
    @Operation(summary = "RF5: Consultar contenedores pendientes de entrega",
            description = "Lista todos los contenedores pendientes con filtros por estado")
    /**
     * Endpoint para consultar el estado del transporte de un contenedor.
     * Permite al cliente verificar el estado actual de su contenedor.
     *
     * GET /api/solicitudes/contenedor/{idContenedor}/estado
     */
            description = "Calcula costo basado en distancia, peso, volumen y estadía en depósitos")
    public ResponseEntity<CalculoCostoDTO> calcularCosto(@PathVariable Long numeroSolicitud) {
        return ResponseEntity.ok(service.calcularCostoTotal(numeroSolicitud));
    }

    @PutMapping("/finalizar")
    @Operation(summary = "RF9: Finalizar solicitud registrando tiempo y costo real",
            description = "Registra el cálculo final de tiempo y costo al completar la entrega")
    public ResponseEntity<Solicitud> finalizarSolicitud(@RequestBody FinalizarSolicitudDTO dto) {
        return ResponseEntity.ok(service.finalizarSolicitud(dto));
    }

    @GetMapping("/contenedor/{idContenedor}/estado")
    @Operation(summary = "RF2: Consultar estado del contenedor",
            description = "Permite al cliente verificar el estado actual de su contenedor")
            EstadoContenedorDTO estado = service.consultarEstadoContenedor(idContenedor);
            return ResponseEntity.ok(estado);
        } catch (RuntimeException e) {
            System.out.println("=== Consultando estado del contenedor " + idContenedor + " ===");
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
            System.out.println("=== Estado encontrado: " + estado.getEstadoActual() + " ===");
        }
            System.err.println("=== ERROR: " + e.getMessage() + " ===");
    }
            System.err.println("=== ERROR inesperado ===");
            e.printStackTrace();

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una solicitud")
    //"Obtener solicitud con su ruta completa desde ms-rutas"
    // localhost:8090/api/solicitudes/1/rutas
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


    /**
     * Endpoint para consultar rutas tentativas con todos los tramos sugeridos,
     * tiempo y costo estimados.
     * Rol: Operador / Administrador
     *
     * GET /api/solicitudes/rutas-tentativas
     */
    @GetMapping("/rutas-tentativas")
    public ResponseEntity<?> consultarRutasTentativas() {
        try {
            System.out.println("=== Consultando rutas tentativas ===");
            List<RutaResumenDTO> rutas = rutasApiClient.obtenerRutasTentativas();
            System.out.println("=== " + (rutas != null ? rutas.size() : 0) + " rutas tentativas encontradas ===");
            return ResponseEntity.ok(rutas);
        } catch (Exception e) {
            System.err.println("=== ERROR al consultar rutas tentativas ===");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al consultar rutas tentativas: " + e.getMessage());
        }
    }

        return ResponseEntity.ok(resultado);
