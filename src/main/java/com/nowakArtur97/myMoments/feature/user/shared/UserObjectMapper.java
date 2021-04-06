package com.nowakArtur97.myMoments.feature.user.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowakArtur97.myMoments.feature.user.registration.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserObjectMapper {

    private final ObjectMapper objectMapper;

    public UserDTO getUserDTOFromString(String userAsString) {

        try {

            return objectMapper.readValue(userAsString, UserDTO.class);

        } catch (IOException exception) {

            return new UserDTO();
        }
    }
}
