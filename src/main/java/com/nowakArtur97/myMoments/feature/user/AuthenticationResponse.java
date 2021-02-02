package com.nowakArtur97.myMoments.feature.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class AuthenticationResponse {

    @ApiModelProperty(notes = "Generated token")
    private final String token;

    @ApiModelProperty(notes = "Expiration time in milliseconds")
    private final long expirationTimeInMilliseconds;
}
