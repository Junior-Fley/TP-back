//package com.microservicio.solicitudes.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.web.SecurityFilterChain;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * Configuración de Spring Security con OAuth2 Resource Server (Keycloak)
// * - Valida JWT emitidos por Keycloak
// * - Extrae roles del claim "realm_access" y los convierte en autoridades de Spring Security
// * - Protege todos los endpoints bajo /api/** excepto Swagger
// */
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    /**
//     * Configuración principal de seguridad HTTP
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(auth -> auth
//                // Permitir acceso público a Swagger UI y OpenAPI docs
//                .requestMatchers(
//                    "/swagger-ui/**",
//                    "/v3/api-docs/**",
//                    "/swagger-ui.html",
//                    "/actuator/health"
//                ).permitAll()
//                // Proteger todos los endpoints bajo /api/**
//                .requestMatchers("/api/**").authenticated()
//                .anyRequest().permitAll()
//            )
//            .oauth2ResourceServer(oauth2 -> oauth2
//                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
//            );
//
//        return http.build();
//    }
//
//    /**
//     * Decodificador JWT configurado para Keycloak
//     * IMPORTANTE: Cambiar la URL según tu configuración de Keycloak
//     */
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        String jwkSetUri = "http://localhost:8081/realms/transporte-realm/protocol/openid-connect/certs";
//        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
//    }
//
//    /**
//     * Convertidor de JWT a Authentication de Spring Security
//     * Extrae roles de Keycloak y los convierte en autoridades (ROLE_CLIENTE, ROLE_ADMIN)
//     */
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//            if (realmAccess == null) {
//                return Collections.emptyList();
//            }
//
//            @SuppressWarnings("unchecked")
//            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
//            if (roles == null) {
//                return Collections.emptyList();
//            }
//
//            return roles.stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
//                .collect(Collectors.toList());
//        });
//
//        return converter;
//    }
//}
