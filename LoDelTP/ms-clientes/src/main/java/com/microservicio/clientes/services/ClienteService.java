package com.microservicio.clientes.services;

import com.microservicio.clientes.models.Cliente;
import com.microservicio.clientes.repositories.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> obtenerTodos() {
        return repo.findAll();
    }

    public Cliente crear(Cliente cliente) {
        return repo.save(cliente);
    }

    public Cliente obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}

