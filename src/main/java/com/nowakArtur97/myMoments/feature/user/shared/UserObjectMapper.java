package com.nowakArtur97.myMoments.feature.user.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowakArtur97.myMoments.feature.user.registration.UserRegistrationDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserObjectMapper {

    private final ObjectMapper objectMapper;

    public UserRegistrationDTO getUserDTOFromString(String userAsString) {

        try {

            return objectMapper.readValue(userAsString, UserRegistrationDTO.class);

        } catch (IOException exception) {

            return new UserRegistrationDTO();
        }
    }

    public UserUpdateDTO getUserDTOFromString2(String userAsString) {

        try {

            return objectMapper.readValue(userAsString, UserUpdateDTO.class);

        } catch (IOException exception) {

            return new UserUpdateDTO();
        }
    }
}
