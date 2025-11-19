package com.ms.transportes.controlles;

import com.ms.transportes.dtos.ActualizarCamionDTO;
import com.ms.transportes.dtos.AsignarTransportistaDTO;
import com.ms.transportes.dtos.CrearCamionDTO;
import com.ms.transportes.models.Camion;
import com.ms.transportes.services.CamionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/camiones")
@CrossOrigin(origins = "*")

@Slf4j
public class CamionController {

    private final CamionService service;

    public CamionController(CamionService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los camiones", description = "Obtiene la lista completa de camiones")
    @GetMapping
    public List<Camion> listar() {
        return service.obtenerTodos();
    }
//
//    @Operation(summary = "Crear camión básico", description = "Crea un nuevo camión con modelo completo")
//    @PostMapping
//    public Camion crear(@RequestBody Camion camion) {
//        return service.guardar(camion);
//    }

    @Operation(summary = "Crear camión simplificado", description = "Crea un nuevo camión con datos simplificados")
    @PostMapping
    public ResponseEntity<?> crearCamion(@RequestBody CrearCamionDTO dto) {
        try {
            Camion camion = service.crearCamion(dto);
            log.info("✅ Camión creado: Patente={}, Teléfono={}", camion.getPatente(), camion.getTelefono());
            return ResponseEntity.status(201).body(camion);
        } catch (RuntimeException e) {
            log.error("❌ Error al crear camión: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener camión por ID", description = "Obtiene un camión específico por su identificador")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Camion obtenerPorId(@PathVariable("id") Long id) {
        return service.obtenerPorId(id);
    }

    @Operation(summary = "Actualizar camión", description = "Actualiza todos los datos de un camión existente (patente, teléfono, capacidades, costos, disponibilidad y transportista)")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id, @RequestBody ActualizarCamionDTO dto) {
        try {
            Camion camionActualizado = service.actualizarCamion(id, dto);
            log.info("✅ Camión actualizado: {}", camionActualizado.getPatente());
            return ResponseEntity.ok(camionActualizado);
        } catch (RuntimeException e) {
            log.error("❌ Error al actualizar camión: {}", e.getMessage());
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar camión", description = "Elimina un camión del sistema")
    @DeleteMapping("/{id}")

    public void eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
    }

    @Operation(summary = "Actualizar disponibilidad", description = "Cambia el estado de disponibilidad de un camión")
    @PatchMapping("/{id}/disponibilidad")
    public Camion actualizarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam("disponible") boolean disponible) {
        return service.actualizarDisponibilidad(id, disponible);
    }

    @Operation(summary = "Asignar transportista", description = "Asigna un transportista a un camión específico")
    @PutMapping("/{idCamion}/asignacion-transportista/{idTransportista}")
    public ResponseEntity<?> asignarTransportista(
            @PathVariable("idCamion") Long idCamion,
            @PathVariable("idTransportista") Long idTransportista) {
        try {
            AsignarTransportistaDTO dto = new AsignarTransportistaDTO();
            dto.setIdTransportista(idTransportista);
            Camion camion = service.asignarTransportista(idCamion, dto);
            log.info("✅ Transportista asignado al camión {}", camion.getPatente());
            return ResponseEntity.ok(camion);
        } catch (RuntimeException e) {
            log.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }
}
