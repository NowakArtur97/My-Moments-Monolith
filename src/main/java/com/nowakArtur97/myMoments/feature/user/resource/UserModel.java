package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.feature.user.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@ApiModel(description = "Details about the User")
public class UserModel implements User {

    @ApiModelProperty(notes = "The unique id of the User", required = true)
    private Long id;

    @ApiModelProperty(notes = "The user's name", required = true)
    private String username;

    @ApiModelProperty(notes = "The user's email", required = true)
    private String email;

    @ApiModelProperty(notes = "The user's password", required = true)
    private String password;

    @ApiModelProperty(notes = "The user's profile")
    private UserProfileModel profile;

    @ApiModelProperty(notes = "The user's roles")
    private Set<RoleModel> roles;
}
