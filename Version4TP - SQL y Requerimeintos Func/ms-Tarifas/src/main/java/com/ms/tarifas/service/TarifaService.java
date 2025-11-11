package com.ms.tarifas.service;

import com.ms.tarifas.dto.CalculoCostoDTO;
import com.ms.tarifas.dto.TarifaDTO;
import com.ms.tarifas.entity.Tarifa;
import com.ms.tarifas.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para Tarifas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    /**
     * Obtener todas las tarifas
     */
    @Transactional(readOnly = true)
    public List<Tarifa> obtenerTodas() {
        log.info("Obteniendo todas las tarifas");
        return tarifaRepository.findAll();
    }

    /**
     * Obtener solo tarifas activas
     */
    @Transactional(readOnly = true)
    public List<Tarifa> obtenerActivas() {
        log.info("Obteniendo tarifas activas");
        return tarifaRepository.findByActivoTrue();
    }

    /**
     * Obtener tarifa por ID
     */
    @Transactional(readOnly = true)
    public Tarifa obtenerPorId(Long id) {
        log.info("Buscando tarifa con ID: {}", id);
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con ID: " + id));
    }

    /**
     * Obtener tarifa por tipo
     */
    @Transactional(readOnly = true)
    public Tarifa obtenerPorTipo(String tipo) {
        log.info("Buscando tarifa de tipo: {}", tipo);
        return tarifaRepository.findByTipo(tipo)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada de tipo: " + tipo));
    }

    /**
     * Crear una nueva tarifa
     */
    @Transactional
    public Tarifa crear(TarifaDTO dto) {
        log.info("Creando nueva tarifa de tipo: {}", dto.getTipo());

        if (tarifaRepository.existsByTipo(dto.getTipo())) {
            throw new RuntimeException("Ya existe una tarifa con el tipo: " + dto.getTipo());
        }

        Tarifa tarifa = new Tarifa();
        tarifa.setTipo(dto.getTipo());
        tarifa.setDescripcion(dto.getDescripcion());
        tarifa.setValor(dto.getValor());
        tarifa.setUnidad(dto.getUnidad());
        tarifa.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        tarifa.setFechaActualizacion(LocalDateTime.now());

        return tarifaRepository.save(tarifa);
    }

    /**
     * Actualizar una tarifa existente
     */
    @Transactional
    public Tarifa actualizar(Long id, TarifaDTO dto) {
        log.info("Actualizando tarifa con ID: {}", id);

        Tarifa tarifa = obtenerPorId(id);

        tarifa.setDescripcion(dto.getDescripcion());
        tarifa.setValor(dto.getValor());
        tarifa.setUnidad(dto.getUnidad());
        if (dto.getActivo() != null) {
            tarifa.setActivo(dto.getActivo());
        }
        tarifa.setFechaActualizacion(LocalDateTime.now());

        return tarifaRepository.save(tarifa);
    }

    /**
     * Eliminar (desactivar) una tarifa
     */
    @Transactional
    public void eliminar(Long id) {
        log.info("Desactivando tarifa con ID: {}", id);
        Tarifa tarifa = obtenerPorId(id);
        tarifa.setActivo(false);
        tarifaRepository.save(tarifa);
    }

    /**
     * Calcular costo total de transporte
     */
    @Transactional(readOnly = true)
    public CalculoCostoDTO calcularCostoTransporte(CalculoCostoDTO calculo) {
        log.info("Calculando costo de transporte");

        // Obtener tarifas configuradas
        BigDecimal costoKmBase = obtenerValorTarifa("COSTO_KM_BASE");
        BigDecimal precioCombustible = obtenerValorTarifa("COMBUSTIBLE");
        BigDecimal costoEstadiaDia = obtenerValorTarifa("ESTADIA_DEPOSITO");

        // Cálculo de costo por kilometraje
        BigDecimal costoKilometraje = costoKmBase
                .multiply(calculo.getDistanciaKm())
                .setScale(2, RoundingMode.HALF_UP);

        // Cálculo de costo de combustible
        BigDecimal litrosCombustible = calculo.getConsumoCombustibleLitrosPorKm()
                .multiply(calculo.getDistanciaKm());
        BigDecimal costoCombustible = litrosCombustible
                .multiply(precioCombustible)
                .setScale(2, RoundingMode.HALF_UP);

        // Cálculo de costo de estadía
        BigDecimal costoEstadia = BigDecimal.ZERO;
        if (calculo.getDiasEstadia() != null && calculo.getDiasEstadia() > 0) {
            costoEstadia = costoEstadiaDia
                    .multiply(BigDecimal.valueOf(calculo.getDiasEstadia()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Costo total
        BigDecimal costoTotal = costoKilometraje
                .add(costoCombustible)
                .add(costoEstadia)
                .setScale(2, RoundingMode.HALF_UP);

        // Llenar resultado
        calculo.setCostoKilometraje(costoKilometraje);
        calculo.setCostoCombustible(costoCombustible);
        calculo.setCostoEstadia(costoEstadia);
        calculo.setCostoTotal(costoTotal);

        log.info("Costo total calculado: {}", costoTotal);
        return calculo;
    }

    /**
     * Obtener el valor de una tarifa por su tipo
     */
    private BigDecimal obtenerValorTarifa(String tipo) {
        try {
            Tarifa tarifa = obtenerPorTipo(tipo);
            return tarifa.getValor();
        } catch (RuntimeException e) {
            log.warn("Tarifa no encontrada: {}. Usando valor por defecto.", tipo);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Inicializar tarifas por defecto
     */
    @Transactional
    public void inicializarTarifasDefecto() {
        log.info("Inicializando tarifas por defecto");

        if (tarifaRepository.count() == 0) {
            crearTarifaDefecto("COSTO_KM_BASE", "Costo base por kilómetro", new BigDecimal("5.00"), "km");
            crearTarifaDefecto("COMBUSTIBLE", "Precio del combustible por litro", new BigDecimal("1.50"), "litro");
            crearTarifaDefecto("ESTADIA_DEPOSITO", "Costo de estadía en depósito por día", new BigDecimal("50.00"), "dia");
            log.info("Tarifas por defecto creadas exitosamente");
        }
    }

    private void crearTarifaDefecto(String tipo, String descripcion, BigDecimal valor, String unidad) {
        Tarifa tarifa = new Tarifa();
        tarifa.setTipo(tipo);
        tarifa.setDescripcion(descripcion);
        tarifa.setValor(valor);
        tarifa.setUnidad(unidad);
        tarifa.setActivo(true);
        tarifa.setFechaActualizacion(LocalDateTime.now());
        tarifaRepository.save(tarifa);
    }
}

