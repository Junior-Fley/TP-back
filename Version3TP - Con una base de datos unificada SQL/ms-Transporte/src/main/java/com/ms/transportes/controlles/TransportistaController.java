package com.ms.transportes.controlles;


import com.ms.transportes.models.Transportista;
import com.ms.transportes.services.TransportistaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
@CrossOrigin(origins = "*")
public class TransportistaController {

    private final TransportistaService service;

    public TransportistaController(TransportistaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Transportista> listar() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Transportista obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Transportista crear(@RequestBody Transportista transportista) {
        return service.guardar(transportista);
    }

    @PutMapping("/{id}")
    public Transportista actualizar(@PathVariable Long id, @RequestBody Transportista transportista) {
        transportista.setIdTransportista(id);
        return service.guardar(transportista);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
