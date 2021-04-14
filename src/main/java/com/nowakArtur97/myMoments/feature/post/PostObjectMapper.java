package com.nowakArtur97.myMoments.feature.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
class PostObjectMapper {

    private final ObjectMapper objectMapper;

    public PostDTO getPostDTOFromString(String postAsString, MultipartFile[] photos) {

        if (postAsString == null) {
            return new PostDTO(photos);
        }

        try {
            PostDTO postDTO = objectMapper.readValue(postAsString, PostDTO.class);
            postDTO.setPhotos(photos);
            return postDTO;

        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
