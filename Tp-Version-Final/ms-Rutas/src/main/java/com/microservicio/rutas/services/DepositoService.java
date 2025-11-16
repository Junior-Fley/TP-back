package com.microservicio.rutas.services;

import com.microservicio.rutas.models.Deposito;
import com.microservicio.rutas.repositories.DepositoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepositoService {

    private final DepositoRepository repository;

    // Listar todos
    public List<Deposito> obtenerTodos() {
        return repository.findAll();
    }

    // Buscar por ID
    public Optional<Deposito> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Crear nuevo
    public Deposito crear(Deposito deposito) {
        return repository.save(deposito);
    }

    // Actualizar existente
    public Deposito actualizar(Long id, Deposito depositoActualizado) {
        return repository.findById(id)
                .map(deposito -> {
                    deposito.setNombre(depositoActualizado.getNombre());
                    deposito.setDireccion(depositoActualizado.getDireccion());
                    deposito.setLatitud(depositoActualizado.getLatitud());
                    deposito.setLongitud(depositoActualizado.getLongitud());
                    deposito.setCostoEstadiaDiario(depositoActualizado.getCostoEstadiaDiario());
                    deposito.setCiudad(depositoActualizado.getCiudad());
                    return repository.save(deposito);
                })
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado con id " + id));
    }

    // Eliminar
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
