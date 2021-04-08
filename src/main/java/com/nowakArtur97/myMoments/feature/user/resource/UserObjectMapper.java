package com.nowakArtur97.myMoments.feature.user.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserObjectMapper {

    private final ObjectMapper objectMapper;

    public <T extends UserDTO> UserDTO getUserDTOFromString(String userAsString, Class<T> clazz) {

        try {
            return objectMapper.readValue(userAsString, clazz);

        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
