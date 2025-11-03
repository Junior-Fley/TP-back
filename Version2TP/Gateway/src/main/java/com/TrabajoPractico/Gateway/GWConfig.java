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
                                        @Value("${uri.ms-rutas}") String rutas,
                                        @Value("${uri.ms-solicitudes}") String solicitudes,
                                        @Value("${uri.ms-camiones}") String camiones) {

        return builder.routes()
                .route("rutas", r -> r.path("/api/rutas/**").uri(rutas))
                .route("solicitudes", r -> r.path("/api/solicitudes/**").uri(solicitudes))
                .route("camiones", r -> r.path("/api/camiones/**").uri(camiones))
                .build();
    }
}
