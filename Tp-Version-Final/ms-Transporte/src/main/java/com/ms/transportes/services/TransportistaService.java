package com.ms.transportes.services;

import com.ms.transportes.dtos.TransportistaDTO;
import com.ms.transportes.models.Transportista;
import com.ms.transportes.repositories.TransportistaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
public class TransportistaService {

    private final TransportistaRepository repo;

    public TransportistaService(TransportistaRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Transportista> obtenerTodos() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Transportista obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Transportista guardar(Transportista transportista) {
        return repo.save(transportista);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /**
     * Crea un nuevo transportista
     */
    public Transportista crearTransportista(TransportistaDTO dto) {
        log.info("👤 Creando nuevo transportista: {} {}", dto.getNombre(), dto.getApellido());

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        Transportista transportista = new Transportista();
        transportista.setNombre(dto.getNombre().trim());
        transportista.setApellido(dto.getApellido() != null ? dto.getApellido().trim() : null);
        transportista.setDni(dto.getDni());
        transportista.setTelefono(dto.getTelefono());
        transportista.setMail(dto.getMail());
        transportista.setDireccion(dto.getDireccion());

        Transportista transportistaGuardado = repo.save(transportista);
        log.info("✅ Transportista creado - ID: {}, Nombre: {} {}",
                transportistaGuardado.getIdTransportista(),
                transportistaGuardado.getNombre(),
                transportistaGuardado.getApellido());

        return transportistaGuardado;
    }

    /**
     * Actualiza un transportista existente
     */
    public Transportista actualizarTransportista(Long idTransportista, TransportistaDTO dto) {
        log.info("🔄 Actualizando transportista con ID: {}", idTransportista);

        Transportista transportista = repo.findById(idTransportista)
                .orElseThrow(() -> new RuntimeException("Transportista no encontrado con ID: " + idTransportista));

        // Actualizar atributos si se proporcionan
        if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
            transportista.setNombre(dto.getNombre().trim());
        }
        if (dto.getApellido() != null) {
            transportista.setApellido(dto.getApellido().trim());
        }
        if (dto.getDni() != null) {
            transportista.setDni(dto.getDni());
        }
        if (dto.getTelefono() != null) {
            transportista.setTelefono(dto.getTelefono());
        }
        if (dto.getMail() != null) {
            transportista.setMail(dto.getMail());
        }
        if (dto.getDireccion() != null) {
            transportista.setDireccion(dto.getDireccion());
        }

        Transportista transportistaActualizado = repo.save(transportista);
        log.info("✅ Transportista {} {} actualizado correctamente",
                transportistaActualizado.getNombre(),
                transportistaActualizado.getApellido());

        return transportistaActualizado;
    }
}
