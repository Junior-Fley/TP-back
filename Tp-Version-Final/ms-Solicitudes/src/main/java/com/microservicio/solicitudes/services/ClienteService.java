package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.models.Cliente;
import com.microservicio.solicitudes.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repo;

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Cliente obtenerPorDni(String dni) {
        return repo.findByDni(dni).orElse(null);
    }

    public Cliente obtenerPorMail(String mail) {
        return repo.findByMail(mail).orElse(null);
    }

    public Cliente crear(Cliente cliente) {
        if (repo.existsByDni(cliente.getDni())) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + cliente.getDni());
        }
        if (repo.existsByMail(cliente.getMail())) {
            throw new RuntimeException("Ya existe un cliente con el email: " + cliente.getMail());
        }
        return repo.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente actualizado) {
        Cliente existente = repo.findById(id).orElseThrow(() ->
                new RuntimeException("Cliente no encontrado con ID " + id));

        existente.setNombre(actualizado.getNombre());
        existente.setApellido(actualizado.getApellido());
        existente.setDni(actualizado.getDni());
        existente.setTelefono(actualizado.getTelefono());
        existente.setMail(actualizado.getMail());
        existente.setDireccion(actualizado.getDireccion());

        return repo.save(existente);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}

