package com.nowakArtur97.myMoments.feature.user.registration;

import com.nowakArtur97.myMoments.feature.user.shared.BasicUserValidationConstraints;
import com.nowakArtur97.myMoments.feature.user.shared.User;
import com.nowakArtur97.myMoments.feature.user.validation.PasswordsMatch;
import com.nowakArtur97.myMoments.feature.user.validation.UniqueEmail;
import com.nowakArtur97.myMoments.feature.user.validation.UniqueUserName;
import com.nowakArtur97.myMoments.feature.user.validation.ValidPasswords;
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
@PasswordsMatch(message = "{user.password.notMatch}", groups = BasicUserValidationConstraints.class)
@ValidPasswords(groups = BasicUserValidationConstraints.class)
@ApiModel(description = "Model responsible for User validation during registration")
public class UserDTO implements User {

    @ApiModelProperty(notes = "The user's id", required = true)
    private Long id;

    @UniqueUserName(message = "{user.name.unique}", groups = BasicUserValidationConstraints.class)
    @NotBlank(message = "{user.name.notBlank}")
    @Size(min = 4, max = 40, message = "{user.name.size}")
    @ApiModelProperty(notes = "The user's name", required = true)
    private String username;

    @UniqueEmail(message = "{user.email.unique}", groups = BasicUserValidationConstraints.class)
    @Email(message = "{user.email.wrongFormat}")
    @NotBlank(message = "{user.email.notBlank}")
    @ApiModelProperty(notes = "The user's email", required = true)
    private String email;

    @NotBlank(message = "{user.password.notBlank}")
    @ApiModelProperty(notes = "The user's password", required = true)
    private String password;

    @NotBlank(message = "{user.matchingPassword.notBlank}")
    @ApiModelProperty(notes = "The user's password for confirmation", required = true)
    private String matchingPassword;

    @Valid
    @ApiModelProperty(notes = "The user's profile")
    private UserProfileDTO profile;

    public UserDTO(String username, String email, String password, String matchingPassword, UserProfileDTO profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.matchingPassword = matchingPassword;
        this.profile = profile;
    }

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