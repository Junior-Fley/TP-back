package com.TrabajoPractico.Gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GWConfig {

    @Bean
    public RouteLocator configurarRutas(RouteLocatorBuilder builder,
                                        @Value("${uri.ms-rutas}") String uriRutas,
                                        @Value("${uri.ms-solicitudes}") String uriSolicitudes,
                                        @Value("${uri.ms-camiones}") String uriCamiones,
                                        @Value("${uri.ms-tarifas}") String uriTarifas,

                                        @Value("${paths.ms-rutas}") String pathsRutas,
                                        @Value("${paths.ms-solicitudes}") String pathsSolicitudes,
                                        @Value("${paths.ms-camiones}") String pathsCamiones,
                                        @Value("${paths.ms-tarifas}") String pathsTarifas
    ) {

        return builder.routes()

                // =======================================
                // MICRO RUTAS
                // =======================================
                .route("ms-rutas", r -> r
                        .path(split(pathsRutas))
                        .uri(uriRutas)
                )

                // =======================================
                // MICRO SOLICITUDES
                // =======================================
                .route("ms-solicitudes", r -> r
                        .path(split(pathsSolicitudes))
                        .uri(uriSolicitudes)
                )

                // =======================================
                // MICRO CAMIONES
                // =======================================
                .route("ms-camiones", r -> r
                        .path(split(pathsCamiones))
                        .uri(uriCamiones)
                )

                // =======================================
                // MICRO TARIFAS
                // =======================================
                .route("ms-tarifas", r -> r
                        .path(split(pathsTarifas))
                        .uri(uriTarifas)
                )

                .build();
    }

    /**
     * Convierte la lista de paths del properties en un array para .path()
     */
    private String[] split(String value) {
        return value.split(",\\s*");
    }
}
