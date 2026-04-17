package com.dhbw.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Konfiguration.
 *
 * Fügt ein JWT Bearer Security-Schema hinzu, damit Swagger UI den "Authorize"-Button anzeigt
 * und Requests mit "Authorization: Bearer <token>" senden kann.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "SWE2 Backend API", version = "v1"),
        security = { @SecurityRequirement(name = "bearerAuth") }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
