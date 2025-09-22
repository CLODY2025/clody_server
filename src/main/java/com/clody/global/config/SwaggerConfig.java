package com.clody.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        Info info = new Info().title("Clody").description("Clody API 명세서").version("0.0.1");

        String securitySchemeName = "Bearer Authentication";
        SecurityRequirement requirement = new SecurityRequirement().addList(securitySchemeName);

        Components components = new Components().addSecuritySchemes(securitySchemeName, new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER));

        return new OpenAPI().info(info)
                .addServersItem(new Server().url("/"))
                .addSecurityItem(requirement)
                .components(components);
    }


}
