package com.nowakArtur97.myMoments.feature.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel(description = "User data required for Authentication")
class AuthenticationRequest implements User {

    @ApiModelProperty(notes = "The user's name")
    private final String username;

    @ApiModelProperty(notes = "The user's password", required = true)
    private final String password;

    @ApiModelProperty(notes = "The user's email")
    private final String email;
}
