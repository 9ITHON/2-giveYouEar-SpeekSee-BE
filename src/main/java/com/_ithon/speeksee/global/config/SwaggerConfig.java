package com._ithon.speeksee.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("SpeekSee API")
                        .description("SpeekSee REST API 문서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("김규일")
                                .email("rlarbdlf222@gmail.com")
                                .url("https://github.com/9ITHON/2-SpeekSee-BE"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
            .addSecurityItem(new SecurityRequirement().addList("JWT"))
            .components(new Components().addSecuritySchemes("JWT", new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")));
    }
}
