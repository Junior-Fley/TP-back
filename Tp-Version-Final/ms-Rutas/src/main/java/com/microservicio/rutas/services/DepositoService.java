package com.microservicio.rutas.services;

import com.microservicio.rutas.dtos.DepositoDTO;
import com.microservicio.rutas.models.Deposito;
import com.microservicio.rutas.models.Ciudad;
import com.microservicio.rutas.repositories.DepositoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepositoService {

    private final DepositoRepository repository;
    private final CiudadService ciudadService;

    // Listar todos
    public List<Deposito> obtenerTodos() {
        return repository.findAll();
    }

    // Buscar por ID
    public Optional<Deposito> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Crear nuevo desde DTO
    public Deposito crearDeposito(DepositoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del depósito es obligatorio");
        }

        Deposito deposito = new Deposito();
        deposito.setNombre(dto.getNombre().trim());
        deposito.setDireccion(dto.getDireccion());
        deposito.setLatitud(dto.getLatitud() != null ? dto.getLatitud() : 0.0);
        deposito.setLongitud(dto.getLongitud() != null ? dto.getLongitud() : 0.0);
        deposito.setCostoEstadiaDiario(dto.getCostoEstadiaDiario());

        if (dto.getIdCiudad() != null) {
            Ciudad ciudad = ciudadService.buscarPorId(dto.getIdCiudad())
                    .orElseThrow(() -> new RuntimeException("Ciudad no encontrada con ID: " + dto.getIdCiudad()));
            deposito.setCiudad(ciudad);
        }

        return repository.save(deposito);
    }

    // Actualizar existente desde DTO
    public Deposito actualizarDeposito(Long id, DepositoDTO dto) {
        return repository.findById(id)
                .map(deposito -> {
                    if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
                        deposito.setNombre(dto.getNombre().trim());
                    }
                    if (dto.getDireccion() != null) deposito.setDireccion(dto.getDireccion());
                    if (dto.getLatitud() != null) deposito.setLatitud(dto.getLatitud());
                    if (dto.getLongitud() != null) deposito.setLongitud(dto.getLongitud());
                    if (dto.getCostoEstadiaDiario() != null) deposito.setCostoEstadiaDiario(dto.getCostoEstadiaDiario());

                    if (dto.getIdCiudad() != null) {
                        Ciudad ciudad = ciudadService.buscarPorId(dto.getIdCiudad())
                                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada con ID: " + dto.getIdCiudad()));
                        deposito.setCiudad(ciudad);
                    }

                    return repository.save(deposito);
                })
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado con id " + id));
    }

    // Crear nuevo (compatibilidad)
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
