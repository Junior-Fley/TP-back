package com.microservicio.rutas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuración de seguridad para ms-Rutas usando Keycloak como Resource Server (JWT).
 * Se habilitan los roles definidos en el enunciado: CLIENTE, ADMIN, TRANSPORTISTA.
 *
 * Decisiones tomadas (suposiciones razonables):
 * - La mayoría de operaciones de gestión (crear, modificar, eliminar) quedan restringidas a ADMIN.
 * - Visualización específica (por id) puede ser accesible a TRANSPORTISTA cuando aplica.
 * - Endpoints referidos a cálculo/tentativas son ADMIN (según comentarios en los controladores).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public / swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()

                        // Rutas
                        .requestMatchers(HttpMethod.GET, "/api/rutas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rutas/tentativas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rutas/*/tentativa").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rutas/*/resumen").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/rutas/*").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/rutas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rutas/*").hasRole("ADMIN")

                        // Tramos
                        .requestMatchers(HttpMethod.GET, "/api/tramos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tramos/*").hasAnyRole("ADMIN", "TRANSPORTISTA")
                        .requestMatchers(HttpMethod.POST, "/api/tramos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tramos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tramos/*/asignar-camion/*").hasRole("ADMIN")

                        // Depositos - gestión por ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/depositos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/depositos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/depositos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/depositos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/depositos/*").hasRole("ADMIN")

                        // Estados y tipos - autenticado
                        .requestMatchers(HttpMethod.GET, "/api/estados-tramo").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tipos-tramo").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return jwtAuthenticationConverter;
    }

    /**
     * Configuración personalizada del JwtDecoder con clock-skew.
     * Permite un margen de 120 segundos para diferencias de sincronización horaria.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

        // Configura validador de timestamp con clock-skew de 120 segundos
        JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(120));
        OAuth2TokenValidator<Jwt> withClockSkew = new DelegatingOAuth2TokenValidator<>(timestampValidator);

        jwtDecoder.setJwtValidator(withClockSkew);
        return jwtDecoder;
    }

    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<String> roles = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
                roles.addAll(realmRoles);
            }

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                for (Object clientAccess : resourceAccess.values()) {
                    if (clientAccess instanceof Map) {
                        Map<String, Object> clientAccessMap = (Map<String, Object>) clientAccess;
                        if (clientAccessMap.containsKey("roles")) {
                            Collection<String> clientRoles = (Collection<String>) clientAccessMap.get("roles");
                            roles.addAll(clientRoles);
                        }
                    }
                }
            }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }
    }
}
