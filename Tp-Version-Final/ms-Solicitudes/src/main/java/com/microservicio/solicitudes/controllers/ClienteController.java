package com.microservicio.solicitudes.controllers;

import com.microservicio.solicitudes.dtos.ClienteDTO;
import com.microservicio.solicitudes.models.Cliente;
import com.microservicio.solicitudes.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteService service;

    @Operation(summary = "Listar todos los clientes", description = "Obtiene la lista completa de clientes")
    @GetMapping
    public List<Cliente> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente específico por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable("id") Long id) {
        Cliente cliente = service.obtenerPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

//    @GetMapping("/dni/{dni}")
//    public ResponseEntity<Cliente> obtenerPorDni(@PathVariable("dni") String dni) {
//        Cliente cliente = service.obtenerPorDni(dni);
//        if (cliente == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(cliente);
//    }
//
//    @GetMapping("/mail/{mail}")
//    public ResponseEntity<Cliente> obtenerPorMail(@PathVariable("mail") String mail) {
//        Cliente cliente = service.obtenerPorMail(mail);
//        if (cliente == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(cliente);
//    }
    @Operation(summary = "Crear nuevo cliente", description = "Crea un nuevo cliente en el sistema")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ClienteDTO dto) {
        try {
            Cliente nuevoCliente = service.crearCliente(dto);
            log.info("✅ Cliente creado: {} {} (ID: {})",
                    nuevoCliente.getNombre(),
                    nuevoCliente.getApellido(),
                    nuevoCliente.getIdCliente());
            return ResponseEntity.status(201).body(nuevoCliente);
        } catch (RuntimeException e) {
            log.error("❌ Error al crear cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza todos los datos de un cliente existente (nombre, apellido, DNI, teléfono, mail, dirección)")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id, @RequestBody ClienteDTO dto) {
        try {
            Cliente clienteActualizado = service.actualizarCliente(id, dto);
            log.info("✅ Cliente actualizado: {} {} (ID: {})",
                    clienteActualizado.getNombre(),
                    clienteActualizado.getApellido(),
                    id);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) {
            log.error("❌ Error al actualizar cliente: {}", e.getMessage());
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Long id) {
        try {
            Cliente cliente = service.obtenerPorId(id);
            if (cliente == null) {
                log.warn("⚠️ Intento de eliminar cliente inexistente con ID: {}", id);
                return ResponseEntity.status(404).body("Error: Cliente no encontrado con ID: " + id);
            }

            service.eliminar(id);
            log.info("✅ Cliente eliminado: {} {} (ID: {})",
                    cliente.getNombre(),
                    cliente.getApellido(),
                    id);

            // Usar HashMap en lugar de Map.of() para compatibilidad con Java 8
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("mensaje", "Cliente eliminado correctamente");
            response.put("id", id);
            response.put("nombre", cliente.getNombre() + " " + cliente.getApellido());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("❌ Error al eliminar cliente: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }
}
