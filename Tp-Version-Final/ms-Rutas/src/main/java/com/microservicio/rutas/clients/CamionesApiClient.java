package com.microservicio.rutas.clients;

import com.microservicio.rutas.dtos.CamionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class CamionesApiClient {
    private final RestClient camionesRestClient;

    /**
     * Verifica si un camión existe en el microservicio de Transporte
     * @param idCamion ID del camión a verificar
     * @return true si el camión existe, false en caso contrario
     */
    public boolean existeCamion(Long idCamion) {
        try {
            log.info("🚛 Verificando existencia del camión con ID: {}", idCamion);

            CamionDTO camion = camionesRestClient.get()
                    .uri("/{id}", idCamion)
                    .retrieve()
                    .body(CamionDTO.class);

            boolean existe = camion != null;
            log.info(existe ? "✅ Camión ID {} encontrado: {}" : "❌ Camión ID {} no encontrado",
                    idCamion, existe ? camion.getPatente() : "N/A");

            return existe;

        } catch (Exception e) {
            log.error("❌ Error al verificar la existencia del camión ID {}: {}", idCamion, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene los datos completos de un camión
     * @param idCamion ID del camión
     * @return DTO con datos del camión o null si no existe
     */
    public CamionDTO obtenerCamion(Long idCamion) {
        try {
            log.info("🚛 Obteniendo datos del camión ID: {}", idCamion);

            return camionesRestClient.get()
                    .uri("/{id}", idCamion)
                    .retrieve()
                    .body(CamionDTO.class);

        } catch (Exception e) {
            log.error("❌ Error al obtener el camión ID {}: {}", idCamion, e.getMessage());
            return null;
        }
    }

    /**
     * Actualiza la disponibilidad de un camión
     * @param idCamion ID del camión
     * @param disponible true para marcar como disponible, false para ocupado
     */
    public void actualizarDisponibilidad(Long idCamion, boolean disponible) {
        try {
            log.info("🔄 Actualizando disponibilidad del camión ID {} a: {}",
                    idCamion, disponible ? "DISPONIBLE" : "OCUPADO");

            camionesRestClient.patch()
                    .uri("/{id}/disponibilidad?disponible={disponible}", idCamion, disponible)
                    .retrieve()
                    .toBodilessEntity();

            log.info("✅ Disponibilidad actualizada exitosamente");

        } catch (Exception e) {
            log.error("❌ Error al actualizar disponibilidad del camión ID {}: {}", idCamion, e.getMessage());
            throw new RuntimeException("No se pudo actualizar la disponibilidad del camión: " + e.getMessage());
        }
    }
}
