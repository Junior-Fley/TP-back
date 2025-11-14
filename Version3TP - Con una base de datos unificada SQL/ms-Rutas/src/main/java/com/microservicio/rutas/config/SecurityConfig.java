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
import org.springframework.security.oauth2.jwt.JwtException;
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
 * ⚠️ SEGURIDAD TEMPORALMENTE DESACTIVADA
 * Configuración de seguridad para ms-Rutas SIN Keycloak
 * Para desarrollo y pruebas sin autenticación
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ⚠️ PERMITE TODO - Solo para desarrollo
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
}

/* ========================================
 * 🔒 CONFIGURACIÓN CON KEYCLOAK (COMENTADA)
 * ========================================
 * Para reactivar Keycloak:
 * 1. Descomenta toda la configuración de abajo
 * 2. Comenta la configuración simple de arriba
 * 3. Descomenta @EnableMethodSecurity
 * 4. Descomenta las propiedades de Keycloak en application.properties
 * 5. Asegúrate de que Keycloak esté corriendo
 */

/*
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
                        .requestMatchers(HttpMethod.GET, "/api/rutas/*\/tentativa").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rutas/*\/resumen").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/rutas/*").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/rutas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rutas/*").hasRole("ADMIN")

                        // Tramos
                        .requestMatchers(HttpMethod.GET, "/api/tramos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tramos/*").hasAnyRole("ADMIN", "TRANSPORTISTA")
                        .requestMatchers(HttpMethod.POST, "/api/tramos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tramos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tramos/*\/asignar-camion/*").hasRole("ADMIN")

                        // Depositos
                        .requestMatchers(HttpMethod.GET, "/api/depositos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/depositos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/depositos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/depositos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/depositos/*").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return new JwtDecoder() {
            private volatile JwtDecoder delegate;

            @Override
            public Jwt decode(String token) throws JwtException {
                if (delegate == null) {
                    synchronized (this) {
                        if (delegate == null) {
                            try {
                                NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
                                JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(120));
                                OAuth2TokenValidator<Jwt> withClockSkew = new DelegatingOAuth2TokenValidator<>(timestampValidator);
                                jwtDecoder.setJwtValidator(withClockSkew);
                                delegate = jwtDecoder;
                            } catch (Exception e) {
                                throw new JwtException("Error al inicializar JwtDecoder: " + e.getMessage(), e);
                            }
                        }
                    }
                }
                return delegate.decode(token);
            }
        };
    }
}
*/
