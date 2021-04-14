package com.nowakArtur97.myMoments.feature.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class PostObjectMapper {

    private final ObjectMapper objectMapper;

    public PostDTO getPostDTOFromString(String postAsString, List<MultipartFile> photos) {

        List<MultipartFile> photosList = hasPhotos(photos) ? photos : new ArrayList<>();

        if (postAsString == null) {
            return new PostDTO(photosList);
        }

        try {
            PostDTO postDTO = objectMapper.readValue(postAsString, PostDTO.class);
            postDTO.setPhotos(photosList);
            return postDTO;

        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private boolean hasPhotos(List<MultipartFile> photos) {

        return photos != null && photos.stream().allMatch(photo -> photo.getSize() > 0);
    }
}
