//package com.ms.transportes.controlles;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Controlador de Transporte protegido con Keycloak
// * Demuestra uso de roles: TRANSPORTISTA, ADMIN
// */
//@RestController
//@RequestMapping("/api/transporte")
//public class TransporteSecurityController {
//
//    /**
//     * Endpoint para que transportistas vean sus asignaciones
//     * ROL: TRANSPORTISTA
//     */
//    @GetMapping("/mis-asignaciones")
//    @PreAuthorize("hasRole('TRANSPORTISTA')")
//    public ResponseEntity<Map<String, Object>> misAsignaciones(Authentication authentication) {
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("usuario", authentication.getName());
//        response.put("rol", "TRANSPORTISTA");
//        response.put("message", "Listado de tus asignaciones de transporte");
//        response.put("asignaciones", new String[]{
//            "Contenedor #101 - Ruta Lima-Cusco",
//            "Contenedor #205 - Ruta Arequipa-Lima"
//        });
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Endpoint para iniciar un tramo de transporte
//     * ROL: TRANSPORTISTA
//     */
//    @PostMapping("/tramo/{tramoId}/iniciar")
//    @PreAuthorize("hasRole('TRANSPORTISTA')")
//    public ResponseEntity<Map<String, Object>> iniciarTramo(
//            @PathVariable Long tramoId,
//            Authentication authentication) {
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Tramo iniciado correctamente");
//        response.put("transportista", authentication.getName());
//        response.put("tramoId", tramoId);
//        response.put("estadoNuevo", "en_transito");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Endpoint para finalizar un tramo de transporte
//     * ROL: TRANSPORTISTA
//     */
//    @PostMapping("/tramo/{tramoId}/finalizar")
//    @PreAuthorize("hasRole('TRANSPORTISTA')")
//    public ResponseEntity<Map<String, Object>> finalizarTramo(
//            @PathVariable Long tramoId,
//            Authentication authentication) {
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Tramo finalizado correctamente");
//        response.put("transportista", authentication.getName());
//        response.put("tramoId", tramoId);
//        response.put("estadoNuevo", "completado");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Ver todos los transportes del sistema (ADMIN)
//     * ROL: ADMIN
//     */
//    @GetMapping("/admin/todos")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> todosLosTransportes(Authentication authentication) {
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("usuario", authentication.getName());
//        response.put("rol", "ADMIN");
//        response.put("message", "Todos los transportes del sistema");
//        response.put("total", 15);
//        response.put("transportes", new String[]{
//            "Transportista: Juan Pérez - Camión ABC123 - Ruta activa",
//            "Transportista: María García - Camión XYZ789 - En depósito"
//        });
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Asignar camión a un tramo (ADMIN)
//     * ROL: ADMIN
//     */
//    @PostMapping("/admin/asignar-camion")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> asignarCamion(
//            @RequestBody Map<String, Object> requestBody,
//            Authentication authentication) {
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Camión asignado correctamente al tramo");
//        response.put("admin", authentication.getName());
//        response.put("tramoId", requestBody.get("tramoId"));
//        response.put("camionId", requestBody.get("camionId"));
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Health check público (sin autenticación)
//     */
//    @GetMapping("/health")
//    public ResponseEntity<Map<String, Object>> health() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "UP");
//        response.put("service", "ms-transporte");
//        return ResponseEntity.ok(response);
//    }
//}
//
