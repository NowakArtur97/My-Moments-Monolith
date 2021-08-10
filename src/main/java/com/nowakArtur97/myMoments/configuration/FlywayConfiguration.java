package com.nowakArtur97.myMoments.configuration;

import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AllArgsConstructor
class FlywayConfiguration {

    private final Environment env;

    @Bean(initMethod = "migrate")
    public Flyway getFlyway() {

        return new Flyway(
                Flyway.configure()
                        .baselineOnMigrate(false)
                        .locations(env.getRequiredProperty("spring.flyway.locations"))
                        .dataSource(
                                env.getRequiredProperty("spring.flyway.url"),
                                env.getRequiredProperty("spring.flyway.user"),
                                env.getRequiredProperty("spring.flyway.password")));
    }
}