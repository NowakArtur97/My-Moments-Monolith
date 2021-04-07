package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.feature.user.registration.UserDTO;
import com.nowakArtur97.myMoments.feature.user.registration.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder bCryptPasswordEncoder;

    public void convertDTOToEntity(UserDTO userDTO, UserEntity userEntity) {

        userEntity.setUsername(userDTO.getUsername());
        userEntity.setEmail(userDTO.getEmail());

        UserProfileEntity userProfileEntity = userEntity.getProfile();
        UserProfileDTO userProfileDTO = userDTO.getProfile();

        if (userProfileDTO == null) {

            userProfileEntity = new UserProfileEntity();
            userEntity.setProfile(userProfileEntity);
        } else {

            userProfileEntity.setAbout(userProfileDTO.getAbout());
            userProfileEntity.setGender(Arrays.stream(Gender.values())
                    .filter(gender -> gender.name().equals(userProfileDTO.getGender().toUpperCase()))
                    .findFirst()
                    .orElse(Gender.UNSPECIFIED));
            userProfileEntity.setInterests(userProfileDTO.getInterests());
            userProfileEntity.setLanguages(userProfileDTO.getLanguages());
            userProfileEntity.setLocation(userProfileDTO.getLocation());
        }

        userEntity.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        setupDefaultProfileValues(userEntity, userProfileEntity);
    }

    private static void setupDefaultProfileValues(UserEntity userEntity, UserProfileEntity userProfileEntity) {

        userProfileEntity.setId(userEntity.getId());
        userProfileEntity.setUser(userEntity);

        if (userProfileEntity.getGender() == null) {
            userProfileEntity.setGender(Gender.UNSPECIFIED);
        }
        if (userProfileEntity.getAbout() == null) {
            userProfileEntity.setAbout("");
        }
        if (userProfileEntity.getInterests() == null) {
            userProfileEntity.setInterests("");
        }
        if (userProfileEntity.getLanguages() == null) {
            userProfileEntity.setLanguages("");
        }
        if (userProfileEntity.getLocation() == null) {
            userProfileEntity.setLocation("");
        }
    }
}
