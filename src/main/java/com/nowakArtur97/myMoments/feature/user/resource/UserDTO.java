package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.feature.user.registration.UserProfileDTO;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class UserDTO {

    protected String username;

    protected String email;

    protected String password;

    protected String matchingPassword;

    protected UserProfileDTO profile;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;

        UserDTO userUpdateDTO = (UserDTO) o;

        return Objects.equals(getUsername(), userUpdateDTO.getUsername()) &&
                Objects.equals(getEmail(), userUpdateDTO.getEmail()) &&
                Objects.equals(getPassword(), userUpdateDTO.getPassword()) &&
                Objects.equals(getMatchingPassword(), userUpdateDTO.getMatchingPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getEmail(), getPassword(), getMatchingPassword());
    }
}