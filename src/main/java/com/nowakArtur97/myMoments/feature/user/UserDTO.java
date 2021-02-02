package com.nowakArtur97.myMoments.feature.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordsMatch(message = "{user.password.notMatch:Passwords don't match.}")
@ValidPasswords
@ApiModel(description = "Model responsible for User validation during registration")
class UserDTO {

    @UniqueUserName(message = "{user.name.unique:Username: '${validatedValue}' is already taken.}")
    @NotBlank(message = "{user.name.notBlank:Username cannot be empty.}")
    @Size(min = 5, max = 40, message = "{user.name.size:Username must be between {min} and {max} characters.}")
    @ApiModelProperty(notes = "The user's name")
    private String username;

    @UniqueEmail(message = "{user.email.unique:Email: '${validatedValue}' is already taken.}")
    @Email(message = "{user.email.wrongFormat:Email must be a valid email address.}")
    @NotBlank(message = "{user.email.notBlank:Email cannot be empty.}")
    @ApiModelProperty(notes = "The user's email")
    private String email;

    @NotBlank(message = "{user.password.notBlank:Password cannot be empty.}")
    @ApiModelProperty(notes = "The user's password")
    private String password;

    @NotBlank(message = "{user.matchingPassword.notBlank:Matching password cannot be empty.}")
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