package com.microservicio.solicitudes.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class RutasApiConfig {

    @Bean
    RestClient rutasRestClient(@Value("${api.rutas.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    // Obtener el token JWT del contexto de seguridad
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (authentication instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                        Jwt jwt = jwtAuth.getToken();
                        String token = jwt.getTokenValue();

                        // Agregar el header Authorization con el token
                        request.getHeaders().setBearerAuth(token);
                    }

                    return execution.execute(request, body);
                })
                .build();
    }
}
