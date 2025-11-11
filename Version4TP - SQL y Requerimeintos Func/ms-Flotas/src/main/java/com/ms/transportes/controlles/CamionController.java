package com.ms.transportes.controlles;

import com.ms.transportes.models.Camion;
import com.ms.transportes.services.CamionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/camiones")
@CrossOrigin(origins = "*") // para evitar problemas con frontend
public class CamionController {

    private final CamionService service;

    public CamionController(CamionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Camion> listar() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Camion obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Camion crear(@RequestBody Camion camion) {
        return service.guardar(camion);
    }

    @PutMapping("/{id}")
    public Camion actualizar(@PathVariable Long id, @RequestBody Camion camion) {
        //camion.setIdCamion(id);
        return service.guardar(camion);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }


