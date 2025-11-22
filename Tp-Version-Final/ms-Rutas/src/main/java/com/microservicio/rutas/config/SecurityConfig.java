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
 * 🔒 CONFIGURACIÓN DE SEGURIDAD CON KEYCLOAK
 * Configuración de seguridad para ms-Rutas con autenticación y autorización
 * Roles: ADMIN, CLIENTE, TRANSPORTISTA
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
                        .anyRequest().permitAll()/// Comentarrrrrr o borrar esta línea para activar seguridad
//
//                        // Endpoints públicos (Swagger, actuator)
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
//
//                        // === RUTAS ===
//                        // GET /api/rutas - Listar todas las rutas: Solo ADMIN
//                        .requestMatchers(HttpMethod.GET, "/api/rutas").hasRole("ADMIN")
//
//                        // GET /api/rutas/tentativas - Listar rutas tentativas: Solo ADMIN
//                        .requestMatchers(HttpMethod.GET, "/api/rutas/tentativas").hasRole("ADMIN")
//
//                        // GET /api/rutas/{id}/tentativa - Ver ruta tentativa específica: Solo ADMIN
//                        .requestMatchers(HttpMethod.GET, "/api/rutas/*/tentativa").hasRole("ADMIN")
//
//                        // GET /api/rutas/{id}/resumen - Ver resumen de ruta: ADMIN y CLIENTE
//                        .requestMatchers(HttpMethod.GET, "/api/rutas/*/resumen").hasAnyRole("ADMIN", "CLIENTE")
//
//                        // GET /api/rutas/{id} - Ver detalle de ruta: ADMIN y CLIENTE
//                        .requestMatchers(HttpMethod.GET, "/api/rutas/*").hasAnyRole("ADMIN", "CLIENTE")
//
//                        // POST /api/rutas - Crear ruta: Solo ADMIN
//                        .requestMatchers(HttpMethod.POST, "/api/rutas").hasRole("ADMIN")
//
//                        // DELETE /api/rutas/{id} - Eliminar ruta: Solo ADMIN
//                        .requestMatchers(HttpMethod.DELETE, "/api/rutas/*").hasRole("ADMIN")
//
//                        // === TRAMOS ===
//                        // GET /api/tramos - Listar todos los tramos: Solo ADMIN
//                        .requestMatchers(HttpMethod.GET, "/api/tramos").hasRole("ADMIN")
//
//                        // GET /api/tramos/{id} - Ver tramo específico: ADMIN y TRANSPORTISTA
//                        .requestMatchers(HttpMethod.GET, "/api/tramos/*").hasAnyRole("ADMIN", "TRANSPORTISTA")
//
//                        // POST /api/tramos - Crear tramo: Solo ADMIN
//                        .requestMatchers(HttpMethod.POST, "/api/tramos").hasRole("ADMIN")
//
//                        // DELETE /api/tramos/{id} - Eliminar tramo: Solo ADMIN
//                        .requestMatchers(HttpMethod.DELETE, "/api/tramos/*").hasRole("ADMIN")
//
//                        // PUT /api/tramos/{id}/asignar-camion/{camionId} - Asignar camión: Solo ADMIN
//                        .requestMatchers(HttpMethod.PUT, "/api/tramos/*/asignar-camion/*").hasRole("ADMIN")
//
//                        // === DEPÓSITOS ===
//                        // Todos los endpoints de depósitos: Solo ADMIN
//                        .requestMatchers(HttpMethod.GET, "/api/depositos/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/depositos").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/depositos/*").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/depositos/*").hasRole("ADMIN")
//
//                        // Cualquier otra petición requiere autenticación
//                        .anyRequest().authenticated()
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
