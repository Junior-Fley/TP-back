package com.TrabajoPractico.Gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private KeycloakRoleConverter keycloakRoleConverter;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges

                        // =========================
                        // 1) ADMIN - recursos administrativos
                        // =========================
                        .pathMatchers("/api/contenedores/**").hasRole("ADMIN")
                        .pathMatchers("/api/clientes/**").hasRole("ADMIN")
                        .pathMatchers("/api/rutas/**").hasRole("ADMIN")
                        .pathMatchers("/api/tipos-tramo/**").hasRole("ADMIN")
                        .pathMatchers("/api/estados-tramo/**").hasRole("ADMIN")
                        .pathMatchers("/api/osrm/**").hasRole("ADMIN")
                        .pathMatchers("/api/ciudades/**").hasRole("ADMIN")
                        .pathMatchers("/api/depositos/**").hasRole("ADMIN")

                        // =========================
                        // 2) SOLICITUDES
                        // =========================
                        // Crear solicitud: solo CLIENTE
                        .pathMatchers(HttpMethod.POST, "/api/solicitudes")
                        .hasRole("CLIENTE")

                        // Cualquier GET sobre /api/solicitudes/**:
                        // - CLIENTE (por ejemplo, sus solicitudes)
                        // - ADMIN (todas las solicitudes)
                        .pathMatchers(HttpMethod.GET, "/api/solicitudes/**")
                        .hasAnyRole("CLIENTE", "ADMIN")

                        // Para otros métodos (PUT, DELETE, etc.) sobre /api/solicitudes/**
                        // solo ADMIN
                        .pathMatchers("/api/solicitudes/**")
                        .hasRole("ADMIN")

                        // =========================
                        // 3) TRANSPORTISTA
                        // =========================
                        .pathMatchers("/api/camiones/**")
                        .hasAnyRole("ADMIN", "TRANSPORTISTA")
                        .pathMatchers("/api/transportistas/**")
                        .hasAnyRole("ADMIN", "TRANSPORTISTA")
                        .pathMatchers("/api/tramos/**")
                        .hasAnyRole("ADMIN", "TRANSPORTISTA")

                        // =========================
                        // Cualquier otra petición debe estar autenticada
                        // =========================
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                                .jwtDecoder(reactiveJwtDecoder())
                        )
                );

        return http.build();
    }

    private ReactiveJwtAuthenticationConverterAdapter grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(keycloakRoleConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * ReactiveJwtDecoder personalizado con validación lazy y clock-skew
     * Versión reactiva del JwtDecoder para Spring WebFlux Gateway
     */
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return new ReactiveJwtDecoder() {
            private volatile ReactiveJwtDecoder delegate;

            @Override
            public Mono<Jwt> decode(String token) throws JwtException {
                if (delegate == null) {
                    synchronized (this) {
                        if (delegate == null) {
                            try {
                                NimbusReactiveJwtDecoder jwtDecoder = (NimbusReactiveJwtDecoder)
                                        ReactiveJwtDecoders.fromIssuerLocation(issuerUri);

                                // Configura validador de timestamp con clock-skew de 120 segundos
                                JwtTimestampValidator timestampValidator =
                                        new JwtTimestampValidator(Duration.ofSeconds(120));
                                OAuth2TokenValidator<Jwt> withClockSkew =
                                        new DelegatingOAuth2TokenValidator<>(timestampValidator);

                                jwtDecoder.setJwtValidator(withClockSkew);
                                delegate = jwtDecoder;
                            } catch (Exception e) {
                                return Mono.error(new JwtException(
                                        "Error al inicializar ReactiveJwtDecoder: " + e.getMessage(), e));
                            }
                        }
                    }
                }
                return delegate.decode(token);
            }
        };
    }
}
