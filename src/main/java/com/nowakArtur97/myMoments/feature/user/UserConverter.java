package com.nowakArtur97.myMoments.feature.user;

import org.modelmapper.Converter;

public class UserConverter {

    public static Converter<UserDTO, UserEntity> userDTOConverter = context -> {

        UserEntity userEntity = new UserEntity();
        UserDTO userDTO = context.getSource();

        userEntity.setUsername(userDTO.getUsername());
        userEntity.setEmail(userDTO.getEmail());

        if (userDTO.getProfile() == null) {
            UserProfileEntity userProfile = new UserProfileEntity();
            userProfile.setGender(Gender.UNSPECIFIED);

            userEntity.setProfile(userProfile);
        }

        return userEntity;
    };
}
