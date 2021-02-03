package com.nowakArtur97.myMoments.feature.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel(description = "API key")
class AuthenticationResponse {

    @ApiModelProperty(notes = "Generated token")
    private String token;

    @ApiModelProperty(notes = "Expiration time in milliseconds")
    private long expirationTimeInMilliseconds;
}
