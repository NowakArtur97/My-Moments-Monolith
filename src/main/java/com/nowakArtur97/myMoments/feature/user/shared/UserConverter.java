package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.feature.user.registration.UserDTO;
import org.modelmapper.Converter;

public class UserConverter {

    public static Converter<UserDTO, UserEntity> userDTOConverter = context -> {

        UserEntity userEntity = context.getDestination();
        UserDTO userDTO = context.getSource();

        if (userDTO.getProfile() == null) {
            UserProfileEntity userProfile = new UserProfileEntity();

            userEntity.setProfile(userProfile);
            userProfile.setUser(userEntity);
        }

        UserProfileEntity userProfileEntity = userEntity.getProfile();

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

        return userEntity;
    };
}
