package com.chencraft.ntu.configuration;

import com.chencraft.ntu.constant.Tags;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * Defines the API information, license, contact details, and available tags.
 */
@Configuration
public class SwaggerDocumentationConfig {
    /**
     * Configures the OpenAPI bean for generating API documentation.
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .specVersion(SpecVersion.V31)
                .info(new Info()
                              .title("Distributed Banking System API")
                              .description(
                                      """
                                              API for the Distributed Banking System project.
                                              Provides services for account management, balance updates, and monitoring.""")
                              .termsOfService("https://swagger.io/terms/")
                              .version("1.0.0")
                              .license(new License()
                                               .name("GPL-3.0")
                                               .url("https://www.gnu.org/licenses/gpl-3.0.html"))
                              .contact(new Contact()
                                               .email("sc6103@chencraft.com")))
                .externalDocs(new ExternalDocumentation().description("Find out more about springdoc-openapi-starter-webmvc-ui")
                                                         .url("https://springdoc.org/"))
                .tags(
                        List.of(
                                new Tag().name(Tags.ACCOUNT),
                                new Tag().name(Tags.BALANCE)
                        )
                );
    }
}
