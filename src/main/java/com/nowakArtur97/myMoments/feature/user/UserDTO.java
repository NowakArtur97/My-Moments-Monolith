package com.nowakArtur97.myMoments.feature.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@PasswordsMatch(message = "{user.password.notMatch}")
@ValidPasswords
@ApiModel(description = "Model responsible for User validation during registration")
class UserDTO {

    @UniqueUserName(message = "{user.name.unique}")
    @NotBlank(message = "{user.name.notBlank}")
    @Size(min = 5, max = 40, message = "{user.name.size}")
    @ApiModelProperty(notes = "The user's name")
    private String username;

    @UniqueEmail(message = "{user.email.unique}")
    @Email(message = "{user.email.wrongFormat}")
    @NotBlank(message = "{user.email.notBlank}")
    @ApiModelProperty(notes = "The user's email")
    private String email;

    @NotBlank(message = "{user.password.notBlank}")
    @ApiModelProperty(notes = "The user's password")
    private String password;

    @NotBlank(message = "{user.matchingPassword.notBlank}")
    @ApiModelProperty(notes = "The user's password for confirmation")
    private String matchingPassword;

    @Valid
    private UserProfileDTO profile;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;

        UserDTO userDTO = (UserDTO) o;

        return Objects.equals(getUsername(), userDTO.getUsername()) &&
                Objects.equals(getEmail(), userDTO.getEmail()) &&
                Objects.equals(getPassword(), userDTO.getPassword()) &&
                Objects.equals(getMatchingPassword(), userDTO.getMatchingPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getEmail(), getPassword(), getMatchingPassword());
    }
}