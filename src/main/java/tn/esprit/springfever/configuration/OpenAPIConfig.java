package tn.esprit.springfever.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;


@Configuration
@OpenAPIDefinition(servers = {@Server(url = "/api/v1/springfever")})
public class OpenAPIConfig {







    @Bean
    public OpenAPI springShopOpenAPI() {
        SecurityConfiguration securityConfiguration = new SecurityConfiguration();
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-jwt", securityConfiguration.securityScheme()))
                .info(infoAPI())
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")));
    }
    public Info infoAPI() {
        return new Info().title("USER Microservice")
                .description("This is a springboot exam")
                .contact(contactAPI());
    }
    public Contact contactAPI() {
        Contact contact = new Contact().name("Aymen_Essid-4SE4")
                .email("aymen.essid@esprit.tn");

        return contact;
    }
}
