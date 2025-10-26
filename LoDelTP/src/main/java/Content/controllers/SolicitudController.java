package Content.controllers;

import Content.models.Solicitud;
import Content.services.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {


    private final SolicitudService service;

    @GetMapping()
    public ResponseEntity<List<Solicitud>> all() {
        return ResponseEntity.ok(service.findAll());
    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Solicitud> getById(@PathVariable Long id) {
//        return service.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/cliente/{idCliente}")
//    public ResponseEntity<List<Solicitud>> byCliente(@PathVariable Long idCliente) {
//        return ResponseEntity.ok(service.findByIdCliente(idCliente));
//    }
//
//    @GetMapping("/estado/{estado}")
//    public ResponseEntity<List<Solicitud>> byEstado(@PathVariable Long estado) {
//        return ResponseEntity.ok(service.findByEstadoSolicitud(estado));
//    }
//
//    @PostMapping
//    public ResponseEntity<Solicitud> create(@RequestBody Solicitud solicitud) {
//        Solicitud created = service.create(solicitud);
//        return ResponseEntity.created(URI.create("/api/solicitudes/" + created.getNumeroSolicitud())).body(created);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Solicitud> update(@PathVariable Long id, @RequestBody Solicitud payload) {
//        return service.update(id, payload)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        return service.deleteById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
//    }
}
