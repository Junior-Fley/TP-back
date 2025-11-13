package com.microservicio.rutas.services;


import com.microservicio.rutas.dtos.RutaResumenDTO;
import com.microservicio.rutas.dtos.RutaTentativaDTO;
import com.microservicio.rutas.dtos.TramoSugeridoDTO;
import com.microservicio.rutas.models.Rutas;
import com.microservicio.rutas.models.Tramo;
import com.microservicio.rutas.repositories.RutasRepository;
import com.microservicio.rutas.repositories.TramoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutasService {

    private final RutasRepository repo;
    private final TramoRepository tramoRepository;

    // 🔹 Obtener todas las rutas
    public List<Rutas> obtenerTodas() {
        return repo.findAll();
    }

    // 🔹 Obtener una ruta por su ID
    public Rutas obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    // 🔹 Crear una nueva ruta
    public Rutas crear(Rutas ruta) {
        // ⚠️ FIX: Asegurar que los tramos tengan la referencia bidireccional a la ruta
        if (ruta.getTramos() != null && !ruta.getTramos().isEmpty()) {
            for (Tramo tramo : ruta.getTramos()) {
                tramo.setRuta(ruta);
            }
        }
        return repo.save(ruta);
    }

    // 🔹 Eliminar una ruta
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // 🔹 Obtener resumen de una ruta (para comunicación entre microservicios)
    public RutaResumenDTO obtenerResumen(Long id) {
        Rutas ruta = repo.findById(id).orElse(null);
        if (ruta == null) {
            return null;
        }

        // Calcular el costo aproximado de todos los tramos
        BigDecimal costoAproximado = BigDecimal.ZERO;
        if (ruta.getTramos() != null && !ruta.getTramos().isEmpty()) {
            costoAproximado = ruta.getTramos().stream()
                    .map(t -> t.getCostoAproximado() != null ? t.getCostoAproximado() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return new RutaResumenDTO(
                ruta.getIdRuta(),
                ruta.getCantidadTramos(),
                ruta.getCantidadDepositos(),
                costoAproximado
        );
    }

    /**
     * 🔹 Requerimiento Funcional #3:
     * Consultar rutas tentativas con todos los tramos sugeridos y el tiempo y costo estimados
     * (Operador / Administrador)
     */
    public List<RutaTentativaDTO> obtenerRutasTentativas() {
        List<Rutas> rutas = repo.findAll();
        return rutas.stream()
                .map(this::convertirARutaTentativa)
                .collect(Collectors.toList());
    }

    /**
     * Obtener una ruta tentativa específica por ID
     */
    public RutaTentativaDTO obtenerRutaTentativaPorId(Long id) {
        Rutas ruta = repo.findById(id).orElse(null);
        if (ruta == null) {
            return null;
        }
        return convertirARutaTentativa(ruta);
    }

    /**
     * Convierte una entidad Rutas a RutaTentativaDTO con todos sus tramos
     */
    private RutaTentativaDTO convertirARutaTentativa(Rutas ruta) {
        // Calcular totales
        BigDecimal costoTotal = BigDecimal.ZERO;
        Double tiempoTotal = 0.0;

        List<TramoSugeridoDTO> tramosSugeridos = null;

        if (ruta.getTramos() != null && !ruta.getTramos().isEmpty()) {
            tramosSugeridos = ruta.getTramos().stream()
                    .map(this::convertirATramoSugerido)
                    .collect(Collectors.toList());

            // Calcular costo total estimado
            costoTotal = ruta.getTramos().stream()
                    .map(t -> t.getCostoAproximado() != null ? t.getCostoAproximado() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular tiempo total (si existe lógica de tiempo por tramo)
            // Por ahora usamos el tiempoEstimadoMin de la ruta si está disponible
        }

        return new RutaTentativaDTO(
                ruta.getIdRuta(),
                ruta.getCantidadTramos(),
                ruta.getCantidadDepositos(),
                ruta.getDistanciaTotal(),
                ruta.getTiempoEstimadoMin() != null ? ruta.getTiempoEstimadoMin() : tiempoTotal,
                ruta.getCostoTotal() != null ? BigDecimal.valueOf(ruta.getCostoTotal()) : costoTotal,
                tramosSugeridos
        );
    }

    /**
     * Convierte un Tramo a TramoSugeridoDTO
     */
    private TramoSugeridoDTO convertirATramoSugerido(Tramo tramo) {
        String origen = obtenerNombreDeposito(tramo.getDepositoOrigen());
        String destino = obtenerNombreDeposito(tramo.getDepositoDestino());

        // Calcular tiempo estimado en minutos si hay fechas
        Integer tiempoMinutos = null;
        if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
            long minutos = java.time.Duration.between(
                    tramo.getFechaHoraInicio(),
                    tramo.getFechaHoraFin()
            ).toMinutes();
            tiempoMinutos = (int) minutos;
        }

        return new TramoSugeridoDTO(
                tramo.getIdTramo(),
                origen,
                destino,
                tramo.getLatitudOrigen(),
                tramo.getLongitudOrigen(),
                tramo.getLatitudDestino(),
                tramo.getLongitudDestino(),
                tramo.getDistanciaKm(),
                tramo.getCostoAproximado(),
                tiempoMinutos,
                tramo.getTipoTramo() != null ? tramo.getTipoTramo().getNombre() : null,
                tramo.getEstado() != null ? tramo.getEstado().getNombre() : null,
                tramo.getFechaHoraInicio(),
                tramo.getFechaHoraFin()
        );
    }

    /**
     * Helper para obtener el nombre del depósito (si existe)
     */
    private String obtenerNombreDeposito(com.microservicio.rutas.models.Deposito deposito) {
        if (deposito == null) {
            return "Sin depósito asignado";
        }
        return deposito.getNombre() != null ? deposito.getNombre() : "Depósito ID: " + deposito.getIdDeposito();
    }

    /**
     * Obtiene todos los tramos de una ruta específica
     * Necesario para calcular el costo final de una solicitud
     */
    public List<Tramo> obtenerTramosPorRuta(Long idRuta) {
        // Verificar que la ruta existe
        if (!repo.existsById(idRuta)) {
            throw new RuntimeException("Ruta no encontrada con ID: " + idRuta);
        }

        // Buscar tramos directamente por id_ruta usando el TramoRepository
        List<Tramo> tramos = tramoRepository.findByRutaIdRuta(idRuta);

        if (tramos == null || tramos.isEmpty()) {
            throw new RuntimeException("No se encontraron tramos para la ruta ID: " + idRuta);
        }

        return tramos;
    }

    /**
     * 🛠️ UTILIDAD: Corrige la relación bidireccional de tramos que no tienen id_ruta asignado
     * Este método busca todos los tramos que están asociados a una ruta por la relación JPA
     * pero que no tienen el campo id_ruta en la base de datos
     */
    public String corregirRelacionTramos() {
        // Obtener TODOS los tramos sin filtro
        List<Tramo> todosLosTramos = tramoRepository.findAll();
        int tramosCorregidos = 0;
        int tramosSinRuta = 0;

        for (Tramo tramo : todosLosTramos) {
            // Si el tramo no tiene ruta asignada, es un tramo huérfano
            if (tramo.getRuta() == null || tramo.getRuta().getIdRuta() == null) {
                tramosSinRuta++;
                // Intentar encontrar la ruta buscando en todas las rutas
                // Este es un caso donde necesitamos asignar manualmente
                // Por ahora solo los contamos
            }
        }

        // Ahora intentamos con el enfoque de cargar las rutas con FETCH
        List<Long> idsRutas = repo.findAll().stream()
                .map(Rutas::getIdRuta)
                .collect(Collectors.toList());

        for (Long idRuta : idsRutas) {
            try {
                Rutas ruta = repo.findByIdWithTramos(idRuta).orElse(null);
                if (ruta != null && ruta.getTramos() != null) {
                    for (Tramo tramo : ruta.getTramos()) {
                        if (tramo.getRuta() == null || !tramo.getRuta().getIdRuta().equals(ruta.getIdRuta())) {
                            tramo.setRuta(ruta);
                            tramoRepository.save(tramo);
                            tramosCorregidos++;
                        }
                    }
                }
            } catch (Exception e) {
                // Continuar con la siguiente ruta
            }
        }

        return String.format("Se corrigieron %d tramos. Tramos huérfanos encontrados: %d",
                tramosCorregidos, tramosSinRuta);
    }

    /**
     * 🛠️ UTILIDAD: Asigna manualmente un tramo específico a una ruta
     */
    public String asignarTramoARuta(Long idRuta, Long idTramo) {
        // Verificar que la ruta existe
        Rutas ruta = repo.findById(idRuta)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + idRuta));

        // Verificar que el tramo existe
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + idTramo));

        // Asignar la ruta al tramo
        tramo.setRuta(ruta);
        tramoRepository.save(tramo);

        return String.format("Tramo ID %d asignado correctamente a Ruta ID %d", idTramo, idRuta);
    }

    /**
     * 🛠️ DEBUG: Lista todos los tramos con su información de asignación a rutas
     */
    public List<java.util.Map<String, Object>> listarTodosLosTramosConEstado() {
        List<Tramo> todosLosTramos = tramoRepository.findAll();

        return todosLosTramos.stream()
                .map(tramo -> {
                    java.util.Map<String, Object> info = new java.util.HashMap<>();
                    info.put("idTramo", tramo.getIdTramo());
                    info.put("idRuta", tramo.getRuta() != null ? tramo.getRuta().getIdRuta() : null);
                    info.put("rutaAsignada", tramo.getRuta() != null ? "Sí" : "NO - HUÉRFANO");
                    info.put("estado", tramo.getEstado() != null ? tramo.getEstado().getNombre() : "sin estado");
                    info.put("origen", tramo.getDepositoOrigen() != null ? tramo.getDepositoOrigen().getNombre() : "sin origen");
                    info.put("destino", tramo.getDepositoDestino() != null ? tramo.getDepositoDestino().getNombre() : "sin destino");
                    return info;
                })
                .collect(Collectors.toList());
    }

    /**
     * 🛠️ UTILIDAD: Recalcula el campo cantidadTramos de todas las rutas basándose en los tramos reales
     */
    public String recalcularCantidadTramos() {
        List<Rutas> todasLasRutas = repo.findAll();
        int rutasActualizadas = 0;

        for (Rutas ruta : todasLasRutas) {
            // Contar los tramos reales de esta ruta
            int cantidadReal = tramoRepository.findByRutaIdRuta(ruta.getIdRuta()).size();

            // Si la cantidad actual es diferente, actualizar
            if (ruta.getCantidadTramos() == null || ruta.getCantidadTramos() != cantidadReal) {
                ruta.setCantidadTramos(cantidadReal);
                repo.save(ruta);
                rutasActualizadas++;
            }
        }

        return String.format("Se actualizaron %d rutas con la cantidad correcta de tramos", rutasActualizadas);
    }

}
