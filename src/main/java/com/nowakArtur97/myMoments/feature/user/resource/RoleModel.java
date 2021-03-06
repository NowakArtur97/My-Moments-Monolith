package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.feature.user.entity.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the User's Role")
public class RoleModel implements Role {

    @ApiModelProperty(notes = "The role's name")
    private String name;
}
