package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.feature.user.registration.UserDTO;
import com.nowakArtur97.myMoments.feature.user.registration.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder bCryptPasswordEncoder;

    public UserEntity convertDTOToEntity(UserDTO userDTO, MultipartFile image, RoleEntity role) throws IOException {

        UserEntity userEntity = new UserEntity();
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userEntity.setProfile(userProfileEntity);

        setUp(userEntity, userDTO, image);

        userEntity.addRole(role);

        return userEntity;
    }

    public void convertDTOToEntity(UserEntity userEntity, UserDTO userDTO, MultipartFile image) throws IOException {
        setUp(userEntity, userDTO, image);
    }

    private void setUp(UserEntity userEntity, UserDTO userDTO, MultipartFile image) throws IOException {

        setUserProperties(userDTO, userEntity, image);

        UserProfileDTO userProfileDTO = userDTO.getProfile();
        UserProfileEntity userProfileEntity = userEntity.getProfile();

        if (userProfileDTO != null) {

            setupUserProfileProperties(userProfileEntity, userProfileDTO);
        }

        setupDefaultProfileValues(userEntity, userProfileEntity);
    }

    private void setUserProperties(UserDTO userDTO, UserEntity userEntity, MultipartFile image) throws IOException {

        userEntity.setUsername(userDTO.getUsername());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        if (image != null) {
            userEntity.getProfile().setImage(image.getBytes());
        }
    }

    private void setupUserProfileProperties(UserProfileEntity userProfileEntity, UserProfileDTO userProfileDTO) {

        userProfileEntity.setAbout(userProfileDTO.getAbout());

        String userGender = userProfileDTO.getGender();
        if (userGender != null) {
            userProfileEntity.setGender(Arrays.stream(Gender.values())
                    .filter(gender -> gender.name().equals(userGender.toUpperCase()))
                    .findFirst()
                    .orElse(Gender.UNSPECIFIED));
        }
        userProfileEntity.setInterests(userProfileDTO.getInterests());
        userProfileEntity.setLanguages(userProfileDTO.getLanguages());
        userProfileEntity.setLocation(userProfileDTO.getLocation());
    }

    private void setupDefaultProfileValues(UserEntity userEntity, UserProfileEntity userProfileEntity) {

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
