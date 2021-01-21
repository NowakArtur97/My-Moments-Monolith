package com.nowakArtur97.myMoments.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain getSecurityWebFilterChain(ServerHttpSecurity http) {

        return http.authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .build();
    }
}
