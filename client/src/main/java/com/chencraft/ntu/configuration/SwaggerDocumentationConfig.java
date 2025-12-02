package com.chencraft.ntu.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerDocumentationConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .specVersion(SpecVersion.V31)
                .info(new Info()
                              .title("TODO: Title - OpenAPI 3.1")
                              .description(
                                      """
                                              TODO: Description""")
                              .termsOfService("https://swagger.io/terms/")
                              .version("1.0.0")
                              .license(new License()
                                               .name("GPL-3.0")
                                               .url("https://www.gnu.org/licenses/gpl-3.0.html"))
                              .contact(new Contact()
                                               .email("sc6103@chencraft.com")))
                .externalDocs(new ExternalDocumentation().description("Find out more about springdoc-openapi-starter-webmvc-ui")
                                                         .url("https://springdoc.org/"));
//                .addServersItem(new Server().url(serverUrl).description(serverDescription))
//                .tags(generateTags())
//                .components(generateComponents());
    }
}
