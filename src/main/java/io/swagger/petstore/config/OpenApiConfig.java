package io.swagger.petstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Exposes the bundled OpenAPI spec file at /api/v3/openapi.yaml and /api/v3/openapi.json.
 * springdoc's Swagger UI is configured (in application.properties) to read from that URL.
 */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/openapi.yaml", "/openapi.json")
                .addResourceLocations("classpath:/");
    }
}
