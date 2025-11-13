package com.microservicio.solicitudes.config;

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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuración de Seguridad con Keycloak para el Microservicio de Solicitudes
 *
 * Roles definidos según el enunciado del TP:
 * - CLIENTE: Puede registrar solicitudes de traslado, consultar estado de contenedores, ver costos y tiempos
 * - ADMIN (Operador/Administrador): Gestiona clientes, contenedores, asigna rutas y camiones
 * - TRANSPORTISTA: No tiene acceso a este microservicio (usa ms-Rutas para sus tramos)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 🔓 Endpoints públicos (Swagger, Actuator)
                .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 📋 SOLICITUDES - CLIENTE puede crear y consultar sus solicitudes
                .requestMatchers(HttpMethod.POST, "/api/solicitudes").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/completa").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/contenedor/*/estado").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/*/rutas").hasAnyRole("CLIENTE", "ADMIN")

                // 📋 SOLICITUDES - ADMIN puede ver todas las solicitudes y eliminar
                .requestMatchers(HttpMethod.GET, "/api/solicitudes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/*").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/solicitudes/*").hasRole("ADMIN")

                // 🚚 RUTAS - Solo ADMIN puede asignar/desasignar rutas
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes/*/asignar-ruta").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/solicitudes/*/desasignar-ruta").hasRole("ADMIN")

                // 📦 CONTENEDORES PENDIENTES - Solo ADMIN puede consultar contenedores pendientes
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/contenedores-pendientes").hasRole("ADMIN")

                // 📦 CONTENEDORES - Solo ADMIN puede gestionar contenedores
                .requestMatchers(HttpMethod.GET, "/api/contenedores").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/contenedores/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/contenedores").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/contenedores/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/contenedores/*").hasRole("ADMIN")

                // 👥 CLIENTES - Solo ADMIN puede gestionar clientes
                .requestMatchers(HttpMethod.GET, "/api/clientes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/clientes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/clientes/dni/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/clientes/mail/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/clientes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/clientes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/*").hasRole("ADMIN")

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * Configuración de CORS
     */
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

    /**
     * Convertidor para extraer roles de Keycloak desde el token JWT
     * Los roles vienen en: realm_access.roles
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return jwtAuthenticationConverter;
    }

    /**
     * Extrae los roles desde realm_access.roles Y resource_access del token JWT de Keycloak
     * y los convierte en autoridades de Spring Security con el prefijo ROLE_
     */
    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<String> roles = new ArrayList<>();

            // Extraer roles del realm (realm_access.roles)
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
                roles.addAll(realmRoles);
            }

            // Extraer roles de los clients (resource_access.{client-id}.roles)
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

            // Convertir roles a autoridades con el prefijo ROLE_
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }
    }
}
