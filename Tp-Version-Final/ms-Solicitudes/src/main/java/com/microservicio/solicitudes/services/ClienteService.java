package com.microservicio.solicitudes.services;

import com.microservicio.solicitudes.dtos.ClienteDTO;
import com.microservicio.solicitudes.models.Cliente;
import com.microservicio.solicitudes.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClienteService {

    private final ClienteRepository repo;

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorDni(String dni) {
        return repo.findByDni(dni).orElse(null);
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorMail(String mail) {
        return repo.findByMail(mail).orElse(null);
    }

    /**
     * Crea un nuevo cliente
     */
    public Cliente crearCliente(ClienteDTO dto) {
        log.info("👤 Creando nuevo cliente: {} {}", dto.getNombre(), dto.getApellido());

        // Validar nombre obligatorio
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        // Validar DNI único
        if (dto.getDni() != null && repo.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + dto.getDni());
        }

        // Validar email único
        if (dto.getMail() != null && repo.existsByMail(dto.getMail())) {
            throw new RuntimeException("Ya existe un cliente con el email: " + dto.getMail());
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre().trim());
        cliente.setApellido(dto.getApellido() != null ? dto.getApellido().trim() : null);
        cliente.setDni(dto.getDni());
        cliente.setTelefono(dto.getTelefono());
        cliente.setMail(dto.getMail());
        cliente.setDireccion(dto.getDireccion());

        Cliente clienteGuardado = repo.save(cliente);
        log.info("✅ Cliente creado - ID: {}, Nombre: {} {}",
                clienteGuardado.getIdCliente(),
                clienteGuardado.getNombre(),
                clienteGuardado.getApellido());

        return clienteGuardado;
    }

    /**
     * Actualiza un cliente existente
     */
    public Cliente actualizarCliente(Long idCliente, ClienteDTO dto) {
        log.info("🔄 Actualizando cliente con ID: {}", idCliente);

        Cliente cliente = repo.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + idCliente));

        // Validar DNI único si se está cambiando
        if (dto.getDni() != null && !dto.getDni().equals(cliente.getDni())) {
            if (repo.existsByDni(dto.getDni())) {
                throw new RuntimeException("Ya existe un cliente con el DNI: " + dto.getDni());
            }
        }

        // Validar email único si se está cambiando
        if (dto.getMail() != null && !dto.getMail().equals(cliente.getMail())) {
            if (repo.existsByMail(dto.getMail())) {
                throw new RuntimeException("Ya existe un cliente con el email: " + dto.getMail());
            }
        }

        // Actualizar atributos si se proporcionan
        if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
            cliente.setNombre(dto.getNombre().trim());
        }
        if (dto.getApellido() != null) {
            cliente.setApellido(dto.getApellido().trim());
        }
        if (dto.getDni() != null) {
            cliente.setDni(dto.getDni());
        }
        if (dto.getTelefono() != null) {
            cliente.setTelefono(dto.getTelefono());
        }
        if (dto.getMail() != null) {
            cliente.setMail(dto.getMail());
        }
        if (dto.getDireccion() != null) {
            cliente.setDireccion(dto.getDireccion());
        }

        Cliente clienteActualizado = repo.save(cliente);
        log.info("✅ Cliente {} {} actualizado correctamente",
                clienteActualizado.getNombre(),
                clienteActualizado.getApellido());

        return clienteActualizado;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
