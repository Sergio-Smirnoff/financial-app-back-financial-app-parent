package com.financialapp.commons.web.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class OpenApiAutoConfiguration {

    private static final String SCHEME_NAME = "InternalToken";

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openAPI(@Value("${spring.application.name:service}") String applicationName) {
        return new OpenAPI()
                .info(new Info().title(applicationName + " API").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
                                .name("X-Internal-Token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)));
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorCodeOperationCustomizer errorCodeOperationCustomizer() {
        return new ErrorCodeOperationCustomizer();
    }
}
