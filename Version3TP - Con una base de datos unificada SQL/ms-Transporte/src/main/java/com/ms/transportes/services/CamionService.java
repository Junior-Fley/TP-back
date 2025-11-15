package com.ms.transportes.services;


import com.ms.transportes.dtos.AsignarTransportistaDTO;
import com.ms.transportes.dtos.CrearCamionDTO;
import com.ms.transportes.models.Camion;
import com.ms.transportes.models.Transportista;
import com.ms.transportes.repositories.CamionRepository;
import com.ms.transportes.repositories.TransportistaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@Transactional
public class CamionService {

    private final CamionRepository repo;
    private final TransportistaRepository transportistaRepository;

    public CamionService(CamionRepository repo, TransportistaRepository transportistaRepository) {
        this.repo = repo;
        this.transportistaRepository = transportistaRepository;
    }

    @Transactional(readOnly = true)
    public List<Camion> obtenerTodos() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Camion obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Camion guardar(Camion camion) {
        return repo.save(camion);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public Camion actualizarDisponibilidad(Long id, boolean disponible) {
        Camion camion = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado con ID: " + id));
        camion.setDisponibilidad(disponible);
        return repo.save(camion);
    }

    /**
     * Crea un camión sin transportista asignado
     * Genera automáticamente teléfono y disponibilidad
     */
    public Camion crearCamion(CrearCamionDTO dto) {
        log.info("🚚 Creando nuevo camión con patente: {}", dto.getPatente());

        if (repo.findByPatente(dto.getPatente()).isPresent()) {
            throw new RuntimeException("Ya existe un camión con la patente: " + dto.getPatente());
        }

        Camion camion = new Camion();
        camion.setPatente(dto.getPatente().toUpperCase());
        camion.setCapacidadPeso(dto.getCapacidadPeso());
        camion.setCapacidadVolumen(dto.getCapacidadVolumen());
        camion.setCostoBaseKm(dto.getCostoBaseKm());
        camion.setConsumoCombustibleKm(dto.getConsumoCombustibleKm());
        camion.setTelefono(generarTelefonoAleatorio());
        camion.setDisponibilidad(true);
        camion.setTransportista(null);

        Camion camionGuardado = repo.save(camion);
        log.info("✅ Camión creado - ID: {}, Patente: {}, Teléfono: {}",
                camionGuardado.getIdCamion(),
                camionGuardado.getPatente(),
                camionGuardado.getTelefono());

        return camionGuardado;
    }

    /**
     * Asigna un transportista a un camión existente
     */
    public Camion asignarTransportista(Long idCamion, AsignarTransportistaDTO dto) {
        log.info("👤 Asignando transportista {} al camión {}", dto.getIdTransportista(), idCamion);

        Camion camion = repo.findById(idCamion)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado con ID: " + idCamion));

        Transportista transportista = transportistaRepository.findById(dto.getIdTransportista())
                .orElseThrow(() -> new RuntimeException("Transportista no encontrado con ID: " + dto.getIdTransportista()));

        camion.setTransportista(transportista);

        Camion camionActualizado = repo.save(camion);
        log.info("✅ Transportista {} {} asignado al camión {}",
                transportista.getNombre(),
                transportista.getApellido(),
                camion.getPatente());

        return camionActualizado;
    }

    /**
     * Genera un teléfono aleatorio con formato: 351XXXXXXX
     */
    private String generarTelefonoAleatorio() {
        Random random = new Random();
        StringBuilder telefono = new StringBuilder("351");
        for (int i = 0; i < 7; i++) {
            telefono.append(random.nextInt(10));
        }
        return telefono.toString();
    }
}
