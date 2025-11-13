//package com.TrabajoPractico.Gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsConfigurationSource;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//
///**
// * Configuración de Seguridad para el Gateway (Reactive)
// * Valida tokens JWT de Keycloak y protege las rutas según roles
// */
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .csrf(csrf -> csrf.disable())
//            .authorizeExchange(exchanges -> exchanges
//                // 🔓 Endpoints públicos (sin autenticación)
//                .pathMatchers("/publico/**").permitAll()
//                .pathMatchers("/actuator/**").permitAll()
//
//                // 🔐 CLIENTE: puede registrar y consultar solicitudes de transporte
//                .pathMatchers("/api/solicitudes/**").hasRole("CLIENTE")
//
//                // 🔐 ADMIN (OPERADOR/ADMINISTRADOR): puede gestionar todo
//                .pathMatchers("/api/rutas/**").hasRole("ADMIN")
//                .pathMatchers("/api/tarifas/**").hasRole("ADMIN")
//                .pathMatchers("/api/depositos/**").hasRole("ADMIN")
//                .pathMatchers("/api/camiones/**").hasRole("ADMIN")
//                .pathMatchers("/api/ciudades/**").hasRole("ADMIN")
//                .pathMatchers("/api/contenedores/**").hasRole("ADMIN")
//
//                // 🔐 TRANSPORTISTA (CONDUCTOR/CHOFER): puede ver tramos asignados y registrar inicio/fin
//                .pathMatchers("/api/tramos/**").hasRole("TRANSPORTISTA")
//                .pathMatchers("/api/transportistas/**").hasRole("TRANSPORTISTA")
//
//                // Cualquier otro endpoint requiere autenticación
//                .anyExchange().authenticated()
//            )
//            .oauth2ResourceServer(oauth2 -> oauth2
//                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
//            );
//
//        return http.build();
//    }
//
//    /**
//     * Configuración de CORS para permitir peticiones desde diferentes orígenes
//     */
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.setExposedHeaders(Arrays.asList("Authorization"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    /**
//     * Convierte los roles de Keycloak a autoridades de Spring Security
//     * Keycloak almacena roles en: realm_access.roles
//     */
//    @Bean
//    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//
//        // Keycloak envía los roles sin el prefijo "ROLE_", así que lo agregamos
//        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//
//        // Los roles están en el claim "realm_access.roles" en Keycloak
//        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
//
//        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
//            new ReactiveJwtGrantedAuthoritiesConverterAdapter(grantedAuthoritiesConverter)
//        );
//
//        return jwtAuthenticationConverter;
//    }
//}
