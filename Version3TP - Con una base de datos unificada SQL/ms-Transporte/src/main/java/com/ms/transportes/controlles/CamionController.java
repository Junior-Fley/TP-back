package com.ms.transportes.controlles;

import com.ms.transportes.dtos.AsignarTransportistaDTO;
import com.ms.transportes.dtos.CrearCamionDTO;
import com.ms.transportes.models.Camion;
import com.ms.transportes.services.CamionService;
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

    @GetMapping
    public List<Camion> listar() {
        return service.obtenerTodos();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Camion obtenerPorId(@PathVariable("id") Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Camion crear(@RequestBody Camion camion) {
        return service.guardar(camion);
    }

    @PutMapping("/{id}")
    public Camion actualizar(@PathVariable("id") Long id, @RequestBody Camion camion) {
        return service.guardar(camion);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PatchMapping("/{id}/disponibilidad")
    public Camion actualizarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam("disponible") boolean disponible) {
        return service.actualizarDisponibilidad(id, disponible);
    }

    /**
     * Crear camión con datos simplificados
     * POST /api/camiones/crear
     */
    @PostMapping("/crear")
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

    /**
     * Asignar transportista a un camión
     * PUT /api/camiones/{idCamion}/asignar-transportista
     */
    @PutMapping("/{idCamion}/asignar-transportista")
    public ResponseEntity<?> asignarTransportista(
            @PathVariable("idCamion") Long idCamion,
            @RequestBody AsignarTransportistaDTO dto) {
        try {
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
