//package com.ms.transportes.config;
//
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//import io.swagger.v3.oas.annotations.info.Info;
//import io.swagger.v3.oas.annotations.security.OAuthFlow;
//import io.swagger.v3.oas.annotations.security.OAuthFlows;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Configuración de OpenAPI/Swagger con autenticación OAuth2 (Keycloak)
// * Permite probar los endpoints protegidos directamente desde Swagger UI
// */
//@Configuration
//@OpenAPIDefinition(
//    info = @Info(
//        title = "API de Transporte - Sistema de Logística",
//        version = "1.0",
//        description = "Microservicio de transporte protegido con Keycloak JWT. " +
//                "Roles disponibles: TRANSPORTISTA, ADMIN"
//    ),
//    security = @SecurityRequirement(name = "keycloak-oauth2")
//)
//@SecurityScheme(
//    name = "keycloak-oauth2",
//    type = SecuritySchemeType.OAUTH2,
//    flows = @OAuthFlows(
//        password = @OAuthFlow(
//            tokenUrl = "http://localhost:8081/realms/transporte-realm/protocol/openid-connect/token"
//        )
//    )
//)
//public class OpenApiConfig {
//}
//
