package com.microservicio.rutas.services;

import com.microservicio.rutas.dtos.*;
import com.microservicio.rutas.models.Deposito;
import com.microservicio.rutas.repositories.DepositoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🎯 Servicio para generar rutas tentativas con OSRM
 *
 * Genera 3 opciones de ruta:
 * 1. Ruta directa (1 tramo)
 * 2. Ruta con 1 depósito intermedio (2 tramos)
 * 3. Ruta con 2 depósitos intermedios (3 tramos)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RutasTentativasService {

    private final OSRMService osrmService;
    private final DepositoRepository depositoRepository;

    // Constantes para cálculo de costos
    private static final double COSTO_BASE_POR_KM = 150.0; // $150 por km
    private static final double COSTO_COMBUSTIBLE_POR_KM = 80.0; // $80 por km
    private static final double CARGO_GESTION_TRAMO = 5000.0; // $5000 por tramo

    /**
     * 🚀 Método principal: genera las 3 rutas tentativas
     */
    public RutasTentativasResponseDTO generarRutasTentativas(GenerarRutasTentativasRequestDTO request) {
        log.info("📍 Generando rutas tentativas desde ({}, {}) hasta ({}, {})",
                request.getLatitudOrigen(), request.getLongitudOrigen(),
                request.getLatitudDestino(), request.getLongitudDestino());

        RutasTentativasResponseDTO response = new RutasTentativasResponseDTO();
        response.setLatitudOrigen(request.getLatitudOrigen());
        response.setLongitudOrigen(request.getLongitudOrigen());
        response.setLatitudDestino(request.getLatitudDestino());
        response.setLongitudDestino(request.getLongitudDestino());

        // 🟦 Ruta 1: Directa (1 tramo)
        log.info("🟦 Generando Ruta 1: Directa");
        response.setRutaDirecta(generarRutaDirecta(request));

        // 🟧 Ruta 2: Con 1 depósito intermedio (2 tramos)
        log.info("🟧 Generando Ruta 2: Con 1 depósito");
        response.setRutaCon1Deposito(generarRutaCon1Deposito(request));

        // 🟩 Ruta 3: Con 2 depósitos intermedios (3 tramos)
        log.info("🟩 Generando Ruta 3: Con 2 depósitos");
        response.setRutaCon2Depositos(generarRutaCon2Depositos(request));

        log.info("✅ Rutas tentativas generadas exitosamente");
        return response;
    }

    /**
     * 🟦 RUTA 1: Directa (1 tramo)
     * Un único tramo directo desde origen hasta destino
     */
    private RutaTentativaDTO generarRutaDirecta(GenerarRutasTentativasRequestDTO request) {
        RutaTentativaDTO ruta = new RutaTentativaDTO();
        ruta.setTipo("DIRECTA");
        ruta.setDescripcion("Ruta directa sin paradas intermedias");
        ruta.setCantidadTramos(1);
        ruta.setDepositosIntermedios(new ArrayList<>());

        List<TramoTentativoDTO> tramos = new ArrayList<>();

        // Crear el único tramo
        TramoTentativoDTO tramo = crearTramo(
                1,
                "directo",
                request.getLatitudOrigen(), request.getLongitudOrigen(), "Punto de origen", null,
                request.getLatitudDestino(), request.getLongitudDestino(), "Punto de destino", null
        );

        tramos.add(tramo);
        ruta.setTramos(tramos);

        // Calcular totales
        ruta.setDistanciaTotalKm(tramo.getDistanciaKm());
        ruta.setDuracionTotalMinutos(tramo.getDuracionMinutos());
        ruta.setCostoTotalAproximado(tramo.getCostoAproximado());

        return ruta;
    }

    /**
     * 🟧 RUTA 2: Con 1 depósito intermedio (2 tramos)
     * Encuentra el depósito más cercano al punto medio entre origen y destino
     */
    private RutaTentativaDTO generarRutaCon1Deposito(GenerarRutasTentativasRequestDTO request) {
        RutaTentativaDTO ruta = new RutaTentativaDTO();
        ruta.setTipo("CON_1_DEPOSITO");
        ruta.setCantidadTramos(2);

        // 1. Calcular punto medio entre origen y destino
        double latitudMedio = (request.getLatitudOrigen() + request.getLatitudDestino()) / 2.0;
        double longitudMedio = (request.getLongitudOrigen() + request.getLongitudDestino()) / 2.0;

        log.info("📍 Punto medio calculado: ({}, {})", latitudMedio, longitudMedio);

        // 2. Encontrar el depósito más cercano al punto medio
        Deposito depositoIntermedio = encontrarDepositoMasCercano(latitudMedio, longitudMedio);

        if (depositoIntermedio == null) {
            log.warn("⚠️ No se encontró ningún depósito para ruta con 1 depósito");
            return generarRutaVacia("CON_1_DEPOSITO", "No se encontraron depósitos disponibles");
        }

        log.info("✅ Depósito intermedio seleccionado: {} (ID: {})",
                depositoIntermedio.getNombre(), depositoIntermedio.getIdDeposito());

        ruta.setDescripcion("Ruta con parada en " + depositoIntermedio.getNombre());
        ruta.setDepositosIntermedios(List.of(depositoIntermedio.getNombre()));

        List<TramoTentativoDTO> tramos = new ArrayList<>();

        // Tramo A: Origen → Depósito
        TramoTentativoDTO tramoA = crearTramo(
                1,
                "a_deposito",
                request.getLatitudOrigen(), request.getLongitudOrigen(), "Punto de origen", null,
                depositoIntermedio.getLatitud(), depositoIntermedio.getLongitud(),
                depositoIntermedio.getNombre(), depositoIntermedio.getIdDeposito()
        );
        tramoA.setCostoEstadiaDiario(depositoIntermedio.getCostoEstadiaDiario());
        tramos.add(tramoA);

        // Tramo B: Depósito → Destino
        TramoTentativoDTO tramoB = crearTramo(
                2,
                "desde_deposito",
                depositoIntermedio.getLatitud(), depositoIntermedio.getLongitud(),
                depositoIntermedio.getNombre(), depositoIntermedio.getIdDeposito(),
                request.getLatitudDestino(), request.getLongitudDestino(), "Punto de destino", null
        );
        tramos.add(tramoB);

        ruta.setTramos(tramos);

        // Calcular totales
        calcularTotalesRuta(ruta);

        return ruta;
    }

    /**
     * 🟩 RUTA 3: Con 2 depósitos intermedios (3 tramos)
     * Selecciona dos depósitos estratégicamente ubicados entre origen y destino
     */
    private RutaTentativaDTO generarRutaCon2Depositos(GenerarRutasTentativasRequestDTO request) {
        RutaTentativaDTO ruta = new RutaTentativaDTO();
        ruta.setTipo("CON_2_DEPOSITOS");
        ruta.setCantidadTramos(3);

        // 1. Calcular punto al 33% del recorrido (primer depósito)
        double lat33 = request.getLatitudOrigen() + (request.getLatitudDestino() - request.getLatitudOrigen()) * 0.33;
        double lon33 = request.getLongitudOrigen() + (request.getLongitudDestino() - request.getLongitudOrigen()) * 0.33;

        // 2. Calcular punto al 66% del recorrido (segundo depósito)
        double lat66 = request.getLatitudOrigen() + (request.getLatitudDestino() - request.getLatitudOrigen()) * 0.66;
        double lon66 = request.getLongitudOrigen() + (request.getLongitudDestino() - request.getLongitudOrigen()) * 0.66;

        log.info("📍 Punto 33%: ({}, {})", lat33, lon33);
        log.info("📍 Punto 66%: ({}, {})", lat66, lon66);

        // 3. Encontrar los depósitos más cercanos
        Deposito deposito1 = encontrarDepositoMasCercano(lat33, lon33);
        Deposito deposito2 = encontrarDepositoMasCercanoExcluyendo(lat66, lon66, deposito1);

        if (deposito1 == null || deposito2 == null) {
            log.warn("⚠️ No se encontraron suficientes depósitos para ruta con 2 depósitos");
            return generarRutaVacia("CON_2_DEPOSITOS", "No se encontraron suficientes depósitos disponibles");
        }

        log.info("✅ Depósito 1 seleccionado: {} (ID: {})", deposito1.getNombre(), deposito1.getIdDeposito());
        log.info("✅ Depósito 2 seleccionado: {} (ID: {})", deposito2.getNombre(), deposito2.getIdDeposito());

        ruta.setDescripcion("Ruta con paradas en " + deposito1.getNombre() + " y " + deposito2.getNombre());
        ruta.setDepositosIntermedios(List.of(deposito1.getNombre(), deposito2.getNombre()));

        List<TramoTentativoDTO> tramos = new ArrayList<>();

        // Tramo A: Origen → Depósito 1
        TramoTentativoDTO tramoA = crearTramo(
                1,
                "a_deposito",
                request.getLatitudOrigen(), request.getLongitudOrigen(), "Punto de origen", null,
                deposito1.getLatitud(), deposito1.getLongitud(), deposito1.getNombre(), deposito1.getIdDeposito()
        );
        tramoA.setCostoEstadiaDiario(deposito1.getCostoEstadiaDiario());
        tramos.add(tramoA);

        // Tramo B: Depósito 1 → Depósito 2
        TramoTentativoDTO tramoB = crearTramo(
                2,
                "entre_depositos",
                deposito1.getLatitud(), deposito1.getLongitud(), deposito1.getNombre(), deposito1.getIdDeposito(),
                deposito2.getLatitud(), deposito2.getLongitud(), deposito2.getNombre(), deposito2.getIdDeposito()
        );
        tramoB.setCostoEstadiaDiario(deposito2.getCostoEstadiaDiario());
        tramos.add(tramoB);

        // Tramo C: Depósito 2 → Destino
        TramoTentativoDTO tramoC = crearTramo(
                3,
                "desde_deposito",
                deposito2.getLatitud(), deposito2.getLongitud(), deposito2.getNombre(), deposito2.getIdDeposito(),
                request.getLatitudDestino(), request.getLongitudDestino(), "Punto de destino", null
        );
        tramos.add(tramoC);

        ruta.setTramos(tramos);

        // Calcular totales
        calcularTotalesRuta(ruta);

        return ruta;
    }

    /**
     * 🔨 Crea un tramo consultando OSRM y calculando costos
     */
    private TramoTentativoDTO crearTramo(
            int orden, String tipo,
            Double latOrigen, Double lonOrigen, String nombreOrigen, Long idDepositoOrigen,
            Double latDestino, Double lonDestino, String nombreDestino, Long idDepositoDestino) {

        TramoTentativoDTO tramo = new TramoTentativoDTO();
        tramo.setOrden(orden);
        tramo.setTipoTramo(tipo);

        // Coordenadas de origen
        tramo.setLatitudOrigen(latOrigen);
        tramo.setLongitudOrigen(lonOrigen);
        tramo.setNombreOrigen(nombreOrigen);
        tramo.setIdDepositoOrigen(idDepositoOrigen);

        // Coordenadas de destino
        tramo.setLatitudDestino(latDestino);
        tramo.setLongitudDestino(lonDestino);
        tramo.setNombreDestino(nombreDestino);
        tramo.setIdDepositoDestino(idDepositoDestino);

        // Consultar OSRM para obtener distancia y duración
        try {
            OSRMRequestDTO osrmRequest = new OSRMRequestDTO();
            osrmRequest.setLatitudOrigen(latOrigen);
            osrmRequest.setLongitudOrigen(lonOrigen);
            osrmRequest.setLatitudDestino(latDestino);
            osrmRequest.setLongitudDestino(lonDestino);

            DistanciaRutaDTO distanciaRuta = osrmService.calcularDistanciaYTiempo(osrmRequest);

            tramo.setDistanciaKm(distanciaRuta.getDistanciaKm());
            tramo.setDuracionMinutos(distanciaRuta.getTiempoMinutos());

            // Calcular costo aproximado
            BigDecimal costoAproximado = calcularCostoAproximado(distanciaRuta.getDistanciaKm());
            tramo.setCostoAproximado(costoAproximado);

            log.info("✅ Tramo {} creado: {} → {} | {} km | {} min | ${}",
                    orden, nombreOrigen, nombreDestino,
                    String.format("%.2f", distanciaRuta.getDistanciaKm()),
                    String.format("%.2f", distanciaRuta.getTiempoMinutos()),
                    costoAproximado);

        } catch (Exception e) {
            log.error("❌ Error al crear tramo {}: {}", orden, e.getMessage());
            // Valores por defecto en caso de error
            tramo.setDistanciaKm(0.0);
            tramo.setDuracionMinutos(0.0);
            tramo.setCostoAproximado(BigDecimal.ZERO);
        }

        return tramo;
    }

    /**
     * 💰 Calcula el costo aproximado de un tramo
     */
    private BigDecimal calcularCostoAproximado(double distanciaKm) {
        double costoBase = distanciaKm * COSTO_BASE_POR_KM;
        double costoCombustible = distanciaKm * COSTO_COMBUSTIBLE_POR_KM;
        double total = costoBase + costoCombustible + CARGO_GESTION_TRAMO;

        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 📊 Calcula los totales de una ruta (suma de todos los tramos)
     */
    private void calcularTotalesRuta(RutaTentativaDTO ruta) {
        double distanciaTotal = 0.0;
        double duracionTotal = 0.0;
        BigDecimal costoTotal = BigDecimal.ZERO;

        for (TramoTentativoDTO tramo : ruta.getTramos()) {
            distanciaTotal += tramo.getDistanciaKm() != null ? tramo.getDistanciaKm() : 0.0;
            duracionTotal += tramo.getDuracionMinutos() != null ? tramo.getDuracionMinutos() : 0.0;
            costoTotal = costoTotal.add(tramo.getCostoAproximado() != null ? tramo.getCostoAproximado() : BigDecimal.ZERO);
        }

        ruta.setDistanciaTotalKm(distanciaTotal);
        ruta.setDuracionTotalMinutos(duracionTotal);
        ruta.setCostoTotalAproximado(costoTotal);
    }

    /**
     * 🔍 Encuentra el depósito más cercano a un punto dado
     */
    private Deposito encontrarDepositoMasCercano(double latitud, double longitud) {
        List<Deposito> depositos = depositoRepository.findAll();

        return depositos.stream()
                .min(Comparator.comparingDouble(d -> calcularDistancia(latitud, longitud, d.getLatitud(), d.getLongitud())))
                .orElse(null);
    }

    /**
     * 🔍 Encuentra el depósito más cercano excluyendo uno específico
     */
    private Deposito encontrarDepositoMasCercanoExcluyendo(double latitud, double longitud, Deposito excluir) {
        List<Deposito> depositos = depositoRepository.findAll();

        return depositos.stream()
                .filter(d -> excluir == null || !d.getIdDeposito().equals(excluir.getIdDeposito()))
                .min(Comparator.comparingDouble(d -> calcularDistancia(latitud, longitud, d.getLatitud(), d.getLongitud())))
                .orElse(null);
    }

    /**
     * 📏 Calcula la distancia euclidiana entre dos puntos (aproximación simple)
     */
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        return Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon);
    }

    /**
     * 🚫 Genera una ruta vacía cuando no hay depósitos disponibles
     */
    private RutaTentativaDTO generarRutaVacia(String tipo, String descripcion) {
        RutaTentativaDTO ruta = new RutaTentativaDTO();
        ruta.setTipo(tipo);
        ruta.setDescripcion(descripcion);
        ruta.setCantidadTramos(0);
        ruta.setTramos(new ArrayList<>());
        ruta.setDistanciaTotalKm(0.0);
        ruta.setDuracionTotalMinutos(0.0);
        ruta.setCostoTotalAproximado(BigDecimal.ZERO);
        ruta.setDepositosIntermedios(new ArrayList<>());
        return ruta;
    }
}

