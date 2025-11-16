package com.microservicio.rutas.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Convertidor personalizado para extraer roles de Keycloak
 * Los roles están en realm_access.roles
 */
@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        System.out.println("=== DEBUG: JWT Claims en ms-Rutas ===");
        System.out.println("All claims: " + jwt.getClaims());
        System.out.println("realm_access: " + realmAccess);

        if (realmAccess == null || realmAccess.isEmpty()) {
            System.out.println("⚠️ WARNING: No realm_access found in token");
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        System.out.println("Roles from token: " + roles);

        if (roles == null || roles.isEmpty()) {
            System.out.println("⚠️ WARNING: No roles found in realm_access");
            return Collections.emptyList();
        }

        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());

        System.out.println("✅ Granted authorities: " + authorities);

        return authorities;
    }
}

