package com.nowakArtur97.myMoments.configuration.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "my-moments.jwt")
@ConstructorBinding
@AllArgsConstructor
@Getter
public final class JwtConfigurationProperties {

    private final String secretKey;

    private final long jwtTokenValidity;

    private final String[] ignoredAntMatchers;

    private final String[] authenticatedAntMatchers;

    public final int authorizationHeaderLength;

    public final String authorizationHeader;
}
