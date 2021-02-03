package com.nowakArtur97.myMoments.feature.user;

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

        if (userEntity.getProfile().getGender() == null) {
            userEntity.getProfile().setGender(Gender.UNSPECIFIED);
        }
        if (userEntity.getProfile().getAbout() == null) {
            userEntity.getProfile().setAbout("");
        }
        if (userEntity.getProfile().getInterests() == null) {
            userEntity.getProfile().setInterests("");
        }
        if (userEntity.getProfile().getLanguages() == null) {
            userEntity.getProfile().setLanguages("");
        }
        if (userEntity.getProfile().getLocation() == null) {
            userEntity.getProfile().setLocation("");
        }

        return userEntity;
    };
}
