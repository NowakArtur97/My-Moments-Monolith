package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.feature.user.registration.UserRegistrationDTO;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public static Converter<UserRegistrationDTO, UserEntity> userDTOConverter = context -> {

        UserEntity userEntity = context.getDestination();
        UserRegistrationDTO userRegistrationDTO = context.getSource();

        UserProfileEntity userProfileEntity = userEntity.getProfile();

        if (userRegistrationDTO.getProfile() == null) {

            userProfileEntity = new UserProfileEntity();
            userEntity.setProfile(userProfileEntity);
        }

        setupDefaultProfileValues(userEntity, userProfileEntity);

        return userEntity;
    };

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
