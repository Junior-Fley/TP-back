//package com.TrabajoPractico.Gateway.config;
//
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * Convertidor personalizado para extraer roles de Keycloak
// * Los roles están en realm_access.roles y resource_access.<client>.roles
// */
//public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
//
//    @Override
//    public Collection<GrantedAuthority> convert(Jwt jwt) {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//
//        // Extraer roles del realm (realm_access.roles)
//        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//        if (realmAccess != null && realmAccess.containsKey("roles")) {
//            @SuppressWarnings("unchecked")
//            List<String> realmRoles = (List<String>) realmAccess.get("roles");
//            authorities.addAll(realmRoles.stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                    .collect(Collectors.toList()));
//        }
//
//        // Extraer roles del cliente (resource_access.<client>.roles)
//        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
//        if (resourceAccess != null) {
//            resourceAccess.values().forEach(resource -> {
//                @SuppressWarnings("unchecked")
//                Map<String, Object> clientAccess = (Map<String, Object>) resource;
//                if (clientAccess.containsKey("roles")) {
//                    @SuppressWarnings("unchecked")
//                    List<String> clientRoles = (List<String>) clientAccess.get("roles");
//                    authorities.addAll(clientRoles.stream()
//                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                            .collect(Collectors.toList()));
//                }
//            });
//        }
//
//        return authorities;
//    }
//}
//
