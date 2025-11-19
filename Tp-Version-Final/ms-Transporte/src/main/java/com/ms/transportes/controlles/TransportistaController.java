package com.ms.transportes.controlles;

import com.ms.transportes.dtos.TransportistaDTO;
import com.ms.transportes.models.Transportista;
import com.ms.transportes.services.TransportistaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
@CrossOrigin(origins = "*")
@Slf4j
public class TransportistaController {
    private final TransportistaService service;

    public TransportistaController(TransportistaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los transportistas", description = "Obtiene la lista completa de transportistas")
    @GetMapping
    public List<Transportista> listar() {
        return service.obtenerTodos();
    }

    @Operation(summary = "Obtener transportista por ID", description = "Obtiene un transportista específico por su identificador")
    @GetMapping("/{id}")
    public Transportista obtenerPorId(@PathVariable("id") Long id) {
        return service.obtenerPorId(id);
    }

    @Operation(summary = "Crear transportista", description = "Crea un nuevo transportista con todos sus datos")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TransportistaDTO dto) {
        try {
            Transportista transportista = service.crearTransportista(dto);
            log.info("✅ Transportista creado: {} {}", transportista.getNombre(), transportista.getApellido());
            return ResponseEntity.status(201).body(transportista);
        } catch (RuntimeException e) {
            log.error("❌ Error al crear transportista: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar transportista", description = "Actualiza todos los datos de un transportista existente (nombre, apellido, DNI, teléfono, mail, dirección)")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id, @RequestBody TransportistaDTO dto) {
        try {
            Transportista transportistaActualizado = service.actualizarTransportista(id, dto);
            log.info("✅ Transportista actualizado: {} {}", transportistaActualizado.getNombre(), transportistaActualizado.getApellido());
            return ResponseEntity.ok(transportistaActualizado);
        } catch (RuntimeException e) {
            log.error("❌ Error al actualizar transportista: {}", e.getMessage());
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar transportista", description = "Elimina un transportista del sistema")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
    }
}
