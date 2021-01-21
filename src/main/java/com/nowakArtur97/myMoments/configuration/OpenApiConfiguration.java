package com.nowakArtur97.myMoments.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = OpenApiConfigurationProperties.class)
class OpenApiConfiguration {

    @Bean
    OpenAPI getOpenAPI(OpenApiConfigurationProperties openApiConfigurationProperties) {
        return new OpenAPI()
                .info(getApiInfo(openApiConfigurationProperties));
    }

    private Info getApiInfo(OpenApiConfigurationProperties openApiConfigurationProperties) {

        Contact contact = new Contact()
                .name(openApiConfigurationProperties.getContactName())
                .email(openApiConfigurationProperties.getContactEmail())
                .url(openApiConfigurationProperties.getContactUrl());

        return new Info()
                .version(openApiConfigurationProperties.getVersion())
                .title(openApiConfigurationProperties.getTitle())
                .description(openApiConfigurationProperties.getDescription())
                .termsOfService(openApiConfigurationProperties.getTermsOfServiceUrl())
                .license(new License().name(openApiConfigurationProperties.getLicense())
                        .url(openApiConfigurationProperties.getLicenseUrl()))
                .contact(contact);
    }
}
