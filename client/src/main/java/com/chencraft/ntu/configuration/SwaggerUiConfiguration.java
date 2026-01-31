package com.chencraft.ntu.configuration;

import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for Swagger UI.
 * Handles view controller registration for the Swagger UI path.
 */
@Configuration
public class SwaggerUiConfiguration implements WebMvcConfigurer {
    /**
     * Maps the /swagger-ui/ path to the actual Swagger UI index.html.
     *
     * @param registry the ViewControllerRegistry
     */
    @Override
    public void addViewControllers(@Nonnull ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui/").setViewName("forward:/swagger-ui/index.html");
    }
}
