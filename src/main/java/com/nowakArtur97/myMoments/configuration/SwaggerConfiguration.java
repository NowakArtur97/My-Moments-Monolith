package com.nowakArtur97.myMoments.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(value = SwaggerConfigurationProperties.class)
class SwaggerConfiguration {

    @Bean
    Docket getDocket(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.ant(swaggerConfigurationProperties.getPathSelectors()))
                .build()
                .apiInfo(getApiDetails(swaggerConfigurationProperties));
    }

    private ApiInfo getApiDetails(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new ApiInfoBuilder()
                .version(swaggerConfigurationProperties.getVersion())
                .title(swaggerConfigurationProperties.getTitle())
                .description(swaggerConfigurationProperties.getDescription())
                .termsOfServiceUrl(swaggerConfigurationProperties.getTermsOfServiceUrl())
                .license(swaggerConfigurationProperties.getLicense())
                .licenseUrl(swaggerConfigurationProperties.getLicenseUrl())
                .contact(getContact(swaggerConfigurationProperties))
                .build();
    }

    private Contact getContact(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new Contact(swaggerConfigurationProperties.getContactName(),
                swaggerConfigurationProperties.getContactUrl(), swaggerConfigurationProperties.getContactEmail());
    }
}
